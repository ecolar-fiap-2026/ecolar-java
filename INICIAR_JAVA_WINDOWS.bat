@echo off
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
 echo Instale JDK 21 de 64 bits e reabra o VS Code. Veja LEIA_PRIMEIRO.md.
 pause
 exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\iniciar-oracle.ps1"
pause
