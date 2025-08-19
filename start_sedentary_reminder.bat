@echo off

REM Sedentary Reminder Tool Launcher
REM Version: Final - English only to avoid encoding issues

REM This batch file runs the application using Maven
REM It keeps the command window open after execution

cmd /k "(
    echo Sedentary Reminder Tool
    echo ===================
    echo Starting application with Maven...
    echo Please wait, initial startup may take time to load dependencies...
    echo.
    mvn exec:java@run-launcher
    
    if errorlevel 1 (
        echo.
        echo Application failed to run!
        echo Check Maven configuration and project dependencies.
        echo Error code: %errorlevel%
    ) else (
        echo.
        echo Application exited
    )
    
    echo.
    echo Press any key to close this window...
    pause >nul
)"

exit /b 0