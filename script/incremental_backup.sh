#!/bin/sh
print_and_clear()
{
cat E:/project/CORE_TA/pluto/temp/1495175258547.tmp
rm -rf E:/project/CORE_TA/pluto/temp/1495175258547.tmp
}

check_result()
{
if [ -z "`tail -1 E:/project/CORE_TA/pluto/temp/1495175258547.tmp| grep 'completed OK!'`" ] ; then
print_and_clear
exit 1
fi
}

innobackupex --user=root --password=root --incremental /ta --incremental-basedir=/ta/a > E:/project/CORE_TA/pluto/temp/1495175258547.tmp 2>&1 
check_result
print_and_clear
