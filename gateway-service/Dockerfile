# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY gateway-service/build.gradle ./gateway-service/

# Cache dependencies
RUN gradle :gateway-service:dependencies --no-daemon

# Copy source code
COPY gateway-service/src ./gateway-service/src

# Build JAR
RUN gradle :gateway-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/gateway-service/build/libs/*.jar app.jar

EXPOSE 8088

CMD ["java", "-jar", "app.jar"]

