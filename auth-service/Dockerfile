# =========================
# 1️⃣ Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (enables Docker layer caching)
COPY pom.xml .

# Download dependencies only
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for CI / Docker safety)
RUN mvn clean package -DskipTests


# =========================
# 2️⃣ Runtime stage
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the final JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose auth-service port
EXPOSE 8081

# Start the service
ENTRYPOINT ["java", "-jar", "app.jar"]
