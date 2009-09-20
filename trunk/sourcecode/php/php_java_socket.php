<?php
//-----------------------------------------------------------
// LAJP-php script. 
//   (2009-09 http://code.google.com/p/lajp/)
// 
// Version: 9.09.01
// License: http://www.apache.org/licenses/LICENSE-2.0
//-----------------------------------------------------------
?>

<?php
define("LAJP_IP", "127.0.0.1");			//Java端IP
define("LAJP_PORT", 21230);				//Java端侦听端口

define("PARAM_TYPE_ERROR", 101);		//参数类型错误
define("SOCKET_ERROR", 102);			//SOCKET错误
define("JAVA_EXCEPTION", 104);			//Java端反馈异常

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


	if (($socket = socket_create(AF_INET, SOCK_STREAM, 0)) === false) 
	{
    	throw new Exception("[LAJP Error] socket create error.", SOCKET_ERROR);
	}

	if (socket_connect($socket, LAJP_IP, LAJP_PORT) === false) 
	{
    	throw new Exception("[LAJP Error] socket connect error.", SOCKET_ERROR);
	}

	//消息体序列化
	$request = serialize($arg_array);
	$req_len = strlen($request);

	$request = $req_len.",".$request;

	//echo "{$request}<br>";

	$send_len = 0;
	do
	{
		//发送
		if (($sends = socket_write($socket, $request, strlen($request))) === false)
		{
			throw new Exception("[LAJP Error] socket write error.", SOCKET_ERROR);
		}
		
		$send_len += $sends;
		$request = substr($request, $sends);

	}while ($send_len < $req_len);

	//接收
	$response = "";
	while(true)
	{
		$recv = "";
		if (($recv = socket_read($socket, 1400)) === false)
		{
			throw new Exception("[LAJP Error] socket read error.", SOCKET_ERROR);
		}
		
		if ($recv == "")
		{
			break;
		}

		$response .= $recv;

		//echo "{$response}<br>";

	} 

	//关闭
	socket_close($socket);

	$rsp_stat = substr($response, 0, 1); 	//返回类型 "S":成功 "F":异常
	$rsp_msg = substr($response, 1);		//返回信息

	//echo "返回类型:{$rsp_stat},返回信息:{$rsp_msg}<br>";

	if ($rsp_stat == "F")
	{
		//异常信息不用反序列化
		throw new Exception("[LAJP Error] Receive Java exception: ".$rsp_msg, JAVA_EXCEPTION);
	}
	else
	{
		//反序列化
		return unserialize($rsp_msg);
	}
}
?>
