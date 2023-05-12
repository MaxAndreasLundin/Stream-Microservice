FROM eclipse-temurin:20-jdk

LABEL maintainer="lundin-89@hotmail.com"

EXPOSE 8080

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
