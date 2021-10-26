package com.follov.sendpicture;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.db.*;
import com.follov.pref.*;
import com.google.android.gms.internal.*;

public class HttpSendThread extends Thread
{
	static String CRLF = "\r\n"; 
	static String twoHyphens = "--"; 
	static String boundary = "*****b*o*u*n*d*a*r*y*****"; 

	public static int testPhoto_no = 1;

	FileInputStream mFileInputStream;
	SendPictureService mParent;
	Handler mHandler;
	String urlString = "http://211.189.20.150:8080/FollovServer/image.follov";
	String absolutePath = DBConfig.IMAGE_FILE_DIR;
	DataOutputStream dataStream = null;
	LinkedList<String> list;
	FollovLocationService follovService;
	
	DBPool db;
	Handler activityHandler;

	public HttpSendThread() 
	{
		//			mParent = parent;
		//			mHandler = handler;
	}
	public HttpSendThread(LinkedList<String> list,FollovLocationService follovService, DBPool db, Handler activityHandler) {

		if(follovService == null) return;
		this.follovService = follovService;
		this.list = list;
		this.db = db;
		this.activityHandler = activityHandler;
	}


	public void doFileUpload(String fileName) {
		FollovLocationService.PRINT_LOG("kkkk", "doFileUpload");
		HttpFileUpload(fileName);
	}
	public void HttpFileUpload(String fileName)
	{
		FollovLocationService.PRINT_LOG("kkkk", "HttpFileUpload");
		File uploadFile = new File(absolutePath+fileName);
		HttpURLConnection conn = null;
		try
		{ 
			FileInputStream fileInputStream = new FileInputStream(uploadFile); 
			URL connectURL = new URL(urlString);
			conn = (HttpURLConnection)connectURL.openConnection(); 
			conn.setDoInput(true); 
			conn.setDoOutput(true); 
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			//conn.setRequestProperty("User-Agent", "myFileUploader");
			conn.setRequestProperty("Connection","Keep-Alive"); 
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary); 
			conn.connect();
			dataStream = new DataOutputStream(conn.getOutputStream()); 
			writeFormField("email", follovService.getUserEmail());
			writeFormField("password", follovService.getUserPw());
			writeFormField("coupleid", follovService.getCoupleId());
			writeFormField("name", fileName);

			FollovLocationService.PRINT_LOG("헐설마",fileName);

			writeFileField("image", fileName, "image/jpg", fileInputStream);

			// final closing boundary line

			//test
			//			DBPool db = DBPool.getInstance(follovService);
			//			db.insertLocPhoto(testPhoto_no++, date_code, loc_no, name, date);
			////

			dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF); 
			fileInputStream.close(); 
			dataStream.flush(); 
			dataStream.close();
			dataStream = null;
			Log.d("업로드 테스트", "***********전송완료***********");

			int response = getResponse(conn);
			follovService.showToast("서버에서 받은 response : "+response);




			//
			if (response == FollovCode.PHOTO_SAVED_SUCESS)
			{
				//				DBPool db = DBPool.getInstance(SendPictureService.this);
				//				//db.insertLocPhoto(date_code, loc_no, name, date);
				//				if(db.updateLocationRoute(date_code, loc_no, LocationRoute.IS_SPECIAL)){
				//					follovService.setCheckedSpecialPoint(true);
				//				}
				//여기서 성공코드
				
				db.updateLoc_photo(fileName, "y");
				Log.e("씨발", "성공"); 
				return ;
			}
			else
			{
				//updateLocationNotSpecialPoint(date_code, loc_no);
				Log.e("씨발", "성공"); 
				// for now assume bad name/password
				return ;
			} 
		}
		catch (MalformedURLException mue) { 
			Log.e("씨발", "error: " + mue.getMessage(), mue); 
			//updateLocationNotSpecialPoint(date_code, loc_no);
			return ;
		} 
		catch (IOException ioe)
		{ 
			Log.e("씨발", "error: " + ioe.getMessage(), ioe); 
			//updateLocationNotSpecialPoint(date_code, loc_no);
			return ;
		} 
		catch (Exception e)
		{ 
			Log.e("씨발", "error: " + e.getMessage(), e); 
			//updateLocationNotSpecialPoint(date_code, loc_no);
			return ;
		}  
		finally{
			sendImgHandler.sendEmptyMessage(0);
		}
	}

	private void updateLocationNotSpecialPoint(int date_code, int loc_no){

		if(follovService == null) return;

		if(follovService.isSaveDataByMyLocation()){
			follovService.updateLocationNotSpecialPoint(date_code, loc_no);
		} else {
			follovService.sendPhotoTakenInfoSaveFailedToLover(date_code, loc_no);
		}
	}


	private int getResponse(HttpURLConnection conn)
	{
		try
		{
			DataInputStream io = new DataInputStream(conn.getInputStream()); 
			int code = io.readInt();
			follovService.showToast("리턴받은 코드 : "+code);
			io.close();
			return code;
		}
		catch(Exception e)
		{
			//System.out.println("AndroidUploader: "+e);
			DBPool.MyStackTrace(e);
			return FollovCode.PHOTO_SAVED_FAILED;
		}
	}
	//	    private String getResponse(HttpURLConnection conn)
	//	    {
	//	        try
	//	        {
	//	            DataInputStream dis = new DataInputStream(conn.getInputStream()); 
	//	            byte []        data = new byte[1024];
	//	            int             len = dis.read(data, 0, 1024);
	//	            dis.close();
	//	            int responseCode = conn.getResponseCode();
	//	            if (len > 0)
	//	                return new String(data, 0, len);
	//	            else
	//	                return "";
	//	        }
	//	        catch(Exception e)
	//	        {
	//	            //System.out.println("AndroidUploader: "+e);
	//	            Log.e("씨발", "AndroidUploader: "+e);
	//	            return "";
	//	        }
	//	    }
	/**
	 *  this mode of reading response no good either
	 */
	private String getResponseOrig(HttpURLConnection conn)	    {
		InputStream is = null;
		try   {
			is = conn.getInputStream(); 
			// scoop up the reply from the server
			int ch; 
			StringBuffer sb = new StringBuffer(); 
			while( ( ch = is.read() ) != -1 ) { 
				sb.append( (char)ch ); 
			} 
			return sb.toString();   // TODO Auto-generated method stub
		}
		catch(Exception e)   {
			//System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
			Log.e("씨발", "AndroidUploader: "+e);
		}
		finally   {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {}
		}
		return "";
	}
	/**
	 * write one form field to dataSream
	 * @param fieldName
	 * @param fieldValue
	 */

	private void writeFormField(String fieldName, String fieldValue)  {
		try  {
			dataStream.writeBytes(twoHyphens + boundary + CRLF);    
			dataStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"" + CRLF);
			dataStream.writeBytes(CRLF);
			dataStream.writeBytes(fieldValue);
			dataStream.writeBytes(CRLF);

		}    catch(Exception e)   {
			//System.out.println("AndroidUploader.writeFormField: got: " + e.getMessage());
			Log.e("씨발", "AndroidUploader.writeFormField: " + e.getMessage());
		}

	}
	/**

	 * write one file field to dataSream

	 * @param fieldName - name of file field

	 * @param fieldValue - file name

	 * @param type - mime type

	 * @param fileInputStream - stream of bytes that get sent up

	 */

	private void writeFileField(String fieldName,String fieldValue,String type,FileInputStream fis)
	{
		try {
			// opening boundary line
			dataStream.writeBytes(twoHyphens + boundary + CRLF);    
			dataStream.writeBytes("Content-Disposition: form-data; name=\""
					+ fieldName
					+ "\";filename=\"" 
					+ fieldValue
					+ "\"" 
					+ CRLF);
			dataStream.writeBytes("Content-Type: " + type +  CRLF);
			dataStream.writeBytes(CRLF); 

			// create a buffer of maximum size 
			int bytesAvailable = fis.available(); 
			int maxBufferSize = 1024; 
			int bufferSize = Math.min(bytesAvailable, maxBufferSize); 
			byte[] buffer = new byte[bufferSize]; 
			// read file and write it into form... 
			int bytesRead = fis.read(buffer, 0, bufferSize); 
			while (bytesRead > 0)   { 
				dataStream.write(buffer, 0, bufferSize); 
				bytesAvailable = fis.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize); 
				bytesRead = fis.read(buffer, 0, bufferSize); 
			} 
			// closing CRLF
			dataStream.writeBytes(CRLF);
		}
		catch(Exception e)  {
			//System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
			Log.e("씨발", "AndroidUploader.writeFormField: got: " + e.getMessage());
		}
	}
	
	public Handler sendImgHandler;

	public void run() {
		
		Looper.prepare();
		
		sendImgHandler = new Handler() {

			public void handleMessage(android.os.Message msg) {
				
				if(list == null || list.isEmpty()) {
					Message handlerMessage = Message.obtain();
					handlerMessage.what = FollovCode.SHOW_MAPS;
					activityHandler.sendMessage(handlerMessage);
					FollovLocationService.PRINT_LOG("kkkk", "handler list null");
					return;
				}
				doFileUpload(list.poll());
			}

		};
		Looper.loop();
		
		FollovLocationService.PRINT_LOG("kkkk", "handler 최초 sendMessage");
		
		
		//		
//		
//		Log.d("CatchCamera", "스레드 시작~!");
//		absolutePath = follovService.getPathFromUri(photoUri);
//		Log.d("CatchCamera", "*uri path: " + absolutePath);
//		doFileUpload(urlString, absolutePath);
	}
}