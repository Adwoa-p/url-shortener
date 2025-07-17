FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/urlShortener-0.0.1-SNAPSHOT.jar app/urlShortener-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "urlShortener-0.0.1-SNAPSHOT.jar"]
