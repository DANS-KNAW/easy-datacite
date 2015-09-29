#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-change-status-to-openaccess.jar \
     nl.knaw.dans.easy.task.ChangeRights $@