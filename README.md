# Nativa

Sistema de gestión de restaurante basado en microservicios con Spring Boot y Docker.

## Tecnologías

| Componente | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Cloud | 2025.1.1 |
| MySQL | 8.0 |
| Maven | 3.9.6 |
| JWT (jjwt) | 0.12.6 |
| Swagger (Springdoc) | 3.0.2 / 2.8.9 |

## Arquitectura

```
Cliente → API Gateway (8080)
               │
               ├── auth-service (8088)       → Autenticación
               ├── usuario-service (8083)    → Usuarios
               ├── menu-service (8081)       → Menú/Productos
               ├── pedido-service (8084)     → Pedidos
               ├── pago-service (8086)       → Pagos
               ├── inventario-service (8091) → Inventario
               ├── reserva-service (8082)    → Reservas
               └── resena-service (8092)     → Reseñas

Todos los servicios se registran en → Eureka Server (8761)
Todos los servicios persisten en   → MySQL (3306)
```

## Requisitos previos

- Docker y Docker Compose
- Java 21 (para ejecución local)
- Maven 3.9+ (o usar `mvnw` incluido)

## Estructura del proyecto

```
Nativa/
├── docker-compose.yml           # Orquestación completa
├── api-gateway/                 # Spring Cloud Gateway (WebFlux)
├── auth-service/                # Autenticación JWT
├── eureka-service/              # Service Discovery (Eureka Server)
├── inventario-service/          # Inventario / Stock
├── menu-service/                # Menú y productos
├── pago-service/                # Pagos
├── pedido-service/              # Pedidos
├── reserva-service/             # Reservas
├── resena-service/              # Reseñas
└── usuario-service/             # Usuarios
```

Cada microservicio contiene su propio `pom.xml`, `Dockerfile` y código fuente en `src/`.

## Cómo levantar con Docker

### 1. Construir e iniciar todos los servicios

```bash
docker compose up --build
```

Los servicios arrancan en este orden gracias a `depends_on`:
1. `mysql-db` — base de datos (con healthcheck)
2. `eureka-service` — service discovery
3. Servicios (auth, usuario, menu, pedido, pago, inventario, etc.) — en paralelo
4. `api-gateway` — gateway (depende solo de eureka)

### 2. Ver logs de un servicio específico

```bash
docker compose logs -f pago-service
docker compose logs -f inventario-service
```

### 3. Limpiar imágenes y volúmenes

```bash
docker compose down -v
docker builder prune -f
```

## Cómo ejecutar localmente

### 1. Base de datos

```bash
docker compose up -d mysql-db
```

### 2. Eureka Server (obligatorio primero)

```bash
cd eureka-service
mvn spring-boot:run
```

### 3. Microservicios (en cualquier orden después de Eureka)

```bash
cd <servicio>
mvn spring-boot:run
# o: mvn clean package -DskipTests && java -jar target/*.jar
```

### 4. API Gateway (al final)

```bash
cd api-gateway
mvn spring-boot:run
```

## Variables de entorno

| Variable | Descripción | Default (local) | Docker |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión MySQL | `jdbc:mysql://localhost:3306/...` | `jdbc:mysql://mysql-db:3306/...` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña BD | (vacío o root) | `rootpassword` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL de Eureka | `http://localhost:8761/eureka/` | `http://eureka-service:8761/eureka/` |
| `JWT_SECRET` | Clave secreta JWT | `a98ba83...` | `MiClaveSuperSecretaDeProduccion2026` |
| `JWT_EXPIRATION` | Expiración del token (ms) | `86400000` (24h) | `86400000` |

## Endpoints de la API Gateway (puerto 8080)

| Ruta pública | Servicio destino |
|---|---|
| `/api/auth/**` | auth-service (8088) |
| `/api/menu/**` | menu-service (8081) |
| `/api/inventario/**` | inventario-service (8091) |
| `/api/reserva/**` | reserva-service (8082) |
| `/api/pago/**` | pago-service (8086) |
| `/api/pedido/**` | pedido-service (8084) |
| `/api/usuario/**` | usuario-service (8083) |
| `/api/resena/**` | resena-service (8092) |

## Swagger UI

Cada servicio expone su documentación OpenAPI:

| Servicio | URL Swagger |
|---|---|
| API Gateway | `http://localhost:8080/swagger-ui.html` |
| auth-service | `http://localhost:8088/swagger-ui.html` |
| usuario-service | `http://localhost:8083/swagger-ui.html` |
| menu-service | `http://localhost:8081/swagger-ui.html` |
| pedido-service | `http://localhost:8084/swagger-ui.html` |
| pago-service | `http://localhost:8086/swagger-ui.html` |
| inventario-service | `http://localhost:8091/swagger-ui.html` |
| reserva-service | `http://localhost:8082/swagger-ui.html` |
| resena-service | `http://localhost:8092/swagger-ui.html` |

## Correcciones conocidas

### 1. Pago-service — Migración Flyway falla

**Archivo:** `pago-service/src/main/resources/db/migration/V1__create_table_pago.sql:13`

El `INSERT` usa la columna `monto` pero la tabla se creó con `total`. Cambiar:
```sql
-- ❌ Actual (falla)
INSERT INTO pago (pedido_id, usuario_id, monto, metodo_pago, fecha_pago) VALUES
-- ✅ Corregido
INSERT INTO pago (pedido_id, usuario_id, total, metodo_pago, fecha_pago) VALUES
```

### 2. Inventario-service — DataLoader no asigna productoId

**Archivo:** `inventario-service/src/main/java/com/example/inventario/DataLoader.java:38`

Agregar `inventario.setProductoId(...)` antes de guardar, porque la columna es `NOT NULL`:
```java
inventario.setProductoId(faker.number().numberBetween(1L, 100L));
```

### 3. Eureka-service — Dockerfile sin etapa de build

**Archivo:** `eureka-service/Dockerfile`

Actualmente copia el jar precompilado. Si se usa con `docker compose` (que espera build automático), conviene cambiarlo a multi-stage build como los demás servicios, o compilar localmente antes:
```bash
cd eureka-service && mvn clean package -DskipTests
```

## Comandos útiles

# para eliminar el cache
docker builder prune -f    

# para levantar los docker
docker compose up --build     
# Ver estado de los contenedores
docker compose ps

# Ver logs en tiempo real
docker compose logs -f

# ver logs especificos
docker compose logs pago-service                                                                              

# Acceder a MySQL
docker exec -it mysql-container mysql -u root -prootpassword

# Ver servicios registrados en Eureka
curl http://localhost:8761/eureka/apps
```
