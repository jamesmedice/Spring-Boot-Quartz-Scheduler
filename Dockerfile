FROM maven:3.5.2-jdk-8-alpine as BUILDER
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app
RUN mvn -v
RUN mvn clean install -Pdev -DskipTests



FROM openjdk:8-jre-alpine
COPY --from=BUILDER /app/target/*.jar  application.jar
ENTRYPOINT ["java", "-jar", "-Djasypt.encryptor.password=keyMaster", "/application.jar"]