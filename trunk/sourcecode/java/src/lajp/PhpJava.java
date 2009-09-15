package lajp;

/**
 * LAJP主线程
 * @author diaoyf
 *
 */
public class PhpJava
{
	/** 消息队列KEY */
	static final int MSGQ_KEY = 0x20021230;

	/** 握手消息类型 */
	final static int HANDSHAKE_TYPE = 1; 
	/** 消息最大字节数 */
	final static int PHPJAVA_MSG_MAX = 4096;
	
	/** 主消息队列id */
	static int msqid;
	
	static
	{
		//加载JNI
		System.loadLibrary("phpjavamsgq");
	}

	public static void main(String[] args)
	{
		//TODO 创建消息队列
		
		//获得消息队列id
		msqid = MsgQ.msgget(MSGQ_KEY);
		//接收buffer
		byte[] buffer = new byte[1024];
		//接收信息长度
		int bufLen = 1024;
		
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
			//System.out.printf("消息类型:0x%x,id:%d\n",type,processId);
			
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
			
		}//循环结束

	}

}
