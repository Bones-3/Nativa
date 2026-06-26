FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Docker solo tomará el .jar que ya compilaste en el paso 1
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]