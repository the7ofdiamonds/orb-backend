FROM openjdk:17

WORKDIR /app

COPY ./investments/target/investments-0.0.1-SNAPSHOT.jar investments-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "investments-0.0.1-SNAPSHOT.jar" ]