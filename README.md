# Notify Gateway
The Notify Gateway provides an interface for Response Management to send communications to users using the [GOV.UK Notify](https://www.gov.uk/government/publications/govuk-notify/govuk-notify) service. It is implemented using [Spring Boot](http://projects.spring.io/spring-boot/).


##################################################
# To build
##################################################
- Prerequisites:
    - see README (Installation - Maven) at https://github.com/alphagov/notifications-java-client

- mvn clean install
- mvn clean install -DskipITs


##################################################
# To run the app
##################################################
- Prerequisites:
    - for logging:
        - cd /var/log/ctp/responsemanagement
        - mkdir notifygatewaysvc
        - chmod 777 notifygatewaysvc
    - Stop RabbitMQ if running: sudo /sbin/service rabbitmq-server stop
    - Install ActiveMQ:
        - Install Apache ActiveMQ 5.13.3: download and unzip under /opt
        - Edit /conf/activemq.xml: replace 61616 with 53445 (port defined in broker-int.xml)
        - Start it by going to /bin and typing: ./activemq console
        - console accessed at http://localhost:8161/ with user = admin - pwd = admin

- To start:
    - cd .../rm-notify-gateway/target
    - java -jar notifygatewaysvc-9.28.0-SNAPSHOT.jar


## Copyright
Copyright (C) 2016 Crown Copyright (Office for National Statistics)
