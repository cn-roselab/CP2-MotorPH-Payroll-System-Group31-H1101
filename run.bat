@echo off
REM Build and run the MotorPH Employee Application on Windows.
REM In PowerShell run it as:  .\run.bat
REM In Command Prompt run it as:  run.bat
REM (You can also double-click the file.)

cd /d "%~dp0"

echo Checking for Java...
where javac >nul 2>&1
if errorlevel 1 (
    echo ERROR: javac was not found on your PATH.
    echo Install a JDK 8 or higher, then open a NEW terminal and try again.
    pause
    exit /b 1
)

echo Compiling sources...
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
set COMPILE_ERR=%errorlevel%
del sources.txt

if not "%COMPILE_ERR%"=="0" (
    echo Build failed.
    pause
    exit /b 1
)

echo Starting MotorPH Employee Application...
java -cp out app.MotorPHApp

echo.
echo Application closed.
