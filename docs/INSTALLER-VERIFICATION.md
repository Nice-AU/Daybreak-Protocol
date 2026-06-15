# Installer Verification

`Install-DaybreakProtocol.exe` was built with Inno Setup 6 and tested through the complete Windows installation lifecycle.

Verified on June 15, 2026:

- Installer completes silently without administrator rights.
- Supplied character icon appears on the installer.
- Game launcher, bundled JavaFX runtime, application JAR, README, license, and gameplay video are installed.
- Start Menu shortcut is created.
- Windows uninstall registration is created.
- The installed `DaybreakProtocol.exe` launches successfully.
- The generated uninstaller removes the installed files and Start Menu shortcut.

The installer is unsigned because no Windows code-signing certificate was provided. A downloadable unsigned installer may trigger a Windows SmartScreen warning.
