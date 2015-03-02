#!/bin/sh

java -Dlogback.configurationFile=$EASY_PID_GENERATOR_HOME/cfg/logback.xml -jar jetty-runner.jar pid-generator.war --port 5555

