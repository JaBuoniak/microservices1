# Build:
#   docker build -f resource-service/Dockerfile -t resource-service .
# Run:
#   docker run -d --name resource-service-app --network microservices1_default -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://resource-db:5432/resource_db -e SONG_SERVICE_URL=http://song-service-app:8081/songs resource-service

# Build stage
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Copy configuration files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY resource-service/build.gradle ./resource-service/

# Cache dependencies
RUN ./gradlew :resource-service:dependencies --no-daemon

# Copy source code
COPY resource-service/src ./resource-service/src

# Build JAR
RUN ./gradlew :resource-service:assemble --no-daemon -x test

# Production Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR
COPY --from=build /app/resource-service/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
