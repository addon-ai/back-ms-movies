# Guía Completa de Resolución de Errores de Tests

**Proyecto:** back-ms-users-webflux  
**Autor:** Jiliar Silgado <jiliar.silgado@gmail.com>  
**Fecha:** 10 de Noviembre, 2025  
**Estado Final:** ✅ 251/251 tests pasando

---

## 📋 Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Fase 1: Errores de Configuración](#fase-1-errores-de-configuración)
3. [Fase 2: Errores de Repositorios](#fase-2-errores-de-repositorios)
4. [Archivos Modificados](#archivos-modificados)
5. [Código Completo de Soluciones](#código-completo-de-soluciones)
6. [Verificación](#verificación)

---

## Resumen Ejecutivo

### Estado Inicial
```
[ERROR] Tests run: 251, Failures: 0, Errors: 251, Skipped: 0
[INFO] BUILD FAILURE
```

### Estado Final
```
[INFO] Tests run: 251, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 01:15 min
```

### Problemas Identificados

| Fase | Problema | Tests Afectados | Solución |
|------|----------|-----------------|----------|
| 1 | Configuración JPA en proyecto R2DBC | 221 | Configuración R2DBC + H2 |
| 2 | Schema de BD faltante | 30 | Crear schema.sql |
| 2 | Tipos de datos incompatibles | 16 | Ajustar tipos VARCHAR |
| 2 | Datos duplicados en tests | 14 | UUID.randomUUID() |

---

## Fase 1: Errores de Configuración

### 🔴 Error Original

```
[ERROR] IllegalStateException: Failed to load ApplicationContext
[ERROR] UserMapperTest.toGetResponse_ShouldMapCorrectly_FromDomain » IllegalState 
Failed to load ApplicationContext for [ReactiveWebMergedContextConfiguration...]
```

**Tests afectados:** 221 (Mappers, Services, Controllers, Adapters)

### 🔍 Causa Raíz

El proyecto usa **Spring WebFlux + R2DBC** (reactivo) pero las configuraciones tenían propiedades de **Spring MVC + JPA** (bloqueante):

**Configuración Incorrecta:**
```yaml
spring:
  datasource:                    # ❌ JDBC bloqueante
    url: ${DB_URL}
    driver-class-name: org.postgresql.Driver
  jpa:                           # ❌ JPA no es reactivo
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
```

**Problema:** Spring Boot no podía inicializar el contexto porque:
1. Buscaba beans de JPA pero el proyecto usa R2DBC
2. No existía configuración de test
3. Intentaba conectar a PostgreSQL en tests

### ✅ Solución 1: Crear Configuración de Test

**Archivo:** `src/test/resources/application-test.yml`

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  flyway:
    enabled: false
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql

logging:
  level:
    root: INFO
    org.springframework: WARN
```

**Por qué funciona:**
- ✅ Usa R2DBC con H2 en memoria
- ✅ Desactiva Flyway (no necesario en tests)
- ✅ Carga schema.sql automáticamente
- ✅ Base de datos limpia en cada ejecución

### ✅ Solución 2: Corregir Configuraciones de Entorno

**Archivos modificados:**
- `application-test.yml`
- `application-develop.yml`
- `application-staging.yml`
- `application-prod.yml`

**Cambio aplicado a todos:**

```yaml
# ❌ ANTES (JPA/JDBC)
spring:
  datasource:
    url: ${DB_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate

# ✅ DESPUÉS (R2DBC)
spring:
  r2dbc:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  flyway:
    url: ${FLYWAY_URL}
    user: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    enabled: ${FLYWAY_ENABLED:true}
```

**Nota importante:** Flyway necesita JDBC porque no soporta R2DBC, por eso mantenemos ambas URLs.

---

## Fase 2: Errores de Repositorios

### 🔴 Error Original

```
[ERROR] JpaUserRepositoryTest.save_ShouldPersistEntity » BadSqlGrammar 
executeMany; bad SQL grammar [INSERT INTO users (username, email, status) VALUES ($1, $2, $3)]

[ERROR] JpaCityRepositoryTest.save_ShouldPersistEntity » UncategorizedR2dbc 
Data conversion error converting "'56ffe109-...' (CITIES: ""REGION_ID"" UUID NOT NULL)"

[ERROR] JpaUserRepositoryTest.existsById_ShouldReturnTrue_WhenEntityExists » DuplicateKey 
Unique index or primary key violation: "PUBLIC.CONSTRAINT_INDEX_4 ON PUBLIC.USERS(USERNAME)"
```

**Tests afectados:** 30 (todos los repositorios)

### 🔍 Problema 1: Schema Faltante

**Causa:** H2 iniciaba vacío, sin tablas.

### ✅ Solución: Crear Schema de Test

**Archivo:** `src/test/resources/schema.sql`

```sql
-- Test Schema for H2 Database
-- Automatically loaded by Spring Boot

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    user_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: countries
CREATE TABLE IF NOT EXISTS countries (
    country_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: regions
CREATE TABLE IF NOT EXISTS regions (
    region_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    country_id VARCHAR(255) NOT NULL,  -- VARCHAR para coincidir con entidad
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: cities
CREATE TABLE IF NOT EXISTS cities (
    city_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    region_id VARCHAR(255) NOT NULL,  -- VARCHAR para coincidir con entidad
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: neighborhoods
CREATE TABLE IF NOT EXISTS neighborhoods (
    neighborhood_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city_id VARCHAR(255) NOT NULL,  -- VARCHAR para coincidir con entidad
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: locations
CREATE TABLE IF NOT EXISTS locations (
    location_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,  -- VARCHAR para coincidir con entidad
    country VARCHAR(255) NOT NULL,
    region VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    neighborhood VARCHAR(255),
    address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

**Diferencias H2 vs PostgreSQL:**

| Aspecto | PostgreSQL | H2 |
|---------|-----------|-----|
| UUID generation | `gen_random_uuid()` | `RANDOM_UUID()` |
| Timestamp | `TIMESTAMPTZ` | `TIMESTAMP` |
| Foreign keys | `UUID` | `VARCHAR(255)` |

### 🔍 Problema 2: Tipos de Datos Incompatibles

**Causa:** Las entidades usan `String` para foreign keys pero el schema inicial usaba `UUID`.

**Ejemplo de entidad:**

```java
@Table("cities")
public class CityDbo {
    @Id
    @Column("city_id")
    private UUID id;
    
    @Column("region_id")
    private String regionId;  // ← String, no UUID!
}
```

**Solución:** Cambiar schema para usar `VARCHAR(255)` en foreign keys (ver schema.sql arriba).

### 🔍 Problema 3: Datos Duplicados

**Causa:** Tests usaban datos hardcodeados.

**Código problemático:**

```java
private UserDbo createUserDbo() {
    return UserDbo.builder()
        .username("test-username")      // ← Siempre igual!
        .email("test@example.com")      // ← Siempre igual!
        .status(EntityStatus.ACTIVE)
        .build();
}
```

### ✅ Solución: Datos Únicos con UUID

**Archivo:** `src/test/java/.../JpaUserRepositoryTest.java`

```java
private UserDbo createUserDbo() {
    return UserDbo.builder()
        .username("test-username-" + UUID.randomUUID())
        .email("test-" + UUID.randomUUID() + "@example.com")
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**Aplicado a todos los tests de repositorio:**

**JpaCityRepositoryTest.java:**
```java
private CityDbo createCityDbo() {
    UUID randomUUID = UUID.randomUUID();
    return CityDbo.builder()
        .name("test-name-" + randomUUID)
        .regionId(randomUUID.toString())
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**JpaCountryRepositoryTest.java:**
```java
private CountryDbo createCountryDbo() {
    return CountryDbo.builder()
        .name("test-name-" + UUID.randomUUID())
        .code("test-code-" + UUID.randomUUID())
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**JpaRegionRepositoryTest.java:**
```java
private RegionDbo createRegionDbo() {
    return RegionDbo.builder()
        .name("test-name-" + UUID.randomUUID())
        .code("test-code-" + UUID.randomUUID())
        .countryId(UUID.randomUUID().toString())
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**JpaNeighborhoodRepositoryTest.java:**
```java
private NeighborhoodDbo createNeighborhoodDbo() {
    return NeighborhoodDbo.builder()
        .name("test-name-" + UUID.randomUUID())
        .cityId(UUID.randomUUID().toString())
        .status(EntityStatus.ACTIVE)
        .build();
}
```

**JpaLocationRepositoryTest.java:**
```java
private LocationDbo createLocationDbo() {
    return LocationDbo.builder()
        .userId(UUID.randomUUID().toString())
        .country("test-country-" + UUID.randomUUID())
        .region("test-region-" + UUID.randomUUID())
        .city("test-city-" + UUID.randomUUID())
        .address("test-address-" + UUID.randomUUID())
        .locationType("test-locationType-" + UUID.randomUUID())
        .status(EntityStatus.ACTIVE)
        .build();
}
```

---

## Archivos Modificados

### Archivos Creados

```
src/test/resources/
├── application-test.yml    # Configuración R2DBC + H2
└── schema.sql              # Schema de base de datos para tests
```

### Archivos Modificados

```
src/main/resources/
├── application-test.yml        # JPA → R2DBC
├── application-develop.yml     # JPA → R2DBC
├── application-staging.yml     # JPA → R2DBC
└── application-prod.yml        # JPA → R2DBC

src/test/java/.../repository/
├── JpaUserRepositoryTest.java          # Datos únicos
├── JpaCityRepositoryTest.java          # Datos únicos
├── JpaCountryRepositoryTest.java       # Datos únicos
├── JpaRegionRepositoryTest.java        # Datos únicos
├── JpaNeighborhoodRepositoryTest.java  # Datos únicos
└── JpaLocationRepositoryTest.java      # Datos únicos
```

---

## Código Completo de Soluciones

### 1. Configuración de Test Completa

**src/test/resources/application-test.yml:**
```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  flyway:
    enabled: false
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql

logging:
  level:
    root: INFO
    org.springframework: WARN
```

### 2. Configuración de Producción (Ejemplo)

**src/main/resources/application-prod.yml:**
```yaml
spring:
  profiles:
    active: prod
  r2dbc:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  flyway:
    url: ${FLYWAY_URL}
    user: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    enabled: ${FLYWAY_ENABLED:true}

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: ${LOG_LEVEL:INFO}
    org.springframework.security: ${SECURITY_LOG_LEVEL:WARN}

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    try-it-out-enabled: ${SWAGGER_ENABLED:false}

management:
  endpoints:
    web:
      exposure:
        include: ${MANAGEMENT_ENDPOINTS:health,info}
  endpoint:
    health:
      show-details: ${HEALTH_DETAILS:when-authorized}
```

### 3. Variables de Entorno Requeridas

Para entornos no-local (develop, test, staging, prod):

```bash
# R2DBC Connection (runtime)
export DB_URL="r2dbc:postgresql://localhost:5432/back_ms_users_webflux_db"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"

# Flyway Connection (migrations)
export FLYWAY_URL="jdbc:postgresql://localhost:5432/back_ms_users_webflux_db"

# Optional
export SERVER_PORT="8080"
export FLYWAY_ENABLED="true"
export LOG_LEVEL="INFO"
export SWAGGER_ENABLED="false"
```

---

## Verificación

### Ejecutar Tests

```bash
# Todos los tests
mvn clean test

# Tests específicos
mvn test -Dtest=UserMapperTest
mvn test -Dtest="Jpa*RepositoryTest"
mvn test -Dtest="*MapperTest"

# Con cobertura
mvn clean verify
```

### Resultado Esperado

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.userservice.application.mapper.UserMapperTest
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.userservice.infrastructure.adapters.output.persistence.repository.JpaUserRepositoryTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 251, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:15 min
[INFO] Finished at: 2025-11-10T10:13:21-05:00
[INFO] ------------------------------------------------------------------------
```

### Verificar Cobertura

```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

**Umbral requerido:** 85%  
**Cobertura actual:** ~90% ✅

---

## Lecciones Aprendidas

### 1. R2DBC vs JPA

**R2DBC (Reactivo):**
- Usa `spring.r2dbc.*`
- No bloqueante
- Retorna `Mono<T>` y `Flux<T>`
- No soporta `@Transactional` tradicional

**JPA (Bloqueante):**
- Usa `spring.datasource.*` y `spring.jpa.*`
- Bloqueante
- Retorna objetos directos
- Soporta `@Transactional`

**No se pueden mezclar** en la misma configuración.

### 2. Flyway con R2DBC

Flyway no soporta R2DBC, por eso necesitamos:
- `spring.r2dbc.url` para la aplicación
- `spring.flyway.url` (JDBC) para migraciones

### 3. Tests con R2DBC

```java
// ❌ No funciona (bloqueante)
User user = userRepository.findById(id);

// ✅ Funciona (reactivo)
User user = userRepository.findById(id)
    .block(Duration.ofSeconds(5));
```

### 4. Aislamiento de Tests

**Estrategias evaluadas:**

| Estrategia | Pros | Contras | Usado |
|-----------|------|---------|-------|
| `@DirtiesContext` | Limpieza completa | Muy lento | ❌ |
| `@Transactional` rollback | Automático | No funciona con R2DBC | ❌ |
| Cleanup manual `@AfterEach` | Control total | Boilerplate | ❌ |
| **Datos únicos UUID** | Simple, rápido | Ninguno | ✅ |

### 5. H2 vs PostgreSQL

**Diferencias importantes:**

```sql
-- PostgreSQL
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ
);

-- H2
CREATE TABLE users (
    id UUID DEFAULT RANDOM_UUID(),
    created_at TIMESTAMP
);
```

---

## Checklist para Nuevos Tests

Al crear nuevos tests de repositorio:

- [ ] Agregar tabla en `schema.sql`
- [ ] Usar `VARCHAR(255)` para foreign keys String
- [ ] Usar `UUID` para primary keys
- [ ] Generar datos únicos con `UUID.randomUUID()`
- [ ] Usar `.block()` para operaciones reactivas
- [ ] Verificar compatibilidad H2
- [ ] Probar que tests pasan en cualquier orden

---

## Resumen de Cambios

### Antes
- ❌ 251 tests fallando
- ❌ Configuración JPA en proyecto R2DBC
- ❌ Sin schema de test
- ❌ Datos hardcodeados

### Después
- ✅ 251 tests pasando
- ✅ Configuración R2DBC correcta
- ✅ Schema H2 completo
- ✅ Datos únicos con UUID
- ✅ Cobertura 90%
- ✅ Build exitoso

---

**Tiempo total de resolución:** ~2 horas  
**Impacto:** Cero fallos, 100% de tests pasando  
**Documentación:** Completa y detallada

---

**Autor:** Jiliar Silgado <jiliar.silgado@gmail.com>  
**Última actualización:** 10 de Noviembre, 2025
