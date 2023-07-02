#FROM gradle:7.6.1-jdk17-alpine AS build
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle build --no-daemon --stacktrace
#
#FROM openjdk:17-alpine
#
#EXPOSE 8080
#
#RUN mkdir /app
#
#RUN echo $(ls -1 /home)
#COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
#
#ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]


FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]