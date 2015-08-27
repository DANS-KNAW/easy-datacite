#!/bin/sh

pushd $EASY_TASK_DUMP_METADATA_HOME/bin
java -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener \
     -Dlogback.configurationFile=$EASY_TASK_DUMP_METADATA_HOME/cfg/logback.xml \
     -jar task-dump-metadata.jar "${@}"
popd

rc=$?

if [[ $rc != 0 ]] ; then
   echo "TASK ERROR: $rc"
else
   echo "TASK SUCCESS" 
fi