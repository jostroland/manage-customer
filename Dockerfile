FROM openjdk:17
VOLUME /tmp
COPY target/*.jar  app.jar
WORKDIR /test-client
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"] CMD ["-start"]