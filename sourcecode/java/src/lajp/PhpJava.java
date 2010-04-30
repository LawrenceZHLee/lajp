//-----------------------------------------------------------
// LAJP-java (2009-09 http://code.google.com/p/lajp/)
// 
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

package lajp;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * LAJP主线程
 * @author diaoyf
 *
 */
public class PhpJava
{
	/** 消息队列KEY */
	static int IPC_KEY = 0x20021230;

	/** 握手消息类型 */
	final static int HANDSHAKE_TYPE = 1; 
	/** 消息最大字节数 */
	final static int MSG_MAX = 4096;
	
	/** 消息队列id */
	static int msqid;
	
	/** 关注点ProcessId */
	static int pointProcessId = -1;
	/** 关注点时刻 */
	static long pointTime = System.currentTimeMillis();
	
	static
	{
		//加载JNI
		System.loadLibrary("lajpmsgq");
	}

	public static void main(String[] args)
	{
		System.out.println("-------------------------------------------");
		System.out.println("- Start LAJP-JAVA...            ");
		System.out.println("- time:" + new Date());
		System.out.println("-------------------------------------------");
		
		//初始化System V IPC
		initIPC();
		//字符集设置
		charset();
		//自动程序运行
		autoRun();
		
		//获得消息队列id
		msqid = MsgQ.msgget(IPC_KEY);
		//接收buffer
		byte[] buffer = new byte[1024];
		//接收信息长度
		int bufLen = 1024;
		
		System.out.println("Start LAJP-JAVA OK...");

		while (true)
		{
			//请求进程消息类型id
			int processId = -1;
			//解析请求类型
			byte type = 0x00;

			try
			{
				//接收握手信息
				bufLen = MsgQ.msgrcv(msqid, buffer, 1024, HANDSHAKE_TYPE);								
			}
			catch (Throwable e)
			{
				System.out.println("[LAJP Exception(warn)]");
				e.printStackTrace();
				continue;
			}
			
			//消息长度检查(握手消息长度35字节)
			if (bufLen != 35)
			{
				System.out.println("[LAJP Error(warn)]:HandShake Message length != 35");
				continue;
			}
			
//			//--
//			for (int i = 0; i < bufLen; i++)
//			{
//				System.out.printf("[%d]0x%x,", i, buffer[i]);
//			}
//			System.out.println();
//			System.out.println("handShake:" + new String(buffer, 0, bufLen));

			
			//解析消息-----------------------------
			type = buffer[14];
			processId = Integer.parseInt(new String(buffer, 23, 10));

//			//--
//			System.out.printf("processId:%d\n",processId);
			
			if (type == 0x73) //0x73: "s"
			{
				try
				{
					new SingleThread(processId).start();
				}
				catch (Throwable e)
				{
					System.out.println("[LAJP Exception(warn)] SingleThread Exception: ");
					e.printStackTrace();
					continue;
				}
			}
			else if (type == 0x6d) //0x6d: "m"
			{
				//TODO 连续请求
			}
			
			//关注点判断(每过1000序列点(500偶数次),且上次关键点已超过5分钟)
			if (System.currentTimeMillis() - pointTime > 300000
					&& processId % 1000 == 0)
			{
				if (pointProcessId < processId)
				{
					//队列垃圾回收
					new PointGC(pointProcessId).start();
				}
				
				//更新关注点
				pointTime = System.currentTimeMillis();
				pointProcessId = processId;
			}
			
		}//循环结束

	}
	
	/**
	 * 初始化IPC
	 */
	public static void initIPC()
	{
		//先删除sem
		int semid = MsgQ.semget(IPC_KEY);
		if (semid == -1)
		{
			System.out.printf("semget(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		if (MsgQ.semclose(semid) == -1)
		{
			System.out.printf("semclose(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		
		//重建msg
		int msgid = MsgQ.msgget(IPC_KEY);
		if (msgid == -1)
		{
			System.out.printf("msgget(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		if (MsgQ.msgclose(msgid) == -1)
		{
			System.out.printf("msgclose(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		if (MsgQ.msgget(IPC_KEY) == -1)
		{
			System.out.printf("msgget(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}

		System.out.println("init [IPC] Message Queue OK...");

		//重建shm
		int shmid = MsgQ.shmget(IPC_KEY, 10);
		if (shmid == -1)
		{
			System.out.printf("shmget(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		if (MsgQ.shmclose(shmid) == -1)
		{
			System.out.printf("shmclose(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		if (MsgQ.shmget(IPC_KEY, 10) == -1)
		{
			System.out.printf("shmctl(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}
		
		System.out.println("init [IPC] Shared Memory OK...");

		//创建sem
		if (MsgQ.semget(IPC_KEY) == -1)
		{
			System.out.printf("semget(0x%x) error, can't start LAJP-JAVA.\n", IPC_KEY);
			System.exit(-1);
		}

		System.out.println("init [IPC] Semaphore OK...");
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
