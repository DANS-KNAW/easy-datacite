#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -Dconfig.file=cfg/application.conf \
     -cp bin/task-compute-checksums.jar \
     nl.knaw.dans.easy.task.FileChecksumSetter "$@" < log/checksums.log

