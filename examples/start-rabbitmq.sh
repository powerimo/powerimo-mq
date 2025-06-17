#!/usr/bin/env sh

docker run -d --hostname my-rabbit \
  --name some-rabbit \
  -e RABBITMQ_DEFAULT_USER=user \
  -e RABBITMQ_DEFAULT_PASS=password \
  -p 5671:5671 \
  -p 5672:5672 \
  rabbitmq:3-management