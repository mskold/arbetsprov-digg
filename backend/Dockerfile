# Bygg-steg
FROM docker.io/library/maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean test package


# Kör-steg
FROM docker.io/library/eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ quarkus-app/
CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]
EXPOSE 8080