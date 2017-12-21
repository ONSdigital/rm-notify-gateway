ARG JAR_FILE=notifygatewaysvc*.jar
FROM openjdk:8u121-jre

ARG JAR_FILE
COPY target/$JAR_FILE /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]

