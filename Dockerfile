# Stage 1: Build project
FROM maven:3.9.3-openjdk-17 AS build
WORKDIR /app

# Copy file pom và source code
COPY pom.xml .
COPY src ./src

# Build project
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy jar từ stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
