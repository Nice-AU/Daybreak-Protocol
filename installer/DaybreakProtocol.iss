#define MyAppName "Daybreak Protocol"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Alan"
#define MyAppExeName "DaybreakProtocol.exe"

[Setup]
AppId={{5F6C3D99-3C33-4FD1-8E75-2E7813D2A81C}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL=https://dev.to/challenges/june-game-jam-2026-06-03
AppSupportURL=https://dev.to/challenges/june-game-jam-2026-06-03
DefaultDirName={localappdata}\Programs\Daybreak Protocol
DefaultGroupName=Daybreak Protocol
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputDir=..\installer-output
OutputBaseFilename=DaybreakProtocol-Setup
SetupIconFile=..\resources\assets\DaybreakProtocol.ico
UninstallDisplayIcon={app}\{#MyAppExeName}
LicenseFile=..\LICENSE
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern
ArchitecturesAllowed=x64
CloseApplications=yes
RestartApplications=no
SetupLogging=yes
VersionInfoVersion=1.0.0.0
VersionInfoProductName={#MyAppName}
VersionInfoProductVersion={#MyAppVersion}
VersionInfoCompany={#MyAppPublisher}
VersionInfoDescription=Installer for Daybreak Protocol

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Additional shortcuts:"; Flags: unchecked

[Files]
Source: "..\DaybreakProtocol.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\app\*"; DestDir: "{app}\app"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\runtime\*"; DestDir: "{app}\runtime"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\README.md"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\LICENSE"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\docs\media\daybreak-protocol-demo.mp4"; DestDir: "{app}\docs\media"; Flags: ignoreversion

[Icons]
Name: "{autoprograms}\Daybreak Protocol"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"
Name: "{autodesktop}\Daybreak Protocol"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch Daybreak Protocol"; Flags: nowait postinstall skipifsilent
