package com.follov.network;

public class BaseException extends  Exception
{
	protected int  mErrCode;
	
	public int getErroCode() { return mErrCode;}
	
	public BaseException(int nErrCode)
	{
		this(nErrCode, new Integer(nErrCode).toString());
	}
	
	public BaseException(int nErrCode ,String sErrMsg)
	{		
		super(sErrMsg);
		mErrCode = nErrCode;		
	}
}
