FROM openjdk:8-jdk-alpine AS builder

WORKDIR /proj
COPY . .
RUN ./gradlew assemble

FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
EXPOSE 8080/tcp

WORKDIR /home/spring/server
COPY --from=builder /proj/build/libs/*.jar ./server.jar

ENTRYPOINT ["java", "-server", "-jar", "server.jar"]
