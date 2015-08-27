#!/bin/sh

pushd $EASY_PAKBON_HOME

#  Note
#  for investigating problems with logback add 
# -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener 

# execute
  java -Dlogback.configurationFile=cfg/logback.xml \
       -jar bin/easy-pakbon-preprocess.jar \
       -dir $HOME/batch/ingest -batch

popd
