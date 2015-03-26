#!/bin/sh

java -Dlogback.configurationFile=$PID_GENERATOR_HOME/cfg/logback.xml -jar jetty-runner.jar pid-generator.war --port 5555

