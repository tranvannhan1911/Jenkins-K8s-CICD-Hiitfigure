FROM adoptopenjdk/openjdk8:alpine

WORKDIR /app

COPY target/store.jar .

CMD ["java", "-jar", "/app/store.jar"]