FROM bellsoft/liberica-openjdk-debian:21


LABEL authors="edzeeee"

COPY ./.mvn ./.mvn
COPY ./mvnw ./mvnw
COPY ./mvnw.cmd ./mvnw.cmd
COPY ./pom.xml ./pom.xml
COPY ./src ./src

EXPOSE 8083

RUN ./mvnw clean install -DskipTests

ENV TZ=Europe/Moscow
RUN cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


CMD ./mvnw spring-boot:run