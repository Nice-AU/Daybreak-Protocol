@echo off
setlocal
cd /d "%~dp0"

set "ISCC=C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
if exist "%ISCC%" goto compile
echo Inno Setup 6 was not found at:
echo %ISCC%
exit /b 1

:compile
"%ISCC%" "installer\DaybreakProtocol.iss"
if errorlevel 1 exit /b 1

copy /Y "installer-output\DaybreakProtocol-Setup.exe" "Install-DaybreakProtocol.exe" >nul
echo Built Install-DaybreakProtocol.exe
