# Hexagonal Architecture Templates

Spring Boot service templates with Hexagonal Architecture (Ports and Adapters).

## Available Templates

- **back-ms-users**: User management microservice (Spring Boot)
- **back-ms-movies**: Movie management microservice (Spring Boot)
- **back-ms-users-webflux**: User management microservice (WebFlux)
- **back-ms-movies-webflux**: Movie management microservice (WebFlux)

## Architecture

All templates follow Hexagonal Architecture principles with three main layers:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and DTOs
- **Infrastructure Layer**: Controllers, repositories, and external adapters
