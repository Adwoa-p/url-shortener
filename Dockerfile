# === Stage 1: Build ===
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only what Maven needs
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

# Build the project
RUN ./mvnw clean package -DskipTests


# === Stage 2: Run ===
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the JAR from the builder stage, not from host
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
