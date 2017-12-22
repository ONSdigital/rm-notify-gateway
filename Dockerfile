FROM openjdk:8u121-jre

ARG JAR_FILE=notifygatewaysvc*.jar
COPY target/$JAR_FILE /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]

