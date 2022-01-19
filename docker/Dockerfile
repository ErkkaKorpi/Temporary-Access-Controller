FROM openjdk:18-ea-11-jdk-alpine3.15

RUN apk update --no-cache && apk add dumb-init

RUN mkdir -p /opt/code/
COPY tac.jar /opt/code/

RUN addgroup -S tac && adduser -S tac -G tac
USER tac

CMD [ \
    "dumb-init", \
    "java", \
    "-jar", "/opt/code/tac.jar" \
]
