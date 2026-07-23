# Cellar Service - Tech Plan

## Responsabilidad
La bodega personal de cada usuario. Gestiona qué vinos tiene, cuántas botellas, dónde están guardadas, cuánto pagó y cuándo es el momento óptimo para beberlos.

## Stack Tecnológico
- **Framework**: Spring Boot 3.x
- **Lenguaje**: Java 21
- **Base de datos**: PostgreSQL
- **Caché**: Redis (estadísticas de bodega)
- **Build**: Maven
- **Contenedor**: Docker

## Dependencias principales
```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-data-redis
spring-boot-starter-validation
postgresql (driver)
spring-cloud-starter-netflix-eureka-client
spring-cloud-openfeign (llamadas a wine-service)
```

## Modelo de Datos

### Tabla: `cellar_entries`
| Campo           | Tipo         | Descripción                            |
|-----------------|--------------|----------------------------------------|
| id              | UUID (PK)    |                                        |
| user_id         | UUID         | Usuario propietario                    |
| wine_id         | UUID         | Referencia a wine-service              |
| quantity        | INTEGER      | Número de botellas                     |
| purchase_date   | DATE         | Fecha de compra                        |
| purchase_price  | DECIMAL(10,2)| Precio pagado por botella              |
| location        | VARCHAR(100) | Ej: "Estante A, balda 2"               |
| drink_from      | DATE         | Fecha desde la que se puede beber      |
| drink_until     | DATE         | Fecha límite para consumir             |
| personal_notes  | TEXT         | Notas personales sobre este lote       |
| status          | ENUM         | AVAILABLE / CONSUMED / GIFTED / SOLD   |
| created_at      | TIMESTAMP    |                                        |
| updated_at      | TIMESTAMP    |                                        |

### Tabla: `consumption_log`
| Campo          | Tipo         | Descripción                          |
|----------------|--------------|--------------------------------------|
| id             | UUID (PK)    |                                      |
| entry_id       | UUID (FK)    | Referencia a cellar_entries          |
| user_id        | UUID         |                                      |
| wine_id        | UUID         |                                      |
| quantity       | INTEGER      | Botellas consumidas                  |
| consumed_at    | TIMESTAMP    | Cuándo se consumió                   |
| occasion       | VARCHAR(100) | Ej: "Cena de cumpleaños"             |

## Endpoints

| Método | Ruta                          | Descripción                              |
|--------|-------------------------------|------------------------------------------|
| GET    | /cellar                       | Mi bodega completa (paginada)            |
| POST   | /cellar                       | Añadir vino a la bodega                  |
| GET    | /cellar/{id}                  | Detalle de una entrada                   |
| PUT    | /cellar/{id}                  | Actualizar entrada (cantidad, notas...) |
| DELETE | /cellar/{id}                  | Eliminar entrada                         |
| POST   | /cellar/{id}/consume          | Registrar consumo de botellas            |
| GET    | /cellar/stats                 | Estadísticas de la bodega                |
| GET    | /cellar/drink-now             | Vinos listos para beber ahora            |
| GET    | /cellar/expiring-soon         | Vinos que caducan próximamente           |

## Estadísticas (GET /cellar/stats)
```json
{
  "totalBottles": 48,
  "totalValue": 1240.50,
  "avgBottlePrice": 25.84,
  "byType": {
    "RED": 30,
    "WHITE": 12,
    "SPARKLING": 6
  },
  "byRegion": {
    "Rioja": 18,
    "Ribera del Duero": 12
  },
  "readyToDrink": 15,
  "expiringSoon": 4
}
```

## Comunicación con otros servicios
- **Llama a wine-service** (Feign Client) para obtener detalles del vino al devolver entradas de bodega
- Devuelve `wine_id` + datos básicos del vino enriquecidos en la respuesta

## Seguridad
- Todos los endpoints requieren JWT
- Filtrado automático por `user_id` (del header `X-User-Id`)
- Un usuario nunca puede ver/editar la bodega de otro

## Variables de entorno
| Variable       | Descripción                     |
|----------------|---------------------------------|
| DB_URL         | JDBC URL de PostgreSQL          |
| DB_USER        | Usuario BD                      |
| DB_PASSWORD    | Password BD                     |
| REDIS_URL      | URL de Redis                    |
| WINE_SERVICE_URL | URL de wine-service           |

## Puerto
`8084`
