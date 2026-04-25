@echo off
echo ============================================================
echo   SATELLITE SIMULATION - BUILD AND RUN SCRIPT
echo ============================================================

:: 1. Setup paths
set LIB_DIR=lib
set SRC_DIR=src
set BIN_DIR=bin

:: S? d?ng tr?c ti?p JDK v?a cài ??t ?? tránh l?i version
set JAVAC_EXE="C:\Program Files\BellSoft\LibericaJDK-8-Full\bin\javac.exe"
set JAVA_EXE="C:\Program Files\BellSoft\LibericaJDK-8-Full\bin\java.exe"

:: 2. Create bin directory if not exists
if not exist %BIN_DIR% mkdir %BIN_DIR%

:: 3. Compile and Sync Resources
echo [+] Syncing resources...
if exist resources xcopy /s /y /q resources %BIN_DIR%

echo [+] Compiling sources...
%JAVAC_EXE% -encoding UTF-8 -d %BIN_DIR% -cp "%LIB_DIR%\*;%SRC_DIR%" %SRC_DIR%\com\planetsim\model\*.java %SRC_DIR%\com\planetsim\utils\*.java %SRC_DIR%\com\planetsim\view\*.java %SRC_DIR%\com\planetsim\controller\*.java %SRC_DIR%\com\planetsim\MainApp.java

if %errorlevel% neq 0 (
    echo [!] Compilation failed!
    pause
    exit /b %errorlevel%
)

:: 4. Run the application
echo [+] Running application...
%JAVA_EXE% -cp "%BIN_DIR%;%LIB_DIR%\*" com.planetsim.MainApp

pause
