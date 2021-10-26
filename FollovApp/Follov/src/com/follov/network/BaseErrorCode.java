package com.follov.network;

public class BaseErrorCode {
	
	public static int SUCCESS = 0;
	
	public static int ERR_CODE = -1000;
	
	public static int ERR_NET = -1100;
	public static int ERR_XML = -1200;
	public static int ERR_COMMON = -1300;
	
	public static int NET_ERR_SEND_DATA = ERR_NET - 1;
	public static int NET_ERR_RECV_DATA = ERR_NET - 2;
	public static int NET_ERR_USER_QUIT = ERR_NET - 3;
	public static int NET_ERR_RECV_FILE = ERR_NET - 4;
	public static int NET_ERR_CONNECT = ERR_NET - 5;
	public static int NET_ERR_DISCONNECT = ERR_NET - 6;
	public static int NET_ERR_HTTP_OK = ERR_NET - 7;
	public static int NET_ERR_URL_ENCODER = ERR_NET - 8;
	public static int NET_ERR_TIMEOUT = ERR_NET - 9;
	public static int NET_ERR_RESULTCODE = ERR_NET - 10;
	public static int NET_ERR_COMMUNICATE = ERR_NET - 11;
	
	public static int XML_ERR_PARSING = ERR_XML - 1;
	public static int XML_ERR_CREATE = ERR_XML - 2;
	
	public static int COMMON_INVALID_PARAM = ERR_COMMON -1;
	public static int COMMON_ERROR_DECOMPRESS = ERR_COMMON -2; 
}
