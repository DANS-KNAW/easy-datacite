#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -jar bin/task-change-depositor.jar \
     --oldDepositor Emiliealsarchivaris \
     --newDepositor Archives \
     --queries 'creator~*SMGI*' \
     $@ # shell scipt arguments
echo
echo "feed easy-update-solr-index with log/pids.log"
