_因Linux发行版众多，安装步骤差别很大，本文档仅作为参考_

## Ubuntu ##

在ubuntu中安装gcc非常简单，只需运行:

```
apt-get install build-essential
```

安装 build-essential 这个软件包,这个包会自动安装上 g++,libc6-dev,linux-libc-dev,libstdc++6-4.1-dev 等一些必须的包。

## Redhat AS4 ##

最好在安装系统时就选择安装编译环境，否则会遇到rpm的相互依赖问题。

从安装光盘中找到以下rpm,按照下面的顺序执行:

```
rpm -Uvh glibc-kernheaders-2.4-9.1.98.EL.i386.rpm
rpm -Uvh glibc-headers-2.3.4-2.13.i386.rpm
rpm -Uvh glibc-devel-2.3.4-2.13.i386.rpm
rpm -Uvh cpp-3.4.4-2.i386.rpm
rpm -Uvh gcc-3.4.4-2.i386.rpm
```

## Redhat AS5 ##

最好在安装系统时就选择安装编译环境，否则会遇到rpm的相互依赖问题。

从安装光盘中找到以下rpm,按照下面的顺序执行:

```
rpm -ivh kernel-headers-2.6.18-53.el5.i386.rpm
rpm -ivh glibc-headers-2.5-18.i386.rpm
rpm -ivh glibc-devel-2.5-18.i386.rpm
rpm -ivh cpp-4.1.2-14.el5.i386.rpm 
rpm -ivh gcc-4.1.2-14.el5.i386.rpm
rpm -ivh libgomp-4.1.2-14.el5.i386.rpm
```