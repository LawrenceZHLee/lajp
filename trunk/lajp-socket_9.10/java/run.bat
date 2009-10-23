@echo off

rem -----------------------------------------------------------
rem  LAJP-Java Socket Service 启动脚本 
rem		
rem 		(2009-10 http://code.google.com/p/lajp/)
rem  
rem -----------------------------------------------------------

rem java服务中需要的jar文件或classpath路径，如业务程序、第三方jar文件log4j等
rem set other_classpath=<some jars>;<some classpath>

rem 自动启动类和方法，LAJP服务启动时会自动加载并执行
rem set AUTORUN_CLASS=com.foo.AutoRunClass
rem set AUTORUN_METHOD=AutoRunMethod

rem 字符集设置  GBK | UTF-8
rem set CHARSET=GBK

rem LAJP服务启动指令
java -classpath .;lajp-socket_9.10.jar;%other_classpath% lajpsocket.PhpJava
