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
ARG JAR_FILE=build/libs/timemanager-1.0.0.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]




#FROM eclipse-temurin:17-jdk-alpine AS build
#WORKDIR /workspace/app
#
#COPY . /workspace/app
#RUN target=/root/.gradle ./gradlew clean build
#RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-1.0.0.jar)
#
#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG DEPENDENCY=/workspace/app/build/dependency
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","TimemanagerApplication.class"]