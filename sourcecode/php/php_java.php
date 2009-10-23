<?php
//-----------------------------------------------------------
// LAJP-php script. (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------

define("LAJP_IPCKEY", 0x20021230); 		//消息KEY
define("LAJP_MSG_TYPE", 1); 			//服务端消息接收类型
define("LAJP_MSG_MAX", 4096); 			//消息最大4k，超出需要拆分
define("LAJP_RCVBUF_MAX", 8192); 		//接收缓冲区最大长度

define("LAJP_SERIALIZE", TRUE);			//序列化
define("LAJP_UNSERIALIZE", FALSE);		//非序列化
define("LAJP_BLOCK", TRUE);				//阻塞

define("PARAM_TYPE_ERROR", 101);		//参数类型错误
define("MSG_SEND_ERROR", 102);			//发送错误
define("MSG_RECEIVE_ERROR", 103);		//接收错误
define("JAVA_EXCEPTION", 104);			//Java端反馈异常

// ---------------------------------------------------
// Java服务调用（单次请求）
//
// 函数参数说明： 
// 	<>第一个参数为string类型，表示Java类、方法名称；随后的参
//	数表示向Java方法传参。
//	<>举例：
//		<1> Java方法： public static final void addUser(String name,int age)
//						在com.foo.abc类中，php应该这样调用：
//			php调用: lajp_call("com.foo.abc::addUser",$userName, $userAge),
//						$userName必须string类型，$userAge必须int类型。
//		<2> Java方法： public static final int addUsers(List<com.foo.abc.User> users)
//						在com.foo.abc类中，php应该这样调用：
//			php调用: lajp_call("com.foo.abc::addUsers",$user),
//						$user对象的类类型为array，数组中的每个元素必须是
// 						com_foo_abc_User类型对象,对象属性必须和Java的
//						com.foo.abc.User类匹配。
// ---------------------------------------------------
function lajp_call()
{
	//参数数量
	$args_len = func_num_args();
	//参数数组
	$arg_array = func_get_args();

	//参数数量不能小于1
	if ($args_len < 1)
	{
		throw new Exception("[LAJP Error] lajp_call function's arguments length < 1", PARAM_TYPE_ERROR);
	}
	//第一个参数是Java类、方法名称，必须是string类型 
	if (!is_string($arg_array[0]))
	{
		throw new Exception("[LAJP Error] lajp_call function's first argument must be string \"class_name::method_name\".", PARAM_TYPE_ERROR);
	}



	// ---------------------------------------------------
	// 获取process_id, 全局递增数字(1000000000~2000000000)
	// 
	// ID存储在共享内存中(shared memory)，通过信号灯(semaphore)同步
	// ---------------------------------------------------

	//open an existing memory segment
	$shm_id = shmop_open(LAJP_IPCKEY, "w", 0, 0);
	if ($shm_id === false) 
	{
		// shared memory not exist.
		exit("Fail to open shared memory, Java Service must be runing.\n");
	} 
	else
	{
		//semaphore
		$sem_id = sem_get(LAJP_IPCKEY, 1, 0666, TRUE);
		//lock
		sem_acquire($sem_id); 

		//id inside shared memory
		$process_id = (int)shmop_read($shm_id, 0, 10);
		if ($process_id == 0 || $process_id >= 2000000000)
		{
			$process_id = 1000000000;
		}
		else
		{
			$process_id += 2;
		}
		//write id into shared memory
		shmop_write($shm_id, $process_id, 0);

		//unlock
		sem_release($sem_id);
	}
	//close shared memory
	shmop_close($shm_id);



	// ---------------------------------------------------
	// 握手通讯
	// 
	// request消息包：
	//	array
	//	{
	//		String		: 请求类型  "s":单次请求， "m":连续请求。
	//		int			: 请求方ID(10位整数)。
	//					: <>单次请求在握手建立后，客户端以此ID作为消息类型发送，服务端以
	//					: $id+1回应。
	//					: <>连续请求在握手建立后，服务端以此为key建立消息队列，客户端
	//					: 以1作为消息类型发送，服务端以2作为消息类型回应。
	//	}
	//
	// response消息包	: 0长度消息包，以"请求方ID+1"作为消息类型
	// ---------------------------------------------------
	
	//握手request消息包
	$hand_shake = array();
	$hand_shake[] = "s";					//单次请求
	$hand_shake[] = $process_id;			//请求方ID

	//消息id
	$msg_id = msg_get_queue(LAJP_IPCKEY);

	//发送握手
	if (!msg_send($msg_id, LAJP_MSG_TYPE, $hand_shake, LAJP_SERIALIZE, LAJP_BLOCK, $msg_err))
	{
		throw new Exception("[LAJP Error] HandShake send: ".$msg_err, MSG_SEND_ERROR);
	}

	//接收握手（服务端回应0长度消息）
	if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, 1024, $msg, LAJP_UNSERIALIZE, 0, $msg_error)) 
	{
		throw new Exception("[LAJP Error] HandShake receive: ".$msg_error, MSG_RECEIVE_ERROR);
	} 


	// ---------------------------------------------------
	// REQUEST
	//
	// request消息包：
	//	array
	//	{
	//		int		: 拆分数量（如果不拆分，值为1）
	//		int		: 拆分序号，从1开始
	//		string	: 拆分后本次的消息体(java方法参数数组序列化)
	//	}
	// ---------------------------------------------------
	
	$msg_body = serialize($arg_array);		//消息体序列化
	$msg_body_len = strlen($msg_body);		//消息体总长度

	//计算消息体拆分数量
	$divisor = $msg_body_len / LAJP_MSG_MAX;		//除数
	$remainder = $msg_body_len % LAJP_MSG_MAX;	//余数
	if ($divisor <= 1)
	{
		$split_count = 1;	//拆分数量：1
	}
	else 
	{
		if ($remainder != 0)	//有小数部分
		{
			$split_count = (int)$divisor + 1;	//拆分数量: 整除 + 1
		}
		else
		{
			$split_count = (int)$divisor;		//拆分数量: 整除 
		}
	}

	for ($i = 0; $i < $split_count; $i++)
	{
		//发送消息结构
		$request = array();
		$request[0] = $split_count;	//拆分数量
		$request[1] = $i + 1;		//拆分序号
		//消息体(本次拆分)
		$request[2] = substr($msg_body, $i * LAJP_MSG_MAX, LAJP_MSG_MAX);
		
		//echo "[$i]:$request[2]";

		//发送
		if (!msg_send($msg_id, $process_id, $request, LAJP_SERIALIZE, LAJP_BLOCK, $msg_err))
		{
			throw new Exception("[LAJP Error] Request: $msg_err", MSG_SEND_ERROR);
		}
	}



	// ---------------------------------------------------
	// RESPONSE
	//
	// response消息包：
	//	array
	//	{
	//		int		: 拆分数量（如果不拆分，值为1）(如果=0，表示返回异常消息)
	//		int		: 拆分序号，从1开始
	//		string	: 拆分后本次的消息体
	//	}
	// ---------------------------------------------------
	
	//接收第一个应答
	if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, LAJP_RCVBUF_MAX, $response1, LAJP_SERIALIZE, 0, $msg_error)) 
	{
		throw new Exception("[LAJP Error] Response: $msg_error", MSG_RECEIVE_ERROR);
	} 

	if ($response1[0] == 0) //收到Java端异常
	{
		//异常信息不用反序列化
		throw new Exception("[LAJP Error] Response receive Java exception: ".$response1[2], JAVA_EXCEPTION);
	}	
	else if ($response1[0] == 1) //只有一个应答消息包
	{
		$rsp_ms = $response1[2];

		//echo "只有一个应答消息包：". $rsp_ms. "<br>";
	}
	else //有多个应答消息包
	{
		//应答消息拆分数量
		$split_count = $response1[0];
		//当前拆分第几次
		$split_index = $response1[1];
		//消息组包(第一个消息包)
		$rsp_ms = $response1[2];

		//print( "多次应答消息包,拆分数量:$split_count,第几次:0,消息:$response1[2] <br>" );

		for ($i = 1; $i < $split_count; $i++)
		{
			//print("准备读取第[$i]条");

			if (!msg_receive ($msg_id, $process_id + 1, $msg_intype, LAJP_RCVBUF_MAX, $response_n, LAJP_SERIALIZE, 0, $msg_error)) 
			{
				throw new Exception("[LAJP Error] Response: $msg_error", MSG_RECEIVE_ERROR);
			}

		//print( "多次应答消息包,拆分数量:$response_n[0],第几次:$response_n[1],消息:$response_n[2] <br>" );

			$rsp_ms .= $response_n[2];		//拼接消息包
		}

	}

	//反序列化
	return unserialize($rsp_ms);
}
?>
