#!/bin/sh
exec java -Dserver.port=${PORT:-3000} -jar app.jar
