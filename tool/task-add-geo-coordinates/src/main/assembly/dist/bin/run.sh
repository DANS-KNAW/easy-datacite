#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-add-geo-coordinates.jar \
     nl.knaw.dans.easy.task.AddGeoCoordinates "$@"