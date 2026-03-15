# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ─────────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]