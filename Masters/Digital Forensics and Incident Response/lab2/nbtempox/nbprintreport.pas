unit nbprintreport;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils,PostScriptCanvas, printers,dialogs,graphics,unix,Fileutil;
  function PrintReport(return: Integer):Integer;

  var PsCanvas: TPostscriptCanvas;

implementation
uses nbtempo1;

function Sx(AX: Integer): Integer;
begin
  // all values were based on 72 dpi
  result := round(PsCanvas.XDPI/72*Ax);
end;

function Sy(AY: Integer): Integer;
begin
  // all values were based on 72 dpi
  result := round(PsCanvas.YDPI/72*Ay);
end;

function PointS(Ax,Ay:Integer): TPoint;
begin
  Result.X:= Sx(Ax);
  Result.Y:= Sy(Ay);
end;

function Pt2Pix(Pt: Integer): Integer;
begin
  result := round(PSCanvas.YDPI/72*Pt);
end;

function FileSizeStr ( filename: string ): string;
const
   K = Int64(1000);     // Comment out this line OR
//  K = Int64(1024);     // Comment out this line
  M = K * K;
  G = K * M;
  T = K * G;
var
  size: Int64;
  handle: integer;
begin
  handle := FileOpen(filename, fmOpenRead);
  if handle = -1 then
    result := 'Unable to open file ' + filename
  else try
    size := FileSeek ( handle, Int64(0), 2 );
    if size < K then result := Format ( '%d bytes', [size] )
    else if size < M then result := Format ( '%f KB', [size / K] )
    else if size < G then result := Format ( '%f MB', [size / M] )
    else if size < T then result := Format ( '%f GB', [size / G] )
    else                  result := Format ( '%f TB', [size / T] );
  finally
    FileClose ( handle );
  end;
end;

function printreport(return:integer):integer;
var
  report_full_name:string;
begin
     //create file postscript
     report_full_name:='REPORT_'+CSV_full_name;
     PsCanvas := TPostscriptCanvas.Create;
     //  psCanvas.XDPI := StrToIntDef(txtResX.Text,300);
     //  psCanvas.YDPI := StrToIntDef(txtResY.Text,300);
     //txt resolution 72 dpi
     psCanvas.XDPI := StrToIntDef('72',300);
     psCanvas.YDPI := StrToIntDef('72',300);
     With PsCanvas do
     begin
       try
         PaperHeight:=Sy(842);   //heigth
         PaperWidth:=Sx(595);    //width
         TopMargin:=Sy(0);
         LeftMargin:=Sx(0);
         BeginDoc;
         Font.Size:=10;
         //Font.Name:='padmaa';
         Font.Name:='nimbus sans l';
         //Font.Name:='OCR A Extended';

         //fixed header
         Font.Style:=[fsBold];
         //textout(<from top>,<from left>,<text>)
         Brush.color:=ClBlack;
         Pen.Color:=ClBlack;
         Rectangle(40,10,560,40);
         Font.color:=clwhite;
         TextOut(200,20,'nbtempox - CSV Timeline generation report');
         Font.Style:=[];

         {//examiner's lab logo ?
           xpm:=TPixMap.Create;
           try
             xpm.LoadFromFile(ExpandFileName(<path>+'<nomefile>.xpm'));
             StretchDraw(Rect(240,20,340,120),xpm);
           finally
             xpm.Free;
           end;
         }

         //report file name
         Font.color:=Clnavy;
         TextOut( 50,50,report_full_name+'.pdf');
         Font.Color:=ClBlack;
         TextOut( 50,80, 'Generation date:');
         TextOut(150,80, Report_create_time);
         //TextOut( 50,80, 'Timeline Name:');
         //TextOut(150,80, CSV_full_name+'.csv');
         TextOut( 50,95, 'Examiner name:');
         TextOut(150,95,Form1.Einv_name.Text);
         TextOut( 50,110,'Case name:');
         TextOut(150,110,Form1.ECase_name.Text);

         TextOut( 50,125,'-----------------------------IMAGE FILE-------------');
         TextOut( 50,140,'Image file :');
         TextOut(150,140,Form1.FNE_image_file.FileName);
         TextOut( 50,155,'Size:');
         TextOut(150,155,IntToStr(FileSize(Form1.FNE_image_file.FileName))+' bytes ('+Filesizestr(Form1.FNE_image_file.FileName)+')');

         TextOut( 50,170,'Info:');
         //TextOut(150,170,CSV_fstype+' '+CSV_secsize+' '+CSV_offset);
         //The hashs that follow are related to the image file
         TextOut(150,170,'The hashs that follow are related to the image file');
         TextOut( 50,185,'Image file SHA1:');
         TextOut(150,185,Report_sha1_image);
         TextOut( 50,200,'Image file MD5:');
         TextOut(150,200,Report_md5_image);


         TextOut( 50,215,'-----------------------------PARAMETERS-------------');
         TextOut( 50,230,'Dates:');
         TextOut(150,230,CSV_date_interval);
         TextOut( 50,245,'Time Zone:');
         TextOut(150,245,CSV_zone_name);
         TextOut( 50,260,'Time skew:');
         TextOut(150,260,SetupList.Values['tsk_s']);

         TextOut( 50,275,'-----------------------------TIME LINE-------------');
         TextOut( 50,290,'CSV Time line file name: '+CSV_full_name+'.csv');
         TextOut( 50,305,'Size:');
         TextOut(150,305,IntToStr(FileSize(outpath+'/'+CSV_full_name+'.csv'))+' bytes ('+Filesizestr(outpath+'/'+CSV_full_name+'.csv')+')');

         TextOut( 50,320,'rows:');
         TextOut(150,320,IntToStr(CSV_row_count));
         TextOut( 50,335,'Timeline SHA1:');
         TextOut(150,335,Report_sha1_timeline);
         TextOut( 50,350,'Timeline MD5:');
         TextOut(150,350,Report_md5_timeline);
         TextOut( 50,365,'-----------------------------END REPORT-------------');
         Font.Size:=8;
         TextOut(340,400,'            Examiner Sign          ');
         Font.Size:=10;
         TextOut(340,450,'___________________________________');

         //       Line(from_left,from_top,to_from_left,to_from_top);
         EndDoc;
         //SaveToFile(<report path>+<reportname>+'.ps');
         SaveToFile(outpath+'/'+report_full_name+'.ps');
       finally
        Pscanvas.free;
       end;//try

       //preview ?
       try
       //fpsystem('gv '+ExpandFileName(<path>+'<filename>'));
       //convert in PDF
       fpsystem('ps2pdf '+outpath+'/'+report_full_name+'.ps '+outpath+'/'+report_full_name+'.pdf');
       //remove postscript
       fpsystem('rm '+outpath+'/'+report_full_name+'.ps');
       Showmessage(' PDF Report generated in: '+outpath+'/'+report_full_name+'.pdf');

       except
             on e: exception do ShowMessage(e.Message);
       end;
       return:=1;
     end;
end;

end.

