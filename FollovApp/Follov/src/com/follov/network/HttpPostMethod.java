package com.follov.network;

import java.io.*;
import java.net.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;

import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;

import com.follov.*;
import com.follov.Manager.*;


public class HttpPostMethod extends Thread
{
	Handler mHandler;
	String url;
	String param;

	
	
	
	public HttpPostMethod(Handler mHandler, String url, String param)
	{
		this.mHandler=mHandler;
		this.url=url;
		this.param=param;

	
	}
	public void run()
	{
		//cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
		
		//wifiManager.setWifiEnabled(false);
		
		Message msg = Message.obtain();
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
	    HttpConnectionParams.setSoTimeout(httpParams, 5000);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		
		
		
		
		HttpPost httpost = new HttpPost("http://211.189.20.150:8080/FollovServer/"+url);
		
		try {
			StringEntity se  =new StringEntity( param , "UTF-8");
            se.setContentType("application/json");
            se.setContentEncoding( "UTF-8" );
			httpost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json;charset=UTF-8");
	    
	    String result = null;
	    try {
	    	
	    	
			HttpResponse response = httpclient.execute(httpost);
			
			if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_OK){
				result = EntityUtils.toString(response.getEntity());
				JSONTokener jtk = new JSONTokener(result);
				JSONObject jo;
				try
				{
					jo = (JSONObject)jtk.nextValue();
					msg.what = jo.getInt("code");
					msg.obj = jo.getString("result");
					
					if(((String)msg.obj).equals("")){
						msg.obj = param;
					}
					
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_NOT_FOUND){
				msg.what = FollovCode.HTTP_ERROR;			
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_UNAUTHORIZED){
				msg.what = FollovCode.HTTP_ERROR;	
			}
			else{
				msg.what = FollovCode.HTTP_ERROR;	
			}			
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			msg.what = FollovCode.HTTP_ERROR;	
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			msg.what = FollovCode.HTTP_ERROR;	
			e.printStackTrace();
		}
	    finally
	    {
	    	//FollovLocationService.PRINT_LOG("network", "다시돌려줌");

			//Log.e("network","finally network : "+cm.getNetworkPreference());
			
			//wifiManager.setWifiEnabled(true);
			
	    	//cm.setNetworkPreference(1);
	
	    	//Log.e("network","finally after network : "+cm.getNetworkPreference());
	        mHandler.sendMessage(msg);
	    }
	}
	public static String excuteHttpPostMethod(String url, String param){
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
	    HttpConnectionParams.setSoTimeout(httpParams, 35000);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

		HttpPost httpost = new HttpPost("http://211.189.20.150:8080/FollovServer/"+url);
				
		try {
			StringEntity se  =new StringEntity( param , "UTF-8");
            se.setContentType("application/json");
            se.setContentEncoding( "UTF-8" );
			httpost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json;charset=UTF-8");
	    
	    String result = null;
	    try {
			HttpResponse response = httpclient.execute(httpost);
			if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_OK){
				result = EntityUtils.toString(response.getEntity());
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_NOT_FOUND){
				result = "HTTP_ERROR";
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_UNAUTHORIZED){
				result = "HTTP_UNAUTHORIEZED";
			}
			else{
				result = "TIME OUT";
			}			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result;
	}

}
