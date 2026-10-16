# Build:
#   docker build -f song-service/Dockerfile -t song-service .
# Run:
#   docker run -d --name song-service-app --network microservices1_default -p 8081:8081 -e SPRING_DATASOURCE_URL=jdbc:postgresql://song-db:5432/song_db song-service

# Build stage
FROM gradle:8.14-jdk17-alpine AS build
WORKDIR /app

# Copy configuration files
# COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY song-service/build.gradle ./song-service/

# Cache dependencies
# RUN ./gradlew :song-service:dependencies --no-daemon
RUN gradle :song-service:dependencies --no-daemon

# Copy source code
COPY song-service/src ./song-service/src

# Build JAR
# RUN ./gradlew :song-service:assemble --no-daemon -x test
RUN gradle :song-service:assemble --no-daemon

# Production Stage 
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR
COPY --from=build /app/song-service/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]