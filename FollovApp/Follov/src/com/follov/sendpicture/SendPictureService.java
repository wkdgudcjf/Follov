package com.follov.sendpicture;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Date;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.db.*;
import com.follov.db.vo.*;
import com.follov.pref.*;
// 전체적인 구조 다시 체크
public class SendPictureService extends IntentService{


	FollovApplication app;
	FollovLocationService follovService;

	public SendPictureService(){
		super("SendPictureService");
	}


	private static final String TAG = SendPictureService.class.getSimpleName();

	
	//	public SendPictureService() {
	//		super("SendPictureService");
	//	}




	public void onHandleIntent(Intent intent)
	{	
		app = (FollovApplication) getApplication();
		follovService = app.getFollovIntentService();


//		if(follovService != null ){
		if(follovService != null && follovService.isDating()){

//			if(follovService.isSaveDataByMyLocation()){

				FollovLocationService.PRINT_LOG(TAG, "sendPicture onHandleIntent()");

				int date_code = follovService.getCurrentDateCode();
				int loc_no = follovService.getCurrent_loc_no();
				
				DBPool db = DBPool.getInstance(SendPictureService.this);
				//db.insertLocPhoto(date_code, loc_no, name, date);
				follovService.updateLocationToSpecialPoint(date_code, loc_no);
				
				Uri photoUri = intent.getData();
				String absolutePath = follovService.getPathFromUri(photoUri);
				String name = absolutePath.substring(absolutePath.lastIndexOf("/")+1, absolutePath.length());
				
				String date = follovService.getDate();
				String email = FollovPref.getString("email", this);
//				int recentPhoto_no = db.recentPhotoNo(email);
				db.insertLocPhoto(date_code, loc_no, email+"_"+name, date, email, "n");
		
				FollovLocationService.PRINT_LOG("CatchCamera", photoUri.toString());
				
				
				new SaveImageThread(photoUri,follovService).start();

//			} else {
//				//현재 트래킹을 안하고 있는 핸드폰에서 사진을 찍을경우
//				if(follovService != null) follovService.PRINT_LOG(TAG, "트래킹 중이 아닌 핸드폰에서 사진찍음");
//				
//				Uri photoUri = intent.getData();
//				
//				follovService.sendPhotoTakenSignalToLover(photoUri);
//				
//			}

		}else{
			//데이트를 안하고있을경우
			if(follovService != null)
				follovService.showToast("데이트 중이 아닌데 사진찍었습니다");
//		}
		}
	}


	
	

	//	@Override
	//	public IBinder onBind(Intent arg0) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
}
