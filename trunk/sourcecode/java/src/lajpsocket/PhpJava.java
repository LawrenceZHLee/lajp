//-----------------------------------------------------------
// LAJP-java(socket) (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajpsocket;

import java.io.IOException;
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
	final static int port = 21230;
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
	

}
