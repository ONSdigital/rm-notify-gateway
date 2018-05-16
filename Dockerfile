FROM openjdk:8-jre-slim

ARG JAR_FILE=notifygatewaysvc*.jar
RUN apt-get update
RUN apt-get -yq install curl
RUN apt-get -yq clean
COPY target/$JAR_FILE /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]

