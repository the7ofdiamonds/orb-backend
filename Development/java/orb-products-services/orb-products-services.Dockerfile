FROM openjdk:17

WORKDIR /app

COPY ./target/orbfin-0.0.1-SNAPSHOT.jar app-1.0.0.jar

CMD [ "java", "-jar", "app-1.0.0.jar" ]