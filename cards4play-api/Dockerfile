
FROM eclipse-temurin:17

WORKDIR /app

COPY target/cards4play-api-1.0-jar-with-dependencies.jar app.jar

RUN mkdir -p data

ENV TASA_DEFECTO=4000

EXPOSE 4567

CMD ["java", "-jar", "app.jar"]
