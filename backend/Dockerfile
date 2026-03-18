# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Cache dependencies before copying source (faster layer-cache on rebuilds)
COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:25-jre-noble
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Default port matches application.yaml; Render overrides via $PORT env var.
EXPOSE 8081

# -XX:+UseContainerSupport + MaxRAMPercentage keeps the JVM within the
# container memory limit (important on Render's free 512 MB tier).
# --spring.datasource.url is assembled from the individual DB_* vars that
# the Render Blueprint injects, avoiding the postgres:// -> jdbc: conversion.
CMD ["sh", "-c", \
  "exec java \
     -XX:+UseContainerSupport \
     -XX:MaxRAMPercentage=75.0 \
     -jar app.jar \
     --server.port=${PORT:-8081} \
     --spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"]
