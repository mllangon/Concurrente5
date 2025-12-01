@echo off
echo ========================================
echo Iniciando Wakanda Smart City System
echo ========================================
echo.

echo [1/8] Compilando proyecto...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Fallo en la compilacion
    pause
    exit /b 1
)
echo.

echo [2/8] Iniciando Discovery Server (Eureka)...
start "Discovery Server" cmd /k "cd discovery-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/8] Iniciando Config Server...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/8] Iniciando API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/8] Iniciando Waste Service...
start "Waste Service" cmd /k "cd waste-service && mvn spring-boot:run"

echo [6/8] Iniciando Traffic Service...
start "Traffic Service" cmd /k "cd traffic-service && mvn spring-boot:run"

echo [7/8] Iniciando Security Service...
start "Security Service" cmd /k "cd security-service && mvn spring-boot:run"

echo [8/8] Iniciando Health Service...
start "Health Service" cmd /k "cd health-service && mvn spring-boot:run"

echo [9/9] Iniciando Emergency Service...
start "Emergency Service" cmd /k "cd emergency-service && mvn spring-boot:run"

echo [10/10] Iniciando UI Portal (Dashboard)...
start "UI Portal" cmd /k "cd ui-portal && mvn spring-boot:run"

echo.
echo ========================================
echo Todos los servicios estan iniciando...
echo ========================================
echo.
echo Servicios disponibles en:
echo   - Eureka Dashboard: http://localhost:8761
echo   - API Gateway: http://localhost:8080
echo   - UI Dashboard: http://localhost:8090
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
echo (Los servicios continuaran ejecutandose en sus propias ventanas)
pause >nul

