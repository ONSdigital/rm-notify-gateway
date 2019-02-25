FROM openjdk:8-jre-slim

ARG JAR_FILE=notifygatewaysvc*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd -g 987 notifygatewaysvc && \
    useradd -r -u 987 -g notifygatewaysvc notifygatewaysvc
USER notifygatewaysvc
COPY target/$JAR_FILE /opt/notifygatewaysvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/notifygatewaysvc.jar" ]
