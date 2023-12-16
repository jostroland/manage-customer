FROM openjdk:21
VOLUME /tmp
COPY target/*.jar  app.jar
WORKDIR /test-client
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"] CMD ["-start"]