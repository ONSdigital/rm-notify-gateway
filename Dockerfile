FROM openjdk:8u121-jre

ADD target/notifygatewaysvc*.jar /opt/notifygatewaysvc.jar

ENTRYPOINT [ "java", "-jar", "/opt/notifygatewaysvc.jar" ]

