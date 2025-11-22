FROM maven:3.9.6-eclipse-temurin-21 AS build

# Installer curl pour healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copier le JAR
COPY target/*.jar app.jar

EXPOSE 8088

CMD ["java", "-jar", "app.jar"]