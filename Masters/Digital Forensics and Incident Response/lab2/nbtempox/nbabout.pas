unit nbabout;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, Forms, Controls, Graphics, Dialogs, StdCtrls
  , fileinfo
  , winpeimagereader {need this for reading exe info}
  , elfreader {needed for reading ELF executables}
  , machoreader {needed for reading MACH-O executables};
  var
  FileVerInfo: TFileVersionInfo;
type

  { TAboutForm }

  TAboutForm = class(TForm)
    Button1: TButton;
    L_comments: TLabel;
    L_productname: TLabel;
    L_filedescription: TLabel;
    L_fileversion: TLabel;
    procedure FormActivate(Sender: TObject);
  private
    { private declarations }
  public
    { public declarations }
  end;

var
  AboutForm: TAboutForm;

implementation

{$R *.lfm}

{ TAboutForm }

procedure TAboutForm.FormActivate(Sender: TObject);
begin
  if Paramcount=0 then
    begin
      //writeln('Missing executable filename parameters. Aborting.');
      //halt(1);
    end;
    FileVerInfo:=TFileVersionInfo.Create(nil);
    try
      FileVerInfo.FileName:=paramstr(0);
      FileVerInfo.ReadFileInfo;
      //('Company: ',FileVerInfo.VersionStrings.Values['CompanyName']);
      L_filedescription.Caption:=(''+FileVerInfo.VersionStrings.Values['FileDescription']);
      L_fileversion.Caption:=('Version '+FileVerInfo.VersionStrings.Values['FileVersion']);
      //('Internal name: ',FileVerInfo.VersionStrings.Values['InternalName']);
      //('Legal copyright: ',FileVerInfo.VersionStrings.Values['LegalCopyright']);
      //('Original filename: ',FileVerInfo.VersionStrings.Values['OriginalFilename']);
      L_productname.Caption:=(''+FileVerInfo.VersionStrings.Values['ProductName']);
      L_comments.Caption:=(''+FileVerInfo.VersionStrings.Values['Comments']);

    finally
      FileVerInfo.Free;
    end;
end;

end.

