# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY eureka-service/build.gradle ./eureka-service/

# Cache dependencies
RUN gradle :eureka-service:dependencies --no-daemon

# Copy source code
COPY eureka-service/src ./eureka-service/src

# Build JAR
RUN gradle :eureka-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/eureka-service/build/libs/*.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]

