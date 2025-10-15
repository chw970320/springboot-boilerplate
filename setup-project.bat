@echo off
chcp 65001 > nul
REM Spring Boot Boilerplate 프로젝트 초기 설정 (Windows Batch)
REM 최초 1회 실행 후 자동 삭제됩니다.

echo.
echo ================================================
echo   Spring Boot Boilerplate 프로젝트 설정
echo ================================================
echo.
echo PowerShell 스크립트를 실행합니다...
echo.

powershell.exe -ExecutionPolicy Bypass -File "%~dp0setup-project.ps1"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] 설정 중 오류가 발생했습니다.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo 설정이 완료되었습니다!
pause

