# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY resource-service/build.gradle ./resource-service/

# Cache dependencies
RUN gradle :resource-service:dependencies --no-daemon

# Copy source code
COPY resource-service/src ./resource-service/src

# Build JAR
RUN gradle :resource-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/resource-service/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
