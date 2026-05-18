# Stage 1: Build with Maven
FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Java
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -t luisrivas3/as241-proyecto:latest .
# minikube image load luisrivas3/as241-proyecto:latest "Cargar la imagen a Minikube"
# docker run -d --name age-detector-be -p 8085:8085 luisrivas3/age-detector-be:1.0

# docker push luisrivas3/age-detector-be:1.0