#!/bin/sh

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#
#
#   spider dedup shell script
#
#
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

echo '****************************'
echo '-dedup rmi server starting--'
echo '****************************'

#
#
#
source /etc/profile
source ~/.bashrc


#
# 设置 jdk 的安装目录
# 打开JAVA_HOME的注释
# JAVA_HOME=${JAVA_HOME}

#
#
# 进入uyint目录
cd $(dirname $0)
cd ..

#
#
# 错误输出文件
errFile="dedup.err"


#
#
# 启动dedup进程
# 进程号写入文件
${JAVA_HOME}/bin/java -Djava.rmi.server.hostname=127.0.0.1 -cp conf:lib/*:spider-dedup-1.0-SNAPSHOT.jar com.hiekn.spider.dedup.server.DedupServer 1>/dev/null 2>$errFile &
pid=$!

#
#
#
sleep 2


retCode=0

#
#
#
if [ -s $errFile ]; then
  kill -15 $pid 2>/dev/null
  retCode=1
  echo '****************************'
  echo '--dedup rmi server start failed--'
  echo '****************************'
else
  echo $pid > uyint_dedup.pid
  echo '****************************'
  echo '--dedup rmi server start success--'
  echo '****************************'
fi

#
#
#
rm -f $errFile

#
exit $retCode 
#
