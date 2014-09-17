#!/bin/sh

# Restores an PostGreSQL Fedora Commons SQL database from one previously 
# exported with pg_dump (with the option -a for data only and -t for binary 
# format).  The export may contain more tables than the ones specified below. 

IMPORT_FILE=$1

restore () {
  echo "Restoring $1 ..."
  sudo -u postgres pg_restore -U postgres -d fedora3 -F t -t $1 $IMPORT_FILE
}

restore datastreampaths 
restore dcdates 
restore dofields 
restore doregistry 
restore modeldeploymentmap
restore objectpaths
restore pidgen

