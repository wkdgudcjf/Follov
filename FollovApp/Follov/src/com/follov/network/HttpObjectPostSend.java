package com.follov.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.follov.Manager.FollovCode;
import com.follov.db.vo.Loc_photo_tb_VO;
import com.follov.db.vo.RecentDataResponse;
import com.follov.db.vo.UnUploadedClientData;


public class HttpObjectPostSend extends Thread
{
	Handler resultHandler;
	String param;
	Object sendObject;
	
	public HttpObjectPostSend(Handler resultHandler, Object sendObject)
	{
		this.resultHandler = resultHandler;
		this.sendObject = sendObject;
	
	}
	
	public void run()
	{
		Message msg = Message.obtain();
		HttpURLConnection urlCon = null;
//	    String result = "";
	    
	    try {
	    	
	    	URL url = new URL("http://211.189.20.150:8080/FollovServer/RecentDataAction.follov");
	    	urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestMethod("POST");
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			
			UnUploadedClientData test = (UnUploadedClientData) sendObject;
			
			ObjectOutputStream out = new ObjectOutputStream(urlCon.getOutputStream());
			out.writeObject(sendObject);
			out.close();
	    	
			
			
			
	    	int responseCode = urlCon.getResponseCode();
	    	
			if(responseCode == HttpURLConnection.HTTP_OK){
				ObjectInputStream in = new ObjectInputStream(urlCon.getInputStream());
				
				Log.i("KIBUM", "여긴오나 HTTP _ OK");
				
				RecentDataResponse result = (RecentDataResponse) in.readObject();
				
				Log.i("KIBUM", "RESULT : "+result.toString());
				
				msg.what = FollovCode.MERGED_DATA_RECEIVED;
				msg.obj = result;
				
				in.close();
//				BufferedReader br = new BufferedReader(
//						new InputStreamReader(urlCon.getInputStream()));
//						
//				for(;;){
//					String line = br.readLine();
//					if(line == null) break;
//					result += line;
//				}
//				
//				JSONTokener jtk = new JSONTokener(result);
//				JSONObject jo;
//				try
//				{
//					jo = (JSONObject)jtk.nextValue();
//					msg.what = jo.getInt("code");
//					msg.obj = jo.getString("result");
//					
//					if(((String)msg.obj).equals("")){
//						msg.obj = param;
//					}
//					
//				} catch (JSONException e)
//				{
//					// 
//					e.printStackTrace();
//				}
				
			}
			else if(responseCode == HttpURLConnection.HTTP_NOT_FOUND){
				msg.what = FollovCode.HTTP_ERROR;			
			}
			else if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			msg.what = FollovCode.HTTP_ERROR;
			e.printStackTrace();
		}
	    finally
	    {
	    	urlCon.disconnect();
	        resultHandler.sendMessage(msg);
	    }
	}
	
//	public static String excuteHttpPostMethod(String url, String param){
//		HttpParams httpParams = new BasicHttpParams();
//	    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
//	    HttpConnectionParams.setSoTimeout(httpParams, 35000);
//		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
//
//		HttpPost httpost = new HttpPost("http://211.189.20.150:8080/FollovServer/"+url);
//				
//		try {
//			StringEntity se  =new StringEntity( param , "UTF-8");
//            se.setContentType("application/json");
//            se.setContentEncoding( "UTF-8" );
//			httpost.setEntity(se);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    httpost.setHeader("Accept", "application/json");
//	    httpost.setHeader("Content-type", "application/json;charset=UTF-8");
//	    
//	    String result = null;
//	    try {
//			HttpResponse response = httpclient.execute(httpost);
//			if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_OK){
//				result = EntityUtils.toString(response.getEntity());
//			}
//			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_NOT_FOUND){
//				result = "HTTP_ERROR";
//			}
//			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_UNAUTHORIZED){
//				result = "HTTP_UNAUTHORIEZED";
//			}
//			else{
//				result = "TIME OUT";
//			}			
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		return result;
//	}

}
