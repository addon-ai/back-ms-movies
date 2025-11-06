# 🚀 Flyway Integration with Spring WebFlux - Complete Guide

## 📋 Executive Summary

This document details the complete integration of **Flyway database migrations** with **Spring WebFlux** and **R2DBC**, solving the challenge of using traditional JDBC-based migrations in a fully reactive application.

**Result**: ✅ **Dual database connectivity** - JDBC for migrations, R2DBC for reactive operations.

---

## 🎯 Challenge Overview

### The Problem
Spring WebFlux applications use **R2DBC** for reactive database operations, but **Flyway requires JDBC** for migrations. This creates a compatibility challenge:

- **R2DBC**: Reactive, non-blocking database operations
- **Flyway**: Traditional JDBC-based migrations
- **Challenge**: How to use both in the same application

### The Solution
**Dual Database Configuration**: Use JDBC for migrations and R2DBC for application operations.

---

## 🛠️ Implementation Details

### 1. Dependencies Configuration

#### ✅ Added Maven Dependencies
```xml
<!-- R2DBC PostgreSQL Driver (Reactive) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
    <version>1.0.4.RELEASE</version>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL JDBC Driver (For Flyway) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Flyway Core -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Flyway PostgreSQL Support -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>10.10.0</version>
</dependency>
```

### 2. Application Configuration

#### ✅ Dual Database URLs in `application.yml`
```yaml
spring:
  # R2DBC Configuration (Reactive Operations)
  r2dbc:
    url: ${DB_URL:r2dbc:postgresql://localhost:5432/back-ms-users-webflux_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password123}
  
  # Flyway Configuration (JDBC Migrations)
  flyway:
    url: ${FLYWAY_URL:jdbc:postgresql://localhost:5432/back-ms-users-webflux_db}
    user: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password123}
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    enabled: true
```

### 3. Custom Flyway Configuration

#### ✅ FlywayConfiguration.java
```java
@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(flywayUrl, flywayUser, flywayPassword)
                .locations(flywayLocations)
                .baselineOnMigrate(baselineOnMigrate)
                .validateOnMigrate(validateOnMigrate)
                .load();
    }
}
```

**Key Features**:
- **Conditional Configuration**: Only runs when Flyway is enabled
- **Automatic Migration**: Runs migrations on application startup
- **Environment Variables**: Supports different environments
- **Validation**: Ensures migration integrity

---

## 📁 Database Schema Design

### Migration Files Structure
```
src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_locations_table.sql
├── V3__Create_countries_table.sql
├── V4__Create_regions_table.sql
├── V5__Create_cities_table.sql
├── V6__Create_neighborhoods_table.sql
└── V7__Insert_sample_data.sql
```

### 1. Users Table (V1)
```sql
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);
```

**Features**:
- **UUID Primary Key**: Distributed system friendly
- **Unique Constraints**: Username and email uniqueness
- **Optimized Indexes**: Fast lookups on email, username, status
- **Audit Fields**: Created and updated timestamps

### 2. Location Hierarchy Tables (V2-V6)

#### Countries → Regions → Cities → Neighborhoods
```sql
-- Countries (V3)
CREATE TABLE countries (
    country_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(3) NOT NULL UNIQUE,
    iso_code VARCHAR(2) NOT NULL UNIQUE,
    phone_code VARCHAR(10),
    currency VARCHAR(3)
);

-- Regions (V4) 
CREATE TABLE regions (
    region_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_regions_country FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

-- Cities (V5)
CREATE TABLE cities (
    city_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region_id VARCHAR(36) NOT NULL,
    population INTEGER,
    CONSTRAINT fk_cities_region FOREIGN KEY (region_id) REFERENCES regions(region_id)
);

-- Neighborhoods (V6)
CREATE TABLE neighborhoods (
    neighborhood_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city_id VARCHAR(36) NOT NULL,
    postal_code VARCHAR(20),
    CONSTRAINT fk_neighborhoods_city FOREIGN KEY (city_id) REFERENCES cities(city_id)
);
```

**Features**:
- **Hierarchical Structure**: Country → Region → City → Neighborhood
- **Foreign Key Constraints**: Data integrity enforcement
- **Composite Indexes**: Optimized hierarchical searches
- **Unique Constraints**: Name uniqueness per parent entity

### 3. Locations Table (V2)
```sql
CREATE TABLE locations (
    location_id VARCHAR(36) PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    city VARCHAR(100),
    neighborhood VARCHAR(100),
    address VARCHAR(255),
    postal_code VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);
```

**Features**:
- **Flexible Structure**: Denormalized for performance
- **Geographic Coordinates**: Latitude/longitude support
- **Hierarchical Indexes**: Fast location-based searches
- **Status Management**: Active/inactive locations

### 4. Sample Data (V7)
```sql
-- Sample countries, regions, cities, neighborhoods, users, and locations
INSERT INTO countries (country_id, name, code, iso_code, phone_code, currency) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Colombia', 'COL', 'CO', '+57', 'COP'),
('550e8400-e29b-41d4-a716-446655440002', 'United States', 'USA', 'US', '+1', 'USD');
-- ... more sample data
```

**Features**:
- **Real Data**: Colombian and US sample data
- **Conflict Handling**: `ON CONFLICT DO NOTHING` for idempotency
- **Complete Hierarchy**: Countries → Regions → Cities → Neighborhoods
- **Test Users**: Ready-to-use sample users

---

## 🔄 Migration Workflow

### Startup Process
1. **Application Starts** → Spring Boot initializes
2. **Flyway Configuration** → FlywayConfiguration bean created
3. **JDBC Connection** → Flyway connects using JDBC URL
4. **Migration Execution** → All pending migrations run
5. **R2DBC Initialization** → Reactive database connections established
6. **Application Ready** → WebFlux endpoints available

### Migration Execution Order
```
V1__Create_users_table.sql          ✅ Users table
V2__Create_locations_table.sql      ✅ Locations table  
V3__Create_countries_table.sql      ✅ Countries table
V4__Create_regions_table.sql        ✅ Regions table (FK to countries)
V5__Create_cities_table.sql         ✅ Cities table (FK to regions)
V6__Create_neighborhoods_table.sql  ✅ Neighborhoods table (FK to cities)
V7__Insert_sample_data.sql          ✅ Sample data insertion
```

---

## 🌍 Environment Configuration

### Development Environment
```yaml
# Local development
spring:
  flyway:
    url: jdbc:postgresql://localhost:5432/back-ms-users-webflux_db
    user: postgres
    password: password123
```

### Docker Environment
```yaml
# Docker Compose - PostgreSQL Service (Line 14 creates database)
services:
  postgres:
    image: postgres:15.0-alpine
    environment:
      POSTGRES_DB: back-ms-users-webflux_db  # ← THIS LINE creates database automatically
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password123
  
  app:
    environment:
      - DB_URL=r2dbc:postgresql://postgres:5432/back-ms-users-webflux_db
      - FLYWAY_URL=jdbc:postgresql://postgres:5432/back-ms-users-webflux_db
      - DB_USERNAME=postgres
      - DB_PASSWORD=password123
    depends_on:
      postgres:
        condition: service_healthy
```

### Production Environment
```yaml
# Production with environment variables
spring:
  flyway:
    url: ${FLYWAY_URL}
    user: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

---

## 🔧 Advanced Configuration Options

### Flyway Properties
```yaml
spring:
  flyway:
    enabled: true                    # Enable/disable Flyway
    baseline-on-migrate: true        # Create baseline for existing DB
    validate-on-migrate: true       # Validate migrations on startup
    locations: classpath:db/migration # Migration files location
    table: flyway_schema_history     # Migration history table name
    schemas: public                  # Target schema
    placeholder-replacement: true    # Enable placeholder replacement
    placeholders:                    # Custom placeholders
      app.name: back-ms-users-webflux
```

### Conditional Migration
```java
@ConditionalOnProperty(
    name = "spring.flyway.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
```

---

## 🧪 Testing Considerations

### Test Configuration
```yaml
# application-test.yml
spring:
  flyway:
    enabled: true
    clean-disabled: false  # Allow clean in tests
    locations: 
      - classpath:db/migration
      - classpath:db/testdata
```

### Test Migrations
```
src/test/resources/db/testdata/
├── V100__Test_users.sql
├── V101__Test_locations.sql
└── V102__Test_cleanup.sql
```

### Integration Tests
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.locations=classpath:db/migration,classpath:db/testdata"
})
class DatabaseIntegrationTest {
    // Test reactive operations after migrations
}
```

---

## 📊 Performance Considerations

### Migration Performance
- **Parallel Execution**: Flyway runs migrations sequentially for consistency
- **Index Creation**: Indexes created after data insertion for better performance
- **Batch Operations**: Large data insertions use batch processing
- **Connection Pooling**: Separate connection pool for migrations

### Runtime Performance
- **R2DBC Connection Pool**: Reactive connection pooling
- **Index Optimization**: Strategic indexes for common queries
- **Query Performance**: Optimized for reactive operations

### Monitoring
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,flyway
  endpoint:
    flyway:
      enabled: true  # Expose Flyway migration info
```

---

## 🚨 Troubleshooting Guide

### Common Issues

#### 1. **Migration Fails on Startup**
```
Error: Migration checksum mismatch
```
**Solution**: 
```bash
# Reset Flyway state (development only)
DELETE FROM flyway_schema_history WHERE version = 'X';
```

#### 2. **Database Does Not Exist**
```
Error: database "back-ms-users-webflux_db" does not exist
```
**Solution**: Flyway cannot create databases. Use one of these approaches:

**Option A - Docker Compose (Recommended)**:
```yaml
services:
  postgres:
    environment:
      POSTGRES_DB: back-ms-users-webflux_db  # Auto-creates database
```

**Option B - Manual Creation**:
```sql
CREATE DATABASE "back-ms-users-webflux_db";
```

**Option C - Application Configuration**:
```yaml
spring:
  flyway:
    create-schemas: true  # Creates schema, not database
```

#### 3. **R2DBC Connection Issues**
```
Error: Cannot connect to r2dbc:postgresql://...
```
**Solution**: Verify R2DBC URL format and network connectivity

#### 4. **Flyway JDBC Connection Issues**
```
Error: Cannot connect to jdbc:postgresql://...
```
**Solution**: Ensure PostgreSQL JDBC driver is in classpath

#### 5. **Schema Conflicts**
```
Error: Table already exists
```
**Solution**: Use `CREATE TABLE IF NOT EXISTS` in migrations

#### 6. **Maven Dependency Version Issues**

**Error A**: `'dependencies.dependency.version' for org.postgresql:postgresql:jar is missing`
```
[ERROR] 'dependencies.dependency.version' for org.postgresql:postgresql:jar is missing
```
**Solution**: Add explicit PostgreSQL version:
```xml
<!-- In properties -->
<postgresql.version>42.7.3</postgresql.version>

<!-- In dependencies -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${postgresql.version}</version>
    <scope>runtime</scope>
</dependency>
```

**Error B**: `'dependencies.dependency.version' for org.flywaydb:flyway-database-postgresql:jar is missing`
**Solution**: Specify explicit version for flyway-database-postgresql:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>10.10.0</version>
</dependency>
```

### Debug Configuration
```yaml
logging:
  level:
    org.flywaydb: DEBUG
    io.r2dbc.postgresql: DEBUG
    org.springframework.r2dbc: DEBUG
```

---

## 🎯 Best Practices

### Migration Best Practices
1. **Versioned Migrations**: Use V{version}__{description}.sql format
2. **Idempotent Scripts**: Use IF NOT EXISTS, ON CONFLICT DO NOTHING
3. **Rollback Strategy**: Plan for rollback scenarios
4. **Data Migration**: Separate structure and data migrations
5. **Testing**: Test migrations on copy of production data

### R2DBC Best Practices
1. **Connection Pooling**: Configure appropriate pool sizes
2. **Reactive Patterns**: Use Mono/Flux consistently
3. **Error Handling**: Implement proper reactive error handling
4. **Backpressure**: Handle backpressure in data streams
5. **Transaction Management**: Use reactive transactions

### Security Best Practices
1. **Environment Variables**: Never hardcode credentials
2. **Connection Encryption**: Use SSL for database connections
3. **Least Privilege**: Use minimal database permissions
4. **Audit Logging**: Log migration activities
5. **Backup Strategy**: Backup before major migrations

---

## 📈 Benefits Achieved

### ✅ **Database Management**
- **Automated Migrations**: Zero-downtime deployments
- **Version Control**: Database schema in source control
- **Environment Consistency**: Same schema across environments
- **Rollback Capability**: Safe migration rollbacks

### ✅ **Reactive Performance**
- **Non-blocking Operations**: Full reactive stack
- **High Concurrency**: Efficient resource utilization
- **Backpressure Support**: Built-in flow control
- **Scalability**: Ready for high-load scenarios

### ✅ **Development Experience**
- **Local Development**: Easy local setup with sample data
- **Testing**: Consistent test data setup
- **CI/CD Integration**: Automated database updates
- **Monitoring**: Migration status visibility

---

## 🚀 Deployment Strategy

### Development Deployment
```bash
# Local development
mvn spring-boot:run
# Flyway runs automatically on startup
```

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d
# Migrations run automatically in container
```

### Production Deployment
```bash
# Production deployment with environment variables
export FLYWAY_URL="jdbc:postgresql://prod-db:5432/app_db"
export DB_URL="r2dbc:postgresql://prod-db:5432/app_db"
export DB_USERNAME="app_user"
export DB_PASSWORD="secure_password"

java -jar app.jar
```

### CI/CD Pipeline Integration
```yaml
# GitHub Actions example
- name: Run Database Migrations
  run: |
    mvn flyway:migrate \
      -Dflyway.url=${{ secrets.DB_URL }} \
      -Dflyway.user=${{ secrets.DB_USER }} \
      -Dflyway.password=${{ secrets.DB_PASSWORD }}
```

---

## 🎉 Conclusion

The integration of **Flyway with Spring WebFlux** provides the best of both worlds:

### ✅ **Achieved Goals**
- **Database Migrations**: Automated, versioned, reliable
- **Reactive Operations**: Full non-blocking database operations
- **Environment Consistency**: Same schema across all environments
- **Developer Experience**: Easy local development with sample data
- **Production Ready**: Scalable, monitored, secure

### 🔮 **Future Enhancements**
- **Migration Rollback**: Implement automated rollback strategies
- **Blue-Green Deployments**: Zero-downtime migration strategies
- **Multi-tenant Support**: Schema per tenant migrations
- **Performance Monitoring**: Advanced migration performance metrics
- **Automated Testing**: Database migration testing in CI/CD

### 📊 **Final Architecture**
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Application   │    │     Flyway       │    │   PostgreSQL    │
│   (WebFlux)     │    │   (Migrations)   │    │   Database      │
│                 │    │                  │    │                 │
│  R2DBC Driver   │◄──►│  JDBC Driver     │◄──►│   Port 5432     │
│  (Reactive)     │    │  (Traditional)   │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

**Result**: A robust, scalable, reactive microservice with professional database management capabilities.

---

*Document generated: 2025-11-06*  
*Project: back-ms-users-webflux v1.0.0*  
*Status: ✅ **FLYWAY + WEBFLUX INTEGRATED***