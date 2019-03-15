FROM openjdk:8-jre-slim

ARG JAR_FILE=notifygatewaysvc*.jar
RUN apt-get update
RUN apt-get -yq clean
RUN groupadd --gid 987 notifygatewaysvc && \
    useradd --create-home --system --uid 987 --gid notifygatewaysvc notifygatewaysvc
USER notifygatewaysvc
COPY target/$JAR_FILE /opt/notifygatewaysvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/notifygatewaysvc.jar" ]
