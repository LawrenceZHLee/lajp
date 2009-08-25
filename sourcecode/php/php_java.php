<?php
define("PHPJAVA_MSGKEY", 0x20021230); 	//消息KEY
define("PHPJAVA_MSG_TYPE", 1); 			//服务端消息接收类型
define("PHPJAVA_MSG_MAX", 4096); 		//消息最大4k，超出需要拆分
define("PHPJAVA_RCVBUF_MAX", 8192); 	//接收缓冲区最大长度

define("PHPJAVA_SERIALIZE", TRUE);		//序列化
define("PHPJAVA_UNSERIALIZE", FALSE);	//非序列化
define("PHPJAVA_BLOCK", TRUE);			//阻塞

define("PARAM_TYPE_ERROR", 101);		//参数类型错误
define("MSG_SEND_ERROR", 102);			//发送错误
define("MSG_RECEIVE_ERROR", 103);		//接收错误
define("JAVA_EXCEPTION", 104);			//Java端反馈异常

//握手请求结构
class PHPJAVA_HS
{
	var $id;	//int		: 发送方ID(10位整数)，由getpid() + rand(0, 300)组成。
				//			: 单次请求在握手建立后，客户端以此作为消息类型发送，服务端以
				//			: $id+1回应。
				//			: 连续请求在握手建立后，服务端以此为key建立消息队列，客户端
				//			: 以1作为消息类型发送，服务端以2作为消息类型回应。
	var $tp; 	//string	: 请求类型  s:单次请求， m:连续请求。
}

//单次请求发送消息结构
class PHPJAVA_REQ
{
	var $ln; 	//int		: 消息体总长度(java方法参数数组序列化后的长度)
	var $ct;	//int		: 拆分数量
	var $sq;	//int		: 第几次(拆分数量)
	var $mn;	//string	: java方法名称
	var $ms;	//string	: 拆分后本次的消息体(java方法参数数组序列化)
}
//单次请求接收消息结构
class PHPJAVA_RSP
{
	var $ln; 	//int		: 消息体总长度(反序列化后的长度)
	var $ct;	//int		: 拆分数量 -1表示异常
	var $sq;	//int		: 第几次(拆分数量)
	var $ex;	//string	: 异常信息，如果正常返回0长度字符串
	var $ms;	//string	: 拆分后本次的消息体
}

//发送消息（单次请求接收）
// $java_method		: java方法名称
// $params_array	: java方法参数数组
// 返回				: java返回
function phpjava_call($java_method, $params_array)
{
	//参数检查 
	if (!is_string($java_method))
	{
		throw new Exception("[PHP-JAVA]Error type of Java method's name, it must be string.", PARAM_TYPE_ERROR);
	}
	if (!is_array($params_array))
	{
		throw new Exception("[PHP-JAVA]Error type of Java method's parameters, it must be array.", PARAM_TYPE_ERROR);
	}

	// ---------------------------------------------------
	// 握手通讯
	// ---------------------------------------------------
	$pid = posix_getpid();	//进程ID
	$rand = rand(0, 300);	//随机数
	//进程消息类型;
	$process_id = (int)($pid.$rand);
	//echo "ID:$process_id <br>";
	
	//握手请求结构
	$hand_shake = new PHPJAVA_HS;
	$hand_shake->id = $process_id;				//发送方ID
	$hand_shake->tp = "s";						//单次请求

	//消息id
	$msg_id = msg_get_queue(PHPJAVA_MSGKEY);
	//发送握手
	if (!msg_send($msg_id, PHPJAVA_MSG_TYPE, $hand_shake, PHPJAVA_SERIALIZE, PHPJAVA_BLOCK, $msg_err))
	{
		throw new Exception("[PHP-JAVA]Message send error: $msg_err", MSG_SEND_ERROR);
	}

	//接收握手（服务段发送0长度消息回应）
	if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, 1024, $msg, PHPJAVA_UNSERIALIZE, 0, $msg_error)) 
	{
		throw new Exception("[PHP-JAVA]Message receive error: $msg_error", MSG_RECEIVE_ERROR);
	} 


	// ---------------------------------------------------
	// 调用-发送
	// ---------------------------------------------------
	
	$msg_body = serialize($params_array);	//消息体序列化
	$msg_body_len = strlen($msg_body);		//消息总长度

	//计算消息体拆分数量
	$divisor = $msg_body_len / PHPJAVA_MSG_MAX;		//除数
	$remainder = $msg_body_len % PHPJAVA_MSG_MAX;	//余数
	if ($divisor <= 1)
	{
		$ct = 1;	//拆分数量：1
	}
	else 
	{
		if ($remainder != 0)	//有小数部分
		{
			$ct = (int)$divisor + 1;	//拆分数量: 整除 + 1
		}
		else
		{
			$ct = (int)$divisor;		//拆分数量: 整除 
		}
	}

	for ($i = 0; $i < $ct; $i++)
	{
		//发送消息结构
		$request = new PHPJAVA_REQ;
		$request->ln = $msg_body_len;	//消息总长度
		$request->ct = $ct;				//拆分数量
		$request->sq = $i + 1;			//第几次(拆分数量)
		$request->mn = $java_method;	//java方法名称
		//消息体
		$request->ms = substr($msg_body, $i * PHPJAVA_MSG_MAX, PHPJAVA_MSG_MAX);

		//发送
		if (!msg_send($msg_id, $process_id, $request, PHPJAVA_SERIALIZE, PHPJAVA_BLOCK, $msg_err))
		{
			throw new Exception("[PHP-JAVA]Message send error: $msg_err", MSG_SEND_ERROR);
		}
	}

	// ---------------------------------------------------
	// 调用-接收
	// ---------------------------------------------------
	
	//接收第一个应答
	if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, PHPJAVA_RCVBUF_MAX, $response1, PHPJAVA_SERIALIZE, 0, $msg_error)) 
	{
		throw new Exception("[PHP-JAVA]Message receive error: $msg_error", MSG_RECEIVE_ERROR);
	} 

	//收到异常
	if ($response1->ct == -1)
	{
		throw new Exception("[PHP-JAVA]Message receive error: $response1->ex", MSG_RECEIVE_ERROR);
	}

	//只有一个应答消息包
	if ($response1->ct == 1)
	{
		return unserialize($response1->ms); //反序列化
	}
	
	//应答消息拆分数量
	$split_count = $response1->ct;
	//当前拆分第几次
	$split_index = $response1->sq;
	//消息组包
	$rsp_ms = $response1->ms;

	while ($split_index < $split_count)
	{
		if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, PHPJAVA_RCVBUF_MAX, $response_n, PHPJAVA_SERIALIZE, 0, $msg_error)) 
		{
			throw new Exception("[PHP-JAVA]Message receive error: $msg_error", MSG_RECEIVE_ERROR);
		}

		$split_index = $response_n->sq; //当前拆分第几次
		$rsp_ms .= $response_n->ms;		//合并消息包
	}

	echo "返回：". $rsp_ms. "<br>";
	return unserialize($rsp_ms); 		//反序列化
}
?>