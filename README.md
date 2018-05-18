[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9d74363c178849d09f6d77e19935fcfe)](https://www.codacy.com/app/sdcplatform/rm-notify-gateway?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ONSdigital/rm-notify-gateway&amp;utm_campaign=Badge_Grade)
[![Docker Pulls](https://img.shields.io/docker/pulls/sdcplatform/notifygatewaysvc.svg)]()
[![Build Status](https://travis-ci.org/ONSdigital/rm-notify-gateway.svg?branch=master)](https://travis-ci.org/ONSdigital/rm-notify-gateway)
# Notify Gateway Service
This repository contains the Notify Gateway service. This microservice is a web service implemented using [Spring Boot](http://projects.spring.io/spring-boot/). It provides a wrapper around [GOV.UK Notify](https://www.notifications.service.gov.uk/), offering both a RESTful and queue interface to clients.

The Notify Gateway provides an interface for Response Management to send communications to users using the [GOV.UK Notify](https://www.gov.uk/government/publications/govuk-notify/govuk-notify) service. It is implemented using [Spring Boot](http://projects.spring.io/spring-boot/).

## Running

There are two ways of running this service

* The easiest way is via docker (https://github.com/ONSdigital/ras-rm-docker-dev)
* Alternatively running the service up in isolation
    ```bash
    cp .maven.settings.xml ~/.m2/settings.xml  # This only needs to be done once to set up mavens settings file
    mvn clean install
    mvn spring-boot:run
    ```

## API
See [API.md](https://github.com/ONSdigital/rm-notify-gateway/blob/master/API.md) for API documentation.

## Copyright
Copyright (C) 2017 Crown Copyright (Office for National Statistics)
