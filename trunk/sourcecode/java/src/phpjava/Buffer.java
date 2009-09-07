package phpjava;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存
 * @author diaoyf
 *
 */
public class Buffer
{
	/** 反射类缓存， key:类名  value: 类 */
	static Map<String, Class> clazzMap = new HashMap<String, Class>();

	static synchronized void putClazzMap(String clazzName, Class clazz)
	{
		clazzMap.put(clazzName, clazz);
	}
}
