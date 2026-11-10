@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo.
    echo JDK introuvable. Installez JDK 17 ou superieur et ajoutez-le au PATH.
    echo.
    pause
    exit /b 1
)

echo Nettoyage de l'ancienne compilation...
if exist out rmdir /s /q out
mkdir out

set "SOURCES=%TEMP%\codelearn_sources_%RANDOM%.txt"
if exist "%SOURCES%" del /q "%SOURCES%"
for /r "src" %%F in (*.java) do (
    if /i not "%%~nxF"=="tempCodeRunnerFile.java" (
        set "FILE=%%F"
        echo "!FILE:\=/!">>"%SOURCES%"
    )
)

echo Compilation...
javac -encoding UTF-8 -d out @"%SOURCES%"
set "RESULT=%ERRORLEVEL%"
del /q "%SOURCES%" >nul 2>nul

if not "%RESULT%"=="0" (
    echo.
    echo La compilation a echoue. Corrigez les erreurs ci-dessus puis relancez.
    echo.
    pause
    exit /b %RESULT%
)

if /i "%~1"=="--build-only" (
    echo Compilation terminee.
    endlocal
    exit /b 0
)

echo Lancement de CodeLearn...
java -cp out Main

if errorlevel 1 (
    echo.
    echo L'application s'est arretee avec une erreur.
    echo.
    pause
)
endlocal
