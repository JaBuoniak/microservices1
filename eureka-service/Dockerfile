# Build:
#   docker build -f eureka-service/Dockerfile -t eureka-service .
# Run:
#   docker run -d --name eureka-service-app --network microservices1_default -p 8761:8761 eureka-service

# Build stage
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Copy configuration files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY eureka-service/build.gradle ./eureka-service/

# Cache dependencies
RUN ./gradlew :eureka-service:dependencies --no-daemon

# Copy source code
COPY eureka-service/src ./eureka-service/src

# Build JAR
RUN ./gradlew :eureka-service:assemble --no-daemon -x test

# Production Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR
COPY --from=build /app/eureka-service/build/libs/*.jar app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]
