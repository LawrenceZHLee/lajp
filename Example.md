# LAJP数据转换示例 #

## 文章简介 ##

本文通过程序样例来介绍LAJP中PHP和Java之间的的数据转换。

## PHP序列化数据简介 ##

在PHP语言中，数据类型是隐匿的，并且是根据上下文而自动变化的，比如：

```
$a = 10;
$a = "a is " . $a;
```

在第一行中，$a是int类型，在第二行中$a变化为string类型。通常“弱”类型语言，像Javascript,VB,PHP等都是这样。PHP中提供了一些函数（is\_array()、is\_bool()、is\_float()、is\_integer()等）来获得变量的类型，更直接的方式是观察变量序列化后的排列规则:


---


```
$a = 10;
echo serialize($a);
```

输出：

```
i:10;
```

i表示int类型，10是其值。


---


```
$a = "abcd";
echo serialize($a);
```

输出：

```
s:4:"abcd";
```

s表示string类型，4表示长度，"abcd"是其值。


---


```
$a = TRUE;
echo serialize($a);
```

输出：

```
b:1;
```

b表示boolean类型，1表示TRUE，0表示FALSE。


---


```
$a = 10.24;
echo serialize($a);
```

输出：

```
d:10.2400000000000002131628207280300557613372802734375;
```

d表示double类型，10.2400000000000002131628207280300557613372802734375是其值。


---


数组、对象等复杂类型也可以序列化：

```
$a = array();
$a[] = 20;
$a[] = "abcde";
$a[] = TRUE;

echo serialize($a);
```

输出：

```
a:3:{i:0;i:20;i:1;s:5:"abcde";i:2;b:1;}
```

开始的a表示array，紧跟着的3表示数组长度，{}内部是数组元素：
  * `i:0;i:20;`是第一个元素，i:0;是KEY（表示下标是int类型的0），i:20;是VALUE。
  * `i:1;s:5:"abcde";`是第二个元素，i:1;是KEY（表示下标是int类型的1），s:5:"abcde";是VALUE。
  * `i:2;b:1;`是第三个元素，i:2;是KEY（表示下标是int类型的2），b:1;是VALUE。

```
$a = array();
$a["a"] = 20;
$a["b"] = "abcde";
$a["c"] = TRUE;

echo serialize($a);
```

输出：

```
a:3:{s:1:"a";i:20;s:1:"b";s:5:"abcde";s:1:"c";b:1;}
```

这里数组下标是字符串，数据结构可以看作是其他语言的Hashtable类型。


---


在LAJP中，PHP和Java之间传输的数据封装形式，即是上面这种PHP序列化数据形式。


## Example 1 基本 ##


---


Java:
```
package aaa.bbb.ccc;
public class MyClass1
{
	public static final int myMethod1(int i)
	{
		return ++i;
	}
}
```


PHP:
```
$a = 10;
echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass1::myMethod1", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---
b--->i:11;<---
b--->11<---
```

在LAJP中，当PHP将整形10传给Java服务时，传送的数据即是字符串`i:10;`，Java服务返回整形11也包装为字符串`i:11;`。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass1
{
	public static final int myMethod2(long i)
	{
		return (int)++i;
	}
}
```


PHP:
```
$a = 10;
echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass1::myMethod2", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---

Fatal error: Uncaught exception 'Exception' with message '[LAJP Error] Response receive Java exception: MethodNotFoundException: Can't match method: aaa.bbb.ccc.MyClass1.myMethod2(long)' in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php:215 Stack trace: #0 /media/sda3/prog/eclipse_php/workspace/LAJP_test/test1_02.php(8): lajp_call('aaa.bbb.ccc.MyC...', 10) #1 {main} thrown in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php on line 215
```

myMethod2方法参数声明为long，在PHP中没有与之对应的数据类型，因此抛出异常。

_也有朋友认为应该允许这种情况，因为在Java中int可以自动转换为long；我的意见还是不允许，因为有带来二义性的可能。_


---


Java:
```
package aaa.bbb.ccc;
public class MyClass1
{
	public static final int myMethod3(Integer i)
	{
		return (int)++i;
	}
}
```


PHP:
```
$a = 10;
echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass1::myMethod3", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---

Fatal error: Uncaught exception 'Exception' with message '[LAJP Error] Response receive Java exception: MethodNotFoundException: Can't match method: aaa.bbb.ccc.MyClass1.myMethod3(java.lang.Integer)' in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php:215 Stack trace: #0 /media/sda3/prog/eclipse_php/workspace/LAJP_test/test1_03.php(8): lajp_call('aaa.bbb.ccc.MyC...', 10) #1 {main} thrown in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php on line 215
```

myMethod3方法参数声明为Integer，在Java语言中有装箱、拆箱，int和Integer通常可互换，但在LAJP中此种情况不被允许。所以，如果PHP传送int，Java方法必声明为基本类型int；传double，必声明为基本类型double；传boolean，必声明为基本类型boolean。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass1
{
	public static final long myMethod4(int i)
	{
		return ++i;
	}
}
```


PHP:
```
$a = 10;
echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass1::myMethod4", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---
b--->O:14:"java_lang_Long":0:{}<---

Catchable fatal error: Object of class __PHP_Incomplete_Class could not be converted to string in /media/sda3/prog/eclipse_php/workspace/LAJP_test/test1_04.php on line 11
```

这里myMethod4方法返回类型为long，在PHP中并没有与之对应的数据类型，但PHP端仍然接收到了:

```
O:14:"java_lang_Long":0:{}
```

其中起始的"O"表示这是一个对象。

在Java端，LAJP是这样来转换返回数据的：

  * 如果方法返回类型是int或包装类Integer，封装为PHP的int序列化数据；
  * 如果返回是double或包装类Double，封装为PHP的float序列化数据；
  * 如果返回是boolean或包装类Boolean，封装为PHP的boolean序列化数据；
  * 如果返回是java.lang.String，封装为PHP的String序列化数据；
  * 如果返回是java.util.List或其子类，封装为PHP的array序列化数据，array下标为递增整数；
  * 如果返回是java.util.Map或其子类，封装为PHP的array序列化数据，array下标为字符串；
  * 如果以上都不是，视为JavaBean，封装为PHP4的对象序列化数据。

本例中，返回类型long被视为最后一种情况。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass1
{
	public static final Integer myMethod5(int i)
	{
		return ++i;
	}
}
```


PHP:
```
$a = 10;
echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass1::myMethod5", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---
b--->i:11;<---
b--->11<---
```


在LAJP中，Java方法的返回类型，当声明为int,boolean,double或它们的包装类型是等价的。我的建议是不要声明为包装类型，将来的版本很可能不再支持。


---


## Example 2 参数 ##


---


Java:
```
package aaa.bbb.ccc;
public class MyClass2
{
	public static final int myMethod1(int a, int b)
	{
		return a + b;
	}
}
```


PHP:
```
$a = 10;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass2::myMethod1", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---

Fatal error: Uncaught exception 'Exception' with message '[LAJP Error] Response receive Java exception: MethodNotFoundException: Can't match method: aaa.bbb.ccc.MyClass2.myMethod1(1 parameters)' in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php:215 Stack trace: #0 /media/sda3/prog/eclipse_php/workspace/LAJP_test/test2_01.php(8): lajp_call('aaa.bbb.ccc.MyC...', 10) #1 {main} thrown in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php on line 215
```


在PHP语言中，方法参数是可变长的，但在LAJP中，不允许。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass2
{
	public static final String myMethod3(boolean a)
	{
		if (a)
		{
			return "input TRUE";
		}
		else
		{
			return "input FALSE";
		}
	}
	//同名方法
	public static final String myMethod3(boolean a, int b)
	{
		if (a)
		{
			return "input TRUE, " + b;
		}
		else
		{
			return "input FALSE, " + b;
		}
	}
}
```


PHP:
```
$a = TRUE;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass2::myMethod3", $a);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->b:1;<---
b--->s:10:"input TRUE";<---
b--->input TRUE<---
```

Java的方法重载(overload)，允许。下面是另一个例子（同名，参数数量也相同）:

Java:
```
package aaa.bbb.ccc;
public class MyClass2
{
	public static final String myMethod4(boolean a)
	{
		if (a)
		{
			return "input TRUE";
		}
		else
		{
			return "input FALSE";
		}
	}

	public static final String myMethod4(int a)
	{
			return "input " + a;
	}
}
```


PHP:
```
$a = TRUE;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$b = lajp_call("aaa.bbb.ccc.MyClass2::myMethod4", 10);

echo "b---&gt;" . serialize($b) . "&lt;---<br/>";
echo "b---&gt;" . $b . "&lt;---<br/>";
```

输出:
```
a--->b:1;<---
b--->s:8:"input 10";<---
b--->input 10<---
```


---


## Example 3 空和异常 ##


---


Java:
```
package aaa.bbb.ccc;
public class MyClass3
{
	public static final int myMethod1(int a, int b)
	{
		return a / b;
	}
}
```


PHP:
```
$a = 10;
$b = 0;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$ret = lajp_call("aaa.bbb.ccc.MyClass3::myMethod1", $a, $b);

echo "ret---&gt;" . serialize($ret) . "&lt;---<br/>";
echo "ret---&gt;" . $ret . "&lt;---<br/>";
```

输出:
```
a--->i:10;<---

Fatal error: Uncaught exception 'Exception' with message '[LAJP Error] Response receive Java exception: InvocationTargetException for call method aaa.bbb.ccc.MyClass3.myMethod1' in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php:215 Stack trace: #0 /media/sda3/prog/eclipse_php/workspace/LAJP_test/test3_01.php(9): lajp_call('aaa.bbb.ccc.MyC...', 10, 0) #1 {main} thrown in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php on line 215
```

这是Java端除0异常的例子。Java方法抛出的异常，如果是Exception或其子类，都可以“抛”给PHP端。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass3
{
	public static final String myMethod2()
	{
		return null;
	}
}
```


PHP:
```
$ret = lajp_call("aaa.bbb.ccc.MyClass3::myMethod2");

echo "ret---&gt;" . serialize($ret) . "&lt;---<br/>";
echo "ret---&gt;" . ($ret == NULL) . "&lt;---<br/>";
```

输出:
```
ret--->N;<---
ret--->1<---
```

这是Java端返回null的例子。

Java:
```
package aaa.bbb.ccc;
public class MyClass3
{
	public static final void myMethod3()
	{
		
	}
}
```


PHP:
```
$ret = lajp_call("aaa.bbb.ccc.MyClass3::myMethod3");

echo "ret---&gt;" . serialize($ret) . "&lt;---<br/>";
echo "ret---&gt;" . ($ret == NULL ? "NULL" : $ret) . "&lt;---<br/>";
```

输出:
```
ret--->N;<---
ret--->NULL<---
```

这是Java端返回void的例子。

在LAJP的9.10版本中，null和void返回类型在PHP端被解释为FALSE，10.04版本中已改正。


---


## Example 4 集合 ##


---


Java:
```
package aaa.bbb.ccc;
public class MyClass4
{
	//方法参数声明为List
	public static final void myMethod1(java.util.List list)
	{
		if (list == null)
		{
			System.out.println("list集合为空");
			return;
		}
		
		System.out.println("list集合长度: " + list.size());
		for (int i = 0; i < list.size(); i++)
		{
			System.out.printf("----list[%d]:%s\n", i, (String)list.get(i));
		}
	}
	//方法参数声明为Map
	public static final void myMethod1(java.util.Map<String, String> map)
	{
		if (map == null)
		{
			System.out.println("map集合为空");
			return;
		}
		
		System.out.println("map集合长度: " + map.size());
		
		java.util.Set<String> keySet = map.keySet();
		for (String key : keySet)
		{
			System.out.printf("----map[%s=>%s]\n", key, (String)map.get(key));
		}
	}
}
```


PHP:
```
$a = array(); //定义一个数组
$a[0] = "aaa";//第一个元素"aaa"
$a[1] = "bbb";//第二个元素"bbb"
$a[2] = "ccc";//第三个元素"ccc"

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

lajp_call("aaa.bbb.ccc.MyClass4::myMethod1", $a);
```

PHP输出:
```
a--->a:3:{i:0;s:3:"aaa";i:1;s:3:"bbb";i:2;s:3:"ccc";}<---
```

Java输出:
```
list集合长度: 3
----list[0]:aaa
----list[1]:bbb
----list[2]:ccc
```

在PHP中，array可以模拟多种数据类型，如队列、哈西、栈等，对应到Java，有以下规定：
  * 如果array的第一个元素的KEY是int类型，对应为Java的java.util.List
  * 如果array的第一个元素的KEY是string类型，对应到Java的java.util.Map
上面的例子中，`$a[0] = "aaa";` 第一个元素KEY是整形，因此调用的是myMethod1(java.util.List list)方法。下面将PHP代码稍加改动：

```
$a["a"] = "aaa";//第一个元素"aaa"
$a["b"] = "bbb";//第二个元素"bbb"
$a["c"] = "ccc";//第三个元素"ccc"
```

array的KEY数据类型被改动为字符串，再来看Java端的输出：

```
map集合长度: 3
----map[b=>bbb]
----map[c=>ccc]
----map[a=>aaa]
```

myMethod1(java.util.Map<String, String> map)方法使用了泛型，在Java中运行态是去泛型化的，因此对于LAJP，泛型无作用。但我建议仍然多用泛型，至少在代码review中清晰很多。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass4
{
	public static final void myMethod2(java.util.ArrayList list)
	{
		if (list == null)
		{
			System.out.println("list集合为空");
			return;
		}
		
		System.out.println("list集合长度: " + list.size());
		for (int i = 0; i < list.size(); i++)
		{
			System.out.printf("----list[%d]:%s\n", i, (String)list.get(i));
		}
	}
}
```

PHP:
```
$a = array();
$a[] = 10;
$a[] = 20;
$a[] = 30;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

lajp_call("aaa.bbb.ccc.MyClass4::myMethod2", $a);
```

PHP输出:
```
a--->a:3:{i:0;i:10;i:1;i:20;i:2;i:30;}<---

Fatal error: Uncaught exception 'Exception' with message '[LAJP Error] Response receive Java exception: MethodNotFoundException: Can't match method: aaa.bbb.ccc.MyClass4.myMethod2(java.util.ArrayList)' in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php:215 Stack trace: #0 /media/sda3/prog/eclipse_php/workspace/LAJP_test/test4_03.php(11): lajp_call('aaa.bbb.ccc.MyC...', Array) #1 {main} thrown in /media/sda3/prog/eclipse_php/workspace/LAJP_test/php_java.php on line 215
```

集合入参类型声明必须是java.util.List或java.util.Map。这里不能声明为ArrayList是因为PHP传入的参数被视为List，而从面向对象角度理解ArrayList继承自List，可以说ArrayList是List，而List不是ArrayList，因此这里类型不匹配。


---


Java:
```
package aaa.bbb.ccc;
public class MyClass4
{
	public static final java.util.HashMap<String, Integer> myMethod4()
	{
		java.util.HashMap map = new java.util.HashMap();
		map.put("aaa", 10);
		map.put("bbb", 20);
		map.put("ccc", 30);
		
		return map; 
	}
}
```

PHP:
```
$ret = lajp_call("aaa.bbb.ccc.MyClass4::myMethod4");
echo "ret---&gt;" . serialize($ret) . "&lt;---<br/>";
```

PHP输出:
```
ret--->a:3:{s:3:"aaa";i:10;s:3:"ccc";i:30;s:3:"bbb";i:20;}<---
```

这里返回类型声明为java.util.Map的子类形是可以的。从面向对象角度理解：HashMap即是Map。


---


## Example 5 对象 ##


---


Java:
```
package aaa.bbb.ccc;
//JavaBean
public class MyBean
{
	private int i;
	private boolean b;
	private String c;
	private java.util.List<Double> list;
	
	public int getI()
	{
		return i;
	}
	public void setI(int i)
	{
		this.i = i;
	}
	public boolean isB()
	{
		return b;
	}
	public void setB(boolean b)
	{
		this.b = b;
	}
	public String getC()
	{
		return c;
	}
	public void setC(String c)
	{
		this.c = c;
	}
	public java.util.List<Double> getList()
	{
		return list;
	}
	public void setList(java.util.List<Double> list)
	{
		this.list = list;
	}
}
```

```
package aaa.bbb.ccc;
public class MyClass5
{
	public static final MyBean myMethod1(MyBean bean)
	{
		if (bean == null)
		{
			return null;
		}
		
		bean.setI(bean.getI() + 1);
		bean.setB(!bean.isB());
		bean.setC(bean.getC() + " OK!");
		
		java.util.ArrayList<Double> retList = new java.util.ArrayList<Double>();
		for (double d : bean.getList())
		{
			retList.add(d + 1);
		}
		bean.setList(retList);
		
		return bean;
	}
}
```


PHP:
```
class aaa_bbb_ccc_MyBean
{
	var $i;
	var $b;
	var $c;
	var $list;
}

$a = new aaa_bbb_ccc_MyBean; //实例化对象
$a->i = 10;
$a->b = TRUE;
$a->c = "zhangsan";
$a->list = array();
$a->list[] = 10.2;
$a->list[] = 20.4;

echo "a---&gt;" . serialize($a) . "&lt;---<br/>";

$ret = lajp_call("aaa.bbb.ccc.MyClass5::myMethod1", $a);
echo "ret---&gt;" . serialize($ret) . "&lt;---<br/>";
```

PHP输出:
```
a--->O:18:"aaa_bbb_ccc_MyBean":4:{s:1:"i";i:10;s:1:"b";b:1;s:1:"c";s:8:"zhangsan";s:4:"list";a:2:{i:0;d:10.199999999999999289457264239899814128875732421875;i:1;d:20.39999999999999857891452847979962825775146484375;}}<---
ret--->O:18:"aaa_bbb_ccc_MyBean":4:{s:1:"i";i:11;s:1:"b";b:0;s:1:"c";s:12:"zhangsan OK!";s:4:"list";a:2:{i:0;d:11.199999999999999289457264239899814128875732421875;i:1;d:21.39999999999999857891452847979962825775146484375;}}<---
```

在LAJP中，对象传输有下面的规则：
  * Java端，对象必须符合JavaBean规则
  * PHP端，必须是PHP4对象（PHP5对象序列化数据不完备，目前不支持）
  * Java和PHP对象映射，属性名称一致（有过Struts编程经历的人很容易理解这点）

LAJP中提供了一个自动生成PHP4对象类的工具，比如可以通过Java的aaa.bbb.ccc.MyBean类，来生成与之对应的PHP4对象类代码：

php:
```
$ret = lajp_call("lajp.ReflectUtil::javaBean2Php", "aaa.bbb.ccc.MyBean");
//$ret = lajp_call("lajpsocket.ReflectUtil::javaBean2Php", "aaa.bbb.ccc.MyBean"); //LAJP的socket版本用这一行
echo $ret;
```

PHP输出:
```
class aaa_bbb_ccc_MyBean
{
    var $b;
    var $c;
    var $i;
    var $list;
}
```


---


对象、数组（Java映射为Map和List）属于容器型数据类型，可以嵌套其他类型数据包括对象和数组，LAJP对嵌套的层级没有限制，但嵌套的子类型（对象内部的属性、数组内部的元素）必须是以下几种：
  * int
  * boolean
  * php(float或double) | Java(double)
  * php(string) | Java(java.lang.String)
  * php4对象 | Java(JavaBean)
  * array | java.util.Map或java.util.List
以上6种数据，也就是LAJP所能支持的可以传输的所有数据类型。


---
