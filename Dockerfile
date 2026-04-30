FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY random-backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY random-backend/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh
EXPOSE 3000
CMD ["/app/start.sh"]
