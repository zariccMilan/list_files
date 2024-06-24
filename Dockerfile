#FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine
FROM --platform=linux/amd64 eclipse-temurin:21-jdk

# Create app directory
WORKDIR /usr/src/app

ENV JAVA_HOME=/opt/java/openjdk

COPY target/list-files-0.0.1-SNAPSHOT.jar /usr/src/app/list-files-0.0.1-SNAPSHOT.jar
EXPOSE 8080
#ENTRYPOINT ["tail", "-f", "/dev/null"]
ENTRYPOINT ["/opt/java/openjdk/bin/java", "-Dspring.profiles.active=dev", "-Xms6G", "-Xmx6G", "-jar","/usr/src/app/list-files-0.0.1-SNAPSHOT.jar"]
