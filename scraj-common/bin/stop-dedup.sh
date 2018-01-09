#!/bin/sh

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 
#
#
#   spider dedup shell script
#
#
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

#
#
#
cd $(dirname "$0")
cd ..

echo '****************************'
echo '-dedup rmi server stopping--'
echo '****************************'

#
#
#
kill -15 `cat uyint_dedup.pid` 2>/dev/null


#
#
#
echo '****************************'
echo '--dedup rmi server stopped--'
echo '****************************'
