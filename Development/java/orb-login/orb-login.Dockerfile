FROM openjdk:17

WORKDIR /app

COPY ./login/target/login-0.0.1-SNAPSHOT.jar login-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "login-0.0.1-SNAPSHOT.jar" ]