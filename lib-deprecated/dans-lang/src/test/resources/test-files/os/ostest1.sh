#!/bin/bash

echo "Executing script $0     PPID=$PPID $$"
echo "ostest1.sh"
echo "you are:"
whoami
echo

cd /usr/local/vm-data/SVN/common/trunk/lang/target/test-classes

echo "at"
pwd
echo

echo "Now executing java nl.knaw.dans.common.lang.os.Main1"
java -cp .:../classes nl.knaw.dans.common.lang.os.Main1 &


exit $?
