FROM openjdk:8-jre-slim

RUN apt-get update
COPY target/notifygatewaysvc.jar /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]

