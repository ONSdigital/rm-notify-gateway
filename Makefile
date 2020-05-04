name := notifygatewaysvc
docker-name := notify-gateway
sha := $(shell git rev-parse --short HEAD)

.PHONY: all
all: clean verify docker

.PHONY: clean
clean:
	rm -f $(name).jar
	rm -f webhook.json
	mvn clean

.PHONY: verify
verify:
	mvn verify
	cp ./target/$(name).jar ./$(name).jar

.PHONY: docker
docker:
	docker build -f _infra/docker/Dockerfile --tag eu.gcr.io/ons-rasrmbs-management/$(docker-name):$(sha) .
	docker push eu.gcr.io/ons-rasrmbs-management/$(docker-name):$(sha)
