package lajp;

/**
 * System V 消息队列JNI
 * @author diaoyf
 *
 */
class MsgQ
{	
	/**
	 * 创建或获得消息队列,返回消息队列标识符msqid.
	 * <br>
	 * 此方法对应System V消息队列函数 int msgget(key_t key,int msgflg)。
	 * Java参数msg_key对应C参数key,msgflag默认为八进制0666
	 * 
	 * @param msg_key 消息队列KEY
	 * @return 
	 * <li>执行成功返回正值int，表示msqid</li>
	 * <li>失败返回-1</li>
	 */
	public native static int msgget(int msg_key);
	
	/**
	 * 发送消息（将消息加入消息队列）
	 * <br>
	 * 此方法对应System V消息队列函数 int msgsnd(int msgid, const void *msg_ptr, size_t msg_sz, int msgflg)。
	 * 
	 * 
	 * @param msqid 消息队列标识符msqid
	 * @param type 消息类型
	 * @param msg 消息体(符合PHP序列化字符串规则)
	 * @param len 消息体长度
	 * @return
	 * <li>0：执行成功</li>
	 * <li>-1：执行失败</li>
	 */
	public native static int msgsnd(int msqid, int type, byte[] msg, int len);
	
	/**
	 * 从消息队列中接收消息
	 * <br>
	 * 此方法对应System V消息队列函数 int msgrcv(int msgid, void *msg_ptr, size_t msg_sz, int msgtype, int msgflg)。
	 * 
	 * @param msqid 消息类型
	 * @param msg 消息接收缓冲区
	 * @param len 接收缓冲区长度
	 * @param type 消息类型
	 * @return
	 * <li>执行成功：接收消息的字节数</li>
	 * <li>执行失败：-1</li>
	 */
	public native static int msgrcv(int msqid, byte[] msg, int len, int type);

	/**
	 * 删除消息队列
	 * <br>
	 * 此方法在内部调用System V消息队列函数msgctl, 相当于执行msgctl(msgid, IPC_RMID, NULL)
	 * 
	 * @param msqid
	 * @return
	 */
	public native static int msgclose(int msqid);
}
