FROM openjdk:8-jre-alpine3.9

ADD	service/target/universal/service.tgz /

RUN apk update && apk add bash

WORKDIR /service

ENTRYPOINT ["bin/service-api"]