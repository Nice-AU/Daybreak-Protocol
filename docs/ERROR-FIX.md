# Direct EXE Error Fix

The original JAR and `run.bat` depended on a specific local JavaFX JDK. On another computer that could produce a missing Java, missing JavaFX, or silent-launch error.

The packaged version fixes that problem and now provides two Windows choices:

- `Install-DaybreakProtocol.exe` is the recommended standalone installer.
- `DaybreakProtocol.exe` is a direct Windows launcher.
- The adjacent `runtime` folder contains a trimmed JavaFX runtime.
- The executable uses the supplied `Seller Final Ico.ico`.
- No separate Java installation is required.

Keep `DaybreakProtocol.exe`, the `app` folder, and the `runtime` folder together. Moving only the `.exe` out of the folder will prevent it from launching.

The installer can be moved and shared by itself. It installs all required files, adds shortcuts, and registers an uninstaller.
