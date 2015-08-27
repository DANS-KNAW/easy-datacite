#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -Dconfig.file=cfg/application.conf \
     -cp bin/task-compute-checksums.jar \
     nl.knaw.dans.easy.task.ChecksumCalculator \
     "$@" # e.g. '*:*' for all objects or 'easy-*:1??' for files, folders and datasets from 100 to 199

