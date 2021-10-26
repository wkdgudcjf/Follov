package com.follov.network;

public class BaseNetworkException extends BaseException
{
	public BaseNetworkException(int nErrCode)
	{
		this(nErrCode, new Integer(nErrCode).toString());
	}
	
	public BaseNetworkException(int nErrCode ,String sErrMsg)
	{		
		super(nErrCode, sErrMsg);
	}
}