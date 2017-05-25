#!/bin/sh
print_and_clear()
{
cat E:/project/CORE_TA/pluto/temp/1495175258557.tmp
rm -rf E:/project/CORE_TA/pluto/temp/1495175258557.tmp
}

check_result()
{
if [ -z "`tail -1 E:/project/CORE_TA/pluto/temp/1495175258557.tmp| grep 'completed OK!'`" ] ; then
print_and_clear
exit 1
fi
}

shutdown_mysql()
{
if [ `netstat -lnt | grep 3306|wc -l` = 1 ]; then
/etc/init.d/mysqld stop
fi
}

startup_mysql()
{
chown -R mysql:mysql /data/tadata
/etc/init.d/mysqld start
}

shutdown_mysql
cp -rf /backup/2017-10-03_11-22-33/. /data/tadata/

startup_mysql
