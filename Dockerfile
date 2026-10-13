# ==============================================================================
# KisanMitra AI — Backend Multi-Stage Dockerfile (Spring Boot 3.3.4 / Java 17)
# ==============================================================================

# Stage 1: Build application JAR using Maven and Temurin JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Layer cache for Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal, secure JRE 17 runtime container
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install curl for container health check probes
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Non-root user execution for principle of least privilege
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=builder /workspace/target/*.jar /app/app.jar
RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Healthcheck probe using Spring Boot Actuator
HEALTHCHECK --interval=20s --timeout=5s --retries=5 --start-period=35s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
