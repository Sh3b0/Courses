unit nbtempo1;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, process, FileUtil, DateTimePicker, Forms, Controls,
  Graphics, Dialogs, ExtCtrls, ComCtrls, StdCtrls, EditBtn, Buttons, Menus,
  md5,sha1,BaseUnix,Unix,StrUtils,
  //PostScriptCanvas,
  printers;

type

  { TForm1 }

  TForm1 = class(TForm)
    B_vid: TBitBtn;
    B_About: TBitBtn;
    B_killgen: TBitBtn;
    BOpen_csv: TBitBtn;
    BT_pdfreport: TBitBtn;
    BT_Setup: TBitBtn;
    BTExit: TBitBtn;
    BT_timeline_gen: TButton;
    cb_skip_image_calc: TCheckBox;
    DTP_to: TDateTimePicker;
    DE_out_dir: TDirectoryEdit;
    DTP_from: TDateTimePicker;
    Einv_name: TEdit;
    ECase_name: TEdit;
    E_csv_done: TEdit;
    FNE_image_file: TFileNameEdit;
    GB_set: TGroupBox;
    Label1: TLabel;
    Label2: TLabel;
    Label3: TLabel;
    Label4: TLabel;
    Label5: TLabel;
    L_csv_done: TLabel;
    L_working_wait: TLabel;
    Lchooseimage: TLabel;
    Ldtfrom: TLabel;
    Ldtto: TLabel;
    Lchooseoutdir: TLabel;
    Panel1: TPanel;
    PB_working_wait: TProgressBar;
    Process1: TProcess;
    RG_Dates: TRadioGroup;
    procedure BT_openpdfClick(Sender: TObject);
    procedure BOpen_csvClick(Sender: TObject);
    procedure BOpen_csvMouseEnter(Sender: TObject);
    procedure BT_openpdfMouseEnter(Sender: TObject);
    procedure BT_pdfreportClick(Sender: TObject);
    procedure BT_setupClick(Sender: TObject);
    procedure BT_timeline_genClick(Sender: TObject);
    procedure BtExitClick(Sender: TObject);
    procedure B_AboutClick(Sender: TObject);
    procedure B_all_datesClick(Sender: TObject);
    procedure B_killgenClick(Sender: TObject);
    procedure B_vidClick(Sender: TObject);
    procedure DE_out_dirChange(Sender: TObject);
    procedure ECase_nameChange(Sender: TObject);
    procedure ECase_nameClick(Sender: TObject);
    procedure Einv_nameChange(Sender: TObject);
    procedure Einv_nameEnter(Sender: TObject);
    procedure E_csv_doneClick(Sender: TObject);
    procedure FNE_image_fileAcceptFileName(Sender: TObject; var Value: String);
    procedure FNE_image_fileChange(Sender: TObject);
    procedure FormActivate(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure M_doClick(Sender: TObject);
    procedure RG_DatesClick(Sender: TObject);

  private
    { private declarations }
  public
    { public declarations }
  end;

var
  Form1: TForm1;
  selectedfile: string;
  SetupList: Tstringlist;
  z,d1,d2,d,macd,dcsv,ctime,fn,dn,zulu,zl,skew,dateinterval,datefrom,dateto: string;
  CSV_full_name,CSV_create_time,CSV_date_from,CSV_date_to,CSV_date_interval,
  CSV_zone_name,CSV_time_skew,CSV_secsize,CSV_fstype,CSV_offset: string;

  image_files_list,Report_mactime_command_line,Report_tsk_command_line:string;
  tsk_a,tsk_DD,tsk_FF,tsk_r,tsk_d,tsk_l,tsk_p:string;
  tsk_m,tsk_i,tsk_b,tsk_f,tsk_s,tsk_o,tsk_z:string;
  mct_d,mct_h,mct_m,mct_y,mct_b,mct_g,mct_p,mct_i,mct_z:string;
  pdf_genrep,timeline_hash,usetlprefix,use_examiner_name,use_case_name,use_image_name:string;
  Report_md5_timeline,Report_sha1_timeline,Report_md5_image,Report_sha1_image,Report_create_time:string;
  wpath,outpath,configpath:string;
  ActualTime: TDateTime;
  CSV_row_count:longint;
  { TODO 0 -ogg -ctodo : //cross platform rebuild }
  { TODO DONE 6 -ogg -cdev : check file name.csv - check dates interval }

implementation
{$R *.lfm}

{ TForm1 }
uses nbsetup,nbviewimagedata,nbabout,nbprintreport;




function ExtractFileNameEX(const AFileName:String): String;
 var
   I: integer;
 begin
    I := LastDelimiter('.'+PathDelim+DriveDelim,AFileName);
        if (I=0)  or  (AFileName[I] <> '.')  then  I := MaxInt;
    Result := ExtractFileName(Copy(AFileName,1,I-1));
 end;

function IsValidFileName(const fileName : string) : boolean;
const
  InvalidCharacters : set of char = ['\', '/', ':', '*', '?', '"', '<', '>', '|'];
var
  c : char;
begin
  result := fileName <> '';

  if result then
  begin
    for c in fileName do
    begin
      result := NOT (c in InvalidCharacters) ;
      if NOT result then break;
    end;
  end;
end; (* IsValidFileName *)

procedure TForm1.BT_timeline_genClick(Sender: TObject);
const
  BUF_SIZE = 2048;
var
  OutputStream    : TStream;
  BytesRead,count : longint;
  Buffer          : array[1..BUF_SIZE] of byte;
  TS_error,CSVsl  : TstringList;

begin
     E_csv_done.text:='';
     L_csv_done.Caption:='FILE CSV CREATED';
     //check imagefile name
     if not IsValidFileName(ExtractFileName(FNE_image_file.FileName)) then
     begin
       Showmessage('Invalid image file name!');
       exit();
     end;
     //check user RW permission in both in/out dir
     //check write permission in outdir
     if fpAccess(DE_out_dir.Directory,W_OK)<>0 then
     begin
         Showmessage('Sorry. I can''t write to '+outpath+' !');
         exit();
     end;
     //R_OK User has read rights permission for image file.
     if fpAccess(ExtractFileName(FNE_image_file.FileName),R_OK)=1 then
     begin
         Showmessage('Sorry. You don''t have read premission for '+ExtractFileName(FNE_image_file.FileName)+' !');
         exit();
     end;

     { TODO -ogg -cdevelop : calc image hash as an option in setup }
     //calculate image md5
     Report_md5_image :='';
     Report_sha1_image:='';
     if not cb_skip_image_calc.checked then
     begin
     L_working_wait.Caption:='Calculating image MD5. Please Wait...';
     L_working_wait.Visible:=True;
     Form1.UpdateShowing;
     Form1.SetCursor(crHourglass);
     Application.ProcessMessages;
     Report_md5_image :=MD5Print(MD5File(FNE_image_file.FileName));
     Form1.SetCursor(crDefault);
     Application.ProcessMessages;
     Form1.UpdateShowing;

     //Calculate image SHA1
     L_working_wait.Caption:='Calculating image SHA1. Please Wait...';
     Form1.UpdateShowing;
     Form1.SetCursor(crHourglass);
     Application.ProcessMessages;
     Report_sha1_image :=SHA1Print(SHA1File(FNE_image_file.FileName));
     Form1.SetCursor(crDefault);
     Application.ProcessMessages;
     Form1.UpdateShowing;
     end;

     TS_error:=Tstringlist.Create;
     B_killgen.Visible:=True;
     B_killgen.enabled:=True;
     E_csv_done.text:='';
     L_csv_done.Caption:='FILE CSV CREATED';
     CSV_full_name:='';
     PB_working_wait.Visible:=True;
     L_working_wait.Caption:='Generating BODY. Please Wait...';
     L_working_wait.Visible:=True;
     Application.ProcessMessages;
     Form1.Refresh;
     Form1.UpdateShowing;
     //old construction csv file name chunk
     //actualtime:= Now;
     //ctime:=FormatDateTime('yyyy-mm-dd_hh_nn_ss', actualtime);
     CSV_create_time:=FormatDateTime('yyyy-mm-dd_hh_nn_ss', now);
     Report_create_time:=FormatDateTime('dd/mm/yyyy hh:nn:ss', now);
     dateinterval:='';
     datefrom    :='';
     dateto      :='';
     //nulldate from ***OR*** to
     if  (DTP_from.DateIsNull) or (DTP_to.DateIsNull) then
     begin
        DTP_from.Date:=-1.7E308;
        DTP_to.Date:=1.7E308;
        CSV_date_from:='';
        CSV_date_to  :='';
        CSV_date_interval:='';
     end;
     //nulldate from ***AND*** to (both)
     if (DTP_from.DateIsNull) and (DTP_to.DateIsNull) then
     begin
       //date interval (text)
       dateinterval:='';
       //mac date (old param)
       macd:=zulu+' -d 0000-00-00';
       //all dates (text)
       //dcsv:='All';
       CSV_date_from:='';
       CSV_date_to  :='';
       CSV_date_interval:='alldates';
     end
     else
     begin
       //from..to dates not null
       CSV_date_from:=FormatDateTime('yyyy-mm-dd', DTP_from.Date);
       CSV_date_to  :=FormatDateTime('yyyy-mm-dd', DTP_to.Date);
       CSV_date_interval:='from_'+CSV_date_from+' to_'+CSV_date_to;
       datefrom:=FormatDateTime('yyyy-mm-dd', DTP_from.Date);
       dateto  :=FormatDateTime('yyyy-mm-dd', DTP_to.Date);
       //argument of mactime command
       dateinterval:=datefrom+'..'+dateto;
     end;
     //hack for remove / char in timezone name used in nomefile
     //stringReplace(Stringvar , '/',  '' ,[rfReplaceAll, rfIgnoreCase]);
     CSV_zone_name := StringReplace(SetupList.Values['mct_z'],'/','',[rfReplaceAll]);

     //skew:='skew'+LE_time_skew.Text;
     CSV_time_skew := 'Skew'+SetupList.Values['tsk_s'];

     //check for multiple/splitted image file for Parameters.Add(FNE_image_file.FileName)
     //list of image files to process - see below
     image_files_list:='';
     Count:=0;
     //****************COMPOSE CSV FULL NAME
     if SetupList.Values['usetlprefix']='n' then CSV_full_name:='timeline_' else CSV_full_name:='TL_';
     if SetupList.Values['use_examiner_name']='y'  then CSV_full_name:=CSV_full_name+Einv_name.Text+'_'
                               else CSV_full_name:=CSV_full_name+'_';
     if SetupList.Values['use_case_name']='y' then CSV_full_name:=CSV_full_name+Ecase_name.Text+'_';
     if SetupList.Values['use_image_name']='y' then CSV_full_name:=CSV_full_name+ExtractFileNameEX(wpath+'/'+FNE_image_file.FileName)+'_';
     CSV_full_name:=CSV_full_name+CSV_create_time+'_'+CSV_date_interval+'_'+CSV_zone_name+'_'+CSV_time_skew;
     //check if CSV_full_name is a valid filename
     if not IsValidFilename(CSV_full_name) then
     begin
        CSV_full_name := StringReplace(CSV_full_name,'/','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'*','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'?','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'<','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'>','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'|','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,':','',[rfReplaceAll]);
        CSV_full_name := StringReplace(CSV_full_name,'"','',[rfReplaceAll]);
     end;
     Application.ProcessMessages;
     Form1.Refresh;
     Form1.UpdateShowing;
     { TODO DONE -ogg -cdevelop : Parameters on setup  }
     L_working_wait.Caption:='Generating BODY with tsk_gettimes. Please Wait...';
     Form1.Refresh;
     Form1.SetCursor(crHourglass);
     //*********************************
     // creating body.txt
     //*********************************
     With Process1 do
     begin
       Parameters.Clear;
       //try to be cross platform
       {$IFDEF WIN32}
               //Executable := 'mactime';
               //Parameters.Add('foopar');
       {$ENDIF}
       {$IFDEF Linux}
              //**************
              //tsk_gettimes
              //**************
              //Executable:=('tsk_gettimes');
              //L_working_wait.Caption:='Generating BODY with tsk_gettimes. Please Wait...';
              //Parameters.Add('FNE_image_file.FileName');
              //***************
              //fls
              //***************
              //Executable := FindDefaultExecutablePath('fls');
              Executable := FindDefaultExecutablePath('tsk_gettimes');
              if (SetupList.Values['tsk_a']<>'')  then Parameters.Add('-a');      //display . and .. dir entries
              if (SetupList.Values['tsk_l']<>'')  then Parameters.Add('-l');      //display file details in long format acc/chg/cre time size uid gid
              if (SetupList.Values['tsk_r']<>'')  then Parameters.Add('-r');      //Recursively display directories.  This will not follow deleted directories, because it can't.
              if (SetupList.Values['tsk_d']<>'')  then Parameters.Add('-d');      //deleted entries only
              if (SetupList.Values['tsk_DD']<>'') then Parameters.Add('-D');      //directory entries only
              if (SetupList.Values['tsk_FF']<>'') then Parameters.Add('-F');      //disp.file entries only
              if (SetupList.Values['tsk_p']<>'')  then Parameters.Add('-p');      //display full path
              if (SetupList.Values['tsk_m']<>'')  then
              begin
                Parameters.Add('-m');      //Display  files  in time machine format so that a timeline can be     created with mactime(1)
                Parameters.Add(SetupList.Values['tsk_m']);
              end;
              if SetupList.Values['tsk_s']<>'' then
              begin;
               Parameters.Add('-s');      //time skew
               Parameters.Add(SetupList.Values['tsk_s']);      //time skew
              end;
              if SetupList.Values['tsk_f']<>'' then
              begin;
               Parameters.Add('-f');      //fstype fls -f list
               CSV_fstype:='FS type '+SetupList.Values['tsk_f'];
               Parameters.Add(SetupList.Values['tsk_f']);      //fstype
              end
              else CSV_fstype:='FS type auto detected';
              if SetupList.Values['tsk_o']<>'' then
              begin;
               CSV_offset:='Offset '+SetupList.Values['tsk_o'] ;
               Parameters.Add('-o');      //offset
               Parameters.Add(SetupList.Values['tsk_o']);      //offset
              end
              else CSV_offset:='Offset autodetected';;
              if SetupList.Values['tsk_b']<>'' then
              begin;
               CSV_secsize:='Sector size '+SetupList.Values['tsk_b'];
               Parameters.Add('-b');      //dev sector size
               Parameters.Add(SetupList.Values['tsk_b']);      //dev sector size
              end
              else CSV_secsize:='Sector Size auto detected';
              if SetupList.Values['tsk_z']<>'' then
              begin;
               Parameters.Add('-z');      //zone
               Parameters.Add(SetupList.Values['tsk_z']);      //zone
              end;
              if SetupList.Values['tsk_i']<>'' then
              begin;
               Parameters.Add('-i');      //zone
               Parameters.Add(SetupList.Values['tsk_i']);      //zone
              end;

              //Parameters.Add('-v');      //verbose output to stderr (NO)

              //image file single file
              //Parameters.Add(FNE_image_file.FileName);
              //Parameters add multiple splitted images
              for Count:=FNE_image_file.DialogFiles.Count-1 downto 0 do
              begin
                   //compose parameter string
                   Parameters.Add(FNE_image_file.DialogFiles[Count]);
              end;

              Options:=[poUsePipes];

       {$ENDIF}
     end;//process1
     //******debug only************
     //uncomment if you want show command line in process1
     //Showmessage(Process1.executable +' '+Process1.Parameters.Text);

     { TODO -ogg -cdev : save fls command line for Report }
     Report_tsk_command_line:=Process1.executable +' '+Process1.Parameters.Text;

     Process1.Active:=True;
     Process1.Execute;
     { TODO DONE -ogg -cdev : Capture stream errors from fls }
     OutputStream := TMemoryStream.Create;     { TODO DONE -ogg -cdev : //check TfileStream }
     repeat
      Application.ProcessMessages;
      Form1.UpdateShowing;

      // Get the new data from the process to a maximum of the buffer size that was allocated.
      // Note that all read(...) calls will block except for the last one, which returns 0 (zero).
      BytesRead := Process1.Output.Read(Buffer, BUF_SIZE);
      Application.ProcessMessages;
      Form1.UpdateShowing;
      // Add the bytes that were read to the stream for later usage
      OutputStream.Write(Buffer, BytesRead);

      //try to abort
      Application.ProcessMessages;
      Form1.UpdateShowing;
     until BytesRead = 0;  // Stop if no more data is available
      // Now that all data has been read, it can be used to save it to a file on disk
      //with TFileStream.Create(wpath+'/body.txt', fmCreate) do (may be wpath need root permission or is unwritable)
      with TFileStream.Create(outpath+'/body.txt', fmCreate) do

      begin
       OutputStream.Position := 0; // Required to make sure all data is copied from the start
       CopyFrom(OutputStream, OutputStream.Size);
       Free;
      end;
      //Process1.errors
      TS_error.Clear;
      TS_error.LoadFromStream(Process1.Stderr);
      //check if errors
      if TS_Error.Text<>'' then
      begin
       Form1.SetCursor(crDefault);
       PB_working_wait.Visible:=False;
       L_working_wait.Visible:=False;
       E_csv_done.Text:='';
       B_killgen.Visible:=False;
       B_killgen.enabled:=False;
       Showmessage(TS_Error.Text);
       TS_error.Free;
       OutputStream.Free;
       Process1.Active:=False;
       Form1.UpdateShowing;
       exit();
      end;//if errors
      TS_error.Free;
      OutputStream.Free;
      Process1.Active:=False;
      Form1.SetCursor(crDefault);
      //************************************************
      //creating timeline.csv from body.txt with mactime
      //************************************************
      L_working_wait.Caption:='Running MACTIME. Please Wait...';
      Form1.Refresh;
      Form1.UpdateShowing;
      TS_Error:=Tstringlist.Create;
      with Process1 do
      begin
       Parameters.Clear;
       {$IFDEF WIN32}
               //Executable := 'boh';
       {$ENDIF}
       {$IFDEF LINUX}
               Executable := ('mactime');
               { Todo DONE -ogg -cdev : path||name not hardcoded }
               Parameters.Add('-b');              //body
               Parameters.Add(outpath+'/body.txt'); //body name
               // -d always for mactime ? no, you can choose
               //Parameters.Add('-d');
               if (SetupList.Values['mct_d']<>'') then Parameters.Add('-d');  //TL comma delimited format
               if (SetupList.Values['mct_h']<>'') then Parameters.Add('-h');  //display header info
               if (SetupList.Values['mct_m']<>'') then Parameters.Add('-m');  //month as a number
               if (SetupList.Values['mct_y']<>'') then Parameters.Add('-y');  //date ISO8601 format
               if (SetupList.Values['mct_g']<>'') then
               begin;
                Parameters.Add('-g');                          //group
                Parameters.Add(SetupList.Values['mct_g']);     //group file
               end;
               if SetupList.Values['mct_p']<>'' then
               begin;
                Parameters.Add('-p');                          //password
                Parameters.Add(SetupList.Values['mct_p']);     //password file
               end;
               if SetupList.Values['mct_i']<>'' then
               begin;
                Parameters.Add('-i');                          //-i index
                Parameters.Add(SetupList.Values['mct_i']);     //index file
               end;
               if SetupList.Values['mct_z']<>'' then
               begin;
                Parameters.Add('-z');                          //zone
                Parameters.Add(SetupList.Values['mct_z']);     //zone   'ZULU'
               end;
               {DATE_RANGE
               The range of dates to make the time line for.  The standard format is yyyy-mm-dd for a starting date and no ending
               date.  For  an ending date, use yyyy-mm-dd..yyyy-mm-dd.  Date can contain time, use format yyyy-mm-ddThh:mm:ss for
               starting and/or ending date.}
               if dateinterval<>'' then Parameters.Add(dateinterval);
               //process1 use pipe
               Options:=[poUsePipes];
       {$ENDIF}

      end;
      Process1.Active:=True;
      Process1.Execute;
      OutputStream:=TmemoryStream.Create;
      repeat
       Application.ProcessMessages;
       Form1.UpdateShowing;
       // Get the new data from the process to a maximum of the buffer size that was allocated.
       // Note that all read(...) calls will block except for the last one, which returns 0 (zero).
       BytesRead := Process1.Output.Read(Buffer, BUF_SIZE);
       // Add the bytes that were read to the stream for later usage
       OutputStream.Write(Buffer, BytesRead);

      until BytesRead = 0;  // Stop if no more data is available

      //compose file name to save in csv format
      //format "TimeLine_"(casename)_(date interval)SKEW(skew)".csv"
      //composefilename(csv_filename);
      // Now that all data has been read it can be used to save it to a file on disk

      //************ CREATE CSV ***************
      with TFileStream.Create(outpath+'/'+CSV_full_name+'.csv', fmCreate) do
      begin
       OutputStream.Position := 0; // Required to make sure all data is copied from the start
       CopyFrom(OutputStream, OutputStream.Size);
       Free
      end;
      //*********************************************************Process1.errors
      TS_error.LoadFromStream(Process1.Stderr);
      //check if any errors in stderr
      if TS_Error.Text<>'' then
      begin
       Form1.SetCursor(crDefault);
       B_killgen.Visible:=False;
       B_killgen.enabled:=False;
       PB_working_wait.Visible:=False;
       L_working_wait.Visible:=False;
       Showmessage(TS_Error.Text);
       TS_error.Free;
       OutputStream.Free;
       Process1.Active:=False;
       E_csv_done.Text:='';
       Form1.UpdateShowing;
       exit();
      end; //errors
      { TODO -ogg -cdev : save fls command line for Report }
      Report_mactime_command_line:=Process1.executable +' '+Process1.Parameters.Text;

      //Report_tsk_command_line:=Process1.executable +' '+Process1.Parameters.Text;

      Process1.Active:=False;
      //timeline done
      { TODO DONE 9 -ogg -cdev : file name under construction }
      //E_csv_done.Text:=dn+dcsv+'_C'+ctime+zl+skew+'.csv';
      E_csv_done.Text:=CSV_full_name+'.csv';
      //****************************************************making csv read only
      //SetFAttr in FPunix is implemented but does nothing
      //uses Unix*
      { TODO -ogg -cdev : for windows cross compiler directive }
      FPChmod(outpath+'/'+E_csv_done.Text,&444);

      //calculate row number in timeline for report purpose only
      CSVsl:=TstringList.Create;
      CSVsl.LoadFromFile(outpath+'/'+E_csv_done.Text);
      CSV_row_count:=CSVsl.Count;
      CSVsl.Free;
      Application.ProcessMessages;
      Form1.UpdateShowing;
      Form1.Refresh;
      //**********************************************calculate mactime MD5 hash

      { TODO done -ogg -cdev : calculate SHA1 hash }
      if (SetupList.Values['timeline_hash']='y') then
      begin
       { TODO -ogg -cdev : show somewhere hashing process in progress }
        L_working_wait.Caption:='Calculating Timeline MD5 Hash...';
        Report_md5_timeline    :=MD5Print(MD5File(outpath+'/'+E_csv_done.Text));
        PB_working_wait.Visible:=False;
        L_working_wait.Visible :=False;
        B_killgen.Visible      :=False;
        B_killgen.enabled      :=False;
        L_csv_done.Caption     :='FILE CSV CREATED. md5: '+Report_md5_timeline;
        //Showmessage('Timeline MD5 '+outpath+'/'+E_csv_done.Text+': '+Report_md5_timeline);
      end;
      Form1.UpdateShowing;
      Form1.Refresh;
      //*********************************************calculate mactime SHA1hash
      if (SetupList.Values['timeline_hash']='y') then
      begin
       { TODO -ogg -cdev : show somewhere hashing process in progress }
        L_working_wait.Caption:='Calculating Timeline SHA1 Hash...';
        Report_sha1_timeline   :=SHA1Print(SHA1File(outpath+'/'+E_csv_done.Text));
        PB_working_wait.Visible:=False;
        L_working_wait.Visible :=False;
        B_killgen.Visible      :=False;
        B_killgen.enabled      :=False;
        L_csv_done.Caption     :='FILE CSV CREATED. sha1: '+Report_sha1_timeline;
        //Showmessage('Timeline SHA1 '+outpath+'/'+E_csv_done.Text+': '+Report_sha1_timeline);
        Form1.UpdateShowing;
        Form1.Refresh;
      end;
      PB_working_wait.Visible:=False;
      L_working_wait.Visible:=False;
      B_killgen.Visible:=False;
      B_killgen.enabled:=False;
      //****************************************************report gen & preview
      //call another unit
      { TODO : unit with generation of report and preview for prtinting }
      if pdf_genrep='y' then
      begin
        BT_pdfreport.enabled:=True;
        BT_pdfreportClick(Self);
      end
      else
      begin
        BT_pdfreport.Enabled:=False;
      end;
      TS_Error.Free;
      Form1.UpdateShowing;

 end;

procedure TForm1.BT_setupClick(Sender: TObject);
begin
  FormSetup.Showmodal;
end;

procedure TForm1.BOpen_csvClick(Sender: TObject);
begin
  { TODO DONE -ogg : Tprocess instead - ShellExecute WinAPI MS Windows only.
  Can start programs with elevation/admin permissions. (NOCROSSPLATFORM) }
  //deprecated
  //ShellExecute(handle,'open',PChar(dn+'/timeline'+dcsv+'_C'+ctime+zl+skew+'.csv'),'','',1)
  with Process1 do
  begin
       {$IFDEF WIN32}
               //Executable := 'boh';
       {$ENDIF}
       {$IFDEF LINUX}
               Parameters.Clear;
               //check editor name in settings
               if SetupList.Values['defeditor']<>'' then
               begin
                  //editor name defined in setup ($HOME/.config/nbtempox/nbtxsetup.txt)
                  { TODO DONE -ogg -cdev : rename nbtempo.txt in nbtempox.cfg }
                  Executable:=FindDefaultExecutablePath(SetupList.Values['defeditor']);
               end
               else
               begin
                  Showmessage('Undefined editor in setup file. ');
                  Form1.SetCursor(crDefault);
                  exit();
               end;
               if E_csv_done.Text<>'' then
               begin
                  Parameters.Add(DE_out_dir.Directory+'/'+E_csv_done.Text);
               end
               else
               begin
                  Showmessage('No Time Line! ');
                  Form1.SetCursor(crDefault);
                  exit();
               end;
               Options:=[];
       {$ENDIF}
  end;
  Form1.SetCursor(crHourglass);
  try
    Process1.Active:=True;
    Process1.Execute;
  except
    on E: Exception do
    begin
      Form1.SetCursor(crDefault);
      ShowMessage('An exception was raised: ' + E.Message);
    end;
  end;
  Form1.SetCursor(crDefault);
end;

procedure TForm1.BT_openpdfClick(Sender: TObject);
begin

  { TODO -ogg -cdev : opendialog file to view }
  with Process1 do
  begin
       {$IFDEF WIN32}
               //Executable := 'boh';
       {$ENDIF}
       {$IFDEF LINUX}
               Parameters.Clear;
               //check pdf reader name in settings
               if SetupList.Values['pdfreader']<>'' then
               begin
                  //pdf reader name defined in setup ($HOME/.config/nbtempox/nbtxsetup.txt)
                  { TODO DONE -ogg -cdev : rename nbtempo.txt in nbtempox.cfg }
                  Executable:=FindDefaultExecutablePath(SetupList.Values['pdfreader']);
               end
               else
               begin
                  Showmessage('Undefined PDF reader in setup file. ');
                  Parameters.Clear;
                  exit();
               end;
               if E_csv_done.Text<>'' then
               begin
                  Parameters.Add(DE_out_dir.Directory+'/REPORT_'+CSV_full_name+'.pdf');
               end
               else
               begin
                  Showmessage('No PDF REPORT! ');
                  Parameters.Clear;
                  exit();
               end;
               Options:=[];
       {$ENDIF}
  end;
  try
    Process1.Active:=True;
    Process1.Execute;
  except
    on E: Exception do
    begin
      Form1.SetCursor(crDefault);
      ShowMessage('An exception was raised: ' + E.Message);
      Process1.Parameters.Clear;
    end;
  end;
end;

procedure TForm1.BOpen_csvMouseEnter(Sender: TObject);
begin
  if SetupList.Values['defeditor']<>'' then
  begin
   BOpen_csv.Hint:='Open with '+SetupList.Values['defeditor'];
  end
  else
  begin
   BOpen_csv.Hint:='Warning! editor not defined!!';
  end;
end;

procedure TForm1.BT_openpdfMouseEnter(Sender: TObject);
begin
   if SetupList.Values['pdfreader']<>'' then
  begin
   //BT_openpdf.Hint:='Open with '+SetupList.Values['pdfreader'];
  end
  else
  begin
   //BT_openpdf.Hint:='Warning! PDF reader not defined!!';
  end;
end;

procedure TForm1.BT_pdfreportClick(Sender: TObject);
begin
  nbprintreport.printreport(1);
end;

procedure TForm1.BtExitClick(Sender: TObject);
begin
  Application.terminate;
  //close();
end;

procedure TForm1.B_AboutClick(Sender: TObject);
begin
   AboutForm.Showmodal;
end;

procedure TForm1.B_all_datesClick(Sender: TObject);
begin
  DTP_from.Date:=-1.7E308;
  DTP_to.Date  :=1.7E308;
end;

procedure TForm1.B_killgenClick(Sender: TObject);
begin
  //process1 terminate
  if Process1.Running=True then
  begin
   { TODO -ogg -ctest : abort does not }
     Form1.SetCursor(crDefault);
     Process1.Terminate(0);
     Process1.Active:=False;
     Process1.Parameters.clear;
     PB_working_wait.Visible:=False;
     L_working_wait.Visible:=False;
     E_csv_done.Text:='';
     B_killgen.enabled:=False;
  end;
  //auto disable button
end;

procedure TForm1.B_vidClick(Sender: TObject);
begin
  FormViewImageData.Showmodal;
end;

procedure TForm1.DE_out_dirChange(Sender: TObject);
begin
  dn:=DE_out_dir.Directory;
  //where to save CSV file
  outpath:=DE_out_dir.Directory;
end;

procedure TForm1.ECase_nameChange(Sender: TObject);
begin
        //invalid char in file names
        ECase_name.Text := StringReplace(ECase_name.Text,'/','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'*','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'?','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'<','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'>','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'|','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,':','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'"','',[rfReplaceAll]);
        //extra
        ECase_name.Text := StringReplace(ECase_name.Text,'\','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'%','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'=','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'^','',[rfReplaceAll]);
        ECase_name.Text := StringReplace(ECase_name.Text,'~','',[rfReplaceAll]);

end;

procedure TForm1.ECase_nameClick(Sender: TObject);
begin
  ECase_name.SelectAll;
end;

procedure TForm1.Einv_nameChange(Sender: TObject);
begin
        //invalid char in file names
        Einv_name.Text := StringReplace(Einv_name.Text,'/','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'*','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'?','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'<','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'>','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'|','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,':','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'"','',[rfReplaceAll]);
        //extra
        Einv_name.Text := StringReplace(Einv_name.Text,'\','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'%','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'=','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'^','',[rfReplaceAll]);
        Einv_name.Text := StringReplace(Einv_name.Text,'~','',[rfReplaceAll]);
end;

procedure TForm1.Einv_nameEnter(Sender: TObject);
begin
  Einv_name.SelectAll;
end;

procedure TForm1.E_csv_doneClick(Sender: TObject);
begin
  E_csv_done.SelectAll;
end;

procedure TForm1.FNE_image_fileAcceptFileName(Sender: TObject; var Value: String
  );
begin
    if not B_vid.Enabled then B_Vid.enabled:=True;
end;

procedure TForm1.FNE_image_fileChange(Sender: TObject);

begin

   E_csv_done.Text:='';
   //fn golbal var - raw or ewf file name
   fn:=ExtractFileName(FNE_image_file.Filename);
   wpath:=ExtractFilePath(FNE_image_file.Filename);
   //don't update out_dir if image file is a /dev/sd*
   if not  AnsiContainsStr(wpath, 'dev') then
      DE_out_dir.Directory:=wpath;
   { TODO -ogg -cdev : check if imagefile is in read only mode (warning) }

end;

procedure TForm1.FormActivate(Sender: TObject);
begin
  //first time execution - no config starts setup procedure
  if Setuplist.Text='' then
  begin
   //empty setup - calling btsetup
   FormSetup.BTmct_defaultClick(Sender);
   FormSetup.BTtsk_defaultClick(Sender);
   BT_setupClick(Sender);
  end;

end;

procedure TForm1.FormCreate(Sender: TObject);
begin
  //check all dates (default)
  RG_Dates.Itemindex:=0;
  //??
  DTP_from.Date:=-1.7E308;
  DTP_to.Date:=1.7E308;
  DTP_from.enabled:=False;
  DTP_to.Enabled:=False;
  //read working directory
  wpath:=GetUserDir;
  outpath:=wpath;
  //set file name edit initialdir
  FNE_image_file.Initialdir:=wpath;
  FNE_image_file.Text:=wpath;
  //set project directory
  DE_out_dir.Directory:=outpath ;
  //the .config directory is specific for the user who is executing the program
  configpath:=GetAppConfigDir(False); //based on executable name
  if not FileExists(configpath+'nbtxsetup.txt') then
  begin
      Createdir(configpath);
      ExecuteProcess('/usr/bin/touch',[configpath+'nbtxsetup.txt']);
  end;
  //read setup files and inizialize global vars from {HOME}/.config file
  Setuplist:=Tstringlist.Create;
  SetupList.LoadFromFile(configpath+'nbtxsetup.txt');
  //settings on main page
  //try Parameters.ParamByName('').Value := physname[];
  if SetupList.Values['mct_z']<>'' then
                Label3.Caption:='mactime Zone: '+SetupList.Values['mct_z']     //zone   'ZULU'
  else Label3.Caption:='mactime Zone: undefined';

  //if SetupList.Values['tsk_z']<>'' then
  //     Label4.Caption:='tsk_gettimes Zone: '+SetupList.Values['tsk_z']     //zone   'ZULU'
  //else Label4.Caption:='tsk_gettimes Zone: undefined';

  if SetupList.Values['mct_d']<>'' then
       Label4.Caption:='Time Line comma delimited ('+SetupList.Values['mct_d']+')'     //-d comma delimited (default)
  else Label4.Caption:='No Time Line comma delimited';

  if SetupList.Values['tsk_s']<>'' then
       Label5.Caption:='Time skew: '+SetupList.Values['tsk_s']     //timeskew
  else Label5.Caption:='No Time skew defined' ;

  if SetupList.Values['defInv']<>'' then
       Einv_name.Text:=SetupList.Values['defInv']     //examiner name
  else EInv_name.Caption:='not_set';

  if SetupList.Values['defCase']<>'' then
       Ecase_name.Text:=SetupList.Values['defCase'];    //Case name

  if SetupList.Values['pdf_genrep']<>'' then
     pdf_genrep:=SetupList.Values['pdf_genrep']
  else pdf_genrep:='y';

end;

procedure TForm1.M_doClick(Sender: TObject);
begin

end;

procedure TForm1.RG_DatesClick(Sender: TObject);
begin
  //discrimino cosa è stato cliccato
  if Rg_Dates.itemindex=0 then
  begin
     //all dates
     //max value for double data type
     DTP_from.Date:=-1.7E308;
     DTP_to.Date:=1.7E308;
     DTP_from.enabled:=False;
     DTP_to.Enabled:=False;
  end
  else
  begin
     //set dtp_to at now
     DTP_from.Date:=-1.7E308 ;
     DTP_to.Date:=Now;
     DTP_from.enabled:=True;
     DTP_to.Enabled:=True;
  end;
end;

end.

