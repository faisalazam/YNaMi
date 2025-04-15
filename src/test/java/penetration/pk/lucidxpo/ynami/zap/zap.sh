#!/usr/bin/env bash

# https://www.zaproxy.org/faq/how-can-you-start-zap/
#
# Generally, most user’s tend to use the Mac OS build, which is a ordinary Mac OS app that can be started as any
# other app: Double-Click on the app to start it.
#
#If you have installed ZAP in ‘/Applications’ then you can run it from the command line using
# ‘/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh’
#
#If you have installed ZAP in another directory then change the initial ‘/Applications’ part accordingly.
#
#If you have downloaded the Linux package, which can also be run on Mac OS, you can use the ‘zap.sh’ script, as per linux.

# This script has been copied from the ZAP's installation directory,
# i.e. "/Applications/OWASP\ ZAP.app/Contents/Java/zap.sh".
# and it is from ZAP's version 2.16.1.
#
# And the zap-2.16.1.jar file can be copied from "/Applications/OWASP\ ZAP.app/Contents/Java/zap-2.16.1.jar"
#
# Easy way to upgrade is:
# 1- Download the "Linux Package" from: https://www.zaproxy.org/docs/desktop/releases/
# 2- You should have ZAP_<VERSION>_Linux.tar now (e.g. ZAP_2.16.1_Linux.tar)
# 3- Extract the ZAP_<VERSION>_Linux.tar (version will vary) file
# 4- Delete the src/test/java/penetration/pk/lucidxpo/ynami/zap/*
# 5- Move the extracted contents to the src/test/java/penetration/pk/lucidxpo/ynami/zap/* folder
# 6- Search for <VERSION> in the whole project and replace with <NEW_VERSION> where makes sense
# 7- That's it, ready to go
# 8- But, after upgrading, if you have failing penetration tests due to errors like below:
# org.zaproxy.clientapi.core.ClientApiException: Does Not Exist (does_not_exist): IDs: [40012, 40014, 40016, 40017]
# Then, the likely cause would be the new src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin dir is missing
# some files which are not available in new version, e.g.: ascanrulesAlpha-alpha-48.zap, ascanrulesBeta-beta-58.zap
# So, basically work out which *.zap contains the "does_not_exist" id, and then download and
# copy it to the src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin dir. e.g.:
# curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrules-release-70.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrules-v70/ascanrules-release-70.zap
# curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrulesAlpha-alpha-19.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesAlpha-v48/ascanrulesAlpha-alpha-19.zap
# curl -L -o src/test/java/penetration/pk/lucidxpo/ynami/zap/plugin/ascanrulesBeta-beta-21.zap https://github.com/zaproxy/zap-extensions/releases/download/ascanrulesBeta-v56/ascanrulesBeta-beta-21.zap
# Or, go to https://www.zaproxy.org/addons/ and download the relevant zap addon file from there
#
# Dereference from link to the real directory
SCRIPTNAME="$0"

# While name of this script is symbolic link
while [ -L "${SCRIPTNAME}" ] ; do
  cd "`dirname "${SCRIPTNAME}"`" > /dev/null
  SCRIPTNAME="$(readlink "`basename "${SCRIPTNAME}"`")"
done
cd "`dirname "${SCRIPTNAME}"`" > /dev/null

# Base directory where ZAP is installed
BASEDIR="`pwd -P`"

# Switch to the directory where ZAP is installed
cd "$BASEDIR"

# Get Operating System
OS=$(uname -s)

# If we're on OS X, try to use the bundled Java; if it's not there, then the system Java
# Life would be much easier if OS X had readlink -f
if [ "$OS" = "Darwin" ]; then
  if [ -e ../PlugIns/jre*/Contents/Home/bin/java ]; then
    pushd ../PlugIns/jre*/Contents/Home/bin > /dev/null
    JAVA_PATH=`pwd -P`
    PATH="$JAVA_PATH:$PATH"
    popd > /dev/null
  fi
fi

# Extract and check the Java version
JAVA_OUTPUT=$(java -version 2>&1)

# Catch warning: Unable to find a $JAVA_HOME at "/usr", continuing with system-provided Java
if [ "`echo ${JAVA_OUTPUT} | grep "continuing with system-provided Java"`" ] ; then
  echo "WARNING, \$JAVA_HOME could be set incorrectly, Java's error is:"
  echo "    " $JAVA_OUTPUT
  echo "Unsetting JAVA_HOME and continuing with ZAP start-up"
  unset JAVA_HOME
fi

DEFAULTJAVAGC=""

JAVA_VERSION=$(java -version 2>&1 | awk -F\" '/version/ { print $2 }')
JAVA_MAJOR_VERSION=${JAVA_VERSION%%[.|-]*}
JAVA_MINOR_VERSION=$(echo $JAVA_VERSION | awk -F\. '{ print $2 }')

if [ ${JAVA_MAJOR_VERSION:-0} -ge 17 ]; then
  echo "Found Java version $JAVA_VERSION"
else
  echo "Exiting: ZAP requires a minimum of Java 17 to run, found $JAVA_VERSION"
  exit 1
fi

if [ "$OS" = "Darwin" ]; then
  JVMPROPS="$HOME/Library/Application Support/ZAP/.ZAP_JVM.properties"
else
  JVMPROPS="$HOME/.ZAP/.ZAP_JVM.properties"
fi

# Work out best memory options
if [ -f "$JVMPROPS" ]; then
  # Local jvm properties file present
  JMEM=$(head -1 "$JVMPROPS")
elif [ "$OS" = "Linux" ]; then
    HOSTMEM=$(expr $(sed -n 's/MemTotal:[ ]\{1,\}\([0-9]\{1,\}\) kB/\1/p' /proc/meminfo) / 1024)
    if [ "$IS_CONTAINERIZED" = "true" ]; then
        # Check if memory.stat exists so it can be used to determine the memory limit
        if [ -f /sys/fs/cgroup/memory/memory.stat ]; then
            # If we are running in a container, we need to use the cgroup memory limit
            MEMLIMIT=$(grep hierarchical_memory_limit /sys/fs/cgroup/memory/memory.stat | cut -d' ' -f2)
            # If the cgroup memory limit is not set, use the host memory
            if [ "$MEMLIMIT" -eq "9223372036854771712" ]; then
                MEM=$HOSTMEM
            else
                MEM=$(expr $MEMLIMIT / 1024 / 1024)
            fi
        else
            MEM=$HOSTMEM
        fi
    else
        MEM=$HOSTMEM
    fi
elif [ "$OS" = "Darwin" ]; then
  MEM=$(system_profiler SPMemoryDataType | sed -n -e 's/.*Size: \([0-9]\{1,\}\) GB/\1/p' | awk '{s+=$0} END {print s*1024}')
  if [ "$MEM" = "0" ]; then
    # Not sure why macOS uses both "Size" and "Memory" on different systems we have tested..
    MEM=$(system_profiler SPMemoryDataType | sed -n -e 's/.*Memory: \([0-9]\{1,\}\) GB/\1/p' | awk '{s+=$0} END {print s*1024}')
  fi

elif [ "$OS" = "SunOS" ]; then
  MEM=$(/usr/sbin/prtconf | awk '/Memory/{print $3}')
elif [ "$OS" = "FreeBSD" ]; then
  MEM=$(($(sysctl -n hw.physmem)/1024/1024))
fi

if [ ! -z "$JMEM" ]; then
  echo "Read custom JVM args from $JVMPROPS"
  JAVAGC=""
elif [ -z "$MEM" ]; then
  echo "Failed to obtain current memory, using jvm default memory settings"
  JAVAGC=${DEFAULTJAVAGC}
else
  echo "Available memory: $MEM MB"
  JAVAGC=${DEFAULTJAVAGC}
  if [ "$MEM" -gt 512 ]; then
    # Always go with 1/4 of the available memory - specific JVMs may round this up or down
    QMEM=$(($MEM/4))
    JMEM="-Xmx${QMEM}m"
  fi
fi

ARGS=()
for var in "$@"; do
  if [[ "$var" == -Xmx* ]]; then
    # Overridden by the user
    JMEM="$var"
  elif [[ $var == --jvmdebug* ]]; then
	JAVADEBUGPORT=`echo "$var" | sed -e "s/--jvmdebug//g" | sed -e "s/=//g"`
	if [ ! "$JAVADEBUGPORT" ]; then
		JAVADEBUGPORT=1044
	fi
	JAVADEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:$JAVADEBUGPORT"
  elif [[ $var != -psn_* ]]; then
    # Strip the automatic -psn_x_xxxxxxx argument that OS X automatically passes into apps, since
    # it freaks out ZAP
    ARGS+=("$var")
  fi
done

if [ -n "$JMEM" ]
then
  echo "Using JVM args: $JMEM"
fi

if [ -n "$JAVADEBUG" ]
then
  echo "Setting debug: $JAVADEBUG"
fi

# Start ZAP; it's likely that -Xdock:icon would be ignored on other platforms, but this is known to work
if [ "$OS" = "Darwin" ]; then
  # It's likely that -Xdock:icon would be ignored on other platforms, but this is known to work
  exec java ${JMEM} ${JAVAGC} ${JAVADEBUG} -Xdock:icon="../Resources/ZAP.icns" -jar "${BASEDIR}/zap-2.16.1.jar" "${ARGS[@]}"
else
  exec java ${JMEM} ${JAVAGC} ${JAVADEBUG} -jar "${BASEDIR}/zap-2.16.1.jar" "${ARGS[@]}"
fi
