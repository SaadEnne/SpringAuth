# ====== Build stage ======
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package
RUN mvn -q -B -DskipTests package spring-boot:repackage
# ====== Runtime stage ======
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

