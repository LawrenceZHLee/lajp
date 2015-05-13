# 快速创建LAJP的Hello World程序 #

## 第一步 下载 ##

在[Downloads标签页](http://code.google.com/p/lajp/downloads/list)中下载压缩文件`lajp_<version>.tar.gz`，或`lajp-socket_<version>.tar.gz`，`<version>`表示版本号，LAJP的发行版本并不是通常1.0，2.0的递增形式，而是以发行时间所在的年月作为版本号，比如`lajp_9.09.tar.gz`表示2009年9月发布。

_针对windows用户，还提供了zip格式下载文件。_

下载文件名中带有socket后缀的，如`lajp-socket_9.09.tar.gz`表示socket通讯模式发行版，没有的表示消息队列通讯模式发行版，二者区别在[LAJP帮助文档](http://code.google.com/p/lajp/wiki/LAJP_manual)中有详细描述。

_Windows环境只能安装socket版，Unix/Linux环境两者都可以。_

## 第二步 安装和配置 ##

在安装LAJP之前需要准备好PHP和Java的运行环境，可参考[LAJP帮助文档](http://code.google.com/p/lajp/wiki/LAJP_manual)的相关章节，或查阅PHP和JAVA的官方文档有关搭建Apache+PHP/IIS+PHP，和安装配置JSDK的相关章节。

解压缩刚下载的文件，目录结构如下：
```
lajp_9.09                            //消息队列版
   |
   |--java
   |    |
   |    |--hello                     //Hello World示例程序
   |    |    |
   |    |    |--HelloClass.class
   |    |    +--HelloClass.java
   |    |
   |    |--lajp_9.09.jar             //Java服务端主程序
   |    +--run.sh                    //运行脚本
   |
   |--jni                            //JNI源代码
   |   |
   |   |--lajp_MsgQ.c
   |   |--lajp_MsgQ.h
   |   +--make.sh
   |    
   +--php
       |
       +--php_java.php               //PHP脚本
```

```
lajp－socket_9.09                    //socket版
   |
   |--java
   |    |
   |    |--hello                     //Hello World示例程序
   |    |    |
   |    |    |--HelloClass.class
   |    |    +--HelloClass.java
   |    |
   |    |--lajp_9.09.jar             //Java服务端主程序
   |    |--run.sh                    //运行脚本(Unix/Linux)
   |    +--run.bat                   //运行脚本(Windows)
   |
   +--php
       |
       +--php_java.php               //PHP脚本
```

如果解压的是消息队列版本文件，需要编译JNI：
  1. 配置gcc编译环境，详细参见[LAJP帮助文档](http://code.google.com/p/lajp/wiki/LAJP_manual)。
  1. cd jni目录
  1. `chmod +x make.sh       //`设置make.sh有执行权限
  1. `vi make.sh             //`编辑make.sh，按注释修改脚本，详细参见[LAJP帮助文档](http://code.google.com/p/lajp/wiki/LAJP_manual)。
  1. `./make.sh              //`编译
  1. 将编译出的动态库文件liblajpmsgq.so复制到/usr/lib目录。

_JNI动态库.so需要复制到java.library.path路径中，此路径可通过Java程序System.out.println(System.getProperties().getProperty("java.library.path"))得到。_

## 第三步 编写php helloworld 程序 ##

复制解压目录中的php\_java.php到PHP发布目录，编写hello.php脚本程序:
```
<?php header("Content-Type:text/html;charset=utf-8"); ?>

<?PHP
require_once("php_java.php");  //引用下载的php_java.php文件

$name = "Ali";

try
{
  //调用Java的hello.HelloClass类中的hello方法
  $ret = lajp_call("hello.HelloClass::hello", $name);
  echo "{$ret}<br>";
}
catch (Exception $e)
{
  echo "Err:{$ret}<br>";
}
?>
```

## 第四步 运行 ##

  * 1.在解压目录的java目录中，运行run.sh(Unix/Linux)或run.bat(Windows)，启动Java服务端。
Unix/Linux 消息队列版:
```
$ ./run.sh 
-------------------------------------------
- Start LAJP-JAVA...            
- time:Tue Sep 22 22:50:43 CST 2009
-------------------------------------------
init [IPC] Message Queue OK...
init [IPC] Shared Memory OK...
init [IPC] Semaphore OK...
set charser: UTF-8
Start LAJP-JAVA OK...
```
Unix/Linux Socket版:
```
$ ./run.sh 
-------------------------------------------
- Start LAJP-JAVA(socket)...            
- time:Tue Sep 22 22:53:15 CST 2009
-------------------------------------------
Listen port: 21230
set charser: UTF-8
```
Windows:
```
C:\develop-socket_9.09\java>run.bat
-------------------------------------------
- Start LAJP-JAVA(socket)...
- time:Tue Sep 22 22:56:09 CST 2009
-------------------------------------------
Listen port: 21230
set charser: UTF-8
```
  * 2.打开浏览器，访问刚创建的hello.php地址，如`http://127.0.0.1/hello.php`，LAJP正常运作页面显示"Hello World! Ali"。