# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY song-service/build.gradle ./song-service/

# Cache dependencies
RUN gradle :song-service:dependencies --no-daemon

# Copy source code
COPY song-service/src ./song-service/src

# Build JAR
RUN gradle :song-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/song-service/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
