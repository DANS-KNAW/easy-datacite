#!/bin/sh

java -Dlogback.configurationFile=$PID_GENERATOR_HOME/cfg/logback.xml -jar jetty-runner.jar --port 8082 pid-generator.war 

