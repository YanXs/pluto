#!/bin/sh

if [ $# = 0 ]; then
  echo "args count is illegal"
  exit 1
fi

BACKUP_DIR="$1"
MYSQL_PORT="$2"
MYSQL_DATA_DIR="$3"
MYSQL_BAK_DIR="$4"
INCREMENTAL_BAK="$5"
FULL_BAK="$6"
TMP_LOG="$BACKUP_DIR"/fullRestore.`date +%Y%m%d%H%M`.log

port_num=`netstat -lnt | grep ${MYSQL_PORT}|wc -l`
if [ $port_num = 1 ]; then
    echo "mysql is running..."
    /etc/init.d/mysqld stop
fi

if [ -d $MYSQL_BAK_DIR ]; then
    sudo rm -rf $MYSQL_BAK_DIR
fi

if [ -d $MYSQL_DATA_DIR ]; then
    sudo mv $MYSQL_DATA_DIR $MYSQL_BAK_DIR
fi

sudo mkdir -p $MYSQL_DATA_DIR

check_innobackupex_fail()
{
    if [ -z "`tail -1 $TMP_LOG | grep 'completed OK!'`" ] ; then
        echo "incrementalRestore failed"
        sudo cat $TMP_LOG
        logfiledate=incrementalRestore.`date +%Y%m%d%H%M`.txt
        sudo cat $TMP_LOG>$BACKUP_DIR/$logfiledate
        sudo rm -f $TMP_LOG
        exit 1
    fi
}

check_apply_log()
{

  if [ "$?" -eq 0 ] ; then
        echo "${filename} has been recovered."
  else
        echo "${filename} has not been completed"
        break
  fi
}

echo "incrementalRestore begin...: `date +%F' '%T' '%w`"

for filename in $(sudo ls -t $BACKUP_DIR/ | sort -n | grep -v '.txt$')

do

 if [[ $filename == $FULL_BAK ]] ;then
 	echo "--apply-log: ${filename}"
        sudo innobackupex --user=root --password=root --user-memory=8G --apply-log --redo-only $BACKUP_DIR/$filename > $TMP_LOG 2>&1
        check_innobackupex_fail
        check_apply_log
 fi

  if [[ $filename > $FULL_BAK ]] && [[ $filename < $INCREMENTAL_BAK ]] ; then
        echo "--apply-log: ${filename}"
        sudo innobackupex --user=root --password=root --user-memory=8G --apply-log --redo-only $BACKUP_DIR/$FULL_BAK --incremental-dir=$BACKUP_DIR/$filename > $TMP_LOG 2>&1
        check_innobackupex_fail
        check_apply_log

  fi

 if [[ $filename == $INCREMENTAL_BAK ]] ;then
        echo "--apply-log: ${filename}"
        sudo innobackupex --user=root --password=root --user-memory=8G --apply-log --redo-only $BACKUP_DIR/$FULL_BAK --incremental-dir=$BACKUP_DIR/$filename > $TMP_LOG 2>&1
        check_innobackupex_fail
        check_apply_log
 fi

done 

echo "start to copy..."

sudo innobackupex --user=root --password=root --user-memory=8G --copy-back $BACKUP_DIR/$FULL_BAK/ > $TMP_LOG 2>&1
check_innobackupex_fail

sudo chown -R amcuser:amcuser $MYSQL_DATA_DIR
if [ $? -eq 0 ]; then
    /etc/init.d/mysqld start
fi
echo "incrementalRestore complete: `date +%F' '%T' '%w`"
