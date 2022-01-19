SHELL=/bin/bash

test:
	mvn test -Dspring.profiles.active=testpackage

build: test
	mvn package -DskipTests=true

docker-copy-files:
	cp target/TemporaryAccessController-0.0.1-SNAPSHOT.jar docker/tac.jar

docker-build-image:
	docker build --file docker/Dockerfile --no-cache --rm -t "tac:latest" .

docker-build: build docker-copy-files docker-build-image

start-local-mq:
	cd docker; docker-compose up -d mq && \
	sleep 10

run: build start-local-mq
	java -jar target/TemporaryAccessController-0.0.1-SNAPSHOT.jar $(ARGUMENTS)

stop-local-mq:
	cd docker; docker-compose down

.PHONY:
	run
	docker-build
	docker-copy-files
	docker-build-image
	run-local-mq
	stop-local-mq
