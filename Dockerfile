FROM openjdk:8u121-jre

ADD target/notifygatewaysvc*.jar /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]

