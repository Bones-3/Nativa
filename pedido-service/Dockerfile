# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el archivo de configuración de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar el archivo ejecutable
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución ligera
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el .jar generado
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]