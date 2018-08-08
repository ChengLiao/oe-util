#!/bin/sh

$BIN_HOME/setenv
cd $BIN_HOME
cd ..
MAIN_HOME=`pwd`
export MAIN_HOME
#echo MAIN_HOME=$MAIN_HOME
ETC_HOME=$MAIN_HOME/etc
CLASSPATH=$ETC_HOME:$JAVA_HOME/lib/tools.jar

files=`ls $MAIN_HOME/lib/*.jar`
for file1 in $files
do
	CLASSPATH=$CLASSPATH:$file1
done

CLASSPATH=$ETC_HOME:$CLASSPATH
export CLASSPATH
CODEBASE_LIST=file:///$MAIN_HOME/classes
#echo "CLASSPATH: "$CLASSPATH
