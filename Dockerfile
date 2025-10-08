# Dockerfile para API Example - Java Spring Boot
FROM maven:3-openjdk-17 AS build

# Información del mantenedor
LABEL maintainer="API Example Team"
LABEL description="API RESTful en Java Spring Boot para migración de transacciones y consulta de balance de usuarios"

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Etapa de runtime
FROM openjdk:17-jdk-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/spring_boot_api_example-1.0.0.jar app.jar

# Crear directorio para reportes
RUN mkdir -p /app/reports/errors

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV APP_ENV=production
ENV PORT=8080
ENV HOST=0.0.0.0

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
