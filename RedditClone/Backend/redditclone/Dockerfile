FROM eclipse-temurin:17-alpine
WORKDIR app
COPY /target/*.jar ./redditCloneBApp.jar
ENTRYPOINT ["java", "-jar", "./redditCloneBApp.jar"]