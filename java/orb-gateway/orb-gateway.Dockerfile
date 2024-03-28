FROM openjdk:17

WORKDIR /app

COPY ./gateway/target/gateway-0.0.1-SNAPSHOT.jar gateway-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "gateway-0.0.1-SNAPSHOT.jar" ]