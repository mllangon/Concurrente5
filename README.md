# Wakanda Smart City - Sistema de Gestión de Microservicios

## Integrantes del Grupo
- [Nombre del integrante 1]
- [Nombre del integrante 2]
- [Nombre del integrante 3] (si aplica)

## Descripción del Proyecto

Sistema de gestión de servicios para la ciudad inteligente de Wakanda implementado con arquitectura de microservicios utilizando Spring Boot y Spring Cloud.

## Arquitectura

El sistema está compuesto por los siguientes módulos:

### Infraestructura

1. **discovery-server** (Puerto 8761)
   - Servidor Eureka para el descubrimiento de servicios
   - Permite que los microservicios se registren y descubran entre sí

2. **config-server** (Puerto 8888)
   - Servidor de configuración centralizada
   - Gestiona las configuraciones de todos los microservicios

3. **api-gateway** (Puerto 8080)
   - Punto único de entrada para todas las peticiones
   - Enruta las peticiones a los microservicios correspondientes
   - Rutas disponibles:
     - `/api/waste/**` → waste-service
     - `/api/traffic/**` → traffic-service
     - `/api/security/**` → security-service
     - `/api/health/**` → health-service
     - `/api/emergency/**` → emergency-service

### Microservicios de Dominio

4. **waste-service** (Puerto 8081)
   - Gestión de residuos y contenedores inteligentes
   - Endpoints:
     - `GET /api/waste/containers` - Lista todos los contenedores
     - `GET /api/waste/containers/{id}` - Obtiene un contenedor específico
     - `GET /api/waste/containers/status` - Estado del servicio

5. **traffic-service** (Puerto 8082)
   - Gestión de tráfico: semáforos inteligentes y sensores
   - Endpoints:
     - `GET /api/traffic/lights` - Lista todos los semáforos
     - `GET /api/traffic/sensors` - Lista todos los sensores
     - `GET /api/traffic/status` - Estado del servicio

6. **security-service** (Puerto 8083)
   - Seguridad y vigilancia: cámaras y drones
   - Endpoints:
     - `GET /api/security/cameras` - Lista todas las cámaras
     - `GET /api/security/drones` - Lista todos los drones
     - `GET /api/security/status` - Estado del servicio

7. **health-service** (Puerto 8084)
   - Salud y bienestar: centros de salud y citas
   - Endpoints:
     - `GET /api/health/centers` - Lista todos los centros de salud
     - `GET /api/health/appointments` - Lista todas las citas
     - `GET /api/health/status` - Estado del servicio

8. **emergency-service** (Puerto 8085)
   - Servicios de emergencia: unidades y incidentes
   - Endpoints:
     - `GET /api/emergency/units` - Lista todas las unidades de emergencia
     - `GET /api/emergency/incidents` - Lista todos los incidentes
     - `GET /api/emergency/status` - Estado del servicio

### Interfaz de Usuario

9. **ui-portal** (Puerto 8090)
   - Dashboard web para visualizar el estado de todos los microservicios
   - Accesible en: `http://localhost:8090`

## Cómo Ejecutar el Sistema

### Opción 1: Script Automático (Recomendado) ⚡

**Windows:**
```bash
# Iniciar todos los servicios
start-all.bat

# Detener todos los servicios
stop-all.bat
```

**Linux/Mac:**
```bash
# Dar permisos de ejecución
chmod +x start-all.sh stop-all.sh

# Iniciar todos los servicios
./start-all.sh

# Detener todos los servicios
./stop-all.sh
```

El script automático:
- Compila el proyecto
- Inicia todos los servicios en el orden correcto
- Abre cada servicio en su propia ventana/terminal
- Espera el tiempo necesario entre servicios

### Opción 2: Manual (Paso a Paso)

Si prefieres iniciar manualmente:

1. **Discovery Server** (debe iniciarse primero):
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```

2. **Config Server**:
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

3. **API Gateway**:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

4. **Microservicios** (en cualquier orden, en terminales separadas):
   ```bash
   # Terminal 1
   cd waste-service
   mvn spring-boot:run
   
   # Terminal 2
   cd traffic-service
   mvn spring-boot:run
   
   # Terminal 3
   cd security-service
   mvn spring-boot:run
   
   # Terminal 4
   cd health-service
   mvn spring-boot:run
   
   # Terminal 5
   cd emergency-service
   mvn spring-boot:run
   ```

5. **UI Portal**:
   ```bash
   cd ui-portal
   mvn spring-boot:run
   ```

### Verificación

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Dashboard UI**: http://localhost:8090
- **Config Server**: http://localhost:8888

## Tecnologías Utilizadas

- **Spring Boot 3.3.4**: Framework principal
- **Spring Cloud 2023.0.3**: Para microservicios
- **Eureka**: Descubrimiento de servicios
- **Spring Cloud Config**: Configuración centralizada
- **Spring Cloud Gateway**: API Gateway
- **Spring Boot Actuator**: Monitoreo y métricas

## Estructura del Proyecto

```
wakanda-smart-city/
├── discovery-server/      # Servidor Eureka
├── config-server/         # Servidor de configuración
├── api-gateway/          # Gateway de API
├── waste-service/        # Microservicio de residuos
├── traffic-service/      # Microservicio de tráfico
├── security-service/     # Microservicio de seguridad
├── health-service/       # Microservicio de salud
├── emergency-service/    # Microservicio de emergencias
└── ui-portal/            # Dashboard web
```

## Notas

- Todos los microservicios se registran automáticamente en Eureka
- El API Gateway enruta las peticiones usando el nombre del servicio registrado en Eureka
- El dashboard se actualiza automáticamente cada 10 segundos
- Cada microservicio expone endpoints de estado para monitoreo
