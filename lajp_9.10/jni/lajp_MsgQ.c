/* -------------------------------- */
/*  System V Message queues         */
/*  Java JNI                        */
/* -------------------------------- */
#include <sys/types.h>  
#include <sys/ipc.h>  
#include <sys/msg.h> 
#include <sys/sem.h> 
#include <sys/shm.h>
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
		perror("[JNI ERROR]msgget Error"); 
	}

	return msqid;
}

/*
 * 发送消息到消息队列
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
 * 从消息队列中获取消息（阻塞）
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
 * 从消息队列中获取消息（非阻塞）
 * Class:     lajp_MsgQ
 * Method:    msgrcvNoBlock
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_msgrcvNoBlock
  (JNIEnv *env, jclass obj, jint msqid, jbyteArray msg, jint mslen, jint mstype)
{
	/* 消息结构 */
	struct message msgq;
	/* 复制消息类型 */
	msgq.msg_type = mstype;

	int readmslen;
    /* 从消息队列读出消息, IPC_NOWAIT表示不阻塞 */  
    if((readmslen = msgrcv(msqid, &msgq, MSG_MAX, mstype, IPC_NOWAIT)) < 0)  
    {  
        return -1;  //消息不存在
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
 * 删除消息队列
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

/*
 * 创建或获得共享内存
 * Class:     lajp_MsgQ
 * Method:    shmget
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_shmget
  (JNIEnv *env, jclass obj, jint key, jint size)
{
	jint shmid;  /* 共享内存标识符 */
	if ((shmid = shmget(key, size, IPC_CREAT | 0666)) == -1)
	{
		perror("[JNI ERROR]shmget Error"); 
	}

	return shmid;	
}


/*
 * 删除共享内存
 * Class:     lajp_MsgQ
 * Method:    shmclose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_shmclose
  (JNIEnv *env, jclass obj, jint shmid)
{
	int ret;
	if ((ret = shmctl(shmid, IPC_RMID, NULL)) < 0)
	{
		perror("[JNI ERROR]shmctl Error"); 
	}

	return ret;
}

/*
 * 创建或获得信号量(二值)
 * Class:     lajp_MsgQ
 * Method:    semget
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_semget
  (JNIEnv *env, jclass obj, jint key)
{
	jint semid;  /* 信号量标识符 */
	/* 创建3个信号量是为适应php的用法 */
	if ((semid = semget(key, 3, IPC_CREAT | 0666)) == -1)
	{
		perror("[JNI ERROR]semget Error"); 
	}

	return semid;	

}

/*
 * 删除信号量
 * Class:     lajp_MsgQ
 * Method:    semclose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_lajp_MsgQ_semclose
  (JNIEnv *env, jclass obj, jint semid)
{
	int ret;
	if ((ret = semctl(semid, 0, IPC_RMID, 0)) == -1)
	{
		perror("[JNI ERROR]semctl Error"); 
	}

	return ret;
	
}

