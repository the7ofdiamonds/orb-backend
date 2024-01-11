FROM openjdk:17

WORKDIR /app

COPY ./finance/target/finance-0.0.1-SNAPSHOT.jar finance-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "finance-0.0.1-SNAPSHOT.jar" ]