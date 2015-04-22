#!/bin/sh

pushd $PID_GENERATOR_HOME
java -Dlogback.configurationFile=cfg/logback.xml \
     -jar bin/jetty-runner.jar \
     --port {{ pid_generator_port }} bin/pid-generator.war 
popd
