# Multi-stage build for optimized image size

# Stage 1: Build stage
FROM gradle:9.0.0-jdk21 AS build

WORKDIR /build

# Copy entire project directory
COPY . .

# Make gradlew executable and build the fat JAR
RUN chmod +x gradlew && ./gradlew fatJar --no-daemon

# Stage 2: Runtime stage
FROM amazoncorretto:22

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /build/libs/*.jar ./simple-tgbot.jar

# Set permissions
RUN chmod +x simple-tgbot.jar

# Run the application
ENTRYPOINT ["java", "-jar", "simple-tgbot.jar"]
