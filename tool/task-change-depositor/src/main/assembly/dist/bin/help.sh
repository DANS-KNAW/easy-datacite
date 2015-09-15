#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -Dlogback.configurationFile=cfg/logback.xml \
     -jar bin/ta1-change-kitlv-depositor.jar \
     --help

