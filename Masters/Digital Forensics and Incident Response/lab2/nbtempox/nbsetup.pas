unit nbsetup;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, Forms, Controls, Graphics, Dialogs, ExtCtrls,
  ComCtrls, StdCtrls, Buttons, EditBtn;

type

  { TFormSetup }

  TFormSetup = class(TForm)
    BTtsk_default: TBitBtn;
    Bok: TBitBtn;
    Bcancel: TBitBtn;
    BTmct_default: TBitBtn;
    CB_pdf_genrep: TCheckBox;
    CB_timeline_hash: TCheckBox;
    CGmactime: TCheckGroup;
    CB_i: TCheckBox;
    CB_s: TCheckBox;
    CB_b: TCheckBox;
    CBmct_b: TCheckBox;
    CBmct_g: TCheckBox;
    CBmct_p: TCheckBox;
    CBmct_i: TCheckBox;
    CBmct_z: TCheckBox;
    CB_casename_csv: TCheckBox;
    CB_examinername_csv: TCheckBox;
    CB_extended_prefix_csv: TCheckBox;
    CB_imagename_csv: TCheckBox;
    CoB_i: TComboBox;
    CBmct_zones: TComboBox;
    CB_editor: TComboBox;
    DE_i: TDirectoryEdit;
    Label3: TLabel;
    Label4: TLabel;
    Label7: TLabel;
    ME_b: TEdit;
    ME_s: TEdit;
    Emct_body: TEdit;
    EInv_name: TEdit;
    ECase_name: TEdit;
    FE_g: TFileNameEdit;
    FE_p: TFileNameEdit;
    Label1: TLabel;
    Label2: TLabel;
    L_check: TLabel;
    PG_setup: TPageControl;
    Panel_setup: TPanel;
    StaticText1: TStaticText;
    TS_General: TTabSheet;
    TS_fls: TTabSheet;
    TS_mactime: TTabSheet;
    procedure BokClick(Sender: TObject);
    procedure BTtsk_defaultClick(Sender: TObject);
    procedure BTmct_defaultClick(Sender: TObject);
    procedure CB_editorExit(Sender: TObject);
    procedure CoB_iChange(Sender: TObject);
    procedure ECase_nameClick(Sender: TObject);
    procedure EInv_nameClick(Sender: TObject);
    procedure FormActivate(Sender: TObject);

  private
    { private declarations }
  public
    { public declarations }
  end;

var
  FormSetup: TFormSetup;

implementation

{$R *.lfm}

{ TFormSetup }
uses nbtempo1;


procedure TFormSetup.BokClick(Sender: TObject);
begin
  //TODO items always checked (mandatory)
  //fls params
  if CB_i.Checked      then tsk_i :='tsk_i='+CoB_i.Caption  else tsk_i :='';
  if CB_s.checked      then tsk_s :='tsk_s='+ME_s.Caption   else tsk_s :='';
  if CB_b.checked      then tsk_b :='tsk_b='+ME_b.Caption   else tsk_b :='';
  //mactime params
  if CGmactime.Checked[0]  then mct_d :='mct_d=-d' else mct_d:='';
  if CGmactime.Checked[1]  then mct_h :='mct_h=-h' else mct_h:='';
  if CGmactime.Checked[2]  then mct_m :='mct_m=-m' else mct_m:='';
  if CGmactime.Checked[3]  then mct_y :='mct_y=-y' else mct_y:='';
  if CBmct_b.checked       then mct_b :='mct_b=body.txt' else mct_b:='';
  if CBmct_g.checked       then mct_g :='mct_g='+FE_g.Filename else mct_g:='';
  if CBmct_p.checked       then mct_p :='mct_p='+FE_p.Filename else mct_p:='';
  if CBmct_i.checked       then mct_i :='mct_i='+DE_i.directory else mct_i:='';
  if CBmct_z.checked       then mct_z :='mct_z='+CBmct_zones.Caption else mct_z:='';
  //save to Tstringlist prev.cleared
  with SetupList do
  begin
   Clear;
   //header of config file
   Add('#NBTempoX Config and default parameters file');
   Add('#Last Update '+DateTimeToStr(now));
   Add('#-------------------------------------');
   Add('#---General params ---');
   if FormSetup.Einv_name.Caption  <> ''  then Add('defInv='+FormSetup.EInv_name.Caption);
   if FormSetup.ECase_name.Caption <> ''  then Add('defCase='+FormSetup.ECase_name.Caption);
   //also update main form1 fields
   Form1.EInv_name.Caption:=FormSetup.EInv_name.Caption;
   Form1.ECase_name.Caption:=FormSetup.ECase_name.Caption;
   Form1.Update;
   if CB_pdf_genrep.checked     then Add('pdf_genrep=y')                else Add('pdf_genrep=n');
   if CB_timeline_hash.checked  then Add('timeline_hash=y')             else Add('timeline_hash=n');
   if CB_editor.Text <> ''      then Add('defeditor='+CB_editor.Text);
   //if CB_pdfreader.Text <> ''   then Add('pdfreader='+CB_pdfreader.Text);

   if CB_extended_prefix_csv.checked then Add('usetlprefix=timeline')   else Add('usetlprefix=TL');
   if CB_examinername_csv.checked    then Add('use_examiner_name=y')    else Add('use_examiner_name=n');
   if CB_casename_csv.checked        then Add('use_case_name=y')        else Add('use_case_name=n');
   if CB_imagename_csv.checked       then Add('use_image_name=y')       else Add('use_image_name=n');
   //fls parameters
   Add('#---tsk_gettimes params ---');
   if tsk_a <> '' then Add(tsk_a);
   if tsk_d <> '' then Add(tsk_d);
   if tsk_DD <> '' then Add(tsk_DD);
   if tsk_FF <> '' then Add(tsk_FF);
   if tsk_l <> '' then Add(tsk_l);
   if tsk_p <> '' then Add(tsk_p);
   if tsk_r <> '' then Add(tsk_r);
   if tsk_i <> '' then Add(tsk_i);
   if tsk_m <> '' then Add(tsk_m);
   if tsk_s <> '' then Add(tsk_s);
   if tsk_f <> '' then Add(tsk_f);
   if tsk_o <> '' then Add(tsk_o);
   if tsk_b <> '' then Add(tsk_b);
   if tsk_z <> '' then Add(tsk_z);
   //mactime parameters
   Add('#---mactime params ---');
   if mct_d <> '' then Add(mct_d);
   if mct_h <> '' then Add(mct_h);
   if mct_m <> '' then Add(mct_m);
   if mct_y <> '' then Add(mct_y);
   if mct_b <> '' then Add(mct_b);
   if mct_g <> '' then Add(mct_g);
   if mct_p <> '' then Add(mct_p);
   if mct_i <> '' then Add(mct_i);
   if mct_z <> '' then Add(mct_z);
   Add('#---end conf.file----------------');
  end;

  SetupList.SaveToFile(configpath+'nbtxsetup.txt');
  //update general setup label on main window
  //settings on main page
  if SetupList.Values['mct_z']<>'' then
                Form1.Label3.Caption:='mactime Zone: '+SetupList.Values['mct_z']     //zone   'ZULU'
  else          Form1.Label3.Caption:='mactime Zone: undefined';

  //if SetupList.Values['tsk_z']<>'' then
  //              Form1.Label4.Caption:='tsk_gettimes Zone: '+SetupList.Values['tsk_z']     //zone   'ZULU'
  //else          Form1.Label4.Caption:='tsk_gettimes Zone: undefined';

  if SetupList.Values['mct_d']<>'' then
                Form1.Label4.Caption:='Time Line: comma delimited (CSV)'     //zone   'ZULU'
  else          Form1.Label4.Caption:='No Time Line comma delimited (no CSV)';

  if SetupList.Values['tsk_s']<>'' then
                Form1.Label5.Caption:='Time skew: '+SetupList.Values['tsk_s']     //zone   'ZULU'
  else          Form1.Label5.Caption:='No Time skew defined' ;



end;

procedure TFormSetup.BTtsk_defaultClick(Sender: TObject);
begin
  //set tsk_gettimes parameters to default value
  CB_i.Checked:=False;  //  imgtype
  CoB_i.Text:='';       //default imgtype
  //CB_m.Checked:=False;   //Display  files  in time machine format so that a timeline can be     created with mactime(1)
  //CoB_m.Text:='/';
  CB_s.Checked:=True;   //time skew
  ME_s.Text:='0';       //time skew
  CB_b.Checked:=False;  //dev sector size
  ME_b.Text:='';        //dev sector size
  //mactime (antother button??)


end;

procedure TFormSetup.BTmct_defaultClick(Sender: TObject);
begin
  CGmactime.Checked[0]:=True;  //-d display matime in comma delimited
  CGmactime.Checked[1]:=False; //-h display header info
  CGmactime.Checked[2]:=False; //-m the mont is given as a number
  CGmactime.Checked[3]:=False; //-y date in iso8601 format
  CBmct_b.Checked:=True;       //-b body
  Emct_body.Caption:='body.txt';
  CBmct_g.checked:=False;      //-g group file
  FE_g.FileName:='';
  CBmct_p.Checked:=False;      //-p password file
  FE_p.Filename:='';
  CBmct_i.Checked:=False;      //-i  day hour index file
  DE_i.Directory:='';
  CBmct_z.Checked:=True;       //zone
end;

procedure TFormSetup.CB_editorExit(Sender: TObject);
begin
  //check if program selected is installed
  if (FileExists('/usr/bin/'+CB_editor.Text)=False) then
    Showmessage('The selected program does not seems installed');

end;

procedure TFormSetup.CoB_iChange(Sender: TObject);
begin

end;

procedure TFormSetup.ECase_nameClick(Sender: TObject);
begin
  ECase_name.SelectAll;
end;

procedure TFormSetup.EInv_nameClick(Sender: TObject);
begin
  EInv_name.SelectAll;
end;

procedure TFormSetup.FormActivate(Sender: TObject);
begin
    //if FileExists('/usr/bin/fls') then
    if FileExists(FindDefaultExecutablePath('tsk_gettimes')) then
            L_check.Caption:='tsk_gettimes installed.'
       else
            L_check.Caption:='tsk_gettimes not installed!';

    if FileExists('/usr/bin/mactime') then
            L_check.Caption:=L_check.Caption+'mactime installed.'
       else
            L_check.Caption:=L_check.Caption+'mactime not installed!'  ;
    if FileExists('/usr/bin/ps2pdf') then
            L_check.Caption:=L_check.Caption+'ps2pdf installed.'
       else
            L_check.Caption:=L_check.Caption+'ps2pdf not installed!'  ;

  //load from setup file
  //Setuplist.Clear; pulisco solo se devo salvare
  Setuplist.LoadFromFile(configpath+'nbtxsetup.txt');
  //general setup read
  if SetupList.Values['defInv']<>'' then
  begin
   FormSetup.EInv_name.Text:=SetupList.Values['defInv'];
   Form1.EInv_name.Text:=SetupList.Values['defInv'];
  end;
  if SetupList.Values['defCase']<>'' then
  begin
   FormSetup.ECase_name.Text:=SetupList.Values['defCase'];
   Form1.ECase_name.Text:=SetupList.Values['defCase'];
  end;
  if SetupList.Values['genrep']<>'' then
  begin
   CB_pdf_genrep.checked:=True;
  end;
  if SetupList.Values['tlhash']<>'' then
  begin
   CB_timeline_hash.checked:=True;
  end;
  if SetupList.Values['defeditor']<>'' then
  begin
   CB_editor.Text:=SetupList.Values['defeditor'];
  end;
  if SetupList.Values['pdfreader']<>'' then
  begin
   //CB_pdfreader.Text:=SetupList.Values['pdfreader'];
  end;

  //set check boxes (see set parameters at process1)
  if (SetupList.Values['tsk_i'] <>'')  then
  begin
   CB_i.Checked:=True;      //TYPE OF IMAGE FILE
   CoB_i.Text:=SetupList.Values['tsk_i'];
  end;

  if SetupList.Values['tsk_s']<>'' then
  begin;
   CB_s.Checked:=True;                        //time skew
   ME_s.Text:=SetupList.Values['tsk_s'];      //time skew
  end;
  if SetupList.Values['tsk_b']<>'' then
  begin;
   CB_b.Checked:=True;                        //dev sector size
   ME_b.Text:=SetupList.Values['tsk_b'];      //dev sector size
  end;
  //mactime
  if (SetupList.Values['mct_d']='-d') then
  begin
    CGmactime.Checked[0]:=True;
  end;
  if (SetupList.Values['mct_h']='-h') then
  begin
    CGmactime.Checked[1]:=True;
  end;
  if (SetupList.Values['mct_m']='-m') then
  begin
    CGmactime.Checked[2]:=True;
  end;
  if (SetupList.Values['mct_y']='-y') then
  begin
    CGmactime.Checked[3]:=True;
  end;
  //body.txt is harcoded
  {
   if SetupList.Values['mct_b']<>'' then
   begin;
    CBmct_b.Checked:=True;                    //body
    Emct.Text:=SetupList.Values['mct_b'];     //body
   end;
  }
  if (SetupList.Values['mct_g']<>'') then
  begin;
   CBmct_g.Checked:=True;                     //group file
   FE_g.FileName:=SetupList.Values['mct_g'];  //group file
  end;

  if (SetupList.Values['mct_p']<>'') then
  begin;
   CBmct_p.Checked:=True;                     //password file
   FE_p.FileName:=SetupList.Values['mct_p'];  //password file
  end;
  if (SetupList.Values['mct_i']<>'') then
  begin;
   CBmct_i.Checked:=True;                     //index out dir
   DE_i.Directory:=SetupList.Values['mct_i']; //index out dir
  end;
  if (SetupList.Values['mct_z']<>'') then
  begin;
   CBmct_z.Checked:=True;                              //zone
   CBmct_zones.Text:=SetupList.Values['mct_z'];        //zone
  end;

  if (SetupList.Values['pdf_genrep']='y')         then CB_pdf_genrep.Checked:=True else CB_pdf_genrep.Checked:=False;
  if (SetupList.Values['timeline_hash']='y')      then CB_timeline_hash.Checked:=True else CB_timeline_hash.Checked:=False;
  if (SetupList.Values['usetlprefix']='timeline') then CB_extended_prefix_csv.Checked:=True else CB_extended_prefix_csv.Checked:=False;
  if (SetupList.Values['use_examiner_name']='y')  then CB_examinername_csv.Checked:=True else CB_examinername_csv.Checked:=False;
  if (SetupList.Values['use_case_name']='y')      then CB_casename_csv.checked:=True else CB_casename_csv.checked:=False;
  if (SetupList.Values['use_image_name']='y')     then CB_imagename_csv.checked:=True else CB_imagename_csv.checked:=False;

end;

end.

