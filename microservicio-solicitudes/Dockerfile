# Dockerfile

# Usar una imagen base de Java (usando la versión 21 que aparece en tus logs)
FROM eclipse-temurin:21-jre

# Argumento para el JAR que Maven produce (el nombre del archivo compilado)
ARG JAR_FILE=target/*.jar

# Copia el JAR construido al contenedor
# Nota: La ruta 'target' es donde Maven deja el archivo JAR.
COPY ${JAR_FILE} app.jar

# Define el punto de entrada para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]