#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -jar bin/task-change-set-to-openaccess.jar \
     --help