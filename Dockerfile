# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Eén-container build: React-frontend wordt als statische resources door de
# Spring Boot-backend geserveerd. Frontend en /api draaien op dezelfde origin
# (poort 10040), dus geen CORS/dev-proxy nodig in productie.
# ---------------------------------------------------------------------------

# 1) Frontend bouwen
FROM node:20-alpine AS frontend
WORKDIR /fe
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# 2) Backend bouwen (jar inclusief de frontend-build)
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/src ./src
# de gebouwde frontend als statische resources meeverpakken
COPY --from=frontend /fe/dist/ ./src/main/resources/static/
RUN mvn -q -B -DskipTests package

# 3) Runtime-image
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SERVER_PORT=10040 \
    SPRING_PROFILES_ACTIVE=stub
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 10040
ENTRYPOINT ["java", "-jar", "app.jar"]
