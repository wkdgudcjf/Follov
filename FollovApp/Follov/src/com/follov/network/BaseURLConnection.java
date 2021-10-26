package com.follov.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Handler;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BaseURLConnection extends BaseNetConnection
{
	protected int MAX_SEND_DATA_LENGTH = 1024 * 4;
	protected int MAX_READ_DATA_LENGTH = MAX_SEND_DATA_LENGTH;
	protected int MAX_INTPUTSTREAM_READ_RETRY_COUNT = 10;
	
	protected Handler mHandler;
	protected String mURLString;
	protected int mResponseContentLength;
	protected HttpURLConnection mURLConn;
	
	protected int mConnTimeout;
	protected int mRequestTimeout;
	
	
	public BaseURLConnection(String sURL)
	{
		this(sURL, null);
	}
	
	public BaseURLConnection(String sURL, Handler handler)
	{
		mURLString = sURL;
		mResponseContentLength = 0;
		mHandler = null;
	}
	
	// content Length 리턴
	public int getResponseContentLength() { return mResponseContentLength;}
	
	// HttpURLConnection 리턴
	public HttpURLConnection getHttpURLConnection() { return mURLConn;}
	
	// connection
	public void connection(byte[] byteData) throws BaseNetworkException
	{
		try
		{
			disconnection();
			URL url = new URL(mURLString);
			//mURLConn = (HttpURLConnection)url.openConnection();
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts(); 
				HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				mURLConn = https;
			}
			else {
				mURLConn = (HttpURLConnection)url.openConnection();
			}
			
			int nContentLength = 0;
			if ( null != byteData)
			{
				nContentLength = byteData.length;
			}
			
			onSetProperties(mURLConn, nContentLength);
			mURLConn.connect();
		}
		catch (Exception e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_CONNECT, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_CONNECT, e.getMessage()));
		}
	}
	
	public void connection() throws BaseNetworkException
	{
		try
		{
			disconnection();
			URL url = new URL(mURLString);
			
			mURLConn = (HttpURLConnection)url.openConnection();
			
			mURLConn.setRequestMethod("GET");
			mURLConn.setDoOutput(true);
			mURLConn.setDoInput(true);
			mURLConn.setUseCaches(false);
			mURLConn.setDefaultUseCaches(false);
			
			mURLConn.setConnectTimeout(mConnTimeout * 1000);
			mURLConn.setReadTimeout(mRequestTimeout * 1000);
			
			mURLConn.connect();
		}
		catch (Exception e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_CONNECT, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_CONNECT, e.getMessage()));
		}
	}
	
	// 코드 확인
	public int checkResponseCode() throws BaseNetworkException
	{
		try
		{
			int responseCode = mURLConn.getResponseCode();
			if(responseCode != HttpURLConnection.HTTP_OK) 
			{ 	
				throw new BaseNetworkException(BaseErrorCode.NET_ERR_HTTP_OK, mURLConn.getResponseMessage());
			}
			
			//  content length 설정
			return mResponseContentLength = mURLConn.getContentLength();
		}
		catch (IOException e)
		{
			throw new BaseNetworkException(BaseErrorCode.NET_ERR_HTTP_OK, String.format("%s [%s]", BaseErrorMessage.MSG_NET_ERR_HTTP_OK, e.getMessage()));
		}
	}
	
	// Connection 설정
	protected void onSetProperties(HttpURLConnection conn, int contentLength){}
	
	// connection 종료
	public void disconnection()
	{
		if ( null != mURLConn )
		{
			mURLConn.disconnect();
			mURLConn = null;
		}
	}
	
	protected static void trustAllHosts() { 
        // Create a trust manager that does not validate certificate chains 
        TrustManager[] trustAllCerts = new TrustManager[] { 
        	new X509TrustManager() 
        	{ 
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                { 
                        return new java.security.cert.X509Certificate[] {}; 
                } 
 
                @Override 
                public void checkClientTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) 
                        throws java.security.cert.CertificateException
                        { 
                    // TODO Auto-generated method stub 
                        } 
 
                @Override 
                public void checkServerTrusted( 
                        java.security.cert.X509Certificate[] chain, 
                        String authType) 
                        throws java.security.cert.CertificateException { 
                    // TODO Auto-generated method stub 
                     
                } 
        	} 
        }; 
 
        // Install the all-trusting trust manager 
        try { 
                SSLContext sc = SSLContext.getInstance("TLS"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                HttpsURLConnection 
                                .setDefaultSSLSocketFactory(sc.getSocketFactory()); 
        } catch (Exception e) { 
                e.printStackTrace(); 
        } 
    } 
     
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() { 
        
		@Override
		public boolean verify(String arg0, SSLSession arg1) {
		// TODO Auto-generated method stub
		return true;
		} 
	};
	///////////////////////////////////////////////////////////////////
}
/*
public class BaseURLConnection
{
	protected int MAX_SEND_DATA_LENGTH = 1024 * 4;
	protected int MAX_READ_DATA_LENGTH = MAX_SEND_DATA_LENGTH;
	protected int MAX_INTPUTSTREAM_READ_RETRY_COUNT = 10;
	
	protected Handler mHandler;
	protected String mURLString;
	protected int mResponseContentLength;
	protected HttpURLConnection mURLConn;
	
	public BaseURLConnection(String sURL)
	{
		this(sURL, null);
	}
	
	public BaseURLConnection(String sURL, Handler handler)
	{
		mURLString = sURL;
		mResponseContentLength = 0;
		mHandler = null;
	}
	
	// content Length
	public int getResponseContentLength() { return mResponseContentLength;}
	
	// HttpURLConnection
	public HttpURLConnection getHttpURLConnection() { return mURLConn;}
	
	// connection
	public void connection(byte[] byteData) throws BaseNetworkException
	{
		try
		{
			disconnection();
			URL url = new URL(mURLString);
			mURLConn = (HttpURLConnection)url.openConnection();
			
			int nContentLength = 0;
			if ( null != byteData)
			{
				nContentLength = byteData.length;
			}
			
			onSetProperties(mURLConn, nContentLength);
			mURLConn.connect();
		}
		catch (Exception e)
		{
			throw new BaseNetworkException(BaseCodes.NET_ERR_CONNECT, String.format("%s [%s]", BaseMessages.MSG_NET_ERR_CONNECT, e.getMessage()));
		}
	}
	
	
	public void checkResponseCode() throws BaseNetworkException
	{
		try
		{
			int responseCode = mURLConn.getResponseCode();
			if(responseCode != HttpURLConnection.HTTP_OK) 
			{ 	
				throw new BaseNetworkException(BaseCodes.NET_ERR_HTTP_OK, mURLConn.getResponseMessage());
			}
			
			//  content length 
			mResponseContentLength = mURLConn.getContentLength();
		}
		catch (IOException e)
		{
			throw new BaseNetworkException(BaseCodes.NET_ERR_HTTP_OK, String.format("%s [%s]", BaseMessages.MSG_NET_ERR_HTTP_OK, e.getMessage()));
		}
	}
	
	// Connection
	protected void onSetProperties(HttpURLConnection conn, int contentLength){}
	
	// connection
	public void disconnection()
	{
		if ( null != mURLConn )
		{
			mURLConn.disconnect();
			mURLConn = null;
		}
	}
}
*/

