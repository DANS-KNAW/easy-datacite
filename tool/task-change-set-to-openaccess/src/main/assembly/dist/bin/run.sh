#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-change-set-to-openaccess.jar \
     nl.knaw.dans.easy.task.Main $@