# Stage 1: Compile
FROM maven:3-eclipse-temurin-25 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests  

# Stage 22: Run
FROM eclipse-temurin:25-jre
COPY --from=build /app/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]