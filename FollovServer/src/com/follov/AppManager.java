package com.follov;

public class AppManager
{
	private static AppManager sInstance = new AppManager();
	
	public static final float SPECIAL_POINT_CHECK_RADIUS = 15.0f;
	
	private AppManager(){};
	
	public AppManager getInstance(){
		return sInstance;
	}
	
	
}
