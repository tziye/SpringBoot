#!/usr/bin/sh

JAVA_OPTS="-Xmx1g -Xms1g -XX:+UseG1GC -Xloggc:log/gc/gc.log -XX:+HeapDumpOnOutOfMemoryError"
DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=28088,suspend=n"
JMX_OPTS="-Dcom.sun.management.jmxremote.rmi.port=19090 \
                -Dcom.sun.management.jmxremote=true \
                -Dcom.sun.management.jmxremote.port=19090  \
                -Dcom.sun.management.jmxremote.ssl=false \
                -Dcom.sun.management.jmxremote.authenticate=true \
                -Dcom.sun.management.jmxremote.password.file=jmxremote.password \
                -Dcom.sun.management.jmxremote.local.only=false \
                -Djava.rmi.server.hostname=${SERVER_HOST}"

exec java ${JAVA_OPTS} ${JMX_OPTS} ${DEBUG_OPTS} -jar app.jar --spring.profiles.active=prod