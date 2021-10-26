package com.follov.Manager;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;

import com.follov.*;
import com.follov.user.*;
import com.nostra13.universalimageloader.core.*;

public class AppManager {

	public String SENDER_ID	= "757348727071";
	
	public static final boolean DEBUG_MODE = true;
	//테스트 변수
	public static final int BATTERY_SEND = 9000;
	public static final int BATTERY_RECEIVED = 9002;
	public static final int DISTANCE_CHECK = 9001;
	public static final int SHOW_TOAST = 9003;	
	
	//private MainActivity mActivity;
	private FollovLocationService follovService;
	private static AppManager sInstance;
	public int getServiceCalledCnt = 0;
	public int setServiceCalledCnt = 0;
	
	
	//형철
	private String imageUrl = "http://211.189.20.150:8080/FollovServer/upload/";
	private static AppManager manager = new AppManager();
	private UserData user;
	private Activity mActivity;
	private Resources mResources;
	private ImageLoaderConfiguration config;
	private DisplayImageOptions options;
	private int mDisplayWidth;
	private int mDisplayHeight;
	
	
	
	
	private AppManager() 
	{
		user = new UserData();
	}


	public static AppManager getInstance() {
		return manager;
	}


	

	
	
	/**
	 * @return
	 */
	public boolean isPossibleInternet()
	{
		// 네트워크 연결 상태 확인하는 로직
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = ni.isConnected();

		if (!isWifiConn && !isMobileConn)
		{
			return false;
		}
		return true;
	}


	public FollovLocationService getFollovService() {
		return follovService;
	}


	public void setFollovService(FollovLocationService follovService) {
		this.follovService = follovService;
	}


	public static AppManager getsInstance() {
		return sInstance;
	}


	public static void setsInstance(AppManager sInstance) {
		AppManager.sInstance = sInstance;
	}


	public int getGetServiceCalledCnt() {
		return getServiceCalledCnt;
	}


	public void setGetServiceCalledCnt(int getServiceCalledCnt) {
		this.getServiceCalledCnt = getServiceCalledCnt;
	}


	public int getSetServiceCalledCnt() {
		return setServiceCalledCnt;
	}


	public void setSetServiceCalledCnt(int setServiceCalledCnt) {
		this.setServiceCalledCnt = setServiceCalledCnt;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public static AppManager getManager() {
		return manager;
	}


	public static void setManager(AppManager manager) {
		AppManager.manager = manager;
	}


	public UserData getUser() {
		return user;
	}


	public void setUser(UserData user) {
		this.user = user;
	}


	public Activity getActivity() {
		return mActivity;
	}


	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}


	public Resources getmResources() {
		return mResources;
	}


	public void setmResources(Resources mResources) {
		this.mResources = mResources;
	}


	public ImageLoaderConfiguration getConfig() {
		return config;
	}


	public void setConfig(ImageLoaderConfiguration config) {
		this.config = config;
	}


	public DisplayImageOptions getOptions() {
		return options;
	}


	public void setOptions(DisplayImageOptions options) {
		this.options = options;
	}


	public int getmDisplayWidth() {
		return mDisplayWidth;
	}


	public void setmDisplayWidth(int mDisplayWidth) {
		this.mDisplayWidth = mDisplayWidth;
	}


	public int getmDisplayHeight() {
		return mDisplayHeight;
	}


	public void setmDisplayHeight(int mDisplayHeight) {
		this.mDisplayHeight = mDisplayHeight;
	}

	
}
