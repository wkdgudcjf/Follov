package com.follov.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class BaseHttpRequest extends BaseURLConnection
{
	protected int mResponseContentLength;
	
	public BaseHttpRequest(String sURL)
	{
		super(sURL);
		mConnTimeout = 0;
		mRequestTimeout = 0;
	}
	
	// time out 설정
	public void setTimeOut(int nConnTimeout, int nRequestTimeout)
	{
		mConnTimeout = nConnTimeout;
		mRequestTimeout = nRequestTimeout;
	}
	
	// 파일 받기
	public void getFile(String sFilePath)  throws BaseNetworkException 
	{
		try
		{
			// connection
			connection(null);
			
			// 코드확인
			checkResponseCode();
	
			// 파일 받기
			doReceiveDataForFileWrite(sFilePath);
		}
		catch (BaseNetworkException e)
		{
			throw new BaseNetworkException(e.getErroCode(), e.getMessage());
		}
		finally
		{
			disconnection();
		}
	}
	
	// 바이트로 받기
	public byte [] getFile() throws BaseNetworkException 
	{
		try
		{
			// connection
			connection();
			
			// 코드 확인
			checkResponseCode();
	
			return doReceiveData();
		}
		catch (BaseNetworkException e)
		{
			throw new BaseNetworkException(e.getErroCode(), e.getMessage());
		}
		finally
		{
			disconnection();
		}
	}
	
	// Http Server Post Data 전송
	public String sendData(String sPostData, String sSendEncoding, String sRecvEncoding) throws BaseNetworkException
	{
		return sendData(sPostData, sSendEncoding, sRecvEncoding, false);
	}
	
	// Http Server Post Data 전송
	public String sendData(String sPostData, String sSendEncoding, String sRecvEncoding, boolean bUseURLEncoder) throws BaseNetworkException
	{
		try
		{
			byte[] byteData;
			
			// URL Encoder
			if ( true == bUseURLEncoder)
			{
				byteData = URLEncoder.encode(sPostData, sSendEncoding).getBytes();
			}
			else
			{
				byteData = sPostData.getBytes(sSendEncoding);
			}
			
			// connection
			connection(byteData);
			
			// 데이터 전송
			doSendData(byteData);
			// 코드 확인
			checkResponseCode();
			// 받은 테이터 리턴
			return doReceiveData(sRecvEncoding);
		}
		catch (UnsupportedEncodingException e)
		{
			//String sLog = BaseLogManager.write(LOG_TYPE.LOG_CAT_E, "BaseHttpRequest.sendData.UnsupportedEncodingException [" + e.getMessage() + "]");
			//BaseLogManager.write(LOG_TYPE.FILE, sLog);
			//BaseLogManager.write(LOG_TYPE.SD_CARD, sLog);
			
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_URL_ENCODER, e.getMessage());
		}
		catch (BaseNetworkException e)
		{
			//String sLog = BaseLogManager.write(LOG_TYPE.LOG_CAT_E, "BaseHttpRequest.sendData.BaseNetworkException [" + e.getErroCode() + ", " + e.getMessage() + "]");
			//BaseLogManager.write(LOG_TYPE.FILE, sLog);
			//BaseLogManager.write(LOG_TYPE.SD_CARD, sLog);
			
			throw new BaseNetworkException(e.getErroCode(), e.getMessage());
		}
		finally
		{
			disconnection();
		}
	}
	
	// connection 헤더 정보 설정
	protected void onSetProperties(HttpURLConnection conn, int contentLength)
	{
		try
		{	
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);
			
			conn.setRequestProperty("User-Agent", "Mozilla/4.0");
			conn.setRequestProperty("Content-Type","application/xml");
			conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
			conn.setConnectTimeout(mConnTimeout * 1000);
			conn.setReadTimeout(mRequestTimeout * 1000);
			
		}
		catch (Exception e){}
	}
	
	// Post Data 전송
	protected void doSendData(final byte[] byteData) throws BaseNetworkException
	{
		try
		{
			super.doSendData(mURLConn.getOutputStream(), byteData);
		}
		catch (IOException e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_SEND_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_SEND_DATA, e.getMessage()));
		}
	}

	// 데이터 받기
	protected byte[] doReceiveData() throws BaseNetworkException
	{
        try
		{
        	return super.doReceiveData(mURLConn.getInputStream(), mURLConn.getContentLength());
        	
		}
        catch (IOException e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_DATA, e.getMessage()));
		}
    }
	
	// 데이터 받기
	protected String doReceiveData(String encoding) throws BaseNetworkException
	{
		try
		{
        	return super.doReceiveData(mURLConn.getInputStream(), encoding);
        	
		}
        catch (IOException e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_DATA, e.getMessage()));
		}
	}
	
	protected void doReceiveDataForFileWrite(String sFilePath) throws BaseNetworkException
	{
		try
		{
        	super.doReceiveDataForFileWrite(mURLConn.getInputStream(), sFilePath, mURLConn.getContentLength());
        	
		}
        catch (IOException e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_DATA, e.getMessage()));
		}
	}
}
