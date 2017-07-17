[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9d74363c178849d09f6d77e19935fcfe)](https://www.codacy.com/app/sdcplatform/rm-notify-gateway?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-notify-gateway&amp;utm_campaign=Badge_Grade) [![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/notifygatewaysvc.svg)]()

# Notify Gateway Service
This repository contains the Notify Gateway service. This microservice is a web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides a wrapper around [GOV.UK Notify](https://www.notifications.service.gov.uk/), offering both a RESTful and queue interface to clients.

## Running
    mvn clean install
    cd notifygatewaysvc
    ./mvnw spring-boot:run

## API
See [API.md](https://github.com/ONSdigital/rm-notify-gateway/blob/master/API.md) for API documentation.

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
