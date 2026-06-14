@echo off
setlocal
cd /d "%~dp0"

set "FX_JDK=C:\Program Files\Java\zulu11.78.15-ca-fx-jdk11.0.26-win_x64"
call build.bat
if errorlevel 1 exit /b 1

"%FX_JDK%\bin\javac.exe" -encoding UTF-8 -d "build\classes" src\daybreak\Puzzle.java src\daybreak\GameSession.java src\daybreak\PuzzleLibrary.java tests\daybreak\LogicTest.java
if errorlevel 1 exit /b 1

"%FX_JDK%\bin\java.exe" -ea -cp "build\classes" daybreak.LogicTest
