//-----------------------------------------------------------
// LAJP-java(socket) (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajpsocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import lajp.MethodNotFoundException;
import lajp.ReflectUtil;

/**
 * LAJP主线程
 * @author diaoyf
 *
 */
public class PhpJava 
{
	
	/** 侦听端口 */
	static int port = 21230;
	/** 侦听Socket */
	ServerSocket serverSocket;

	public PhpJava()
	{
		try
		{
			//侦听
			serverSocket = new ServerSocket(port);
			
			System.out.println("Listen port: " + port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		//自动程序运行
		autoRun();

		
		while(true)
		{
			try
			{
				Socket socket = serverSocket.accept();

				new SingleThread(socket).start();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

	}


	public static void main(String[] args)
	{
		System.out.println("-------------------------------------------");
		System.out.println("- Start LAJP-JAVA(socket)...            ");
		System.out.println("- time:" + new Date());
		System.out.println("-------------------------------------------");

		PhpJava server = new PhpJava();
	}
	
	/**
	 * 字符集设置
	 */
	private static void charset()
	{
		String charset = System.getenv("CHARSET");
		if (charset != null && !charset.trim().equals(""))
		{
			SingleThread.PHP_CHARSET = charset;
		}
	}
	
	/**
	 * 自动运行的程序
	 */
	public static void autoRun()
	{
		String autoRunClassName = System.getenv("AUTORUN_CLASS");
		String autoRunMethodName = System.getenv("AUTORUN_METHOD");
		
		if (autoRunClassName != null && !autoRunClassName.trim().equals("")
				&& autoRunMethodName != null && !autoRunMethodName.trim().equals(""))
		{
			
			//查找自动运行的方法
			Method method = null;
			try
			{
				method = ReflectUtil.matchingMethod(autoRunClassName, autoRunMethodName, null);
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("Can't find Class: " + autoRunClassName);
				e.printStackTrace();
				System.exit(-1);
			}
			catch (MethodNotFoundException e)
			{
				System.err.println("Can't find method: " + autoRunMethodName);
				e.printStackTrace();
				System.exit(-1);
			}
			
			//调用
			try
			{
				method.invoke(null, null);
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("IllegalArgumentException for call method " + autoRunClassName + "." + method.getName());
				e.printStackTrace();
				System.exit(-1);
			}
			catch (IllegalAccessException e)
			{
				System.err.println("IllegalAccessException for call method " + autoRunClassName + "." + method.getName());
				e.printStackTrace();
				System.exit(-1);
			}
			catch (InvocationTargetException e)
			{
				System.err.println("InvocationTargetException for call method " + autoRunClassName + "." + method.getName());
				e.printStackTrace();
				System.exit(-1);
			}

		}
	}

}
