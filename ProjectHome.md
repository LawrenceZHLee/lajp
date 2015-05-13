### LAJP名称含义 ###
> LAJP名称来源于著名的LAMP(Linux,Apache,Mysql,Php)，LAMP是轻量级的开发Web程序的环境，在Internet上有广泛的应用，但对于企业开发，如金融、电信领域，LAMP略显能力不足，而这些领域通常是Java(J2EE)的势力范围。LAJP是将LAMP的简便性和Java高端能力结合起来的一项技术，LAJP中的J指的是Java，由于数据库厂商对Java的广泛支持，数据库也不再特别限制为Mysql。

> LAJP可以理解为PHP和Java相结合的技术，也可称为PHP和Java混合编程技术，或者PHP调用Java服务的技术，也有人习惯称之为前台PHP后台Java的技术框架。

### 特点 ###
  * **优势互补**: PHP是非常流行的WEB编程脚本语言，有易学、易用、开发部署效率高的特点，非常适合网页编程；JAVA适合编写具有复杂的业务功能和数据的程序，二者结合可发挥各自优势。
  * **高效稳定**：Apache+PHP组合可带来优异的WEB服务稳定性，而JAVA可补充如连接池、事物管理、分布式、对象模型等高端特性。
  * **创新的通信机制** PHP和Java间的通讯方式采用系统消息队列和Socket两种机制，兼顾通讯效率和平台兼容性。
  * **数据类型自动转换机制** PHP数据和Java数据可准确地自动匹配和转换，无须程序员编写解析代码。
  * **易用**：LAJP安装配置简单，PHP端和JAVA端编程符合各自的编程习惯。
  * **轻量级**：LAJP架构非常轻量级，除了最基本的PHP和Java环境，不需要任何扩充的、第三方的组件、容器。

**LAMP和LAJP的简要对比**
LAMP从传统技术架构上看属于2层结构，虽然在php5以后增强了面向对象的能力，有了形成业务逻辑层的语言基础，但对于复杂的企业级WEB应用，php语言能力仍显不足。LAJP继承了LAMP在WEB领域编程的活力，并用java构建业务逻辑层，通过“PHP调用Java的方法”来实现二者间的互通。

![http://lajp.googlecode.com/svn/wiki/images/lamp_lajp.png](http://lajp.googlecode.com/svn/wiki/images/lamp_lajp.png)

### php和java的互通 ###

php和java是两种不同的语言，在LAJP架构中二者之间的互通有两种机制。

  * **一、消息队列**

以操作系统的消息队列为沟通媒介，在通讯过程中php作为客户端调用java端服务。消息队列属于IPC技术(进程间通讯)，php语言中内置了一组函数（msg\_send、msg\_receive等）可以和System V消息队列通讯，而java中没有相似的方法，因此通过调用底层JNI接口使用C函数来实现。
使用消息队列有以下好处：

  1. 使php和java保持独立性
  1. 有极高的传输速度，大于socket
  1. 相对于socket方式，Java服务端只向本机提供服务(没有对外侦听端口)，相对安全，易于管理。

  * **二、Socket**

消息队列技术只能适用于Unix/Linux/BSD系统，因此LAJP提供基于TCP/IP的通讯机制，从而适应各种平台。

![http://lajp.googlecode.com/svn/wiki/images/lajp2model.png](http://lajp.googlecode.com/svn/wiki/images/lajp2model.png)

### 数据类型转换 ###

PHP和Java各有其语言内部定义的数据类型，当PHP数据传送到Java，或Java数据传送到PHP时，LAJP在内部自动地、准确地对他们进行转换，程序员无需进行任何的解码工作。

![http://lajp.googlecode.com/svn/wiki/images/type_covert.png](http://lajp.googlecode.com/svn/wiki/images/type_covert.png)

详细参看《lajp数据转换示例》 http://code.google.com/p/lajp/wiki/Example

### 示例 ###

示例程序表现了一个简单的PHP调用Java的程序片段，PHP在调用过程中向Java传递了3个参数，参数类型分别是字符串、数组、对象，Java服务方法返回字符串应答。

  * **php端程序**

```
  require_once("php_java.php"); //LAJP提供的程序脚本

  //php类，映射到JavaBean类：cn.com.ail.test.Bean
  class cn_com_ail_test_Bean
  {
    var $a = "v1";
    var $b = "v2";
  }

  $p1 = "a";     //字符串，传给Java方法的第一个参数

  $p2 = array(); //数组，传给Java方法的第二个参数
  $p2[] = 10;
  $p2[] = 20;

  $p3 = new cn_com_ail_test_Bean; //php对象，传给Java方法的第三个参数

  //"lajp_call"是LAJP提供的函数，用来调用java端服务
  //"cn.com.ail.test.Objtest::method1"表示调用java的cn.com.ail.test.Objtest类中的method1方法
  //"$p1,$p2,$p3"是向method1方法传递的3个参数。
  $ret = lajp_call("cn.com.ail.test.Objtest::method1", $p1, $p2, $p3);

  echo "返回信息：".$ret;    //打印"OK,收到并返回字符串应答"
```

  * **java端程序**

```
  //对应php中$p3的JavaBean（普通的JavaBean）
  package cn.com.ail.test;
  public class Bean
  {
    private String a;
    private String b;
	
    public String getA()
    {
      return a;
    }
    public void setA(String a)
    {
      this.a = a;
    }
    public String getB()
    {
      return b;
    }
    public void setB(String b)
    {
      this.b = b;
    } 
  }
```

```
  //java端服务
  
  package cn.com.ail.test;
  public class Objtest
  {
    //PHP调用的Java方法（普通的Java方法，LAJP仅要求声明为public static final）
    //php传来的三个参数自动转换为相应的Java数据类型
    public static final String method1(String param1, java.util.List param2, Bean param3)
    {
      System.out.println("$p1=" + param1);
      for (int i = 0; i < param2.size(); i++)
      {
        System.out.printf("$p2[%i]=%i\n", i, (Integer)param2.get(i));
      }
      System.out.println("$p3->a=" + param3.getA());
      System.out.println("$p3->b=" + param3.getB());

      //返回给PHP的应答字符串
      return "OK,收到并返回字符串应答";
    }
  }
```


---

详细请浏览：[LAJP帮助文档](http://code.google.com/p/lajp/wiki/LAJP_manual)
[PHP和Java数据转换说明](http://code.google.com/p/lajp/wiki/Example)