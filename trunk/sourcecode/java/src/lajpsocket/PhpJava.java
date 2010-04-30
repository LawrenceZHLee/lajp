//-----------------------------------------------------------
// LAJP-java(socket) (2009-09 http://code.google.com/p/lajp/)
// 
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajpsocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

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
		//设置侦听端口
		setListenPort();
		
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
		
		//设置字符集
		charset();
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
	 * 设置侦听端口
	 */
	private static void setListenPort()
	{
		String s_port = System.getenv("SERVICE_PORT");
		
		if (s_port != null && !s_port.trim().equals(""))
		{
			try
			{
				port = Integer.parseInt(s_port);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
		else
		{
			//default 21230 
		}
	}
	
	/**
	 * 字符集设置
	 */
	private static void charset()
	{
		String charset = System.getenv("CHARSET");
		
		if (charset != null && !charset.trim().equals(""))
		{
			try
			{
				"中文".getBytes(SingleThread.PHP_CHARSET);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
			
			System.out.println("set charser: " + charset);
			SingleThread.PHP_CHARSET = charset;
		}
		else
		{
			System.out.println("set charser: UTF-8");
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
				method = ReflectUtil.matchingMethod(autoRunClassName, autoRunMethodName, new Class[]{});
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
