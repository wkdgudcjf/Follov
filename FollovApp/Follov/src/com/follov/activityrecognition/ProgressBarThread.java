package com.follov.activityrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

class ProgressBarThread extends Thread{
	Looper mLoop;
	ProgressDialog dialog;
	Context mContext;
	ProgressBarThread(Context context){
		mContext = context;
	}


	public void run() {
		Looper.prepare();
		
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("please waiting...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
			
		// TODO Auto-generated method stub
		mLoop = Looper.myLooper();
		Looper.loop();
		
	}	
	
	public void stopProgressBar(){
		dialog.dismiss();
		try{
			Thread.sleep(100);
		}catch(Exception e){
			
		}
		mLoop.quit();
	}
	

}