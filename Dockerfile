FROM maven:3-amazoncorretto-21

WORKDIR /test-client
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run