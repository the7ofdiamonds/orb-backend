FROM openjdk:17

WORKDIR /app

COPY ./real-estate/target/real-estate-0.0.1-SNAPSHOT.jar real-estate-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "real-estate-0.0.1-SNAPSHOT.jar" ]