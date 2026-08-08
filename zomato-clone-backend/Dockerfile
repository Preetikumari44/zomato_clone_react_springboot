# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "if [ -n \"$DATABASE_URL\" ] && [ -z \"$DB_URL\" ]; then export DB_URL=\"jdbc:$DATABASE_URL\"; fi; exec java -jar app.jar"]
