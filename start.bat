@echo off

REM Sedentary Reminder Tool - Silent Launcher
REM This batch file runs the application in silent mode without showing terminal window

REM Create temporary VBScript to run in background
set "vbsFile=%temp%\sedentary_reminder_temp.vbs"

echo Set objShell = CreateObject("WScript.Shell") > "%vbsFile%"
echo objShell.Run "cmd /c mvn exec:java@run-launcher", 0, False >> "%vbsFile%"
echo Set objShell = Nothing >> "%vbsFile%"

REM Run VBScript to launch application silently
cscript //nologo "%vbsFile%"

REM Clean up temporary file
del "%vbsFile%" /f /q

REM Exit without showing any window
exit /b 0