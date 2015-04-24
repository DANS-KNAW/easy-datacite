#!/bin/sh

java -Dlogback.configurationFile=$PID_GENERATOR_HOME/cfg/logback.xml \
     -jar $PID_GENERATOR_HOME/bin/jetty-runner.jar \
     --port {{ pid_generator_port }} $PID_GENERATOR_HOME/bin/pid-generator.war 
