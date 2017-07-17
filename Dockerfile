FROM openjdk:8u121-jre
ARG jar
VOLUME /tmp
ADD $jar notifygatewaysvc.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java -jar /notifygatewaysvc.jar" ]

