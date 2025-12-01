@echo off
echo ========================================
echo Deteniendo todos los servicios...
echo ========================================
echo.

echo Cerrando ventanas de servicios...
taskkill /FI "WINDOWTITLE eq Discovery Server*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Config Server*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq API Gateway*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Waste Service*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Traffic Service*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Security Service*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Health Service*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Emergency Service*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq UI Portal*" /T /F >nul 2>&1

echo Cerrando procesos Java de Spring Boot...
for /f "tokens=2" %%a in ('jps -l ^| findstr "spring-boot"') do (
    taskkill /PID %%a /F >nul 2>&1
)

echo.
echo Todos los servicios han sido detenidos.
timeout /t 2 /nobreak >nul

