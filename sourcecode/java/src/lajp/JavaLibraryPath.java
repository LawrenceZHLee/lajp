package lajp;

import java.util.Properties;

/**
 * 测试java.library.path路径
 * @author diaoyf
 *
 */
public class JavaLibraryPath
{
	public static void main(String[] args)
	{
		Properties properties = System.getProperties();
		System.out.println(properties.getProperty("java.library.path"));
	}
}
