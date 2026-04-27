@echo off
echo =======================================================
echo    INICIANDO EL SISTEMA DE AUTOS (V3 FINAL)
echo =======================================================
echo.

echo [1] Cerrando cualquier servidor anterior que este atorado en el puerto 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8080" ^| find "LISTENING"') do taskkill /F /PID %%a >nul 2>&1

echo [2] Limpiando la base de datos corrupta residual...
cd /d "%~dp0"
if exist "ProyectoSpringBoot\sistema_tienda_autos.mv.db" del /f /q "ProyectoSpringBoot\sistema_tienda_autos.mv.db"
if exist "ProyectoSpringBoot\sistema_tienda_autos.trace.db" del /f /q "ProyectoSpringBoot\sistema_tienda_autos.trace.db"

echo [3] Iniciando Servidor de Spring Boot (Espera 10 segundos)...
start "Servidor TiendaAutos" cmd /c "cd ProyectoSpringBoot && ..\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

echo.
echo Esperando a que Spring Boot compile y cargue la base de datos...
timeout /t 10 /nobreak >nul

echo [4] Abriendo la aplicacion en el navegador!
start http://localhost:8080/

echo.
echo TODO LISTO! Ya puedes iniciar sesion con usuario: admin y clave: admin
echo Si deseas apagar el servidor, cierra la ventana negra emergente que dice "Servidor TiendaAutos".
pause
