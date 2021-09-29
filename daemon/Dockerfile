FROM openjdk:8-jre-alpine3.9

ADD	daemon/target/universal/daemon.tgz /

RUN apk update && apk add bash

WORKDIR /daemon

ENTRYPOINT ["bin/redis-daemon"]