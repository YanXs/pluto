#!/bin/sh

if [ $# = 0 ]; then
  echo "args count is illegal"
  exit 1
fi

BACKUP_DIR="$1"
BACKUP_NAME="$2"
TMP_LOG="$BACKUP_DIR"/fullBackup.`date +%Y%m%d%H%M`.log

print_and_clear()
{
  cat $TMP_LOG
  rm -rf $TMP_LOG
}

echo "Backup begin... : `date +%F' '%T' '%w`"

innobackupex --user=root --password=root --use-memory=8G --parallel=8 $BACKUP_DIR > $TMP_LOG  2>&1

if [ -z "`tail -1 $TMP_LOG | grep 'completed OK!'`" ] ; then
  print_and_clear
  exit 1
fi

print_and_clear
echo "Backup complete : `date +%F' '%T' '%w`"

