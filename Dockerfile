FROM maven:3.9.6-eclipse-temurin-21 AS build
LABEL authors="dvargas42"

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/target/events-*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]