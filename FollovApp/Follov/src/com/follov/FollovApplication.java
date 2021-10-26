package com.follov;

import android.app.*;

import com.follov.activityrecognition.*;

public class FollovApplication extends Application{
	
	private FollovLocationService follovIntentService;
	public MapActivity testmapActivity;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public FollovLocationService getFollovIntentService() {
		return follovIntentService;
	}

	public void setFollovIntentService(FollovLocationService follovIntentService) {
		this.follovIntentService = follovIntentService;
	}
	
	

}
