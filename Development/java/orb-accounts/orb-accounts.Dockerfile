FROM openjdk:17

WORKDIR /app

COPY ./accounts/target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "accounts-0.0.1-SNAPSHOT.jar" ]