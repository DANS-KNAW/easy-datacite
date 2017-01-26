#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -jar bin/task-change-accessrights-to-cc0.jar \
     --help
