FROM eclipse-temurin:17-alpine
WORKDIR app
COPY /target/*.jar ./employeeApp.jar
ENTRYPOINT ["java", "-jar", "./employeeApp.jar"]