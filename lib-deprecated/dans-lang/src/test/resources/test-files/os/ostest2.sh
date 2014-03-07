#!/bin/bash

echo "Executing script $0     PPID=$PPID $$"

cd /usr/local/vm-data/SVN/common/trunk/lang/target/test-classes

echo "Now executing java nl.knaw.dans.common.lang.os.Main2"
java -cp .:../classes nl.knaw.dans.common.lang.os.Main2


exit $?
