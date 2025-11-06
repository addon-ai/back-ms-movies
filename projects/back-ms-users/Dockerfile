FROM openjdk:21-jdk-slim AS build

LABEL maintainer="Jiliar Silgado <jiliar.silgado@gmail.com>"
LABEL version="1.0.0"
LABEL description="Microservice for users management"

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Create final image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar file
COPY --from=build /app/target/user-service-1.0.0.jar app.jar

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]