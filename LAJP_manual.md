# LAJP帮助文档 #

LAJP是用来解决PHP和Java通讯的一项技术，在PHP中可以通过"正常"的PHP函数来调用Java的一个方法，如同下面的一个例子：

java(service):
```
package aaa.bbb.ccc;
public class MyClass
{
  public static final int addMethod(int a, int b)
  {
    return a + b;
  }
}
```

php(client):
```
$ret = lajp_call("aaa.bbb.ccc.MyClass::addMethod", 10, 20);
echo $ret;  //30
```

LAJP有两个核心能力：
  1. PHP优雅、高效地调用Java方法的能力
  1. PHP数据和Java数据合理、自动地转换的能力

在LAJP的当前版本中，使用两种技术进行PHP和Java间的通信，我对它们分别命名为： **消息队列模式** 和 **socket模式** 。它们各自有优缺点，在使用中应根据程序所在环境特点加以选择：
  * **消息队列** 以System V的消息队列作为PHP和Java间的通信媒介，优点是理论速度快，占用资源较小；缺点是只能使用在支持System V的系统中，可运用于大多数的Unix/Linux/BSD系统，但不能用于windows。
  * **socket** 以TCP/IP作为PHP和Java间的通信媒介，优点是基本无系统限制；缺点是理论速度慢，占用资源较大。

## 一、LAJP运行环境要求 ##

"消息队列模式"和"socket模式"对运行环境的要求是不同的，下面分别加以阐述：

### 消息队列模式 ###

环境需要满足System V消息队列的运行：

  * **系统** 目前常见的Unix/Linux系统都可满足php(Apache)、java的运行，其中大部分默认支持System V消息队列。

  * **php** php需要通过消息队列和java进程通信，按php的说明，php在4.3.0版本以后支持System V消息队列。

  * **apache** 无特殊要求，满足php要求即可。

  * **java** java版本在1.5以后。

  * 在Unix/Linux环境中，推荐使用消息队列模式。

### socket模式 ###

  * **系统** 没有限制，很难找到不支持TCP/IP的系统。

  * **php** 按php的说明，php版本>=4.1.0支持socket

  * **apache** 无特殊要求，满足php要求即可。

  * **java** java版本在1.5以后。

  * Windows系统只能使用socket模式

**在开发过程中可以同时使用这两种模式，比如一般开发者使用Windows环境，而程序部署在Linux系统中，LAJP在模式的配置上和编码无关。**

## 二、LAJP安装与运行 ##

### Windows下的LAJP安装配置 ###

请阅读 [《图解LAJP在Windows系统上的安装配置》 ](http://code.google.com/p/lajp/wiki/Win_Install)

### Unix/Linux下的LAJP安装配置 ###

  * **下载** 下载Lajp的安装文件，解压后目录结构如下：
```
lajp安装包
  |
  |--jin                      //消息队列模式必要的JNI源程序
  |   |
  |   |--lajp_MsgQ.h
  |   |--lajp_MsgQ.c
  |   |--make.sh
  |   
  |--php                       //PHP端脚本
  |   |
  |   |--php_java.php.msgq
  |   |--php_java.php.socket
  |   
  |--test_service/             //Hello World 示例服务程序
  |   
  |--lajp-10.05.jar            //LAJP主程序
  |--run_msgq.sh               //Unix/Linux使用消息队列模式启动脚本
  |--run-socket.bat            //windows使用启动脚本
  |--run-socket.sh             //Unix/Linux使用socket模式启动脚本
```

#### Unix/Linux中运行LAJP依赖以下前提设置 ####

  * **Apache+php环境** 部分发行版本的php默认安装不支持消息队列(System V messages)、信号量(System V semaphore)、共享内存(System V shared memory), 如使用消息队列模式需在编译php时附带编译选项 --enable-sysvsem,--enable-sysvshm和--enable-sysvmsg；如使用socket模式则要检查sockets是否激活，这些可以通过phpinfo()函数来观察。

  * **java环境** 要求Java5.0以上。

#### Unix/Linux中socket模式的配置运行 ####

Socket模式使用run-socket.sh脚本，运行前确保run-socket.sh有执行权限，在脚本内部可以配置Java服务端口（默认21230），PHP和Java传输字符集（默认UTF-8），classpath等。

#### Unix/Linux中消息队列模式的配置运行 ####

  * **首先配置好c语言编译环境**
  * **编译JNI** 将下载的lajp安装包中的3个源代码文件：lajp\_MsgQ.c,lajp\_MsgQ.h,make.sh复制到某个目录，确保make.sh有执行权限，按注释要求编辑make.sh
```
#!/bin/sh

# -----------------------------------------------------------
#  LAJP-JNI 编译脚本 (2009-09 http://code.google.com/p/lajp/)
#  
#  编译环境: Unix/Linux
#  
#  源文件: lajp_MsgQ.c lajp_MsgQ.h
#  目标文件: liblajpmsgq.so
#  编译参数:
#    --share  : 编译为动态库
#    -I       : 搜索编译JNI需要的.h文件, 注意"/usr/lib/jvm/java-6-sun/"要换成编译环境中
#               的JAVA_HOME路径
#
#  liblajpmsgq.so发布 : 
#    复制到<java.library.path>中，可通过java程序
#    System.out.println(System.getProperties().getProperty("java.library.path")); 
#    获得本机的<java.library.path>
# -----------------------------------------------------------


gcc lajp_MsgQ.c --share -I. -I/usr/lib/jvm/java-6-sun/include -I/usr/lib/jvm/java-6-sun/include/linux -o liblajpmsgq.so
```
    1. 编译 ： 执行 ./make.sh
    1. 部署编译好的liblajpmsgq.so库文件： 如果编译成功，会生成liblajpmsgq.so文件，将它复制到任一个"java.library.path"路径中，"java.library.path"路径可以通过java程序侦测: System.out.println(System.getProperties().getProperty("java.library.path"))

消息队列模式使用run\_msgq.sh脚本，运行前确保run\_msgq.sh有执行权限，在脚本内部可以配置PHP和Java传输字符集（默认UTF-8），classpath等。




## 三、LAJP使用注意事项 ##

  * **Java** Java方法如果要做为LAJP的服务方法，必须声明为 **public static final**

  * **数据类型** PHP和Java通过LAJP传输的数据，包括PHP调用时向Java传递的参数，和Java方法的返回值，都需要遵循LAJP数据类型要求。

php语言规范定义了8中数据类型：boolean、int、float、string、array、object、resource、NULL；java语言的数据类型分为2类：基本数据类型和对象类型，基本数据类型有byte、short、int、long、 char、boolean、float、double, 对象类型包括数组、集合、javaBean等。在LAJP架构中，php数据以参数形式传递给Java方法，Java方法的返回值再回传给php调用程序，在调用过程中，php数据“自动”转换为Java数据，反之亦然。

并不是所有数据类型都可以转换，在LAJP中建立了以下转换规则：

表1
| | **php** | **java** | **说明** |
|:|:--------|:---------|:-----------|
| 布尔 | boolean | boolean |  |
| 整形 |int|int|  |
| 浮点 |float|double| 在php中float和double含义相同 |
| 字符串 |string|java.lang.String|  |
| 顺序集合 |array(_key:int_)|java.util.List| php中array的每个元素的key类型必须是int |
| key-value集合 |array(_key:string_)|java.util.Map| php中array的每个元素的key类型必须是string |
| 对象 |object| _JavaBean_ |  |
| 空 |NULL|null|  |

详细的数据转换规则请查阅 [《LAJP数据转换示例》 ](http://code.google.com/p/lajp/wiki/Example)



## 四、LAJP基本配置 ##

LAJP只是单纯的PHP和Java传输的中间机制，像Web系统中常见的数据库连接池、JNDI、缓存等需要开发者自己管理。

  * **消息队列配置**

对于消息队列，有三个系统配置影响其性能：
  1. MSGMNI 指定系统中消息队列最大数目
  1. MSGMAX 指定一个消息的最大长度
  1. MSGMNB 指定在一个消息队列中最大的字节数

一般性的，Linux系统的默认消息队列配置非常可怜，通过查看下面三个文件获得系统配置信息：
  * /proc/sys/kernel/msgmni 缺省设置：16
  * /proc/sys/kernel/msgmax 缺省设置：8192
  * /proc/sys/kernel/msgmnb 缺省设置：16384
为了更好的性能，可编辑`/etc/sysctl.conf`文件，修改缺省配置：
```
# /etc/sysctl.conf

# set message queue 20M
kernel.msgmnb = 20971520
kernel.msgmni = 20480
```

  * **socket侦听端口配置**

在socket模式中，Java端默认的侦听端口是21230，如要修改，有两处：

  * php\_java.php ： 在下载包中命名为php\_java.php.socket
```
define("LAJP_IP", "127.0.0.1");			//Java端IP
define("LAJP_PORT", 新的端口);			//Java端侦听端口
```

  * run-socket启动脚本 ： windows中使用run-socket.bat，Unix/Linux中使用run-socket.sh

windows中在run-socket.bat中添加下面两行：
```
rem 设置服务侦听端口
set SERVICE_PORT=新的端口
```

Unix/linux中在run-socket.sh中修改：
```
# 设置服务侦听端口
export SERVICE_PORT=新的端口
```

  * **传输字符集**

LAJP中默认的PHP和Java交互字符集是UTF-8，可通过修改启动脚本环境变量变更。

run-socket.bat
```
rem 字符集设置  GBK | UTF-8
set CHARSET=字符集
```

run-socket.sh或run-msgq.sh
```
# 字符集设置  GBK|UTF-8
export CHARSET=字符集
```

### 配置示例 ###

一般性的使用Java链接数据库需要配置数据源，这里提供一个简单的配置示例，以供参考。

项目：
  1. 一个简单的Web应用
  1. 数据库：Mysql
  1. 数据源：DBCP

run-msgq.sh
```
#!/bin/sh

# -----------------------------------------------------------
#  LAJP-Java Service 启动脚本 
#		
# 		(2009-10 http://code.google.com/p/lajp/)
#  
# -----------------------------------------------------------

#-----------------------
#DBCP配置
export DBCP_url="jdbc:mysql://127.0.0.1:3306/paper?characterEncoding=utf8&zeroDateTimeBehavior=round"
export DBCP_username=root
export DBCP_password=root
export DBCP_maxActive=30
export DBCP_maxIdle=10
export DBCP_maxWait=1000
export DBCP_removeAbandoned=false
export DBCP_removeAbandonedTimeout=120
export DBCP_testOnBorrow=true
export DBCP_validationQuery="select 1"
export DBCP_logAbandoned=true
#-----------------------


# java服务中需要的jar文件或classpath路径，如业务程序、第三方jar文件(如log4j等)
export classpath=lib/commons-beanutils-1.8.2.jar:lib/commons-logging-1.1.1.jar:lib/log4j-1.2.8.jar:lib/commons-dbcp-1.2.2.jar:lib/commons-collections-3.2.1.jar:lib/commons-pool-1.5.4.jar:lib/mysql-connector-java-5.1.7-bin.jar:lib/lajp_10.04.jar:bin/

# 自动启动类和方法，LAJP服务启动时会自动加载并执行
export AUTORUN_CLASS=cn.programmerdigest.Init
export AUTORUN_METHOD=init

# 字符集设置  GBK|UTF-8
# export CHARSET=GBK

# LAJP服务启动指令(前台)
java -classpath .:$classpath lajp.PhpJava

# LAJP服务启动指令(后台)
# nohup java -classpath .:$classpath lajp.PhpJava &
```

Java自动启动方法cn.programmerdigest.Init类中的init方法:
```
	/**
	 * 初始化DBCP数据源
	 */
	public static void init()
	{

		try
		{
			//从环境变量中获取DBCP配置信息
			Properties p = new Properties();
			p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
			p.setProperty("url", System.getenv("DBCP_url"));
			p.setProperty("username", System.getenv("DBCP_username"));
			p.setProperty("password", System.getenv("DBCP_password"));
			p.setProperty("maxActive", System.getenv("DBCP_maxActive"));
			p.setProperty("maxIdle", System.getenv("DBCP_maxIdle"));
			p.setProperty("maxWait", System.getenv("DBCP_maxWait"));
			p.setProperty("removeAbandoned", System.getenv("DBCP_removeAbandoned"));
			p.setProperty("removeAbandonedTimeout", System.getenv("DBCP_removeAbandonedTimeout"));
			p.setProperty("testOnBorrow", System.getenv("DBCP_testOnBorrow"));
			p.setProperty("validationQuery", System.getenv("DBCP_validationQuery"));
			p.setProperty("logAbandoned", System.getenv("DBCP_logAbandoned"));

			//创建数据源
			dataSource = (BasicDataSource) BasicDataSourceFactory
					.createDataSource(p);

		}
		catch (Exception e)
		{
			//--
			throw new RuntimeException(e.getMessage());
		}
	}
```


## 五、其他的文档 ##

LAJP的blog http://programmerdigest.cn/category/lajp