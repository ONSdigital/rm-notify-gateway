FROM openjdk:8-jre-slim

RUN groupadd --gid 999 notifygatewaysvc && \
    useradd --create-home --system --uid 999 --gid notifygatewaysvc notifygatewaysvc
USER notifygatewaysvc

COPY notifygatewaysvc.jar /opt/notifygatewaysvc.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /opt/notifygatewaysvc.jar" ]