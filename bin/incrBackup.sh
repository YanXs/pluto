#!/bin/sh

if [ $# = 0 ]; then
  echo "args count is illegal"
  exit 1
fi

BACKUP_DIR="$1"

if [ -z "$BACKUP_DIR" ]; then
  echo "BACKUP_DIR must not be null"
fi

BASE_BACKUP="$2"
if [ -z "$BASE_BACKUP" ]; then
  echo "BASE_BACKUP must not be null"
fi
BACKUP_NAME="$3"
TMP_LOG="$BACKUP_DIR"/incrBackup.`date +%Y%m%d%H%M`.log

print_and_clear()
{
  echo "Backup failed"
  sudo cat $TMP_LOG
  sudo rm -f $TMP_LOG
}

echo "Backup begin... : `date +%F' '%T' '%w`"

sudo innobackupex --user=root --password=root --use-memory=8G --parallel=32 --incremental $BACKUP_DIR --incremental-basedir=$BASE_BACKUP > $TMP_LOG  2>&1

if [ $? -gt 0 ]; then
  print_and_clear
  exit 1
fi

if [ -z "`tail -1 $TMP_LOG | grep 'completed OK!'`" ] ; then
  print_and_clear
  exit 1
fi

echo $BACKUP_NAME >> $TMP_LOG
echo "Backup complete : `date +%F' '%T' '%w`"
