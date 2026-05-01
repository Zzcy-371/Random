#!/bin/sh
exec java -Duser.timezone=Asia/Shanghai -Dserver.port=${PORT:-10000} -jar app.jar
