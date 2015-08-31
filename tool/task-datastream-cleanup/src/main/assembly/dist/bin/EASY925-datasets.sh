#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -cp bin/task-datastream-cleanup.jar \
     nl.knaw.dans.easy.task.RemoveDataStreams \
     --streamId EASY_ITEM_CONTAINER_MD \
     --objectIds 'easy-dataset:*' \
     --nodesToRemove '*' \
     $@ # shell scipt arguments
        # e.g. --url http://localhost:8080/fedora --doUpdate
        # the class prompts for the fedora username and password
