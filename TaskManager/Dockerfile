FROM eclipse-temurin:26-jdk-alpine
WORKDIR /app
COPY target/taskmanager-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]