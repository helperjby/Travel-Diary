# First stage: Build the application
FROM gradle:8.5-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and the build scripts
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Ensure the Gradle wrapper has execution permissions
RUN chmod +x gradlew

# Copy the application source code
COPY src ./src

# Build the application
RUN ./gradlew clean build -x test

# Second stage: Run the application
FROM openjdk:17-jdk-alpine

# Add Maintainer Info
LABEL maintainer="belicia1226@gmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set the working directory
WORKDIR /app

# Copy the .env file to the working directory (if needed)
COPY .env .env

# Copy the packaged jar from the first stage
COPY --from=build /app/build/libs/*.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
