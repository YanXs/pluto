#!/bin/sh
print_and_clear()
{
cat E:/project/CORE_TA/pluto/temp/1495175258541.tmp
rm -rf E:/project/CORE_TA/pluto/temp/1495175258541.tmp
}

check_result()
{
if [ -z "`tail -1 E:/project/CORE_TA/pluto/temp/1495175258541.tmp| grep 'completed OK!'`" ] ; then
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

move_data_dir()
{
if [ -d /data/tadata_bak ]; then
rm -rf /data/tadata_bak
fi
if [ -d /data/tadata ]; then
mv /data/tadata /data/tadata_bak
fi
}

make_data_dir()
{
mkdir -p /data/tadata
}

startup_mysql()
{
chown -R mysql:mysql /data/tadata
/etc/init.d/mysqld start
}

shutdown_mysql
move_data_dir
make_data_dir
innobackupex --user=root --password=root --apply-log  /backup/2017-10-03_11-22-33 > E:/project/CORE_TA/pluto/temp/1495175258541.tmp 2>&1 
check_result
print_and_clear
innobackupex --user=root --password=root  --copy-back /backup/2017-10-03_11-22-33 > E:/project/CORE_TA/pluto/temp/1495175258541.tmp 2>&1 
check_result
print_and_clear
startup_mysql
