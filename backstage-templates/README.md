# Hexagonal Architecture Templates

Spring Boot service templates with Hexagonal Architecture (Ports and Adapters).

## Available Templates

- **back-ms-users**: Microservice for users management (Spring Boot)
- **back-ms-movies**: Microservice for movie rental management (Spring Boot)
- **back-ms-users-webflux**: Reactive microservice for users management with Spring WebFlux (WebFlux)
- **back-ms-movies-webflux**: Reactive microservice for movie rental management with Spring WebFlux (WebFlux)

## Architecture

All templates follow Hexagonal Architecture principles with three main layers:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and DTOs
- **Infrastructure Layer**: Controllers, repositories, and external adapters
