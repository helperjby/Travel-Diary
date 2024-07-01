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

# Copy the application properties to ensure server.address is set
COPY src/main/resources/application.properties ./src/main/resources/application.properties

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

# Copy the faqs.json file to the appropriate location within the classpath
COPY src/main/resources/faqs.json /app/resources/faqs.json

# Ensure the server listens on all network interfaces
RUN echo "server.address=0.0.0.0" >> /app/application.properties
RUN echo "server.port=8080" >> /app/application.properties

# Uncomment this line if you have the .aws directory in your context
# COPY .aws /root/.aws

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
