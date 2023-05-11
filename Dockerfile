# Start with a base image containing Java runtime (JDK 11 in this case)
FROM eclipse-temurin:20-jdk

# Add Maintainer Info
LABEL maintainer="lundin-89@hotmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
