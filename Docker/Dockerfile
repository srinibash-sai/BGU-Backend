FROM openjdk:17-jdk-alpine

WORKDIR /app
COPY target/bgu-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Define the command to run the jar file when the container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
