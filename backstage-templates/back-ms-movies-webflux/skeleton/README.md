{{=<% %>=}}# ${{ values.component_id }}

${{ values.description }}

## 🏛️ Architecture

This project follows **Hexagonal Architecture (Ports and Adapters)** principles with proper layer separation:

- **Domain Layer**: Pure business logic and entities
- **Application Layer**: Use cases, ports, and DTOs
- **Infrastructure Layer**: Adapters, controllers, and repositories

## 🚀 Tech Stack

- **Java**: ${{ values.javaVersion }}
- **Spring Boot**: ${{ values.springBootVersion }}
- **Database**: ${{ values.database }}
- **Build Tool**: Maven
- **Architecture**: Hexagonal (Ports & Adapters)

## 📋 Prerequisites

- Java ${{ values.javaVersion }}
- Maven 3.8+
- Docker (optional, for database)

## 🛠️ Setup

### 1. Clone the repository

```bash
git clone ${{ values.repoUrl }}
cd ${{ values.component_id }}
```

### 2. Configure database

Update `src/main/resources/application.yml` with your database credentials.

### 3. Build the project

```bash
mvn clean install
```

### 4. Run tests

```bash
mvn test
```

### 5. Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── ${{ values.java_package_path }}/
│   │       ├── domain/          # Business logic
│   │       ├── application/     # Use cases & DTOs
│   │       └── infrastructure/  # Adapters & Controllers
│   └── resources/
│       ├── application.yml
│       └── db/migration/        # Database migrations
└── test/
    └── java/                    # Test suite
```

## 🔧 Configuration

Key configuration files:

- `application.yml`: Main application configuration
- `pom.xml`: Maven dependencies and build configuration

## 🚢 Deployment

### Docker

```bash
# Build image
docker build -t ${{ values.component_id }}:latest .

# Run container
docker run -p 8080:8080 ${{ values.component_id }}:latest
```

## 📝 License

This project is licensed under the MIT License.

## 👥 Team

- **Owner**: ${{ values.owner }}
- **System**: ${{ values.system }}

## 🔗 Links

- [Repository](${{ values.repoUrl }})
- [Documentation](${{ values.repoUrl }}/blob/main/README.md)
<%={{ }}=%>
