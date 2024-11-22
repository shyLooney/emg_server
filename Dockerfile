FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package


FROM maven:3.9.9-eclipse-temurin-21

ARG APP_BUILD_VERSION='DEV-SNAPSHOT'
WORKDIR /app
COPY --from=build /app/target/emg-server-${APP_BUILD_VERSION}.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]