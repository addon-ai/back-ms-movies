# 👥 back-ms-movies

🚀 Microservice for movie rental management

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
│   └── MovieServiceApplication.java
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

### 🔧 Development Mode
```bash
mvn spring-boot:run
```

### 🏭 Production Build
```bash
mvn clean package
java -jar target/movie-service-1.0.0.jar
```

### 🧪 Running Tests
```bash
# Unit tests
mvn test

# With coverage report
mvn test jacoco:report

# Coverage check (85% threshold)
mvn verify
```

## ⚙️ Configuration

### 🔧 Development (application.properties)
- **Port:** 8081
- **Database:** H2 in-memory
- **Security:** Disabled CSRF, permissive access
- **H2 Console:** Enabled

### 🏭 Production
- **Database:** PostgreSQL
- **Security:** Full security enabled
- **Profiles:** Use `spring.profiles.active=prod`

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
    <artifactId>movie-service</artifactId>
    <version>1.0.0</version>
</dependency>
```