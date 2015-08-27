#!/bin/sh

TASK_NAME=$1
pushd $EASY_EBIU_HOME
nice -n 10 java -jar bin/easy-ebiu.jar application.context=cfg/spring/$TASK_NAME-context.xml process.name=$TASK_NAME log.console=true
popd