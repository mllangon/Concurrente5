@echo off
echo ========================================
echo Verificando Microservicios de Wakanda
echo ========================================
echo.

set GATEWAY_URL=http://localhost:8080/api
set TIMEOUT=5

echo [1/5] Verificando Waste Service...
curl -s -o nul -w "Status: %%{http_code}\n" %GATEWAY_URL%/waste/containers/status
if %errorlevel% neq 0 (
    echo   ERROR: No se pudo conectar al servicio
) else (
    echo   OK: Waste Service funcionando
)
echo.

echo [2/5] Verificando Traffic Service...
curl -s -o nul -w "Status: %%{http_code}\n" %GATEWAY_URL%/traffic/status
if %errorlevel% neq 0 (
    echo   ERROR: No se pudo conectar al servicio
) else (
    echo   OK: Traffic Service funcionando
)
echo.

echo [3/5] Verificando Security Service...
curl -s -o nul -w "Status: %%{http_code}\n" %GATEWAY_URL%/security/status
if %errorlevel% neq 0 (
    echo   ERROR: No se pudo conectar al servicio
) else (
    echo   OK: Security Service funcionando
)
echo.

echo [4/5] Verificando Health Service...
curl -s -o nul -w "Status: %%{http_code}\n" %GATEWAY_URL%/health/status
if %errorlevel% neq 0 (
    echo   ERROR: No se pudo conectar al servicio
) else (
    echo   OK: Health Service funcionando
)
echo.

echo [5/5] Verificando Emergency Service...
curl -s -o nul -w "Status: %%{http_code}\n" %GATEWAY_URL%/emergency/status
if %errorlevel% neq 0 (
    echo   ERROR: No se pudo conectar al servicio
) else (
    echo   OK: Emergency Service funcionando
)
echo.

echo ========================================
echo Verificacion completada
echo ========================================
echo.
echo URLs utiles:
echo   - Eureka Dashboard: http://localhost:8761
echo   - UI Dashboard: http://localhost:8090
echo   - API Gateway: http://localhost:8080
echo.
pause

