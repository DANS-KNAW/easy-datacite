#!/bin/sh

# Restores an PostGreSQL easy_db database from one previously exported with 
# pg_dump (with the option -a for data only and -t for binary format).  The 
# export may contain more tables than the ones specified below. 

sudo -u postgres pg_restore -U postgres -d easy_db -F t \
   -t easy_files \
   -t easy_folder_accessibility_status \
   -t easy_folder_creator \
   -t easy_folder_visibility_status \
   -t easy_folders \
   $1
