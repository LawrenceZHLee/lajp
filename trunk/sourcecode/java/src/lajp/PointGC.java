package lajp;

public class PointGC extends Thread
{
	/** 关注点 */
	private int pointId;
	
	byte[] rcvBuf = new byte[8192];
	
	public PointGC(int pointId)
	{
		//System.out.printf("关键点回收,id=%d\n", pointId);
		
		this.pointId = pointId;
	}
	
	@Override
	public void run()
	{
		if (pointId < 1)
		{
			return;
		}
		
		//最多500次
		for (int i = 0; i < 500; i++)
		{
			if (MsgQ.msgrcvNoBlock(PhpJava.msqid, rcvBuf, 8192, -pointId) == -1)
			{
				//没有可回收的;
				break;
			}
		}
	}
}
