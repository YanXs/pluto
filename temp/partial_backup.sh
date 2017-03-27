#!/bin/sh
print_and_clear()
{
cat E:/project/pluto/temp/1490591704274.tmp
rm -rf E:/project/pluto/temp/1490591704274.tmp
}

check_result()
{
if [ -z "`tail -1 E:/project/pluto/temp/1490591704274.tmp| grep 'completed OK!'`" ] ; then
print_and_clear
exit 1
fi
}

innobackupex --user=root --password=root --databases="account mutual " /ta > E:/project/pluto/temp/1490591704274.tmp 2>&1 
check_result
print_and_clear
