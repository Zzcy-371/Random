#!/bin/sh
exec java -Dserver.port=${PORT:-10000} -jar app.jar
