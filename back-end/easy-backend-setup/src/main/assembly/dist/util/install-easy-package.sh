#!/bin/sh

# Script to install a new version on a server.
#
# Usage: install-package.sh <product-name> <version>
# Example: install-package.sh easy-webui 2.8-beta-1
#
# This script assumes a number of things:
# 1) The package is in the working directory
# 2) The package name has the format <product-name>-<version>.tar.gz
# 3) The configuration directory cfg should be a link to a default configuration 
#    directory in $INSTALL_BASE/easy-app-cfg/<product-name>
# 4) The installed version should be linked to from $INSTALL_BASE/<product-name>
# 5) The installed product must have the unix user "tomcat" as owner
#
# In other words, you might as well read the rest of the script to figure these
# things out for yourself :)

PRODUCT=$1
VERSION=$2
PACKAGE_BASE=$PRODUCT-$VERSION
TARFILE=$PACKAGE_BASE.tar.gz
INSTALL_BASE=/opt

[ -f "$TARFILE" ] || ( echo "$TARFILE not found" && exit 1 ) 

if [ -d "$INSTALL_BASE/$PACKAGE_BASE" ]
then  
  echo "Target directory $INSTALL_BASE/$PACKAGE_BASE already exists" && exit 2
fi

echo "Extracting package ..."
sudo tar -xzf $TARFILE -C $INSTALL_BASE

echo "Changing ownership to tomcat"
sudo chown -R tomcat:tomcat $INSTALL_BASE/$PACKAGE_BASE/

echo "Linking cfg directory to standard config ..."
sudo rm $INSTALL_BASE/$PACKAGE_BASE/cfg/ -fr
sudo ln -s $INSTALL_BASE/easy-app-cfg/$PRODUCT/ $INSTALL_BASE/$PACKAGE_BASE/cfg

echo "Making $PACKAGE_BASE the default version for $PRODUCT ..."
sudo rm /opt/$PRODUCT 
sudo ln -s $INSTALL_BASE/$PACKAGE_BASE/ $INSTALL_BASE/$PRODUCT

echo "Done."
