# syntax=docker/dockerfile:1

FROM gradle:8.11.1-jdk-21-and-23-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:23
EXPOSE 8080
RUN mkdir "/app"
COPY --from=build /home/gradle/src/build/libs/*.jar /app/
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/financeViewer-0.0.1-SNAPSHOT.jar"]
