FROM adoptopenjdk/openjdk11:alpine
EXPOSE 8080

COPY target/*.jar github-file-server.jar
ENTRYPOINT ["java","-jar","/github-file-server.jar"]
