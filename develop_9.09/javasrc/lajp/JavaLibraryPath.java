//-----------------------------------------------------------
// LAJP-java (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

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
