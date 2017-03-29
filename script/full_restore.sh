#!/bin/sh
print_and_clear()
{
cat F:/project/pluto/temp/1490797049040.tmp
rm -rf F:/project/pluto/temp/1490797049040.tmp
}

check_result()
{
if [ -z "`tail -1 F:/project/pluto/temp/1490797049040.tmp| grep 'completed OK!'`" ] ; then
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
innobackupex --user=root --password=root --apply-log  /backup/2017-10-03_11-22-33 > F:/project/pluto/temp/1490797049040.tmp 2>&1 
check_result
print_and_clear
innobackupex --user=root --password=root  --copy-back /backup/2017-10-03_11-22-33 > F:/project/pluto/temp/1490797049040.tmp 2>&1 
check_result
print_and_clear
startup_mysql
