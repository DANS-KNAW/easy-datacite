#!/bin/bash

DATE=`date +%Y-%m-%d`
ZIPPED_EXPORT="$EASY_TASK_DUMP_METADATA_HOME/tmp/metadata-export-$DATE.tgz"
METADATA_EXPORT_DIR="$EASY_TASK_DUMP_METADATA_HOME/tmp/output"
SETTINGS=$EASY_TASK_DUMP_METADATA_HOME/cfg/settings.sh

############ BEGIN: GENERIC REPORTING PART ##############
send_error_mail()
{
   echo "$1" | mail -s "Error message from `basename $0`"  $ERROR_MAIL_RECIPIENT
}

check_set()
{
  if [ -z "$1" ]
  then
     echo "Variable $2 is not set"
     send_error_mail "Variabele $2 is not set"
     exit 1
  fi
}

check_errs()
{
  if [ "${1}" -ne "0" ]; then
    send_error_mail "ERROR # ${1}: ${2}"
    exit 1
  fi
}

if [ -f "$SETTINGS" ]
then
        . "$SETTINGS"
else
        send_error_mail "Could not find settings file: $SETTINGS"
fi

check_set "$ERROR_MAIL_RECIPIENT" "ERROR_MAIL_RECIPIENT"
############ END: GENERIC REPORTING PART ##############

echo "Recreating metadata-export directory: $METADATA_EXPORT_DIR ..."
rm -fr $METADATA_EXPORT_DIR
check_errs $? "Failed to remove metadata-export directory: $METADATA_EXPORT_DIR"

echo "Exporting metadata ..."
$EASY_TASK_DUMP_METADATA_HOME/bin/run-task.sh
check_errs $? "Failed to export metadata"

echo "Zipping metadata ..."
pushd $METADATA_EXPORT_DIR
nice tar -czvf $ZIPPED_EXPORT *
check_errs $? "Failed to zip metadata export"
popd

check_set "$KEY_FOR_TARGET_SERVER" "KEY_FOR_TARGET_SERVER"
check_set "$TARGET_URL" "TARGET_URL"

scp -i $KEY_FOR_TARGET_SERVER $ZIPPED_EXPORT $TARGET_URL
check_errs $? "Failed to copy zipped metadata export to talfalab01 server"

rm $ZIPPED_EXPORT
check_errs $? "Failed to remove zipped metadata export"
