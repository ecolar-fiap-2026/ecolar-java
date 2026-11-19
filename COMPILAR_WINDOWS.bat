@echo off
cd /d "%~dp0"
call mvnw.cmd clean verify
if errorlevel 1 goto falha
if not exist bin mkdir bin
copy /Y target\ecolar-api-1.0.0.jar bin\ecolar-api.jar >nul
echo Codigo compilado e testes aprovados. Execute INICIAR_JAVA_WINDOWS.bat.
pause
exit /b 0
:falha
echo Falha na compilacao ou nos testes. Copie a mensagem do terminal para investigar.
pause
exit /b 1
