# 🍷 Cellar Service

Microservicio de **bodega personal** del ecosistema Tu Somelier. Gestiona qué vinos tiene cada usuario, cuántas botellas, dónde están guardadas, cuánto pagó y cuándo es el momento óptimo para beberlos.

## Stack

| | |
|---|---|
| **Framework** | Spring Boot 3.3 |
| **Lenguaje** | Java 21 |
| **Base de datos** | PostgreSQL |
| **Caché** | Redis |
| **Migraciones** | Flyway |
| **Comunicación** | OpenFeign → wine-service |
| **Registro** | Netflix Eureka |

## Arquitectura

El servicio sigue **arquitectura hexagonal** (puertos y adaptadores):

```
src/main/java/tu/somelier/cellar/
│
├── domain/                         ← núcleo, sin dependencias de frameworks
│   ├── model/                      ← CellarEntry, ConsumptionLog, WineDetails, CellarStats
│   ├── port/
│   │   ├── in/                     ← casos de uso (interfaces)
│   │   └── out/                    ← puertos de salida (interfaces)
│   ├── service/                    ← CellarDomainService (lógica de negocio)
│   └── exception/                  ← excepciones de dominio
│
└── infrastructure/                 ← adaptadores que implementan los puertos
    ├── adapter/
    │   ├── in/web/                 ← CellarController + DTOs
    │   └── out/
    │       ├── persistence/        ← JPA (PostgreSQL)
    │       ├── cache/              ← Redis
    │       └── feign/              ← wine-service client
    └── config/
```

## API

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/cellar` | Bodega completa (paginada) |
| `POST` | `/cellar` | Añadir vino a la bodega |
| `GET` | `/cellar/{id}` | Detalle de una entrada |
| `PUT` | `/cellar/{id}` | Actualizar entrada |
| `DELETE` | `/cellar/{id}` | Eliminar entrada |
| `POST` | `/cellar/{id}/consume` | Registrar consumo de botellas |
| `GET` | `/cellar/stats` | Estadísticas de la bodega |
| `GET` | `/cellar/drink-now` | Vinos listos para beber ahora |
| `GET` | `/cellar/expiring-soon` | Vinos que caducan próximamente |

Todos los endpoints requieren el header **`X-User-Id: <uuid>`**. El filtrado por usuario es automático — ningún usuario puede ver ni modificar la bodega de otro.

### Ejemplo: añadir un vino

```bash
POST /cellar
X-User-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6

{
  "wineId": "a1b2c3d4-...",
  "quantity": 6,
  "purchaseDate": "2024-03-15",
  "purchasePrice": 28.50,
  "location": "Estante A, balda 2",
  "drinkFrom": "2026-01-01",
  "drinkUntil": "2030-12-31",
  "personalNotes": "Reserva especial para ocasiones"
}
```

### Ejemplo: estadísticas

```json
GET /cellar/stats

{
  "totalBottles": 48,
  "totalValue": 1240.50,
  "avgBottlePrice": 25.84,
  "byType": { "RED": 30, "WHITE": 12, "SPARKLING": 6 },
  "byRegion": { "Rioja": 18, "Ribera del Duero": 12 },
  "readyToDrink": 15,
  "expiringSoon": 4
}
```

Las estadísticas se cachean en Redis por usuario con un TTL de **1 hora** y se invalidan automáticamente al modificar la bodega.

## Variables de entorno

| Variable | Descripción | Default local |
|----------|-------------|---------------|
| `DB_URL` | JDBC URL de PostgreSQL | `jdbc:postgresql://localhost:5432/cellar_db` |
| `DB_USER` | Usuario de la BD | `postgres` |
| `DB_PASSWORD` | Password de la BD | `postgres` |
| `REDIS_URL` | URL de Redis | `redis://localhost:6379` |
| `WINE_SERVICE_URL` | URL base de wine-service | `http://wine-service` |
| `EUREKA_URL` | URL de Eureka Server | `http://localhost:8761/eureka` |

## Arranque local

**Requisitos:** Java 21, PostgreSQL, Redis

```bash
# 1. Crear la base de datos
psql -U postgres -c "CREATE DATABASE cellar_db;"

# 2. Compilar
mvn compile

# 3. Arrancar
DB_URL=jdbc:postgresql://localhost:5432/cellar_db \
DB_USER=postgres \
DB_PASSWORD=postgres \
REDIS_URL=redis://localhost:6379 \
WINE_SERVICE_URL=http://localhost:8083 \
mvn spring-boot:run
```

Flyway aplica las migraciones automáticamente al arrancar. El servicio escucha en el **puerto 8084**.

## Modelo de datos

### `cellar_entries`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | UUID | PK |
| `user_id` | UUID | Usuario propietario |
| `wine_id` | UUID | Referencia a wine-service |
| `quantity` | INTEGER | Número de botellas |
| `purchase_date` | DATE | Fecha de compra |
| `purchase_price` | DECIMAL(10,2) | Precio por botella |
| `location` | VARCHAR(100) | Ubicación física |
| `drink_from` | DATE | Desde cuándo se puede beber |
| `drink_until` | DATE | Fecha límite de consumo |
| `personal_notes` | TEXT | Notas personales |
| `status` | VARCHAR | `AVAILABLE` / `CONSUMED` / `GIFTED` / `SOLD` |

### `consumption_log`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | UUID | PK |
| `entry_id` | UUID | FK → cellar_entries |
| `user_id` | UUID | Usuario |
| `wine_id` | UUID | Vino consumido |
| `quantity` | INTEGER | Botellas consumidas |
| `consumed_at` | TIMESTAMP | Cuándo se consumió |
| `occasion` | VARCHAR(100) | Ocasión (ej: "Cena de cumpleaños") |

## Comunicación entre servicios

- Llama a **wine-service** vía Feign para enriquecer las respuestas con nombre, tipo y región del vino.
- Si wine-service no está disponible, los endpoints responden igualmente sin datos de vino (degradación elegante).
