# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY storage-service/build.gradle ./storage-service/

# Cache dependencies
RUN gradle :storage-service:dependencies --no-daemon

# Copy source code
COPY storage-service/src ./storage-service/src

# Build JAR
RUN gradle :storage-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/storage-service/build/libs/*.jar app.jar

EXPOSE 8087

CMD ["java", "-jar", "app.jar"]

