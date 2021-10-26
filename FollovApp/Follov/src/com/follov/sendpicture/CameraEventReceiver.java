package com.follov.sendpicture;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.follov.R;
import com.follov.pref.FollovPref;

public class CameraEventReceiver extends BroadcastReceiver {
	
	Uri uri;
	
	static String CRLF = "\r\n"; 
    static String twoHyphens = "--"; 
    static String boundary = "*****b*o*u*n*d*a*r*y*****"; 

	
	public void onReceive(Context context, Intent intent) {

		
		Log.d("slog", "in CameraEventReceiver.");
		Uri uri = intent.getData();

		Toast.makeText(context, "Photo taken - " + uri, Toast.LENGTH_LONG).show();
		Log.d("slog", "[onReceive] URI - " + uri);

		//onHandleIntent(intent);
		
//		ComponentName comp = new ComponentName(context.getPackageName(), SendPictureService.class.getName());
//
//		context.startService(new Intent().setComponent(comp));
//		
		intent.setClass(context, SendPictureService.class);
		context.startService(intent);

	}
	
	

	
}