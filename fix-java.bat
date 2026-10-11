@echo off
echo Fixing JAVA_HOME for this session...
echo.

REM Try to find Android Studio's JBR
if exist "C:\Program Files\Android\Android Studio\jbr" (
    set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
    echo Found Android Studio JBR
    goto :found
)

REM Try to find JDK 17
if exist "C:\Program Files\Java\jdk-17" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    echo Found JDK 17
    goto :found
)

REM Try to find JDK 21
if exist "C:\Program Files\Java\jdk-21" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    echo Found JDK 21
    goto :found
)

echo WARNING: Could not find a suitable JDK automatically.
echo.
echo Please install JDK 17 or later, or set JAVA_HOME manually:
echo set JAVA_HOME=C:\Path\To\Your\JDK
echo.
pause
exit /b 1

:found
echo JAVA_HOME set to: %JAVA_HOME%
echo.
echo Testing Java version...
"%JAVA_HOME%\bin\java" -version
echo.
echo Now you can run:
echo   gradlew.bat :shared:build
echo.
echo Or open a new command prompt and run this script first.
pause
