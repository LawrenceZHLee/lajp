//-----------------------------------------------------------
// LAJP-java (2009-09 http://code.google.com/p/lajp/)
// 
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajp;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil
{
	/**
	 * 反射获得Class, 本方法有缓存。
	 * @param clazzName
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private static Class<?> getClass(String clazzName) throws ClassNotFoundException
	{
		//从缓存中获取class
		Class<?> clazz = Buffer.clazzMap.get(clazzName);
		if (clazz == null)
		{
			clazz = Class.forName(clazzName);
			Buffer.clazzMap.put(clazzName, clazz);
		}
		
		return clazz;
	}
	
	/**
	 * 从类中获得所有public、static、final方法。本方法有缓存
	 * @param clazzName 类名
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private static List<Method> getAllStaticPublicFinalMethods(String clazzName) throws ClassNotFoundException
	{
		//从缓存中获取class
		Class<?> clazz = getClass(clazzName);
		
		//从缓存中获得
		List<Method> retList = Buffer.methodMap.get(clazzName);
		if (retList != null)
		{
			//缓存中有，直接返回
			return retList;
		}
		else
		{
			//缓存中无
			retList = new ArrayList<Method>();
		}
		
		//获得本类(不包括super)中的所有方法
		Method[] methods = clazz.getDeclaredMethods(); 
		for (Method method : methods)
		{
			//获得方法的Java语言修饰符
			int m = method.getModifiers();
			
			//public、static、final方法
			if (Modifier.isPublic(m) && Modifier.isStatic(m) && Modifier.isFinal(m))
			{
				retList.add(method);
			}
		}
		
		//缓存
		Buffer.methodMap.put(clazzName, retList);
		
		return retList;
	}
	
	/**
	 * 根据类名、方法名、入参类型匹配方法
	 * @param clazzName 方法所属类
	 * @param method 方法名
	 * @param argsClazz 有同名方法时，按照入参类型匹配最相近者，如果最相近者不止一个，抛出异常
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws MethodNotFoundException 
	 */
	public static Method matchingMethod(String clazzName, String methodName, Class<?>[] argsClazz) throws ClassNotFoundException, MethodNotFoundException
	{
		//获得类中所有public、static方法
		List<Method> methodsInClass = getAllStaticPublicFinalMethods(clazzName);
		
		//匹配出方法名称相同的---------------------------
		List<Method> sameNameMethods = new ArrayList<Method>();
		for (Method method : methodsInClass)
		{
			if (method.getName().equals(methodName))
			{
				sameNameMethods.add(method);
			}
		}
		//没匹配到
		if (sameNameMethods.size() == 0)
		{
			throw new MethodNotFoundException("Can't match method: " + clazzName + "." + methodName + ", it must be [public] [static] [final]."); 
		}
		
		//按入参类型匹配---------------------------------
		//唯一同名方法
		if (sameNameMethods.size() == 1)
		{
			//方法形参
			Class<?>[] paramTypes = sameNameMethods.get(0).getParameterTypes();
			
//			System.out.println("形参数量:" + paramTypes.length);
//			System.out.println("实参数量:" + argsClazz.length);
			
			//参数数量不一致
			if (paramTypes.length != argsClazz.length)
			{
				//抛出异常
				StringBuilder sb = new StringBuilder();
				sb.append(clazzName);
				sb.append(".");
				sb.append(methodName);
				sb.append("(");
				sb.append(argsClazz.length).append(" parameters");
				sb.append(")");
				throw new MethodNotFoundException("Can't match method: " + sb.toString()); 
			}
			else //参数数量一致, 匹配参数类型
			{
				boolean matchOK = true;
				for (int i = 0; i < argsClazz.length; i++)
				{
					if (argsClazz[i] == null) //不比较null实参
					{
						continue;
					}
					else
					{
						if (!argsClazz[i].equals(paramTypes[i]))
						{
							matchOK = false;
							break;
						}
					}
				}
				
				if (matchOK)
				{
					return sameNameMethods.get(0);
				}
				else
				{
					//抛出异常
					StringBuilder sb = new StringBuilder();
					sb.append(clazzName);
					sb.append(".");
					sb.append(methodName);
					sb.append("(");
					for (int i = 0; i < argsClazz.length; i++)
					{
						sb.append(argsClazz[i] == null ? "null" : paramTypes[i].getName());
						if (i != argsClazz.length - 1)
						{
							sb.append(",");
						}
					}
					sb.append(")");
					throw new MethodNotFoundException("Can't match method: " + sb.toString()); 
				}
			}
		}
		else //有2个以上同名方法
		{
			//先匹配参数数量
			ArrayList<Method> countMatch = new ArrayList<Method>();
			for (Method method : sameNameMethods)
			{
				//方法形参类型数组
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length == argsClazz.length)
				{
					countMatch.add(method);
				}

			}
			if (countMatch.size() == 0) //形参数量匹配不上
			{
				//抛出异常
				StringBuilder sb = new StringBuilder();
				sb.append(clazzName);
				sb.append(".");
				sb.append(methodName);
				sb.append("(");
				for (int i = 0; i < argsClazz.length; i++)
				{
					sb.append(argsClazz[i].getName());
					if (i != argsClazz.length - 1)
					{
						sb.append(",");
					}
				}
				sb.append(")");
				throw new MethodNotFoundException("Can't match method: " + sb.toString()); 
			}
			
			//匹配形参类型
			ArrayList<Method> allMatchMeshods = new ArrayList<Method>();
			for (Method method : countMatch)
			{
				boolean matchOK = true;
				for (int i = 0; i < argsClazz.length; i++)
				{
					if (argsClazz[i] == null) //不比较null实参
					{
						continue;
					}
					else
					{
						if (!argsClazz[i].equals(method.getParameterTypes()[i]))
						{
							matchOK = false;
							break;
						}
					}
				}
				if (matchOK)
				{
					allMatchMeshods.add(method);
				}

			}
			
			if (allMatchMeshods.size() == 0) //参数类型匹配不上
			{
				//抛出异常
				StringBuilder sb = new StringBuilder();
				sb.append(clazzName);
				sb.append(".");
				sb.append(methodName);
				sb.append("(");
				for (int i = 0; i < argsClazz.length; i++)
				{
					sb.append(argsClazz[i].getName());
					if (i != argsClazz.length - 1)
					{
						sb.append(",");
					}
				}
				sb.append(")");
				throw new MethodNotFoundException("Can't match method: " + sb.toString()); 
			}
			else if (allMatchMeshods.size() == 1) //匹配出一个
			{
				return allMatchMeshods.get(0);
			}
			else	//匹配出两个以上
			{
				//抛出异常
				StringBuilder sb = new StringBuilder();
				sb.append(clazzName);
				sb.append(".");
				sb.append(methodName);
				sb.append("(");
				sb.append(argsClazz.length).append(" parameters");
				sb.append(")");
				throw new MethodNotFoundException("More than one matching methods for " + sb.toString()); 
			}
		}
	}
	
	/**
	 * 将JavaBean转换为PHP显示方式
	 * @param clazzName JavaBean类
	 * @return
	 */
	public static final String javaBean2Php(String clazzName)
	{
		Class<?> javaBean = null;
		try
		{
			javaBean = getClass(clazzName);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			
			return "Can't find JavaBean Class: " + clazzName;
		}
		
		BeanInfo beanInfo;
		try
		{
			beanInfo = Introspector.getBeanInfo(javaBean, Object.class);
		}
		catch (IntrospectionException e)
		{
			//内省异常
			e.printStackTrace();
			return "IntrospectionException for " + javaBean;
		}
		
		//获得javaBean属性集
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		
		StringBuilder ret = new StringBuilder();
		ret.append("class ").append(clazzName.replace('.', '_')).append("<br>\n");
		ret.append("{").append("<br>\n");
		for (PropertyDescriptor pd : pds) 
		{
			ret.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("var $").append(pd.getName()).append(";<br>\n");
		}
		ret.append("}").append("<br>\n");
		
		return ret.toString();
	}
}
