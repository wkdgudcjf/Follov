package com.follov.sendpicture;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.follov.FollovLocationService;
import com.follov.db.DBConfig;
import com.follov.pref.FollovPref;

public class SaveImageThread extends Thread
{
	Uri photoUri;
	FollovLocationService follovService;
	String path;
	String name;
	public SaveImageThread(Uri photoUri, FollovLocationService follovService)
	{
		this.photoUri=photoUri;
		this.follovService=follovService;
		path=follovService.getPathFromUri(photoUri);
	}
	
	public void run() {
		Log.d("CatchCamera", "스레드 시작~!");
		imageSave();
	}

	private void imageSave()
	{
		Log.d("CatchCamera", "*uri path: " + follovService.getPathFromUri(photoUri));
		BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inSampleSize = 2;  
	    Bitmap orgImage = BitmapFactory.decodeFile(path, options);  
	    Bitmap resize = Bitmap.createScaledBitmap(orgImage, 100, 100, true); 
		name = path.substring(path.lastIndexOf("/")+1, path.length());
		String email = follovService.getUserEmail();
	    try {
	    	imageMake(resize , email+"_"+name);
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
	/** 
	 * 비트맵 이미지를 SD카드에 저장 
	 *  
	 * @throws IOException 
	 */
	public static boolean imageMake(Bitmap image, String imageName) throws IOException 
	{ 
	    // 파일 생성 
	    File file = new File(DBConfig.IMAGE_FILE_DIR + imageName); 
	    // Cache 폴더에 저장하려면 getExternalCacheDir() 
//	    file.createNewFile(); 
	
	    BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream(file)); 
	    
	    image.compress(CompressFormat.JPEG, 100, out); 
	    out.flush(); 
	    out.close(); 
	
	    return true; 
	} 
}
