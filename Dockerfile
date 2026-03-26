FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-XX:+UseG1GC", "-jar", "app.jar"]
