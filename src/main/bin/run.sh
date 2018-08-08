#!/bin/sh

#BIN_HOME=$(dirname $(readlink -f $0))
BIN_HOME=$(cd `dirname $0`; pwd)
export BIN_HOME

#source $BIN_HOME/setEnv.sh
. $BIN_HOME/setEnv.sh

OSTYPE=`uname -a |awk '{print $1}'`
#echo OS Type: $OSTYPE

DEBUG=""
if [ "$1" = "debug" ]; then
    DEBUG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,address=10000,suspend=n"
fi

PHYSICALMEM=256
MEMMX=-1
MEMMS=1
MEMSS=1
if [ "$OSTYPE" = "Linux" ]; then
	PHYSICALMEM=`cat /proc/meminfo|grep MemTotal|awk '{print $2}'`
	PHYSICALMEM=`expr $PHYSICALMEM / 1024`
	if [ `expr $PHYSICALMEM` -gt 3000 ]; then
		MEMMX=2048;
		MEMMS=256;
	fi
#	LD_LIBRARY_PATH=$LD_LIBRARY_PATH
elif [ "$OSTYPE" = "SunOS"  ]; then
	PHYSICALMEM=`prtconf -v | grep Memory |awk '{print $3}'`
	if [ `expr $PHYSICALMEM` -gt 4000 ]; then
		MEMMX=3072;
		MEMMS=512;
	fi
#	LD_LIBRARY_PATH=$LD_LIBRARY_PATH
fi

export LD_LIBRARY_PATH
#echo Physical Memory $PHYSICALMEM M


if [ `expr $MEMMX` -lt 0 ]; then
    MEMMX=`expr $PHYSICALMEM \* 3 / 4`
    MEMMS=`expr $PHYSICALMEM / 4`
fi

#echo MEMMX=$MEMMX
#echo MEMMS=$MEMMS
MXPARAM=-Xmx"$MEMMX"M
MSPARAM=-Xms"$MEMMS"M
SSPAPAM=-Xss"$MEMSS"M
#java -version
#echo "CLASSPATH= "$CLASSPATH
#echo CODEBASE_LIST=$CODEBASE_LIST
#echo java -Djava.rmi.server.codebase="$CODEBASE_LIST" com.teradata.mds.load.MDSLoader

# use for supporting firewall solution with fixed RMI ports ,Pls add item -Djava.rmi.server.hostname=???

(java -cp $CLASSPATH com.teradata.util.DataTransfer $*)
