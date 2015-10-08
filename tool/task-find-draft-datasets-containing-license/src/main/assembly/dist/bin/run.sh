#!/usr/bin/env bash
cd $(dirname $0)/..
pwd
java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-find-draft-datasets-containing-license.jar \
     nl.knaw.dans.easy.task.FindDraftContainingLicense $@