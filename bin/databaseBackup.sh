#!/bin/sh
innobackupex --user=root --password=root --use-memory=1G --parallel=2 $BACKUP_DIR > $TMP_LOG  2>&1