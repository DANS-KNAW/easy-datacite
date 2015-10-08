#!/usr/bin/env bash
cd $(dirname $0)/..
pwd

java -jar bin/task-find-draft-datasets-containing-license.jar\
     --help