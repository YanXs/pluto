#!/bin/sh
print_and_clear()
{
cat F:\project\pluto\temp/1490541299624.tmp
rm -rf F:\project\pluto\temp/1490541299624.tmp
}

check_result()
{
if [ -z "`tail -1 F:\project\pluto\temp/1490541299624.tmp| grep 'completed OK!'`" ] ; then
print_and_clear
exit 1
fi
}

innobackupex --user=root --password=root --databases="account mutual " C:/ta > F:\project\pluto\temp/1490541299624.tmp 2>&1 
check_result
print_and_clear
