package com.follov.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BaseNetConnection 
{
	protected int MAX_SEND_DATA_LENGTH = 1024 * 4;
	protected int MAX_READ_DATA_LENGTH = MAX_SEND_DATA_LENGTH;
	protected int MAX_INTPUTSTREAM_READ_RETRY_COUNT = 10;
	private boolean mQuitFileJob;	
	
	public BaseNetConnection()
	{
		mQuitFileJob = false;
	}
	
	public boolean getQuitFileJob() { return mQuitFileJob;}
	public void setQuitFileJob(boolean bQuitJob) { mQuitFileJob = bQuitJob; }
	
	// 코드확인
	public int checkResponseCode() throws BaseNetworkException {return -1;}
	
	// 전송 상태 확인
	protected void onSendingProgressStatusMessage(int nTotalReaded, int nTotalLength) {}
	
	// 데이터 전송
	protected void doSendData(OutputStream connOutputStream, final byte[] byteData) throws BaseNetworkException
	{
		OutputStream outStream = null;
		try
		{
			outStream = connOutputStream;
			outStream.write(byteData);
			outStream.flush();
			outStream.close();
			outStream = null;
		}
		catch (Exception e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_SEND_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_SEND_DATA, e.getMessage()));
		}
		finally
		{
			if ( null != outStream )
			{
				try
				{
					outStream.flush();
					outStream.close();
				}
				catch (Exception e)
				{
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_SEND_DATA, e.getMessage());
				}
				
				outStream = null;
			}
		}
	}

	// 바이트로 데이터 받기
	protected byte[] doReceiveData(InputStream connInputStream, int nBuffSize) throws BaseNetworkException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		InputStream ins = null;
        try
		{
        	byte[] buf = new byte[nBuffSize];
 	        ins = connInputStream;
 	        int nTotalLength = nBuffSize;
 	        int nTotalRead = 0;
 	        int nRead = 0;
 	        int nReadRetryCount = 0;

 	        while (nTotalLength > nTotalRead)
 	        {
 	        	nRead = ins.read (buf , nTotalRead, (nTotalLength - nTotalRead<= MAX_READ_DATA_LENGTH ? nTotalLength - nTotalRead:MAX_READ_DATA_LENGTH));
 	        	if ( -1 == nRead)
 	        	{
 	        		throw new Exception(BaseErrorMessage.MSG_NET_ERR_CLOSE_STREAM);
 	        	}
 	        	else if ( 0 == nRead )
 	        	{
 	        		if ( ++nReadRetryCount >= MAX_INTPUTSTREAM_READ_RETRY_COUNT)
 	        		{
 	        			throw new Exception(BaseErrorMessage.MSG_NET_ERR_READ_RETRY_COUNT);
 	        		}
 	        	}
 	        	
 	        	nTotalRead += nRead;
 	        }
 	        
 	        bout.write(buf, 0, nTotalLength);
 	        bout.flush();
 	        return bout.toByteArray();
		}
        catch (Exception e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_DATA, e.getMessage()));
		}
        finally 
        { 
        	if ( null != ins)
        	{
        		try
				{
        			ins.close();
				}
				catch (Exception e)
				{
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, e.getMessage());
				}
				
				ins = null;
        	}
        	
        	if ( null != bout )
			{
				try
				{
					bout.flush();
					bout.close();
				}
				catch (Exception e)
				{
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, e.getMessage());
				}
				
				bout = null;
			}
        }
    }
	
	// 파일 받기
	protected void doReceiveDataForFileWrite(InputStream connInputStream, String sFilePath, int nBuffSize) throws BaseNetworkException
	{
		BufferedOutputStream bout = null;
		InputStream ins = null;
		
        try
		{
        	FileOutputStream fout = new FileOutputStream(sFilePath);
        	bout = new BufferedOutputStream(fout); 
        	byte[] buf = new byte[MAX_READ_DATA_LENGTH];
 	        ins = connInputStream;
 	        int nTotalLength = nBuffSize;
 	        int nTotalRead = 0;
 	        int nRead = 0;
 	        int nReadRetryCount = 0;

 	        //Message msg = null;
 	        while (nTotalLength > nTotalRead)
 	        {
 	        	if ( true == mQuitFileJob )
 	        	{
 	        		throw new BaseNetworkException(BaseErrorCode.NET_ERR_USER_QUIT, BaseErrorMessage.MSG_NET_ERR_USER_QUIT);
 	        	}
 	        	
 	        	nRead = ins.read (buf , 0, (nTotalLength - nTotalRead<= MAX_READ_DATA_LENGTH ? nTotalLength - nTotalRead:MAX_READ_DATA_LENGTH));
 	        	if ( -1 == nRead)
 	        	{
 	        		throw new Exception(BaseErrorMessage.MSG_NET_ERR_CLOSE_STREAM);
 	        	}
 	        	else if ( 0 == nRead )
 	        	{
 	        		if ( ++nReadRetryCount >= MAX_INTPUTSTREAM_READ_RETRY_COUNT)
 	        		{
 	        			throw new Exception(BaseErrorMessage.MSG_NET_ERR_READ_RETRY_COUNT);
 	        		}
 	        	}
 	        	
 	        	bout.write(buf, 0, nRead);
 	        	nTotalRead += nRead;

 	        	onSendingProgressStatusMessage(nTotalRead, nTotalLength);
// 	        	msg = Message.obtain();
// 	        	msg.what = BASE_HANDLER_MESSAGE_LIST.ACTIVITY_PROGRESS_STATUS;
// 	        	msg.arg1 = nTotalLength;
// 	        	msg.arg2 = nTotalRead;
// 	        	mHandler.sendMessage(msg);
 	        }
		}
        catch (BaseNetworkException e)
		{
        	throw new BaseNetworkException(e.getErroCode(), e.getMessage());
		}
        catch (Exception e)
		{
        	throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_FILE, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_FILE, e.getMessage()));
		}
        finally 
        { 
        	if ( null != ins)
        	{
        		try
				{
        			ins.close();
				}
				catch (Exception e)
				{
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_FILE, e.getMessage());
				}
				
				ins = null;
        	}
        	
        	if ( null != bout )
			{
				try
				{
					bout.flush();
					bout.close();
				}
				catch (Exception e)
				{
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_FILE, e.getMessage());
				}
				
				bout = null;
			}
        }
    }
	
	// string 데이터 받기
	protected String doReceiveData(InputStream connInputStream, String encoding) throws BaseNetworkException
	{
		BufferedReader br = null;
		
		try
		{
			br = (encoding == null || encoding.length() <= 0) ? new BufferedReader(new InputStreamReader(connInputStream)) :
				new BufferedReader(new InputStreamReader(connInputStream, encoding));
			
			//br = new BufferedReader(new InputStreamReader(connInputStream, encoding));
			String inputLine = "";
			StringBuffer buff = new StringBuffer(); 
			while ( null != (inputLine = br.readLine()))
			{
				buff.append(inputLine); 
			}
			
			return buff.toString();
		}
		catch (Exception e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, e.getMessage());
		}
		finally 
		{ 
			if ( null != br )
			{
				try 
				{ 
					br.close(); 
				} 
				catch (Exception e) 
				{ 
					throw new BaseNetworkException(BaseErrorCode.NET_ERR_RECV_DATA, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_RECV_DATA, e.getMessage()));
				}
				
				br = null;
			}
		} 
	}
}

