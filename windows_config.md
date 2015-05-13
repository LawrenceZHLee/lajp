
---

## called to undefined function socket\_create 错误 ##

安装好Apache和PHP后，调用lajp\_call(), 出现上述故障。

### 分析 ###

执行：

```
<?php 
var_dump(extension_loaded("sockets")); 
?>
```

如果输出false，是php\_sockets.dll没有加载。

### 解决办法 ###

  * 检查php.ini，确保extension=php\_sockets.dll前没有";"。
```
extension=php_sockets.dll
```


  * 检查httpd.conf有下面几行:
```
# c:/PHP/是php的安装路径(PHPzip解压方式安装)

LoadModule php5_module c:/PHP/php5apache2_2.dll

# 按模块模式加载
PHPIniDir "c:/PHP/"
AddType application/x-httpd-php .php

# 按CGI模式加载, c:/php/是php的安装目录
#ScriptAlias /php/ "c:/php/"
#AddType application/x-httpd-php .php
#Action application/x-httpd-php "/php/php-cgi.exe"
```

  * 检查php目录下有没有php\_sockets.dll文件，如果没有从ext目录中copy过来，php5搜索路径在php，而不是php/ext。

  * 尝试以命令行方式运行，如果命令行可以，将php.ini复制到c:\windows，再试。
```
//命令行方式运行, php.exe和php.ini在php安装目录下
php.exe -c php.ini <路径>\php文件
```



---

## 500 内部服务器错误 ##
php页面出错时只显示"500 内部服务器错误",不显示具体出错的语句，可以通过修改php.ini配置文件:
```
display_errors = Off 
```
改为:
```
display_errors = On
```
即可打开报错调试开关。