#!/bin/sh

PID=$1
DS_ID=$2
DS_LABEL=$3
DS_FILE=$2
FEDORA_USER=$4
FEDORA_PASSWD=$5
FEDORA_HOST=$6
FEDORA_PORT=$7


#cat $DS_FILE | \
curl -H "Content-Type: text/xml" \
	 -v -X POST \
	 -d @$DS_FILE \
     "http://$FEDORA_USER:$FEDORA_PASSWD@$FEDORA_HOST:$FEDORA_PORT/fedora/objects/$PID/datastreams/$DS_ID?\
controlGroup=M\
&versionable=false\
&mimeType=text/xml\
&checksumType=DISABLED\
&dsLabel=$DS_LABEL"