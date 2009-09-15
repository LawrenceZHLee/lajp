/* -------------------------------- */
/*  System V Message queues         */
/*  Java JNI                        */
/* -------------------------------- */
#include <sys/types.h>  
#include <sys/ipc.h>  
#include <sys/msg.h>  
#include <stdio.h>  
#include <stdlib.h>  
#include <unistd.h>  
#include <string.h>
#include "lajp_MsgQ.h"

#define MSG_MAX 8192 /** 消息最大长度 */

/*消息缓冲区*/  
struct message  
{  
    long msg_type;			//消息标识符  
    char msg_text[MSG_MAX];	//消息
};

/*
 * 创建或获得消息队列
 * 参数 key: 消息队列key
 * 返回: 消息队列ID(成功) -1(失败)
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_msgget
  (JNIEnv *env, jclass obj, jint key)
{
	jint msqid;  /* 消息队列标识符 */
	if ((msqid = msgget(key, IPC_CREAT | 0666)) == -1)
	{
		perror("[JNI ERROR]msgget Error, ret=-1"); 
	}

	return msqid;
}

/*
 * Class:     phpjava_MsgQ
 * Method:    msgsnd
 * Signature: (II[BI)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_msgsnd
  (JNIEnv *env, jclass obj, jint msqid, jint mstype, jbyteArray msg, jint mslen)
{
	if (MSG_MAX < mslen)
	{
		perror("[JNI ERROR]msgsnd Error: jbyteArray msg too big."); 
	}

	/* 消息结构 */
	struct message msgq;
	/* 复制消息类型 */
	msgq.msg_type = mstype;
	/* 复制java传来的消息字节数组 */
	(*env)->GetByteArrayRegion(env, msg, 0, mslen, msgq.msg_text);

	int ret;
    if((ret = msgsnd(msqid, &msgq, mslen, 0)) < 0)  
    {  
        perror("[JNI ERROR]msgsnd Error");  
    }
	return ret;

return 0;
}

/*
 * Class:     phpjava_MsgQ
 * Method:    msgrcv
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_msgrcv
  (JNIEnv *env, jclass obj, jint msqid, jbyteArray msg, jint mslen, jint mstype)
{
	/* 消息结构 */
	struct message msgq;
	/* 复制消息类型 */
	msgq.msg_type = mstype;

	int readmslen;
    /* 从消息队列读出消息,到msg */  
    if((readmslen = msgrcv(msqid, &msgq, MSG_MAX, mstype, 0)) < 0)  
    {  
        perror("[JNI ERROR]msgrcv Error");  
    }

	if (mslen < readmslen)
	{
		perror("[JNI ERROR]msgrcv Error: jbyteArray msg too small."); 
	}

	/* 将msg中的消息复制到java字节数组中 */
	(*env)->SetByteArrayRegion(env, msg, 0, readmslen, msgq.msg_text);
	

	return readmslen;
}

/*
 * Class:     phpjava_MsgQ
 * Method:    msgclose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_msgclose
  (JNIEnv *env, jclass obj, jint msqid)
{
	int ret;
	if ((ret = msgctl(msqid, IPC_RMID, NULL)) < 0)
	{
		perror("[JNI ERROR]msgctl Error"); 
	}

	return ret;
}

