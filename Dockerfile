FROM adoptopenjdk/openjdk8:alpine

WORKDIR /app

COPY target/store-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/store-0.0.1-SNAPSHOT.jar"]