FROM adoptopenjdk:16.0.1_9-jre-hotspot-focal
WORKDIR app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]