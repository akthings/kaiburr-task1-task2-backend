# Stage 1: Build the application (using a standard Maven image)
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# 1. Copy the POM file first for dependency caching
COPY pom.xml .

# 2. Download dependencies (uses the image's built-in 'mvn')
RUN mvn dependency:go-offline -B

# 3. Copy source code and perform the final compile/package
COPY src ./src
# Compile and package the application. Creates 'task-executor-backend-0.0.1-SNAPSHOT.jar'
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
WORKDIR /app

# 🚨 DEFINITIVE FIX: Copy the JAR using a partial name wildcard.
# This finds 'task-executor-backend-0.0.1-SNAPSHOT.jar' and copies it to 'app.jar'.
COPY --from=build /app/target/task-executor-backend*.jar app.jar 

# The entrypoint to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]