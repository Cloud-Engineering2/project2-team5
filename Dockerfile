FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} memo-app.jar
ENTRYPOINT ["java", "-jar", "/memo-app.jar"]