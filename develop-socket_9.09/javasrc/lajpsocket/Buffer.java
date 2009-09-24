//-----------------------------------------------------------
// LAJP-java(socket) (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajpsocket;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存
 * @author diaoyf
 *
 */
public class Buffer
{
	/** 反射类缓存， key:类名  value: 类 */
	static Map<String, Class<?>> clazzMap = new HashMap<String, Class<?>>();

	static synchronized void putClazzMap(String clazzName, Class<?> clazz)
	{
		clazzMap.put(clazzName, clazz);
	}
	
	/** 反射类中的共有静态方法, key:类名 value:方法集合 */
	static Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();
	
	static synchronized void putMethodMap(String clazzName, List<Method> methods)
	{
		methodMap.put(clazzName, methods);
	}
}
