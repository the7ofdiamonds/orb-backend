FROM openjdk:17

WORKDIR /app

COPY ./products-services/target/products-services-0.0.1-SNAPSHOT.jar products-services-0.0.1-SNAPSHOT.jar

CMD [ "java", "-jar", "products-services-0.0.1-SNAPSHOT.jar" ]