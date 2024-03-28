FROM openjdk:17

WORKDIR /app

COPY ./communications/target/communications-0.0.1-SNAPSHOT.jar communications-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "communications-0.0.1-SNAPSHOT.jar" ]