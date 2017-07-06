[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9d74363c178849d09f6d77e19935fcfe)](https://www.codacy.com/app/sdcplatform/rm-notify-gateway?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-notify-gateway&amp;utm_campaign=Badge_Grade)

# Notify Gateway Service
This repository contains the Notify Gateway service. This microservice is a web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/).


##################################################
# To build
##################################################
- Prerequisites:
    - see README (Installation - Maven) at https://github.com/alphagov/notifications-java-client

    - Add this snippet to your Maven settings.xml file (under MAVEN_HOME/conf).
```xml
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
```

- mvn clean install


## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
