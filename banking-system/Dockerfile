# Multi-stage build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q -e dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/banking-system-*.jar app.jar
RUN apk add --no-cache curl
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
