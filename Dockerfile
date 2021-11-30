FROM maven:3.8.4 as build
WORKDIR /workspace/
COPY src src
COPY pom.xml .
RUN mvn package -Dskiptest

FROM openjdk:11-jdk
VOLUME /tmp
COPY --from=build /workspace/target/currency-converter-21.4.jar currency-converter.jar
ENTRYPOINT ["java","-jar","currency-converter.jar"]


