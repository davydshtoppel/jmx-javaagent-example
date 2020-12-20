#!/usr/bin/env bash

java -javaagent:target/libs/jmx_prometheus_javaagent-0.14.0.jar=8080:config.yaml \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.port=9000 \
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
    -jar target/quickstart.jar
