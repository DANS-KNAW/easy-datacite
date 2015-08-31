#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-datastream-cleanup.jar \
     nl.knaw.dans.easy.task.RemoveTagsFromDataStreams \
     --streamId EASY_ITEM_CONTAINER_MD \
     --objectIds 'easy-folder:*' \
     --nodesToRemove sid parentSid datasetSid \
     $@ # shell scipt arguments
        # e.g. --url http://localhost:8080/fedora --doUpdate
        # the class prompts for the fedora username and password
