# Dockerfile para Payment Service
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar
RUN ./mvnw clean package -DskipTests

# ==================== STAGE 2: Runtime ====================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Variables de entorno
ENV EUREKA_SERVER_URL=http://eureka-server:8761/eureka \
    POSTGRES_HOST=postgres-payment \
    POSTGRES_PORT=5432 \
    POSTGRES_DB=paymentService \
    POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=123456 \
    RABBITMQ_HOST=rabbitmq \
    RABBITMQ_PORT=5672 \
    RABBITMQ_USER=guest \
    RABBITMQ_PASSWORD=guest

ENTRYPOINT ["java", "-jar", "app.jar"]