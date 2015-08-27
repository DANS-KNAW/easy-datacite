#!/bin/sh

. /etc/profile.d/task-assign-datasets-to-collections.sh

pushd $EASY_TASK_ASSIGN_DATASETS_TO_COLLECTIONS_HOME/bin
java -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener \
     -Dlogback.configurationFile=$EASY_TASK_ASSIGN_DATASETS_TO_COLLECTIONS_HOME/cfg/logback.xml \
     -jar task-assign-datasets-to-collections.jar "${@}"
popd

rc=$?

if [[ $rc != 0 ]] ; then
   echo "TASK ERROR: $rc"
else
   echo "TASK SUCCESS" 
fi