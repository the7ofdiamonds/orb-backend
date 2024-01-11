FROM openjdk:17

WORKDIR /app

COPY ./insurance/target/insurance-0.0.1-SNAPSHOT.jar insurance-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "insurance-0.0.1-SNAPSHOT.jar" ]