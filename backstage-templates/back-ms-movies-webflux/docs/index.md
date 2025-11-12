# 👥 back-ms-movies-webflux

🚀 Reactive microservice for movie rental management with Spring WebFlux

**Version:** 1.0.0  
**Author:** Jiliar Silgado <jiliar.silgado@gmail.com>  
**License:** MIT

## 📋 Overview

This is a Spring Boot application following Hexagonal Architecture (Ports and Adapters) principles, generated from Smithy service definitions.

## 🛠️ Technology Stack

- **☕ Java:** 21
- **🍃 Spring Boot:** 3.2.5
- **🗄️ Database:** H2 (development), PostgreSQL (production)
- **🏗️ Architecture:** Hexagonal (Ports and Adapters)
- **🔄 Mapping:** MapStruct 1.5.5.Final
- **🧪 Testing:** JUnit 5, Spring Boot Test
- **📊 Code Coverage:** Jacoco (85% threshold)
- **📚 Documentation:** SpringDoc OpenAPI 3
- **🔨 Build Tool:** Maven
- **⚙️ CI/CD:** GitHub Actions

## 📁 Project Structure

```
src/
├── main/java/com.example.movieservice/
│   ├── application/          # Application Layer (Use Cases)
│   │   ├── port/            # Input/Output Ports (Interfaces)
│   │   └── service/         # Application Services
│   ├── domain/              # Domain Layer (Business Logic)
│   │   ├── model/           # Domain Models/Entities
│   │   └── exception/       # Domain Exceptions
│   ├── infrastructure/      # Infrastructure Layer
│   │   ├── adapter/         # Adapters (Controllers, Repositories)
│   │   │   ├── input/       # Input Adapters (REST Controllers)
│   │   │   └── output/      # Output Adapters (JPA Repositories)
│   │   ├── config/          # Configuration Classes
│   │   ├── entity/          # JPA Entities
│   │   └── mapper/          # MapStruct Mappers
│   └── MovieServiceWebFluxApplication.java
└── test/                    # Test Classes
```

## 🏛️ Hexagonal Architecture Layers

### 🎯 Domain Layer
- **Purpose:** Contains business logic and domain models
- **Dependencies:** No external dependencies
- **Components:** Domain models, business rules, domain exceptions

### 🔧 Application Layer
- **Purpose:** Orchestrates domain objects and coordinates application flow
- **Dependencies:** Only depends on domain layer
- **Components:** Use cases, application services, ports (interfaces)

### 🏗️ Infrastructure Layer
- **Purpose:** Implements technical details and external integrations
- **Dependencies:** Depends on application and domain layers
- **Components:** REST controllers, JPA repositories, configurations, mappers

## 📖 API Documentation
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **H2 Console:** http://localhost:8081/h2-console
- **Actuator:** http://localhost:8081/actuator

## 🔄 Development Workflow

### 🌿 Branch Strategy

- **main:** Production-ready code, protected branch
- **develop:** Integration branch for features
- **staging:** Pre-production testing
- **test:** Testing environment
- **feature/*:** Feature development branches
- **hotfix/*:** Emergency fixes (only branch allowing direct push)

### 📋 GitFlow Rules

1. **No direct push** to `main`, `develop`, `staging`, `test`
2. **All changes** must go through Pull Requests
3. **Only hotfix branches** allow direct push (emergencies)
4. **Tags** trigger automatic package publishing

### 🚀 CI/CD Pipeline

#### ⚡ Triggers
- **Pull Requests:** to `main`, `develop`, `staging`, `test`
- **Push:** to `hotfix/*` branches
- **Tags:** `v*` pattern (e.g., v1.0.0)

#### 📝 Pipeline Steps
1. **Build & Test:** Unit tests, code coverage (85% threshold)
2. **Package:** Create JAR artifact
3. **Publish:** Deploy to GitHub Packages (only on tags)

#### 🚀 Publishing Releases
```bash
# Create and push tag
git tag v1.0.0
git push origin v1.0.0

# This automatically:
# 1. Runs all tests
# 2. Checks code coverage
# 3. Publishes to GitHub Packages
```

## ▶️ Running the Application

### 🏠 Local Development (Default)
```bash
# Uses application-local.properties with hardcoded values
mvn spring-boot:run
# or
java -jar target/movie-service-webflux-1.0.0.jar
```

### 🌍 Environment-Specific Profiles

#### Available Profiles
- **develop**: develop environment with environment variables
- **test**: test environment with environment variables
- **staging**: staging environment with environment variables
- **master**: master environment with environment variables

#### Running with Specific Profile
```bash
# Development environment
mvn spring-boot:run -Dspring-boot.run.profiles=develop

# Test environment  
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Staging environment
mvn spring-boot:run -Dspring-boot.run.profiles=staging

# Production environment
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Using JAR with Profile
```bash
# Build JAR
mvn clean package

# Run with specific profile
java -jar target/movie-service-webflux-1.0.0.jar --spring.profiles.active=develop
java -jar target/movie-service-webflux-1.0.0.jar --spring.profiles.active=test
java -jar target/movie-service-webflux-1.0.0.jar --spring.profiles.active=staging
java -jar target/movie-service-webflux-1.0.0.jar --spring.profiles.active=prod
```

#### Environment Variables (Non-Local Profiles)
For profiles other than `local`, set these environment variables:
```bash
# Database Configuration
export DB_URL="jdbc:postgresql://localhost:5432/back-ms-movies-webflux_db"
export DB_USERNAME="your_db_user"
export DB_PASSWORD="your_db_password"

# Optional Configuration (with defaults)
export SERVER_PORT="8080"
export JPA_DDL_AUTO="validate"
export JPA_SHOW_SQL="false"
export LOG_LEVEL="INFO"
export SWAGGER_ENABLED="false"
```

### 🐳 Docker Development

#### Local Development with Docker Compose
```bash
# Start PostgreSQL + Application (local profile)
docker-compose up -d

# View logs
docker-compose logs -f back-ms-movies-webflux-app

# Stop services
docker-compose down

# Start with pgAdmin for database management
docker-compose --profile tools up -d
```

#### Docker Services
- **Application**: http://localhost:8080 (uses `local` profile)
- **PostgreSQL**: localhost:5432 (postgres/password123)
- **pgAdmin**: http://localhost:5050 (admin@back-ms-movies-webflux.local/admin123)

#### Production Docker Build
```bash
# Build optimized image
docker build -t back-ms-movies-webflux:latest .

# Run with environment variables
docker run -d \
  --name back-ms-movies-webflux \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:postgresql://your-db:5432/back-ms-movies-webflux_db" \
  -e DB_USERNAME="your_user" \
  -e DB_PASSWORD="your_password" \
  back-ms-movies-webflux:latest
```

### 🧪 Running Tests
```bash
# Unit tests (uses application-test.properties)
mvn test

# With coverage report
mvn test jacoco:report

# Coverage check (85% threshold)
mvn verify

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

## ⚙️ Configuration

### 🏠 Local Profile (`application-local.properties`)
- **Port:** 8080
- **Database:** PostgreSQL (localhost:5432/back-ms-movies-webflux_db)
- **Credentials:** postgres/password123 (hardcoded)
- **JPA:** DDL auto-update, SQL logging enabled
- **Swagger:** Enabled for development
- **Logging:** DEBUG level

### 🌍 Environment Profiles (Variables Required)

#### develop Profile (`application-develop.properties`)
- **Database:** Uses `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`
- **JPA:** DDL validate mode, SQL logging disabled
- **Swagger:** Disabled by default
- **Logging:** INFO level
- **Security:** Production-ready settings

#### test Profile (`application-test.properties`)
- **Database:** Uses `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`
- **JPA:** DDL validate mode, SQL logging disabled
- **Swagger:** Disabled by default
- **Logging:** INFO level
- **Security:** Production-ready settings

#### staging Profile (`application-staging.properties`)
- **Database:** Uses `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`
- **JPA:** DDL validate mode, SQL logging disabled
- **Swagger:** Disabled by default
- **Logging:** INFO level
- **Security:** Production-ready settings

#### master Profile (`application-master.properties`)
- **Database:** Uses `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`
- **JPA:** DDL validate mode, SQL logging disabled
- **Swagger:** Disabled by default
- **Logging:** INFO level
- **Security:** Production-ready settings


### 📋 Profile Selection Priority
1. Command line: `--spring.profiles.active=profile`
2. Environment variable: `SPRING_PROFILES_ACTIVE=profile`
3. Default: `local` (from application.properties)

### 🐳 Docker Configuration

#### Dockerfile Features
- **Multi-stage build**: Optimized for production
- **Non-root user**: Security best practices
- **Health checks**: Container health monitoring
- **JVM optimization**: Container-aware settings
- **Layer caching**: Efficient builds

#### Docker Compose (Local Only)
- **PostgreSQL 15**: Local database with persistent storage
- **pgAdmin**: Database management interface (optional)
- **Health checks**: Service dependency management
- **Local profile**: Uses hardcoded development values
- **Volume mounts**: Logs and database persistence

## ✅ Code Quality

- **Coverage Threshold:** 85%
- **Excluded from Coverage:** DTOs, Entities, Configuration classes
- **MapStruct:** Auto-generated Spring beans
- **Lombok:** Reduces boilerplate code

## 🤝 Contributing

1. Create feature branch from `develop`
2. Implement changes following hexagonal architecture
3. Ensure tests pass and coverage ≥ 85%
4. Create Pull Request to target branch
5. Wait for CI/CD validation
6. Merge after approval

## 🚀 Deployment

### 📦 GitHub Packages
Artifacts are automatically published to GitHub Packages when tags are created.

### 📥 Consuming the Package
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>movie-service-webflux</artifactId>
    <version>1.0.0</version>
</dependency>
```