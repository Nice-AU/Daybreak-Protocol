@echo off
setlocal
cd /d "%~dp0"

set "FX_JDK=C:\Program Files\Java\zulu11.78.15-ca-fx-jdk11.0.26-win_x64"
if not exist "%FX_JDK%\bin\javac.exe" (
    echo JavaFX JDK not found at %FX_JDK%
    exit /b 1
)

if not exist "build\classes" mkdir "build\classes"
xcopy /E /I /Y "resources" "build\classes" >nul

"%FX_JDK%\bin\javac.exe" -encoding UTF-8 -d "build\classes" src\daybreak\Puzzle.java src\daybreak\GameSession.java src\daybreak\PuzzleLibrary.java src\daybreak\SunDial.java src\daybreak\DaybreakProtocol.java
if errorlevel 1 exit /b 1

"%FX_JDK%\bin\jar.exe" --create --file "build\DaybreakProtocol.jar" --main-class daybreak.DaybreakProtocol -C "build\classes" .
if errorlevel 1 exit /b 1

echo Built build\DaybreakProtocol.jar
