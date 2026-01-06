# =========================
# 1️⃣ Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (better Docker layer caching)
COPY pom.xml .

# Download dependencies only
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster, safer CI builds)
RUN mvn clean package -DskipTests


# =========================
# 2️⃣ Runtime stage
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Eureka port (documentation + compose support)
EXPOSE 8761

# Run with docker profile
ENTRYPOINT ["java", "-jar", "app.jar"]
