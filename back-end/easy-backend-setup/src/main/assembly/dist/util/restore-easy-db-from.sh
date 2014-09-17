#!/bin/sh

# Restores an PostGreSQL easy_db database from one previously exported with 
# pg_dump (with the option -a for data only and -t for binary format).  The 
# export may contain more tables than the ones specified below. 

IMPORT_FILE=$1

restore () {
  echo "Restoring $1 ..."
  sudo -u postgres pg_restore -U postgres -d easy_db -F t -t $1 $IMPORT_FILE
}

restore easy_files
restore easy_folders 
restore easy_folder_accessibility_status 
restore easy_folder_creator 
restore easy_folder_visibility_status 
