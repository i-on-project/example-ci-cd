FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /home/spring/server
EXPOSE 8080/tcp

COPY ./build/libs/*.jar ./server.jar
ENTRYPOINT ["java", "-server", "-jar", "server.jar"]
