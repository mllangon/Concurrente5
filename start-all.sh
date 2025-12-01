#!/bin/bash

echo "========================================"
echo "Iniciando Wakanda Smart City System"
echo "========================================"
echo ""

echo "[1/8] Compilando proyecto..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Fallo en la compilación"
    exit 1
fi
echo ""

echo "[2/8] Iniciando Discovery Server (Eureka)..."
cd discovery-server
mvn spring-boot:run > ../logs/discovery-server.log 2>&1 &
DISCOVERY_PID=$!
cd ..
sleep 10

echo "[3/8] Iniciando Config Server..."
cd config-server
mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..
sleep 10

echo "[4/8] Iniciando API Gateway..."
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
sleep 10

echo "[5/8] Iniciando Waste Service..."
cd waste-service
mvn spring-boot:run > ../logs/waste-service.log 2>&1 &
WASTE_PID=$!
cd ..

echo "[6/8] Iniciando Traffic Service..."
cd traffic-service
mvn spring-boot:run > ../logs/traffic-service.log 2>&1 &
TRAFFIC_PID=$!
cd ..

echo "[7/8] Iniciando Security Service..."
cd security-service
mvn spring-boot:run > ../logs/security-service.log 2>&1 &
SECURITY_PID=$!
cd ..

echo "[8/8] Iniciando Health Service..."
cd health-service
mvn spring-boot:run > ../logs/health-service.log 2>&1 &
HEALTH_PID=$!
cd ..

echo "[9/9] Iniciando Emergency Service..."
cd emergency-service
mvn spring-boot:run > ../logs/emergency-service.log 2>&1 &
EMERGENCY_PID=$!
cd ..

echo "[10/10] Iniciando UI Portal (Dashboard)..."
cd ui-portal
mvn spring-boot:run > ../logs/ui-portal.log 2>&1 &
UI_PID=$!
cd ..

echo ""
echo "========================================"
echo "Todos los servicios están iniciando..."
echo "========================================"
echo ""
echo "Servicios disponibles en:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway: http://localhost:8080"
echo "  - UI Dashboard: http://localhost:8090"
echo ""
echo "PIDs de los procesos:"
echo "  Discovery: $DISCOVERY_PID"
echo "  Config: $CONFIG_PID"
echo "  Gateway: $GATEWAY_PID"
echo "  Waste: $WASTE_PID"
echo "  Traffic: $TRAFFIC_PID"
echo "  Security: $SECURITY_PID"
echo "  Health: $HEALTH_PID"
echo "  Emergency: $EMERGENCY_PID"
echo "  UI Portal: $UI_PID"
echo ""
echo "Para detener todos los servicios, ejecuta: ./stop-all.sh"
echo ""

# Guardar PIDs en archivo para poder detenerlos después
echo "$DISCOVERY_PID $CONFIG_PID $GATEWAY_PID $WASTE_PID $TRAFFIC_PID $SECURITY_PID $HEALTH_PID $EMERGENCY_PID $UI_PID" > .pids

