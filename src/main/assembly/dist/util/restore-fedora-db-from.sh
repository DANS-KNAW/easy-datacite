#!/bin/sh

# Restores an PostGreSQL Fedora Commons SQL database from one previously 
# exported with pg_dump (with the option -a for data only and -t for binary 
# format).  The export may contain more tables than the ones specified below. 

sudo -u postgres pg_restore -U postgres -d fedora3 -F t \
  -t datastreampaths \
  -t dcdates \
  -t dofields \
  -t doregistry \
  -t modeldeploymentmap \
  -t objectpaths \
  -t pidgen \
  $1
