FROM maven:3.5.2-jdk-8-alpine as BUILDER
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app
RUN mvn -v
RUN mvn clean install -Pdev -DskipTests

ARG DATABASE_HOST=127.0.0.1
ARG DATABASE_PORT=3306
ARG DATABASE_NAME=Quartz
 

EXPOSE 8080
LABEL maintainer=“yiago.sllater@gmail.com”
COPY --from=BUILDER /app/target/*.jar  application.jar
ENTRYPOINT ["java", "-jar", "-Djasypt.encryptor.password=keyMaster", "/application.jar"]