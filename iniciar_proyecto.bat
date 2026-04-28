@echo off
color 0A
echo =======================================================
echo    SISTEMA TIENDA AUTOS - ARRANQUE SEGURO
echo =======================================================
echo.
cd /d "%~dp0"

echo [1] Deteniendo versiones anteriores de la Tienda (Por favor espera)...
taskkill /F /IM java.exe /T >nul 2>&1
taskkill /F /IM javaw.exe /T >nul 2>&1
timeout /t 2 /nobreak >nul

echo [2] Reseteando Base de Datos Local H2...
if exist "ProyectoSpringBoot\sistema_tienda_autos.mv.db" del /f /q "ProyectoSpringBoot\sistema_tienda_autos.mv.db"
if exist "ProyectoSpringBoot\sistema_tienda_autos.lock.db" del /f /q "ProyectoSpringBoot\sistema_tienda_autos.lock.db"

echo [3] Iniciando Servidor Empresarial Spring Boot...
cd ProyectoSpringBoot

echo.
echo Presiona CTRL+C para detener el servidor web cuando hayas terminado.
echo Al terminar de arrancar, abre http://localhost:8080/ en tu navegador!
echo.
call ..\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
pause
