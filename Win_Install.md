# 图解LAJP在Windows系统上的安装配置 #

在Windows系统上运行PHP有多种选择，这里描述的是比较流行的Apapche+PHP方式。

### 一、安装Apache ###

从Apache网站下载Apache服务安装程序，直接运行：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_01.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_01.png)

点击运行：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_02.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_02.png)

Next：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_03.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_03.png)

Next：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_04.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_04.png)

输入域名，如果是开发机器随便起名，Next：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_05.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_05.png)

Next：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_06.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_06.png)

选择安装路径，NEXT：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_07.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_07.png)

Install：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_08.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_08.png)

安装：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_09.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_09.png)

安装完成后，在系统托盘中出现Apache服务图标：

![http://lajp.googlecode.com/svn/wiki/images/apache_install_10.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_10.png)

测试一下Apache服务，在地址栏中敲入 http://127.0.0.1  正常显示It works!页面。

![http://lajp.googlecode.com/svn/wiki/images/apache_install_11.png](http://lajp.googlecode.com/svn/wiki/images/apache_install_11.png)

### 二、安装PHP ###

从PHP网站下载PHP安装程序，直接运行：

![http://lajp.googlecode.com/svn/wiki/images/php_install_01.png](http://lajp.googlecode.com/svn/wiki/images/php_install_01.png)

Next：

![http://lajp.googlecode.com/svn/wiki/images/php_install_02.png](http://lajp.googlecode.com/svn/wiki/images/php_install_02.png)

Next：

![http://lajp.googlecode.com/svn/wiki/images/php_install_03.png](http://lajp.googlecode.com/svn/wiki/images/php_install_03.png)

选择PHP的安装路径，Next：

![http://lajp.googlecode.com/svn/wiki/images/php_install_04.png](http://lajp.googlecode.com/svn/wiki/images/php_install_04.png)

这里选择PHP和WWW服务器的组合方式，这里选择PHP作为Apapche的模块运行方式，Next：

![http://lajp.googlecode.com/svn/wiki/images/php_install_05.png](http://lajp.googlecode.com/svn/wiki/images/php_install_05.png)

输入Apapche配置目录路径，注意和上面安装的Apache相一致，Next：

![http://lajp.googlecode.com/svn/wiki/images/php_install_06.png](http://lajp.googlecode.com/svn/wiki/images/php_install_06.png)

注意在Extensions中选择安装Socket，这一点非常重要：

![http://lajp.googlecode.com/svn/wiki/images/php_install_07.png](http://lajp.googlecode.com/svn/wiki/images/php_install_07.png)

![http://lajp.googlecode.com/svn/wiki/images/php_install_08.png](http://lajp.googlecode.com/svn/wiki/images/php_install_08.png)

点击Install安装：

![http://lajp.googlecode.com/svn/wiki/images/php_install_09.png](http://lajp.googlecode.com/svn/wiki/images/php_install_09.png)

安装结束：

![http://lajp.googlecode.com/svn/wiki/images/php_install_10.png](http://lajp.googlecode.com/svn/wiki/images/php_install_10.png)

PHP安装结束后，需要重启Apapche服务，在托盘中点击Apapche图标，在弹出菜单中先点击“Stop”，然后“Start”

![http://lajp.googlecode.com/svn/wiki/images/php_install_11.png](http://lajp.googlecode.com/svn/wiki/images/php_install_11.png)

写一个PHP文件，进行测试。在Apache安装目录下的htdocs目录中创建一个文件“test.php”：

```
<?php
phpinfo();
?>
```

打开浏览器，地址栏输入 http://127.0.0.1/test.php，输出页面如下：

![http://lajp.googlecode.com/svn/wiki/images/php_install_12.png](http://lajp.googlecode.com/svn/wiki/images/php_install_12.png)

观察phpinfo中Sockets support，如果是enabled，PHP安装顺利。

### 安装Java环境 ###

去Java网站，下载Java虚拟机：

![http://lajp.googlecode.com/svn/wiki/images/java_install_01.png](http://lajp.googlecode.com/svn/wiki/images/java_install_01.png)

根据自己的机器和操作系统，选择适合的Java虚拟机：

![http://lajp.googlecode.com/svn/wiki/images/java_install_02.png](http://lajp.googlecode.com/svn/wiki/images/java_install_02.png)

运行安装程序：

![http://lajp.googlecode.com/svn/wiki/images/java_install_03.png](http://lajp.googlecode.com/svn/wiki/images/java_install_03.png)

安装：

![http://lajp.googlecode.com/svn/wiki/images/java_install_04.png](http://lajp.googlecode.com/svn/wiki/images/java_install_04.png)

针对Windows的安全警告，选择“运行”：

![http://lajp.googlecode.com/svn/wiki/images/java_install_05.png](http://lajp.googlecode.com/svn/wiki/images/java_install_05.png)

开始安装：

![http://lajp.googlecode.com/svn/wiki/images/java_install_06.png](http://lajp.googlecode.com/svn/wiki/images/java_install_06.png)

安装成功：

![http://lajp.googlecode.com/svn/wiki/images/java_install_07.png](http://lajp.googlecode.com/svn/wiki/images/java_install_07.png)

### 四、安装LAJP ###

从LAJP的网站 http://code.google.com/p/lajp/downloads/list 下载最新的LAJP开发包(目前lajp-10.05.zip)。

解压到指定目录，本文中为 C:\lajp-10.05 (为方便叙述，下面以C:\lajp-10.05为指定目录)。

双击运行 run-socket.bat，如果是第一次运行Java程序，Windows会弹出安全警报，点击“解除阻止”：

![http://lajp.googlecode.com/svn/wiki/images/java_install_08.png](http://lajp.googlecode.com/svn/wiki/images/java_install_08.png)

cmd窗口中显示下图，表示Java端服务已经启动，“Listen port: 21230”表示LAJP中Java服务的侦听端口，“UTF-8”表示PHP和Java通讯使用的字符集，这两个设置可在run-socket.bat文件中修改。

![http://lajp.googlecode.com/svn/wiki/images/java_install_09.png](http://lajp.googlecode.com/svn/wiki/images/java_install_09.png)

拷贝 C:\lajp-10.05\php\php\_java.php.socket 到Web发布目录，前面的Apapche安装后Web发布目录在"C:\Program Files\Apache Software Foundation\Apache2.2\htdocs"。

进入目录"C:\Program Files\Apache Software Foundation\Apache2.2\htdocs",将“php\_java.php.socket”改名为“php\_java.php”，再创建一个测试PHP文件“hello.php”：

```
<?php

require_once("php_java.php"); //引用LAJP提供的PHP脚本

$name = "LAJP";  //定义一个名称

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

浏览器地址栏输入 http://127.0.0.1/hello.php 能输出"Hello World! LAJP"则表示成功:

![http://lajp.googlecode.com/svn/wiki/images/java_install_10.png](http://lajp.googlecode.com/svn/wiki/images/java_install_10.png)

【完】