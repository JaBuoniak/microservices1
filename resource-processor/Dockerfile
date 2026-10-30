# Build stage
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

# Copy gradle configuration files for dependency caching
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY resource-processor/build.gradle ./resource-processor/

# Cache dependencies
RUN gradle :resource-processor:dependencies --no-daemon

# Copy source code
COPY resource-processor/src ./resource-processor/src

# Build JAR
RUN gradle :resource-processor:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR using wildcard
COPY --from=build /app/resource-processor/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]
