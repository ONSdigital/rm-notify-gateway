# Notify Gateway
The Notify Gateway provides an interface for Response Management to send communications to users using the [GOV.UK Notify](https://www.gov.uk/government/publications/govuk-notify/govuk-notify) service. It is implemented using [Spring Boot](http://projects.spring.io/spring-boot/).


##################################################
# To build
##################################################
- Prerequisites:
    - see README (Installation - Maven) at https://github.com/alphagov/notifications-java-client

    - Add this snippet to your Maven settings.xml file (under MAVEN_HOME/conf).
            <?xml version='1.0' encoding='UTF-8'?>
            <settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd' xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
            <profiles>
                <profile>
                    <repositories>
                        <repository>
                            <snapshots>
                                <enabled>true</enabled>
                            </snapshots>
                            <id>artifactory-maven</id>
                            <name>artifactory</name>
                            <url>http://192.168.11.11:8081/artifactory/libs-snapshot-local</url>
                        </repository>
                    </repositories>
                    <pluginRepositories>
                        <pluginRepository>
                            <snapshots>
                              <enabled>true</enabled>
                            </snapshots>
                            <id>artifactory-maven</id>
                            <name>artifactory-plugins</name>
                            <url>http://192.168.11.11:8081/artifactory/libs-snapshot-local</url>
                        </pluginRepository>
                    </pluginRepositories>
                    <id>artifactory</id>
                </profile>
            </profiles>
            <activeProfiles>
                <activeProfile>artifactory</activeProfile>
            </activeProfiles>
            </settings>


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
