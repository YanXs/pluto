#!/bin/sh
#xtrabackup version 2.4.4

if [ $# = 0 ]; then
  echo "args count is illegal"
  exit 1
fi

BACKUP_DIR="$1"
MYSQL_PORT="$2"
MYSQL_DATA_DIR="$3"
MYSQL_DATA_BAK_DIR="$4"
BASE_DIR="$5"
TMP_LOG="$BACKUP_DIR"/fullRestore.`date +%Y%m%d%H%M`.log

if [ `netstat -lnt | grep ${MYSQL_PORT}|wc -l` = 1 ]; then
    echo "mysql is running..."
    /etc/init.d/mysqld stop
fi

if [ -d $MYSQL_DATA_BAK_DIR ]; then
    rm -rf $MYSQL_DATA_BAK_DIR
fi

if [ -d $MYSQL_DATA_DIR ]; then
    mv $MYSQL_DATA_DIR $MYSQL_DATA_BAK_DIR
fi

sudo mkdir -p $MYSQL_DATA_DIR

print_and_clear()
{
  cat $TMP_LOG
  rm -rf $TMP_LOG
}

check_restore_status()
{
    if [ -z "`tail -1 $TMP_LOG | grep 'completed OK!'`" ] ; then
        print_and_clear
        exit 1
    fi
}

echo "Restore begin... : `date +%F' '%T' '%w`"
innobackupex --user=root --password=root --user-memory=8G --apply-log $BASE_DIR > $TMP_LOG 2>&1
check_restore_status

innobackupex --user=root --password=root --user-memory=8G --copy-back $BASE_DIR > $TMP_LOG 2>&1
check_restore_status

chown -R mysql:mysql $MYSQL_DATA_DIR
if [ $? -eq 0 ]; then
    /etc/init.d/mysqld start
fi

print_and_clear
echo "Restore complete : `date +%F' '%T' '%w`"
