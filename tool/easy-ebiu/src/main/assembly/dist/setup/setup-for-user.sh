#!/bin/sh

##############################################################################
# setup-for-user.sh
#
# Creates the input directory try in the specified user's home directory, 
# creates the EASY_EBIU_HOME environment variabele and modifies the user's PATH.
##############################################################################

# Print usage if wrong number of arguments
if [ $# -ne 2 ]
then
  echo "Sets up EBIU for a Linux account"
  echo "Usage: `basename $0` <install dir EBIU> <user account name>"
  exit
fi

USER_ACCOUNT=$2

pushd /home/$USER_ACCOUNT

if [ -e batch ]
then
  echo "It seems EBIU has already been set up"
  echo "Found a file or directory called '`pwd`/batch'"
  echo "Please remove if you want to rerun setup"
  exit
fi

# Create input directory tree there
mkdir -p batch/ingest batch/update-files batch/update-metadata

# Change ownership to user
sudo chown -R $USER_ACCOUNT:$USER_ACCOUNT batch

# Go back where you came from
popd
