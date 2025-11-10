# Test Errors Resolution

## 📋 Error Summary

**Date:** 2024
**Author:** Jiliar Silgado <jiliar.silgado@gmail.com>

### Error Description

All tests were failing with `IllegalStateException: Failed to load ApplicationContext` errors. The tests affected were:

- **Mapper Tests:** UserMapperTest, RegionMapperTest, NeighborhoodMapperTest, LocationMapperTest, CountryMapperTest, CityMapperTest
- **Repository Tests:** JpaUserRepositoryTest, JpaRegionRepositoryTest, JpaNeighborhoodRepositoryTest, JpaLocationRepositoryTest, JpaCountryRepositoryTest, JpaCityRepositoryTest

### Root Cause Analysis

The project is built with **Spring WebFlux + R2DBC** (reactive stack), but the configuration files were incorrectly set up for **Spring MVC + JPA/JDBC** (blocking stack).

#### Specific Issues:

1. **Configuration Mismatch:**
   - Application configuration files (`application-test.yml`, `application-prod.yml`, `application-develop.yml`, `application-staging.yml`) used JPA/JDBC properties:
     ```yaml
     spring:
       datasource:
         url: ${DB_URL}
         driver-class-name: org.postgresql.Driver
       jpa:
         database-platform: org.hibernate.dialect.PostgreSQLDialect
     ```
   - But the project dependencies and code use R2DBC (reactive database connectivity)

2. **Test Configuration Missing:**
   - No `src/test/resources/application-test.yml` existed
   - Tests tried to load full application context with production database configuration
   - `@SpringBootTest` in mapper tests required database connection
   - `@DataR2dbcTest` in repository tests expected R2DBC configuration but found JPA properties

3. **Technology Stack Conflict:**
   - **pom.xml** includes: `spring-boot-starter-data-r2dbc`, `r2dbc-postgresql`, `r2dbc-h2`
   - **Configuration** expected: JPA/JDBC datasource properties
   - **Result:** Spring couldn't initialize the application context

## ✅ Solution Applied

### 1. Created Test Configuration

**File:** `src/test/resources/application-test.yml`

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  flyway:
    enabled: false
  sql:
    init:
      mode: never

logging:
  level:
    root: INFO
    org.springframework: WARN
```

**Purpose:**
- Uses H2 in-memory database with R2DBC driver for tests
- Disables Flyway migrations during tests
- Prevents SQL initialization scripts from running
- Reduces logging noise during test execution

### 2. Fixed Environment Configuration Files

#### application-test.yml
**Before:**
```yaml
spring:
  datasource:
    url: ${DB_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

**After:**
```yaml
spring:
  r2dbc:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  flyway:
    url: ${FLYWAY_URL}
    user: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

#### application-prod.yml, application-develop.yml, application-staging.yml
Applied the same R2DBC configuration pattern to all environment files.

### 3. Key Changes

| Aspect | Before | After |
|--------|--------|-------|
| Database Driver | JDBC (`org.postgresql.Driver`) | R2DBC (`r2dbc:postgresql://`) |
| Spring Module | `spring.datasource` + `spring.jpa` | `spring.r2dbc` + `spring.flyway` |
| Test Database | Required PostgreSQL | H2 in-memory |
| Flyway URL | Not specified | Separate JDBC URL for migrations |

## 🔍 Technical Details

### Why R2DBC Requires Different Configuration

1. **R2DBC is Reactive:**
   - Non-blocking database access
   - Uses reactive streams (Mono/Flux)
   - Different connection URL format: `r2dbc:postgresql://` vs `jdbc:postgresql://`

2. **Flyway Still Needs JDBC:**
   - Flyway doesn't support R2DBC yet
   - Requires separate JDBC connection for migrations
   - That's why we have both `spring.r2dbc.url` and `spring.flyway.url`

3. **Test Configuration:**
   - `@DataR2dbcTest` expects R2DBC configuration
   - `@SpringBootTest` loads full context including database beans
   - H2 supports both JDBC and R2DBC, perfect for testing

## 📝 Environment Variables Required

For non-local profiles (develop, test, staging, prod):

```bash
# R2DBC Connection (for application runtime)
export DB_URL="r2dbc:postgresql://localhost:5432/back_ms_users_webflux_db"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_db_password"

# Flyway Connection (for migrations)
export FLYWAY_URL="jdbc:postgresql://localhost:5432/back_ms_users_webflux_db"
```

## ✅ Verification

Run tests to verify the fix:

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=UserMapperTest

# Run with coverage
mvn clean verify
```

Expected result: All tests should pass successfully.

## 📚 Lessons Learned

1. **Match Configuration to Dependencies:** Always ensure configuration files match the actual dependencies in pom.xml
2. **Separate Test Configuration:** Tests should have their own lightweight configuration
3. **R2DBC vs JDBC:** These are fundamentally different - can't mix configuration properties
4. **Flyway with R2DBC:** Requires dual configuration (R2DBC for app, JDBC for migrations)

## 🔗 References

- [REPOSITORY_TEST_ERRORS_RESOLUTION.md](./REPOSITORY_TEST_ERRORS_RESOLUTION.md) - Follow-up repository test fixes
- [Spring Data R2DBC Documentation](https://spring.io/projects/spring-data-r2dbc)
- [R2DBC Specification](https://r2dbc.io/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
