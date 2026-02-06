@echo off
setlocal ENABLEDELAYEDEXPANSION

REM ==================================================
REM Корень проекта
REM ==================================================
set PROJECT_DIR=%~dp0
set PROJECT_DIR=%PROJECT_DIR:~0,-1%

set LIB_DIR=%PROJECT_DIR%\lib
set BUILD_DIR=%PROJECT_DIR%\build
set CLASSES_DIR=%BUILD_DIR%\classes\java\main
set JAR=%LIB_DIR%\jcurses.jar

REM ==================================================
REM Меню
REM ==================================================
:MENU
cls
echo ==================================================
echo Dungeon Game
echo ==================================================
echo.
echo 1: Скомпилировать и запустить (по умолчанию)
echo 2: Только скомпилировать
echo 3: Только запустить
echo 0: Закрыть
echo.
set /p choice="Выберите пункт [1]: "
if "%choice%"=="" set choice=1

if "%choice%"=="0" exit /b
if "%choice%"=="1" goto BUILD_RUN
if "%choice%"=="2" goto BUILD_ONLY
if "%choice%"=="3" goto RUN_ONLY

echo Неверный выбор!
pause
goto MENU

REM ==================================================
:BUILD_RUN
call "%PROJECT_DIR%\gradlew.bat" build
if errorlevel 1 (
    echo.
    echo BUILD FAILED. Game not started.
    pause
    exit /b 1
)
goto RUN_ONLY

:BUILD_ONLY
call "%PROJECT_DIR%\gradlew.bat" build
if errorlevel 1 (
    echo.
    echo BUILD FAILED.
    pause
    exit /b 1
)
echo Компиляция завершена.
pause
exit /b

:RUN_ONLY
if not exist "%CLASSES_DIR%" (
    echo ERROR: classes not found: %CLASSES_DIR%
    pause
    exit /b 1
)
if not exist "%JAR%" (
    echo ERROR: jcurses.jar not found: %JAR%
    pause
    exit /b 1
)
echo.
echo ================================================
echo Starting Dungeon Game...
echo ================================================
echo.
java -Djava.library.path="%LIB_DIR%" -cp "%CLASSES_DIR%;%JAR%" presentation.GameApplication
exit /b
