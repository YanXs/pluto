#!/bin/sh
print_and_clear()
{
cat E:/project/pluto/temp/1490591704284.tmp
rm -rf E:/project/pluto/temp/1490591704284.tmp
}

check_result()
{
if [ -z "`tail -1 E:/project/pluto/temp/1490591704284.tmp| grep 'completed OK!'`" ] ; then
print_and_clear
exit 1
fi
}

shutdown_mysql()
{
if [ `netstat -lnt | grep 3306|wc -l` = 1 ]; then
/etc/init.d/mysqld start
fi
}

startup_mysql()
{
chown -R mysql:mysql /data/tadata
/etc/init.d/mysqld start
}

shutdown_mysql
innobackupex --user=root --password=root --apply-log --export /backup/2017-10-03_11-22-33 > E:/project/pluto/temp/1490591704284.tmp 2>&1 
check_result
cp -rf /backup/2017-10-03_11-22-33/. /data/tadata/

startup_mysql
