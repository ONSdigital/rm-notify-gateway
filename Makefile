name := notifygatewaysvc

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
ifndef tag
	$(error No tag provided. Aborting build)
endif
	docker build -f _infra/docker/Dockerfile --tag eu.gcr.io/ons-rasrmbs-management/$(name):$(tag) .
	docker push eu.gcr.io/ons-rasrmbs-management/$(name):$(tag)
