FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY common/ common/
COPY domain/ domain/
COPY application/ application/
COPY infrastructure/ infrastructure/
COPY presentation/ presentation/
RUN ./mvnw clean package -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/presentation/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
