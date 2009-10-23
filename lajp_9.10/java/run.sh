#!/bin/sh

# -----------------------------------------------------------
#  LAJP-Java Service 启动脚本 
#		
# 		(2009-10 http://code.google.com/p/lajp/)
#  
# -----------------------------------------------------------

# java服务中需要的jar文件或classpath路径，如业务程序、第三方jar文件log4j等
# export other_classpath=<some jars>:<some classpath>

# 自动启动类和方法，LAJP服务启动时会自动加载并执行
export AUTORUN_CLASS=com.foo.AutoRunClass
# export AUTORUN_METHOD=AutoRunMethod

# 字符集设置  GBK|UTF-8
# export CHARSET=GBK

# LAJP服务启动指令(前台)
java -classpath .:lajp_9.10.jar:$other_classpath lajp.PhpJava

# LAJP服务启动指令(后台)
# nohup java -classpath lajp_9.10.jar:$other_classpath lajp.PhpJava &
