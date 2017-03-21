#!/bin/sh

JAVA_OPTS="-server -ea
-Xms1000M -Xmx1000M -Xmn128M
-XX:SurvivorRatio=65536
-XX:MaxTenuringThreshold=0
-Xnoclassgc
-XX:+DisableExplicitGC
-XX:+UseParNewGC
-XX:+UseConcMarkSweepGC
-XX:+UseCMSCompactAtFullCollection
-XX:CMSFullGCsBeforeCompaction=0
-XX:+CMSClassUnloadingEnabled
-XX:-CMSParallelRemarkEnabled
-XX:CMSInitiatingOccupancyFraction=90
-XX:SoftRefLRUPolicyMSPerMB=0"

JAVA_PATH=`which java 2>/dev/null`
if [ -z "$_RUNJAVA" ]; then
  _RUNJAVA="$JAVA_PATH"
fi

PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`

if [ -z "$PLUTO_HOME" ]; then
 PLUTO_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`
fi

if [ -z "$PLUTO_DATA_DIR" ]; then
  PLUTO_DATA_DIR="$PLUTO_HOME"/data
fi

if [ -z "$PLUTO_LOG_DIR" ]; then
  PLUTO_LOG_DIR="$PLUTO_HOME"/logs
fi

if [ -z "$PLUTO_CONF_DIR" ]; then
  PLUTO_CONF_DIR="$PLUTO_HOME"/conf
fi

CLASSPATH=
# Add on extra jar files to CLASSPATH
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH"
fi

if [ -z "$PLUTO_OUT" ] ; then
  PLUTO_OUT="$PLUTO_LOG_DIR"/pluto.out
fi

if [ -z "$PLUTO_PID" ]; then
  PLUTO_PID="$PLUTO_DATA_DIR"/pluto.pid
fi

if [ -z "$PLUTO_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Catalina
  PLUTO_TMPDIR="$PLUTO_HOME"/temp
fi

if [ -z "$JSSE_OPTS" ] ; then
  JSSE_OPTS="-Djdk.tls.ephemeralDHKeySize=2048"
fi
JAVA_OPTS="$JAVA_OPTS $JSSE_OPTS"

if [ -z "$USE_NOHUP" ]; then
    if $hpux; then
        USE_NOHUP="true"
    else
        USE_NOHUP="false"
    fi
fi
unset _NOHUP
if [ "$USE_NOHUP" = "true" ]; then
    _NOHUP=nohup
fi

# remote debug
if [ "$1" = "jpda" ] ; then
  if [ -z "$JPDA_TRANSPORT" ]; then
    JPDA_TRANSPORT="dt_socket"
  fi
  if [ -z "$JPDA_ADDRESS" ]; then
    JPDA_ADDRESS="9000"
  fi
  if [ -z "$JPDA_SUSPEND" ]; then
    JPDA_SUSPEND="n"
  fi
  if [ -z "$JPDA_OPTS" ]; then
    JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
  fi
  PLUTO_OPTS="$JPDA_OPTS $PLUTO_OPTS"
  shift
fi


remove_pid_file()
{
  echo "Removing/clearing stale PID file."
  rm -f "$PLUTO_PID" >/dev/null 2>&1
  if [ $? != 0 ]; then
    if [ -w "$PLUTO_PID" ]; then
      cat /dev/null > "$PLUTO_PID"
    else
      echo "Unable to remove or clear stale PID file. Start aborted."
      exit 1
    fi
  fi
}

have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

if [ $have_tty -eq 1 ]; then
  echo "Using PLUTO_HOME:      $PLUTO_HOME"
  echo "Using PLUTO_TMPDIR:    $PLUTO_TMPDIR"
  echo "Using JAVA_HOME:       $JAVA_HOME"
  if [ ! -z "$CATALINA_PID" ]; then
    echo "Using CATALINA_PID:    $CATALINA_PID"
  fi
fi

if [ "$1" = "start" ] ; then
  if [ ! -z "$PLUTO_PID" ]; then
    if [ -f "$PLUTO_PID" ]; then
      if [ -s "$PLUTO_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$PLUTO_PID" ]; then
          PID=`cat "$PLUTO_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "Pluto appears to still be running with PID $PID. Start aborted."
            echo "If the following process is not a Pluto process, remove the PID file and try again:"
            ps -f -p $PID
            exit 1
          else
            remove_pid_file
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$PLUTO_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$PLUTO_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift
  touch "$PLUTO_OUT"
  touch "$PLUTO_PID"
  eval $_NOHUP "$_RUNJAVA" $JAVA_OPTS $PLUTO_OPTS \
        -Djava.io.tmpdir="$PLUTO_TMPDIR" \
        -Dpluto.base.dir="$PLUTO_HOME" \
        -Dpluto.conf.file="$PLUTO_CONF_DIR"/pluto.conf \
        -jar "$PLUTO_HOME"/pluto.jar \
        >> "$PLUTO_OUT" 2>&1 "&"

  if [ $? -eq 0 ]; then
    echo $! > "$PLUTO_PID"
    if [ $? -eq 0 ]; then
      sleep 1
    else
      echo FAILED TO WRITE PID
      exit 1
    fi
  fi
  echo "Pluto started."

elif [ $1 = "stop" ] ; then
  if [ ! -z "$PLUTO_PID" ]; then
    if [ -f "$PLUTO_PID" ]; then
      if [ -s "$PLUTO_PID" ]; then
        kill -9 `cat "$PLUTO_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          echo "PID file found but no matching process was found. Stop aborted."
          exit 1
        fi
        remove_pid_file
      else
        echo "PID file is empty and has been ignored."
      fi
    else
      echo "\$PLUTO_PID was set but the specified file does not exist. Is Pluto running? Stop aborted."
      exit 1
    fi
  fi

else
  echo "Usage: catalina.sh ( commands ... )"
  echo "commands:"
  echo "  jpda start        Start Pluto under JPDA debugger"
  echo "  start             Start Pluto in a separate window"
  echo "  stop              Stop Pluto, waiting up to 5 seconds for the process to end"
  exit 1
fi