package com.follov;


import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import org.json.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.*;
import android.location.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.provider.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.follov.Manager.*;
import com.follov.activityrecognition.*;
import com.follov.db.*;
import com.follov.db.vo.*;
import com.follov.geofence.*;
import com.follov.locationupdates.*;
import com.follov.network.*;
import com.follov.pref.*;
import com.follov.sendpicture.*;
import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message;
import com.google.android.gms.common.*;
import com.google.android.gms.gcm.*;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationListener;


public class FollovLocationService extends Service implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	///test
	String testLoversTime;

	public static final String TAG = FollovLocationService.class.getSimpleName();
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static float MINIMUM_TRACKING_DISTANCE = FollovSetUp.CEILING_TRACKING_DISTANCE;
	//public int updateInterval = FollovSetUp.UPDATE_INTERVAL_IN_SECONDS_NOT_DATING;

	private long updateInterval = FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING;
	private long fastestInterval = FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING;

	private boolean isDating = false;

	private int currentDateCode;
	private int current_loc_no = 0;

	int myBatteryLevel;
	int loversBatteryLevel;

	private Timer trackingEndTimer;
	private TrackingEndTimer trackingEndTimerTask;
	private Timer requestLocationTimer;

	String apiKey = "AIzaSyBlZQVIai6Odl7R8M4BGW0r1aOAPFFiv2g";

	GoogleCloudMessaging gcm;
	// AtomicInteger msgId = new AtomicInteger();

	String regid = "";
	Context context;

	int stayOnePositionCnt;
	//locationChange�� �Ҹ� Ƚ��
	int locationHistoryCnt;

	String sendTo;

	// A request to connect to Location Services
	private LocationRequest mLocationSlowRequest;

	private LocationRequest mLocationFastRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;

	// The activity recognition update request object
	private DetectionRequester mDetectionRequester;

	// The activity recognition update removal object
	private DetectionRemover mDetectionRemover;

	private boolean activityDetectionConnected = false;

	private ProgressBar mActivityIndicator;

	private Location myLocation;

	double loversLatitude;
	double loversLongitude;

	// db�� ������ ���� Location�� ������� �����Ұ������� �����ϴ� ����
	private boolean saveDataByMyLocation = false;

	boolean isFemale = false;

	String coupleId;

	String email;
	String pw;

	Sender sender;

	boolean checkedSpecialPoint = false;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	WifiManager wifiManager;

	boolean fastLocationCheckCycle = false;


	ArrayList<Location> locationHistory = new ArrayList<Location>();


	/*
	 * An instance of an inner class that receives broadcasts from listeners and from the
	 * IntentService that receives geofence transition events
	 */
	private GeofenceSampleReceiver mBroadcastReceiver;

	// An intent filter for the broadcast receiver
	private IntentFilter mIntentFilter;

	// Store the list of geofences to remove
	private List<String> mGeofenceIdsToRemove;

	// Store a list of geofences to add
	List<Geofence> mCurrentGeofences;

	// Add geofences handler
	private GeofenceRequester mGeofenceRequester;
	// Remove geofences handler
	private GeofenceRemover mGeofenceRemover;

	SimpleDateFormat receiveTimeFromLoverFormatter;
	SimpleDateFormat dateFormatter;
	SimpleDateFormat loc_timeFormatter;

	public static int defaultNetworkPreference;

	//public static ConnectivityManager cm;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void initGeofence(){
		// Create a new broadcast receiver to receive updates from the listeners and service
		mBroadcastReceiver = new GeofenceSampleReceiver();

		// Create an intent filter for the broadcast receiver
		mIntentFilter = new IntentFilter();

		// Action for broadcast Intents that report successful addition of geofences
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

		// Action for broadcast Intents that report successful removal of geofences
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

		// Action for broadcast Intents containing various types of geofencing errors
		mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

		// All Location Services sample apps use this category
		mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

		// Instantiate a new geofence storage area

		// Instantiate the current List of geofences
		mCurrentGeofences = new ArrayList<Geofence>();

		// Instantiate a Geofence requester
		mGeofenceRequester = new GeofenceRequester(this);

		// Instantiate a Geofence remover
		mGeofenceRemover = new GeofenceRemover(this);
	}

//	//test 나중에는 빼고할것
//	private void testPref(){
//		FollovPref.saveString("", value, context)
//	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		if(startId != 1)
			return START_STICKY;
		sendTo = FollovPref.getString("couplegcm", this);
		isFemale = FollovPref.getBoolean("gender", this);
		coupleId = FollovPref.getString("coupleid", this);
		email = FollovPref.getString("email", this);
		pw = FollovPref.getString("pw", this);
		
		PRINT_LOG(TAG, "sendTo : "+sendTo);
		

		FollovApplication app = (FollovApplication) getApplication();
		app.setFollovIntentService(this);
		AppManager.getInstance().setFollovService(this);
		//return 

		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti = new Notification();

		noti.contentView = new RemoteViews(getPackageName(), R.layout.transparent_notification);

		startForeground(1, noti);

		context = getApplicationContext();
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		//gcmInit();
		//initNetworkSettings();
		initDateFormatter();
		initLocationClient();
		gcmsenderThread = new GCMSenderThread();
		gcmsenderThread.start();

		sender = new Sender(apiKey);
		//ActivityRecognition은 나중에 합시다.
		//initActivityRecognition();
		//startActivityRecognition();
		//

		mLocationClient.connect();

		//registerBatteryReceiver();

		return START_STICKY;
	}

//	private void initNetworkSettings(){
//		defaultNetworkPreference = cm.getNetworkPreference();
//	}

	//	private void setNetworkDefaultSettings(){
	//		cm.setNetworkPreference(defaultNetworkPreference);
	//	}


	private void initDateFormatter(){
		receiveTimeFromLoverFormatter = new SimpleDateFormat("hh:mm:ss");
		receiveTimeFromLoverFormatter.setLenient(false);
		dateFormatter = new SimpleDateFormat(getString(R.string.follov_date_format));
		dateFormatter.setLenient(false);
		loc_timeFormatter = new SimpleDateFormat(getString(R.string.follov_loc_time_format));
		loc_timeFormatter.setLenient(false);
	}

	private void initActivityRecognition(){
		// Get detection requester and remover objects
		mDetectionRequester = new DetectionRequester(this);
		mDetectionRemover = new DetectionRemover(this);

	}

	public void startActivityRecognition(){

		if(!servicesConnected()){

			return;
		}

		mDetectionRequester.requestUpdates();


	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		PRINT_LOG(TAG, "FollovIntentService Destroy()");
		//showToast("Follov onDestroy()호출");
		//setNetworkDefaultSettings();
		DBPool.getInstance(this).dbClose();
		//unregisterReceiver(mBattery);
		super.onDestroy();

	}


	public void locationReceived(String lat, String lng) {

		this.loversLatitude = Double.valueOf(lat);
		this.loversLongitude = Double.valueOf(lng);

		////test
		if(receiveTimeFromLoverFormatter == null) return;

		showToast("상대방으로 부터 위치 gcm받음");
		testLoversTime = receiveTimeFromLoverFormatter.format(new Date());
		/////test
		//		PRINT_LOG(TAG, "service location Received from lover \n");
		PRINT_LOG(TAG, "lovers lat : " + this.loversLatitude);
		PRINT_LOG(TAG, "lovers lng : " + this.loversLongitude);

	}

	public final Handler follovHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch(msg.what){

			case AppManager.SHOW_TOAST:
				Toast.makeText(getApplicationContext(), (String)msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
				//			case FollovConfig.RECEIVED_LOVERS_LOCATION:
				//				//�Ⱦ��µ� �����
				//				break;
				//			case FollovConfig.RECIEVED_LOVERS_BATTERY:
				//				//�Ⱦ��µ� �� ���̽� �����
				//				break;
				//			case FollovConfig.SPEED_UP_LOCATION_CYCLE:
				//				speedUpLocationCheckCycle();
				//				break;
				//			case FollovConfig.SLOW_DOWN_LOCATION_CYCLE:
				//				slowDownLocationCheckCycle();
				//				break;
			case FollovCode.FAST_LOCATION_REQUEST:
				locationRequest(true);
				break;
			case FollovCode.SLOW_LOCATION_REQUEST:
				locationRequest(false);
				break;
				
			case FollovCode.DATE_INSERT_SUCCESS:
				//				insertDateDataToDeviceDB((String)msg.obj);
				break;
			case FollovCode.LOCATION_INSERT_SUCCESS:
				//			insertLocationRouteToDeviceDB((String)msg.obj);
				break;


			}

		}

	};





	private void sendDateDataToServer(int date_code, int year, 
			int month, int day, String day_of_week, String start_time){

		try{
			JSONObject json = new JSONObject();
			json.put("code", FollovCode.DATE_INSERT_REQUEST);
			json.put("email", email);
			json.put("pw", pw);
			json.put("couple_id", coupleId);
			json.put("date_code", date_code);
			json.put("year", year);
			json.put("month", month);
			json.put("day", day);
			json.put("day_of_week", day_of_week);
			json.put("start_time", start_time);
			//showToast("sendServer: "+json.toString());
			new HttpPostMethod(follovHandler,"locationAction.do", json.toString()).start();
		} catch(JSONException e) {
			MyStackTrace(e);
		}

	}

	private void insertDateDataToDeviceDB(int date_code, int year, int month, int day, String day_of_week, String start_time){
		DBPool dbPool = DBPool.getInstance(context);
		
		if (dbPool.insertDate(new com.follov.db.vo.Date_tb_client_VO(date_code, year,
				month, day, day_of_week, start_time), "n")) {
			//showToast("sendDevice: "+json.toString());

			PRINT_LOG(TAG, "insert Success");
		} else
			PRINT_LOG(TAG, "insertDateStart failed");
	}



	private void insertDateDataToDeviceDB(String jsonString){
		JSONTokener jtk = new JSONTokener(jsonString);
		JSONObject json = null;
		try
		{
			json = (JSONObject)jtk.nextValue();
			int date_code = 0;
			int year = 0;
			int month = 0;
			int day = 0;
			String day_of_week = "";
			String start_time = "";
			date_code = json.getInt("date_code");
			year = json.getInt("year");
			month = json.getInt("month");
			day = json.getInt("day");
			day_of_week = json.getString("day_of_week");
			start_time = json.getString("start_time");

			DBPool dbPool = DBPool.getInstance(context);

			if (dbPool.insertDateStart(new com.follov.db.vo.Date_tb_client_VO(date_code, year,
					month, day, day_of_week, start_time))) {
				//showToast("sendDevice: "+json.toString());

				PRINT_LOG(TAG, "insertDateStart Success");
			} else
				PRINT_LOG(TAG, "insertDateStart failed");

		} catch (JSONException e)
		{
			MyStackTrace(e);
		}

	}

	private void sendLocationRouteToServer(String isSpecial){


		try{
			double latitude = myLocation.getLatitude();
			double longitude = myLocation.getLongitude();

			JSONObject json = new JSONObject();
			json.put("code", FollovCode.LOCATION_INSERT_REQUEST);
			json.put("email", email);
			json.put("pw", pw);
			json.put("couple_id", coupleId);
			json.put("date_code", this.currentDateCode);
			json.put("loc_no", ++this.current_loc_no);
			json.put("latitude", latitude);
			json.put("longitude", longitude);
			json.put("isSpecial", isSpecial);

			//showToast("sendServer: "+json.toString());

			new HttpPostMethod(follovHandler,"locationAction.do", json.toString()).start();
		} catch(JSONException e) {
			MyStackTrace(e);
		}

	}


	private void insertLocationRouteToDeviceDB(int date_code, int loc_no, 
			double latitude, double longitude, String isSpecial, String loc_time){
		
		
		DBPool dbPool = DBPool.getInstance(context);
		
		if(dbPool.insertLocationRoute(new Loc_route_tb_VO(date_code,
				loc_no, latitude, longitude, isSpecial, loc_time))) {
			
			PRINT_LOG(TAG, "insertLoc_route Success");
		} else {
			PRINT_LOG(TAG, "insertLoc_route failed");
	
		}
	}

	/**
	 * ���� ��ġ�� ��� �����ϴ� �޼ҵ�
	 * 
	 * @param latitude
	 * @param longitude
	 */
//	private void insertLocationRouteToDeviceDB(String jsonString) {
//
//		JSONTokener jtk = new JSONTokener(jsonString);
//		JSONObject json = null;
//		try
//		{
//			json = (JSONObject)jtk.nextValue();
//
//			int date_code = json.getInt("date_code");
//			int loc_no = json.getInt("loc_no");
//			double latitude = json.getDouble("latitude");
//			double longitude = json.getDouble("longitude");
//			String isSpecial = json.getString("isSpecial");
//
//			DBPool dbPool = DBPool.getInstance(context);
//
//			dbPool.insertLocationRoute(new LocationRoute(date_code,
//					loc_no, latitude, longitude, isSpecial));
//			//showToast("sendDevice: "+json.toString());
//			PRINT_LOG(TAG, "insertLocation Success");
//
//		} catch (JSONException e)
//		{
//			PRINT_LOG(TAG, "insertLocation Failed");
//			MyStackTrace(e);
//		}
//
//
//		//		if (saveDataByMyLocation) {
//		//			latitude = myLocation.getLatitude();
//		//			longitude = myLocation.getLongitude();
//		//		} else {
//		//			latitude = loversLatitude;
//		//			longitude = loversLongitude;
//		//		}
//
//	}



	public void turnOnWiFi(){

		//WiFi�� ����������
		if(!wifiManager.isWifiEnabled()){
			PRINT_LOG(TAG, "wifi�� ���������Ƿ� wifi�� �մϴ�");
			wifiManager.setWifiEnabled(true);
		}

	}

	public void test(){
		if(isDating){

		}
	}


	class LocationChecker extends Thread {

		private final String TAG = LocationChecker.class.getSimpleName();

		//Location loversLocation;

		Handler locationCheckerHandler;
		

		int between;

		public LocationChecker() {
			super();
		}

		public LocationChecker(Location loversLocation) {
			super();
			//this.loversLocation = loversLocation;
		}

		public LocationChecker(String lat, String lng) {
			// this.lat = Double.valueOf(lat);
			// this.lng = Double.valueOf(lng);
		}

		
		public void isCurrentDateEnd(){
			
			PRINT_LOG(TAG, "isCurrentDateEnd 호출");
			DBPool db = DBPool.getInstance(FollovLocationService.this);
			
			int recentDateCode = db.recentDateCode();
			
			if(!db.isCurrentDateEnd(recentDateCode)){
				isDating = true;
				currentDateCode = recentDateCode;
				current_loc_no = db.recentLoc_no(recentDateCode);
				
				PRINT_LOG(TAG, "recentDate_code : "+recentDateCode);
				PRINT_LOG(TAG, "recent loc_no : "+current_loc_no);
			}
			
		}

		public void run() {

			isCurrentDateEnd();
			
			Looper.prepare();
			locationCheckerHandler = new Handler(){

				public void handleMessage(android.os.Message msg){
					
					boolean isDatingDistance = distanceCheck();
					
					//showToast("거리 가까움 : "+isDatingDistance);
					
					
					if(isDating && !isDatingDistance){
						
						checkDateEnd();
						
					} else if (isDating || isDatingDistance) {

						turnOnWiFi();
						
						if(MINIMUM_TRACKING_DISTANCE == FollovSetUp.CEILING_TRACKING_DISTANCE){
							MINIMUM_TRACKING_DISTANCE = FollovSetUp.WIFI_TRACKING_DISTANCE;
							return;
						}

						if(!fastLocationCheckCycle)
						{
							fasterLocationTimer();
							fastLocationCheckCycle = true;
							return;
						}

						if (isDatingDistance && trackingEndTimer != null)
						{
							showToast("트래킹 종료 타이머 취소");
							trackingEndTimer.cancel();
							trackingEndTimerTask.cancel();
							trackingEndTimer = null;
							trackingEndTimerTask = null;
						}

						if (!isDating) {

//							if (myBatteryLevel > loversBatteryLevel) {
//								saveDataByMyLocation = true;
//							} else if (myBatteryLevel == loversBatteryLevel) {
//								if(isFemale)
//									saveDataByMyLocation = true;
//							}
							isDating = true;
//							if(saveDataByMyLocation)
							dateStart();

//							unregisterReceiver(mBattery);
						}

//						if(saveDataByMyLocation)
							saveLocation();


					} else {// �Ÿ��� �հ��

						MINIMUM_TRACKING_DISTANCE = FollovSetUp.CEILING_TRACKING_DISTANCE;
						if (isDating) {

//							if(saveDataByMyLocation) saveLocation();

							//예외처리 넣기
							//checkDateEnd();

						} 

					}

					showToast("상대방과의 거리 " + between + "m");

				}
			};
			
			Looper.loop();


		}

		void saveLocation(){
			//��ö�̶� ��ġ�� ���� �̰� ����ǥ�� �����Ѵ�.
//			if(saveDataByMyLocation){

				locationHistory.add(myLocation);

				locationFasade();

//			}
		}



		/**
		 * ����Ʈ ����. ����ð��� �̾ƿ��� Date��ü�� ����� date_tb���̺? �ִ´�.
		 */
		void dateStart() {

			PRINT_LOG(TAG, "date_start");
			DBPool dbPool = DBPool.getInstance(context);

			int date_code = dbPool.recentDateCode() + 1;
			currentDateCode = date_code;

			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int day = c.get(Calendar.DATE);

			String day_of_week = DayOfWeek.isWhichDayOfWeek(
					c.get(Calendar.DAY_OF_WEEK));

			if(dateFormatter == null) return;
			String start_time = dateFormatter.format(new Date());

			insertDateDataToDeviceDB(date_code, year, month, day, day_of_week, start_time);

			//sendDateDataToServer(date_code, year, month, day, day_of_week, start_time);

			//����Ʈ�� ���۵Ǹ� ���̻� ���͸� ���� ����� ��� ���� �ʴ´� ����Ʈ�� ������ �ٽ� ���


		}

		private void locationFasade(){

			String isSpecial = isSpecialPoint();
			
			double latitude = myLocation.getLatitude();
			double longitude = myLocation.getLongitude();
			
			String loc_time = "";
			
			if(loc_timeFormatter != null)
				loc_time = loc_timeFormatter.format(new Date());
			if(isSpecial.equals("")){
				insertLocationRouteToDeviceDB(currentDateCode, ++current_loc_no, latitude, longitude, isSpecial, loc_time);
				
				//sendLocationRouteToServer(isSpecial);
				checkedSpecialPoint = false;
			}else if(isSpecial.equals("y") && !checkedSpecialPoint){
				insertLocationRouteToDeviceDB(currentDateCode, ++current_loc_no, latitude, longitude, isSpecial, loc_time);
				//sendLocationRouteToServer(isSpecial);
				checkedSpecialPoint = true;
			}

		}


		private String isSpecialPoint(){

			if(locationHistory != null){

				int historySize = locationHistory.size();

				if(historySize >= FollovSetUp.CHECK_SPECIAL_CNT){

					float result[] = new float[5];

					Location firstLocation = locationHistory.get(0);
					Location lastLocation = locationHistory.get(historySize - 1);

					locationHistory.remove(0);

					Location.distanceBetween(
							firstLocation.getLatitude(),firstLocation.getLongitude(), 
							lastLocation.getLatitude(), lastLocation.getLongitude(), result);

					float between = (int) result[0];

					PRINT_LOG(TAG, "ù��°, ������ ��ǥ�� �Ÿ� : " + between + "m");


					if (between < FollovSetUp.SPECAIL_POINT_CHECK_RADIUS){
						showToast(" " + between + "m, special : y");

						return "y";
					} else {
						showToast(" " + between + "m, special : n");
						return "";
					}

				}else if(checkedSpecialPoint){
					return "y";
				}

			}
			return "";

		}

		boolean distanceCheck() {

			if (myLocation == null)
				return false;

			float result[] = new float[5];

			PRINT_LOG(TAG, "myLocation : " + myLocation.toString());
			PRINT_LOG(TAG, "loverslatitude : " + loversLatitude);
			PRINT_LOG(TAG, "loversLongitude : " + loversLongitude);

			Location.distanceBetween(myLocation.getLatitude(),
					myLocation.getLongitude(), loversLatitude, loversLongitude,
					result);

			PRINT_LOG(TAG, "result[0] = " + result[0]);

			between = (int) result[0];
			if (result[0] < MINIMUM_TRACKING_DISTANCE) {
				return true;
			}

			return false;

		}


	}
	
	private void startSendLocationToLoverTimer(long periodInMilliSec){
		
		if(requestLocationTimer != null) {
			requestLocationTimer.cancel();
			requestLocationTimerTask.cancel();
		}

		requestLocationTimer = new Timer();
		requestLocationTimerTask = new SendLocationToLoverTimer();
		requestLocationTimer.schedule(requestLocationTimerTask
				, 1000
				, periodInMilliSec);
	}


	public void fasterLocationTimer(){
		showToast("위치 트래킹 주기를 빠르게합니다.");
		
		requstLocation(true);

		startSendLocationToLoverTimer(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

	}

	public void slowerLocationTimer(){
		
		showToast("위치 트래킹 주기를 늘립니다.");
		
		requstLocation(false);

		startSendLocationToLoverTimer(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);
	}

	private void checkDateEnd(){
 
//		if(saveDataByMyLocation){
			if(trackingEndTimer == null){
				
				PRINT_LOG(TAG, "트래킹 종료 타이머 시작");
				showToast("트래킹 종료 타이머 시작");

				trackingEndTimer = new Timer();

				SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.follov_date_format));
				dateFormatter.setLenient(false);
				String end_time = dateFormatter.format(new Date());

				trackingEndTimerTask = new TrackingEndTimer(currentDateCode, current_loc_no, end_time);


				trackingEndTimer.schedule(trackingEndTimerTask,
						FollovSetUp.TRACKING_END_INTERVAL_MILLISECONDS);


			}
//		}
	}

	private void locationRequest(boolean isFastRequest){

		mLocationClient.removeLocationUpdates(this);
		
		if(isFastRequest)
			mLocationClient.requestLocationUpdates(mLocationFastRequest, FollovLocationService.this);
		else
			mLocationClient.requestLocationUpdates(mLocationSlowRequest, FollovLocationService.this);
		//mLocationClient.removeLocationUpdates(FollovIntentService.this);

	}

	/**
	 * ��ġ������ ��û�ϴ� Ÿ�̸��̴�.
	 * @author kimkibum
	 *
	 */
	private class SendLocationToLoverTimer extends TimerTask {
	
		public void run() {
			//PRINT_LOG(TAG, "requestTimer호출");
			
			if(gcmsenderThread == null || myLocation == null || locationChecker == null) return;
			
			android.os.Message msg = android.os.Message.obtain();
			msg.what = FollovCode.SEND_LOCATION_TO_LOVER;
			msg.obj = myLocation;
			
			gcmsenderThread.sendGCMHandler.sendMessage(msg);
			
			locationChecker.locationCheckerHandler.sendEmptyMessage(0);
			
		}
	}
	
	
	public void requstLocation(boolean isFastRequest){
	
//		android.os.Message msg = android.os.Message.obtain();
//		msg.what = FollovConfig.LOCATION_REQUEST;
		if(isFastRequest){
			follovHandler.sendEmptyMessage(FollovCode.FAST_LOCATION_REQUEST);
		} else {
			follovHandler.sendEmptyMessage(FollovCode.SLOW_LOCATION_REQUEST);
		}
	}
	
	private class SendLocationGcmTimer extends TimerTask{
		
		public void run(){
			PRINT_LOG(TAG, "SendLocationGcmTimer");
			android.os.Message msg = android.os.Message.obtain();
			msg.what = FollovCode.SEND_LOCATION_TO_LOVER;
			msg.obj = myLocation;

			if(gcmsenderThread == null) return;
			//gcm 보내는거 모듈 분리
			gcmsenderThread.sendGCMHandler.sendMessage(msg);
		}
		
	}
	
	
	/**
	 * Ŀ�ð��� �Ÿ��� �־������� Ʈ��ŷ�� �����ϴ� TimerTask�̴�.
	 * 
	 * @author kimkibum
	 * 
	 */
	private class TrackingEndTimer extends TimerTask {

		int date_code;
		int loc_no;
		String end_time;

		public TrackingEndTimer(int date_code, int loc_no, String end_time){
			this.date_code = date_code;
			this.loc_no = loc_no;
			this.end_time = end_time;
		}

		public void run() {
			//test
			showToast("데이트가 종료되었습니다");

			MINIMUM_TRACKING_DISTANCE = FollovSetUp.CEILING_TRACKING_DISTANCE;

			if(fastLocationCheckCycle){

				slowerLocationTimer();

				fastLocationCheckCycle = false;
				//slowDownLocationCheckCycle();

			}

			DBPool dbPool = DBPool.getInstance(context);

			//			JSONObject json = new JSONObject();
			//			json.put("code", FollovCode.DATE_END_TIME_UPDATE_REQUEST);
			//			json.put("email", FollovPref.getString("email", FollovLocationService.this));
			//			json.put("pw", FollovPref.getString("pw", FollovLocationService.this));
			//			json.put("couple_id", coupleId);
			//			json.put("date_code", date_code);
			//			json.put("year", year);
			//			json.put("month", month);
			//			json.put("day", day);
			//			json.put("day_of_week", day_of_week);
			//			json.put("start_time", start_time);
			//			

			//			new HttpPostMethod(follovHandler, "locationAction.do", param, cm, wifiManager)

			//showToast("데이트가 종료되엇습니다");
			dbPool.updateDateEnd(end_time, date_code);
			dbPool.updateLocationRoute(date_code, loc_no,
					"y");

			// ����Ʈ �ϰ��ִٴ� ������ false�� �ٲ۴�.
			isDating = false;
			saveDataByMyLocation = false;

			current_loc_no = 0;
			currentDateCode = 0;

			locationHistoryCnt = 0;
			locationHistory = new ArrayList<Location>();

			//서버로 DB데이터 동기화 하는거 하기

			//registerBatteryReceiver();
		}
	}



	public void initLocationClient() {

		// Get detection requester and remover objects
		mDetectionRequester = new DetectionRequester(this);
		mDetectionRemover = new DetectionRemover(this);

		PRINT_LOG(TAG, "LocationClient ��");
		// Create a new global location parameters object
		mLocationSlowRequest = LocationRequest.create();
		
		mLocationFastRequest = LocationRequest.create();


		//mLocationSlowRequest.setNumUpdates(1);

		/*
		 * Set the update interval
		 */
		//		mLocationRequest
		//		.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

		// Use high accuracy
		//mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		mLocationSlowRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationFastRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		mLocationFastRequest.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING);
		mLocationFastRequest.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);
		
		mLocationSlowRequest.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING);
		mLocationSlowRequest.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_SECONDS_NOT_DATING);

		// Set the interval ceiling to one minute
		//		mLocationRequest
		//		.setFastestInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

		// Note that location updates are off until the user turns them on
		mUpdatesRequested = false;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);
		locationChecker = new LocationChecker();
		locationChecker.start();
	}

	public void gcmInit() {

		if (servicesConnected()) {

			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			PRINT_LOG(TAG, "gcmInit regid : " + regid);

			if (regid.equals("")) {
				PRINT_LOG(TAG, "service regid�� �� ���� ����մϴ�");
				registerInBackground();
			}

		} else {
			PRINT_LOG(TAG, "���� �÷��� ���񽺰� ����ϴ�.");
		}

	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");

		if (registrationId.equals("")) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(FollovLocationService.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			PRINT_LOG(TAG, "appVersion : "+packageInfo.versionCode);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(AppManager.getInstance().SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					PRINT_LOG(TAG, "service ��ϵ� regid : " + regid);

					sendRegistrationIdToBackend();

					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					StackTraceElement[] elem = ex.getStackTrace();

					for(int i = 0; i < elem.length; i++){
						Log.e("kkk", elem[i].toString());
					}
					Log.i(TAG,msg);

					//					if(db != null)
					//						db.close();
				}
				return msg;
			}

			protected void onProgressUpdate(Integer... progress) {

			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				PRINT_LOG(TAG, "service register REG Id ��");
			}

			// ���� ������ regId�� ����ϴ� �޼ҵ�
			private void sendRegistrationIdToBackend() {

			}

		}.execute(null, null, null);

	}

	public void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}


	/**
	 * ��ġ ������Ʈ �ֱ⸦ ��� �ϴ� �޼ҵ�
	 */
	public void speedUpLocationCheckCycle(){
		mLocationClient.removeLocationUpdates(this);
		fastLocationCheckCycle = true;

		//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)

		mLocationSlowRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationSlowRequest
		.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

		// Use high accuracy
		mLocationSlowRequest
		.setPriority(LocationRequest.PRIORITY_LOW_POWER);

		// Set the interval ceiling to one minute
		mLocationSlowRequest
		.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING);

		mLocationClient.requestLocationUpdates(mLocationSlowRequest, this);
	}

	public void slowDownLocationCheckCycle(){
		mLocationClient.removeLocationUpdates(this);
		fastLocationCheckCycle = false;

		//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)

		mLocationSlowRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationSlowRequest
		.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);

		// Use high accuracy
		mLocationSlowRequest
		.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationSlowRequest
		.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING);

		mLocationClient.requestLocationUpdates(mLocationSlowRequest, this);
	}

	/**
	 * ��ġ����� �����ϴ� �޼ҵ�
	 */
	//	public void stopTrackingLocation(){
	//		// If the client is connected
	//		if (mLocationClient.isConnected()) {
	//			stopPeriodicUpdates();
	//		}
	//
	//		// After disconnect() is called, the client is considered "dead".
	//		mLocationClient.disconnect();
	//	}


	/**
	 * ��ġ����� �ٽ� �����ϴ� �޼ҵ�
	 * @param updateInterval
	 * @param fastestInterval
	 */
	//	public void startTrackingLocation(){
	//		initLocationClient();
	//		mLocationClient.connect();
	//	}



	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			//try {

			// Start an Activity that tries to resolve the error
			//				connectionResult.startResolutionForResult(this,
			//						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

			/*
			 * Thrown if Google Play services canceled the original
			 * PendingIntent
			 */

			//} catch (IntentSender.SendIntentException e) {

			// Log the error
			//	e.printStackTrace();
			//}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public static void PRINT_LOG(String TAG, String msg) {

		if (AppManager.DEBUG_MODE) {
			Log.i(TAG, msg);
		}
	}

	SendLocationToLoverTimer requestLocationTimerTask;
	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {

		PRINT_LOG(TAG, getString(R.string.connected));
		// mConnectionStatus.setText(R.string.connected);

		//if (mUpdatesRequested) {

		
		locationRequest(false);
		startSendLocationToLoverTimer(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);
		
		//ó�� LocationClient�� �����Ͽ������� Ŀ���� ���� ������ �ƴϹǷ� not dating�� �ֱ�� Ÿ�̸Ӹ� �����Ѵ�.
//		requestLocationTimer = new Timer();
//		requestLocationTimerTask = new LocationRequestTimer();
//		requestLocationTimer.schedule(requestLocationTimerTask, 0 
//				,FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);

		//		requestLocationTimer.schedule(new LocationRequestTimer(), FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING
		//				,FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

		//������ �Ǿ����� ����

		//startPeriodicUpdates();
		//}
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void dropTable() {
		DBPool dbpool = DBPool.getInstance(context);
		dbpool.deleteAllLoc_Route_tb();
		dbpool.deleteAllDate_tb();

		isDating = false;
		saveDataByMyLocation = false;

		current_loc_no = 0;
		currentDateCode = 0;

	}

	/**
	 * Invoked by the "Start Updates" button Sends a request to start location
	 * updates
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void startUpdates(View v) {
		// �̰��� true�� �ϰ� onPause()���� SharedPreference�� �ִ´� ���� onResume���� �̰��� Ȯ���Ͽ�
		// onConnected���� �ٷ� startPeriodicUpdates()�� ȣ���Ѵ�.
		mUpdatesRequested = true;

		PRINT_LOG(TAG, "startUpdates()�޼ҵ� ȣ���");
		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/**
	 * 
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void getLocation(View v) {

		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			// Location currentLocation = mLocationClient.getLastLocation();

			// Location currentLocation = mLocationClient.reque
			// Display the current location in the UI

			// mLocationClient.reque

			// PRINT_LOG(LOG_TAG, LocationUtils.getLatLng(this,
			// currentLocation));
			// mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
		}

	}

	LocationChecker locationChecker;
	GCMSenderThread gcmsenderThread;
	//Uri photoUri;

	public void sendPhotoTakenSignalToLover(Uri photoUri){

		if(gcmsenderThread == null) return;

		android.os.Message msg= android.os.Message.obtain();
		msg.what = FollovCode.SEND_PHOTO_TAKEN_SINAL_TO_LOVER;
		msg.obj = photoUri;

		gcmsenderThread.sendGCMHandler.sendMessage(msg);
	}

	public void sendPhotoTakenLocationInfoToLoverGCM(String photoUri){
		if(gcmsenderThread == null) return;

		android.os.Message msg = android.os.Message.obtain();
		msg.what = FollovCode.SEND_PHOTO_TAKEN_INFO_TO_LOVER;
		msg.obj = photoUri;

		gcmsenderThread.sendGCMHandler.sendMessage(msg);
	}

	public void sendPhotoTakenInfoSaveFailedToLover(int date_code, int loc_no){

		if(gcmsenderThread == null) return;

		android.os.Message msg = android.os.Message.obtain();
		msg.what = FollovCode.SEND_PHOTO_INFO_SAVE_FAILED_SIGNAL_TO_LOVER;
		msg.arg1 = date_code;
		msg.arg2 = loc_no;

		gcmsenderThread.sendGCMHandler.sendMessage(msg);
	}

	public synchronized void updateLocationToSpecialPoint(int date_code, int loc_no){
		DBPool db = DBPool.getInstance(FollovLocationService.this);
		//db.insertLocPhoto(date_code, loc_no, name, date);
		if(db.updateLocationRoute(date_code, loc_no, "y")){
			checkedSpecialPoint = true;
		}
	}

	public synchronized void updateLocationNotSpecialPoint(int date_code, int loc_no){
		DBPool db = DBPool.getInstance(FollovLocationService.this);
		//db.insertLocPhoto(date_code, loc_no, name, date);
		if(db.updateLocationRoute(date_code, loc_no, "")){
			checkedSpecialPoint = false;
		}
	}


	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {

		//mLocationClient.removeLocationUpdates(this);
		showToast("onLocationChanged");

		this.myLocation = location;
//
//
//		android.os.Message msg = android.os.Message.obtain();
//		msg.what = FollovCode.SEND_LOCATION_TO_LOVER;
//		msg.obj = location;

		//if(gcmsenderThread == null || locationChecker == null) return;

		if(locationChecker == null) return;
		//gcm 보내는거 모듈 분리
		//gcmsenderThread.sendGCMHandler.sendMessage(msg);
		//test중에는 밑에꺼 주석처리
		//if(loversBatteryLevel != 0) locationChecker.locationCheckerHandler.sendEmptyMessage(0);
		//locationChecker.locationCheckerHandler.sendEmptyMessage(0);
	}

	private void geoCoding(final Location location){

		new Thread(){

			@Override
			public void run() {

				android.os.Message testMsg = android.os.Message.obtain();

				Geocoder mCoder = new Geocoder(FollovLocationService.this);

				List<Address> addr = null;

				try{
					addr = mCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3); 
				} catch (IOException e){
					//Log.e(TAG, e.getMessage());
					MyStackTrace(e);		
					return;
				}

				if(addr == null){
					PRINT_LOG(TAG, "addr null");
					return;
				}

				for(int i = 0; i < addr.size(); i++){

					Log.d(TAG, "----------------------------" + i);

					printAddressInfo(addr.get(i));

					//					testMsg.what = AppManager.SHOW_TOAST;
					//					testMsg.obj = addr.get(i).getFeatureName();
					//					follovHandler.sendMessage(testMsg);

					Log.d(TAG, "----------------------------");

				}

			}

		}.start();


	}

	private void printAddressInfo(Address address){
		Log.i(TAG, "addressLine(0) : "+address.getAddressLine(0));
		Log.i(TAG, "addressLine(1) : "+address.getAddressLine(1));
		Log.i(TAG, "featureName : "+address.getFeatureName());
		Log.i(TAG, "latitude : "+address.getLatitude());
		Log.i(TAG, "longitude : "+address.getLongitude());
		Log.i(TAG, "countryCode : "+address.getCountryCode());
		Log.i(TAG, "describeContents :"+address.describeContents());
		Log.i(TAG, "adminArea :"+address.getAdminArea());
		Log.i(TAG, "countryName :"+address.getCountryName());
		Log.i(TAG, "postalCode :"+address.getPostalCode());
		Log.i(TAG, "premises :"+address.getPremises());
		Log.i(TAG, "subAdminArea :"+address.getSubAdminArea());
		Log.i(TAG, "subLocality :"+address.getSubLocality());
		Log.i(TAG, "SubThoroughfare :"+address.getSubThoroughfare());
		Log.i(TAG, "Thoroughfare :"+address.getThoroughfare());
		Log.i(TAG, "url : "+address.getUrl());
		Log.i(TAG, "locale : "+address.getLocale());
	}


	class GCMSenderThread extends Thread {
		Location location;
		Handler sendGCMHandler;
		public GCMSenderThread() {
			super();
		}

		public GCMSenderThread(Location location) {
			super();
			this.location = location;

		}

		public void run() {

			Looper.prepare();
			sendGCMHandler = new Handler(){

				public void handleMessage(android.os.Message msg){

					switch(msg.what){
					case FollovCode.SEND_LOCATION_TO_LOVER:

						location = (Location)msg.obj;
						sendlocationGCM();

						break;
//					case FollovCode.SEND_PHOTO_TAKEN_SINAL_TO_LOVER:
//						Uri photoUri = (Uri)msg.obj;
//						sendPhotoTakenSignalToLoverGCM(photoUri);
//						break;
//					case FollovCode.SEND_PHOTO_TAKEN_INFO_TO_LOVER:
//
//						sendPhotoTakenLocationInfoToLoverGCM((String)msg.obj);
//						break;
//					case FollovCode.SEND_PHOTO_INFO_SAVE_FAILED_SIGNAL_TO_LOVER:
//						int date_code = msg.arg1;
//						int loc_no = msg.arg2;
//						sendPhotoTakenInfoSaveFailedToLover(date_code, loc_no);
//						break;
					}

				}

			};
			Looper.loop();

		}

		public void sendlocationGCM() {

			if(sender == null) return;

			Message msg = new Message.Builder().addData("type", "location")
					.addData("lat", String.valueOf(location.getLatitude()))
					.addData("lng", String.valueOf(location.getLongitude()))
					.build();
			try {
				sender.send(msg, sendTo, 2);
			} catch (Exception e) {
				MyStackTrace(e);

			}
		}
		
		
//		public void sendPhotoTakenSignalToLoverGCM(Uri photoUri){
//			if(sender == null) return;
//
//			Message msg = new Message.Builder().addData("type", "photo_taken")
//					.addData("photoUri", photoUri.toString())
//					.build();
//			try {
//
//				//wifiManager.setWifiEnabled(false);
//				//cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
//
//				sender.send(msg, sendTo, 2);
//
//				//wifiManager.setWifiEnabled(true);
//				//cm.setNetworkPreference(defaultNetworkPreference);
//			} catch (Exception e) {
//				MyStackTrace(e);
//			}
//		}
//
//		public void sendPhotoTakenLocationInfoToLoverGCM(String photoUri){
//			if(sender == null) return;
//
//			Message msg = null;
//
//			synchronized(FollovLocationService.this){
//				int date_code = currentDateCode;
//				int loc_no = current_loc_no;
//				msg = new Message.Builder().addData("type", "photo_taken_location_info")
//						.addData("photoUri", photoUri)
//						.addData("date_code", String.valueOf(date_code))
//						.addData("loc_no", String.valueOf(loc_no))
//						.build();
//				updateLocationToSpecialPoint(date_code, loc_no);
//			}
//			try {
//
//				//wifiManager.setWifiEnabled(false);
//				//cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
//
//				sender.send(msg, sendTo, 2);
//
//				//wifiManager.setWifiEnabled(true);
//				//cm.setNetworkPreference(defaultNetworkPreference);
//			} catch (Exception e) {
//				MyStackTrace(e);
//			}
//		}
//
//		public void sendPhotoTakenInfoSaveFailedToLover(int date_code, int loc_no){
//
//			if(sender == null) return;
//
//			Message msg = new Message.Builder().addData("type", "photo_save_failed")
//					.addData("date_code", String.valueOf(date_code))
//					.addData("loc_no", String.valueOf(loc_no))
//					.build();
//
//
//			try {
//
//				//wifiManager.setWifiEnabled(false);
//				//cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
//
//				sender.send(msg, sendTo, 2);
//
//				//wifiManager.setWifiEnabled(true);
//				//cm.setNetworkPreference(defaultNetworkPreference);
//			} catch (Exception e) {
//				MyStackTrace(e);
//			}
//		}

	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			//Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
			//		this, 0);
			//if (dialog != null) {
			//	ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			//	errorFragment.setDialog(dialog);
			//	errorFragment.show(getSupportFragmentManager(),
			//			LocationUtils.APPTAG);
			//}
			return false;
		}
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
		PRINT_LOG(TAG, getString(R.string.location_updates_stopped));
		// mConnectionState.setText(R.string.location_updates_stopped);
	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {

		//PRINT_LOG(TAG, getString(R.string.location_requested));
		//mLocationClient.requestLocationUpdates(mLocationRequest, this);

		// mConnectionState.setText(R.string.location_requested);
		// PRINT_LOG(LOG_TAG, getString(R.string.location_requested));
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		//testBetweenHandler.
		// Get the error dialog from Google Play services
		//Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
		//	this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		//if (errorDialog != null) {

		// Create a new DialogFragment in which to show the error dialog
		//	ErrorDialogFragment errorFragment = new ErrorDialogFragment();

		// Set the dialog in the DialogFragment
		//	errorFragment.setDialog(errorDialog);

		// Show the error dialog in the DialogFragment
		//	errorFragment.show(getSupportFragmentManager(),
		//			LocationUtils.APPTAG);
		//}
	}

//	public void registerBatteryReceiver() {
//
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//		registerReceiver(mBattery, filter);
//
//	}

//	BroadcastReceiver mBattery = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			String action = intent.getAction();
//
//			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
//				PRINT_LOG(TAG, "배터리 Received ");
//				onBatteryChanged(intent);
//			}
//
//		}
//
//		public void onBatteryChanged(Intent intent) {
//			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//
//			PRINT_LOG(TAG, "나의 : "+level);
//
//			//if(myBatteryLevel != level){
//			myBatteryLevel = level;
//			new BatteryGCMSenderThread(level).start();
//			//}
//
//		}
//
//		class BatteryGCMSenderThread extends Thread {
//
//			int level;
//
//			public BatteryGCMSenderThread() {
//				super();
//			}
//
//			public BatteryGCMSenderThread(int level) {
//				super();
//				this.level = level;
//			}
//
//			public void run() {
//
//				sendGCM();
//
//			}
//
//			public void sendGCM() {
//				if(sender == null) return;
//				// MainActivity.PRINT_LOG(LOG_TAG, "regid : \n");
//				// MainActivity.PRINT_LOG(LOG_TAG, registrationId);
//
//				//Sender sender = new Sender(apiKey);
//				Message msg = new Message.Builder().addData("type", "battery")
//						.addData("battery_level", String.valueOf(level))
//						.build();
//
//				try {
//
//					sender.send(msg, sendTo, 2);
//
//				} catch (IOException e) {
//					MyStackTrace(e);
//				}
//
//			}
//
//		}
//
//	};

	/**
	 * Define a Broadcast receiver that receives updates from connection listeners and
	 * the geofence transition service.
	 */
	public class GeofenceSampleReceiver extends BroadcastReceiver {
		/*
		 * Define the required method for broadcast receivers
		 * This method is invoked when a broadcast Intent triggers the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			// Check the action code and determine what to do
			String action = intent.getAction();

			// Intent contains information about errors in adding or removing geofences
			if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

				handleGeofenceError(context, intent);

				// Intent contains information about successful addition or removal of geofences
			} else if (
					TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED)
					||
					TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {

				handleGeofenceStatus(context, intent);

				// Intent contains information about a geofence transition
			} else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

				handleGeofenceTransition(context, intent);

				// The Intent contained an invalid action
			} else {
				Log.e(GeofenceUtils.APPTAG, getString(R.string.invalid_action_detail, action));
				Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * If you want to display a UI message about adding or removing geofences, put it here.
		 *
		 * @param context A Context for this component
		 * @param intent The received broadcast Intent
		 */
		private void handleGeofenceStatus(Context context, Intent intent) {

		}

		/**
		 * Report geofence transitions to the UI
		 *
		 * @param context A Context for this component
		 * @param intent The Intent containing the transition
		 */
		private void handleGeofenceTransition(Context context, Intent intent) {
			/*
			 * If you want to change the UI when a transition occurs, put the code
			 * here. The current design of the app uses a notification to inform the
			 * user that a transition has occurred.
			 */
		}

		/**
		 * Report addition or removal errors to the UI, using a Toast
		 *
		 * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
		 */
		private void handleGeofenceError(Context context, Intent intent) {
			String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
			Log.e(GeofenceUtils.APPTAG, msg);
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}


	public int getLoversBatteryLevel() {
		return loversBatteryLevel;
	}

	public void setLoversBatteryLevel(int loversBatteryLevel) {

		//showToast("상대방의 배터리 : "+loversBatteryLevel);
		this.loversBatteryLevel = loversBatteryLevel;
	}


	public static void MyStackTrace(Throwable e){
		String msg;
		msg = "Error :" + e.getMessage() +"\n";
		StackTraceElement[] elem = e.getStackTrace();

		for(int i = 0; i < elem.length; i++){
			msg += elem[i].toString()+"\n";
		}
		Log.e(TAG,msg);
	}

	public void showToast(String msg){

		if(AppManager.DEBUG_MODE){
			android.os.Message tempMsg = android.os.Message.obtain();
			tempMsg.what = AppManager.SHOW_TOAST;
			tempMsg.obj = msg;
			follovHandler.sendMessage(tempMsg);
		}
	}

	public String getPathFromUri(Uri uri) {
		Cursor c = getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
		c.moveToNext();
		String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
		c.close();

		return path;
	}

	public String getUserEmail(){
		return FollovPref.getString("email", FollovLocationService.this);
	}

	public String getUserPw(){
		return FollovPref.getString("pw", FollovLocationService.this);
	}

	public String getCoupleId(){
		return FollovPref.getString("coupleid", FollovLocationService.this);
	}

	public String getDate(){
		//SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.follov_photo_date_format));
		if(dateFormatter == null) return "";
		
		dateFormatter.setLenient(false);
		return dateFormatter.format(new Date());
	}





	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	public int getStayOnePositionCnt() {
		return stayOnePositionCnt;
	}

	public void increaseStayOnePositionCnt(){
		this.stayOnePositionCnt++;
	}

	public void setStayOnePositionCnt(int stayOnePositionCnt) {
		this.stayOnePositionCnt = stayOnePositionCnt;
	}


	public boolean isActivityDetectionConnected() {
		return activityDetectionConnected;
	}


	public void setActivityDetectionConnected(boolean activityDetectionConnected) {
		this.activityDetectionConnected = activityDetectionConnected;
	}

	public boolean isDating() {
		return isDating;
	}

	public void setDating(boolean isDating) {
		this.isDating = isDating;
	}

	public boolean isSaveDataByMyLocation() {
		return saveDataByMyLocation;
	}

	public void setSaveDataByMyLocation(boolean saveDataByMyLocation) {
		this.saveDataByMyLocation = saveDataByMyLocation;
	}

	public int getCurrentDateCode() {
		return currentDateCode;
	}

	public void setCurrentDateCode(int currentDateCode) {
		this.currentDateCode = currentDateCode;
	}

	public int getCurrent_loc_no() {
		return current_loc_no;
	}

	public void setCurrent_loc_no(int current_loc_no) {
		this.current_loc_no = current_loc_no;
	}

	public boolean isCheckedSpecialPoint() {
		return checkedSpecialPoint;
	}

	public void setCheckedSpecialPoint(boolean checkedSpecialPoint) {
		this.checkedSpecialPoint = checkedSpecialPoint;
	}



}
