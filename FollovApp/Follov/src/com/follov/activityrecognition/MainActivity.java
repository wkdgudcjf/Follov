package com.follov.activityrecognition;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.follov.BuildConfig;
import com.follov.FollovSetUp;
import com.follov.R;
import com.follov.Manager.AppManager;
import com.follov.db.DBPool;
import com.follov.db.vo.DayOfWeek;
import com.follov.db.vo.Loc_route_tb_VO;
import com.follov.locationupdates.LocationUpdatesMainActivity.ErrorDialogFragment;
import com.follov.locationupdates.LocationUtils;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends FragmentActivity implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	public static final String TAG = MainActivity.class.getSimpleName();

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

	// ����ũž ������Ʈ���̵�
	String SENDER_ID = "53984757974";
	// NoteBook ������Ʈ ���̵�
	//String SENDER_ID = "443657579287";

	GoogleCloudMessaging gcm;
	// AtomicInteger msgId = new AtomicInteger();

	String regid = "";
	Context context;




	//��� note2 ��Ʈũž 4.5�� regid
	//String sendTo = "APA91bGzDBx8-LHOIdsSol70O7Evd-_meYbTvVmasZwlNE63HaOaZVkKMG1KUqC9Tj6vgjJWRM_bs3Hek88E_gYWKv2QOWorYN7ldtkjE2qa0uk4TealIb75bg3elv7xiu3t499I2xBaK8BxafLMIQHbai-30-rQvQ";
	//���� regid
	String sendTo = "APA91bGCjFEtTXfzVgDB0YFGlN8PQq77cLmsMqBdEZR_O03XNaNfpjPp_BkllYHIU8UpE3zAqwyovdX6-OugpoZTykWQJq3E8Y777VRmqu4q8BNKM2-bXFeDDcLEkhzfqOkzxeHKmo2jZxH3_ep1mSlMyShuMZkFgg";



	// ��� note2 ��Ʈ�Ͽ��� ���ο� regid
	//String sendTo = "APA91bEkicEGswqexdH3H31XlroZo3enoBfJ9mUcF3jdEE1fPIpSZ3wy8FG7XHwx2gcSrGk8LsuN9BqgwSdqDCPuAiylXhAy7Ep4MVVPu6q2eMLyF_HwGQU0V_2Ml6HklGCOqpBRzGZ2CWeKW_DCSpK-1QTCXe754w";
	// �Ժ� regid
	//String sendTo = "APA91bFVjDjBQTlRDdQWw2Dzsq8ohIG4TmPuWS_uvFoHzBoJNdOXHNtEDqMLNBvuWx30yD46U27Zz_lCHhgAi-J7eX6-6ZWpzzl8rUIhKbNsjNhQ9EbOf9GZVvEJZqZuNeHhj5h8xUDjr5H2ISxhvcs5p8_d-DLKQA";
	// ���� regid
	// String sendTo = "";
	// ���� regid
	// String sendTo = "";

	// �� reg
	// String sendTo =
	// "APA91bHs0wPxj97DI8YJX9rZLrsrmlAZSurI3EHJHecqdvAJaN933-hmL4aV_bOmDez-uUBbiU6j-547skLjpVCaRdVXMFziiAsEIj-nSHR9vwb4xXr1R1oPl1hu49H2CpCmGA5s9wFY2NXmwC0dlYUHFrVWIjB8mw";
	// ��ö reg
	// String sendTo =
	// "APA91bHusd_CDCa_kY25sgIncYd48u9kPWGwUoMGMNys8-5RRz3occ1slN3p15cCVbLEuEqf5G-tPs3jBGNFVazkOUobRnMFDP760w9jKVKNlcVe1ulBpv3mKWHK_ILn_beDNXgd7FBNQ7w8jtG92hQnBopJHdH9gg";
	// ����reg
	// String sendTo =
	// "APA91bHNqTL4adjb_RHjwZzRmphqLuhRYytdwq64gw26qxTOPt6zgxlrJRfxkezSKmi6NRT7d6AIxwms746iavBg1lUPiynT7W94wmGsDLyWcPsyMHq0Y5MEWAGzTx0D9MoT3n9E1Xjz8Y9CLJiQZbP2QKpSri3jiw";





	// apiKey Server DeskTop
	String apiKey = "AIzaSyC9HExyXftYaL2HIdEDrq8cx-RdR0TIlNQ";

	// apiKey for Notebook
	//String apiKey = "AIzaSyB4ZcmVjQiaXRkhtNVYmYnrXTbFCARISNc";

	//static final Handler mHandler = new Handler();

	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;

	private ProgressBar mActivityIndicator;

	private Location myLocation;

	double loversLatitude;
	double loversLongitude;

	// db�� ������ ���� Location�� ������� �����Ұ������� �����ϴ� ����
	boolean saveDataByMyLocation = false;

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


	public void locationReceived(String lat, String lng) {

		this.loversLatitude = Double.valueOf(lat);
		this.loversLongitude = Double.valueOf(lng);
		PRINT_LOG(TAG, "location Received from lover \n");
		PRINT_LOG(TAG, "lovers lat : " + this.loversLatitude);
		PRINT_LOG(TAG, "lovers lng : " + this.loversLongitude);

	}

	public Handler testBetweenHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch(msg.what){

			case AppManager.DISTANCE_CHECK:
				Toast.makeText(MainActivity.this, "������ �Ÿ� " + msg.arg1 + "m",
						Toast.LENGTH_SHORT).show();
				break;
			case AppManager.BATTERY_SEND:
				//				Toast.makeText(MainActivity.this, "����� �� ���͸��� :  " + msg.arg1,
				//						Toast.LENGTH_SHORT).show();
				break;
			case AppManager.BATTERY_RECEIVED:
				//				Toast.makeText(MainActivity.this, "��۹��� ����� ���͸��� " + msg.arg1,
				//						Toast.LENGTH_SHORT).show();
				break;
			case AppManager.SHOW_TOAST:
				Toast.makeText(MainActivity.this, (String)msg.obj,
						Toast.LENGTH_SHORT).show();
				break;

			}

		}

	};

	/**
	 * ����Ʈ ����. ����ð��� �̾ƿ��� Date��ü�� ����� date_tb���̺? �ִ´�.
	 */
	void dateStart() {

		DBPool dbPool = DBPool.getInstance(context);

		int date_code = dbPool.recentDateCode() + 1;
		this.currentDateCode = date_code;

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DATE);

		String day_of_week = DayOfWeek.isWhichDayOfWeek(c
				.get(Calendar.DAY_OF_WEEK));

		SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.follov_date_format));
		dateFormatter.setLenient(false);
		String start_time = dateFormatter.format(new Date());

//		if (dbPool.insertDateStart(new com.follov.db.vo.Date(date_code, year,
//				month, day, day_of_week, start_time))) {
//			isDating = true;
//			PRINT_LOG(TAG, "insertDateStart Success");
//		} else
//			PRINT_LOG(TAG, "insertDateStart failed");

		//����Ʈ�� ���۵Ǹ� ���̻� ���͸� ���� ����� ��� ���� �ʴ´� ����Ʈ�� ������ �ٽ� ���
		unregisterReceiver(mBRBattery);

	}

	/**
	 * ���� ��ġ�� ��� �����ϴ� �޼ҵ�
	 * 
	 * @param latitude
	 * @param longitude
	 */
	void insertLocationRoute(String isSpecial) {

		double latitude;
		double longitude;

		if (saveDataByMyLocation) {
			latitude = myLocation.getLatitude();
			longitude = myLocation.getLongitude();
		} else {
			latitude = loversLatitude;
			longitude = loversLongitude;
		}

		DBPool dbPool = DBPool.getInstance(context);

//		dbPool.insertLocationRoute(new LocationRoute(this.currentDateCode,
//				++this.current_loc_no, latitude, longitude, isSpecial));

	}

	public void turnOnWiFi(){

		//WiFi�� ����������
		if(!wifiManager.isWifiEnabled()){
			PRINT_LOG(TAG, "wifi�� ���������Ƿ� wifi�� �մϴ�");
			wifiManager.setWifiEnabled(true);
		}

	}


	class LocationChecker extends Thread {

		private final String TAG = LocationChecker.class.getSimpleName();

		Location loversLocation;
		// double lat;
		// double lng;

		int between;

		public LocationChecker() {
			super();
		}

		public LocationChecker(Location loversLocation) {
			super();
			this.loversLocation = loversLocation;
		}

		public LocationChecker(String lat, String lng) {
			// this.lat = Double.valueOf(lat);
			// this.lng = Double.valueOf(lng);
		}


		public void run() {

			// 1�������־����
			if (distanceCheck()) {

				//1�� üŷ ������ �ֱ�� �Ÿ��� �ٲٰ� �ٽ� �˻��ϰ� �ؾ���
				//2�� üŷ�� �ؼ� ����ϸ� dating���� �����ϰ� �ؿ��͵� ����



				//���� ���� �о� �ֱ�
				turnOnWiFi();
				//�ּҰŸ� ���̱� 
				MINIMUM_TRACKING_DISTANCE = FollovSetUp.WIFI_TRACKING_DISTANCE;

				//��ġ ������Ʈ �ֱⰡ ����Ʈ���� �ƴҶ�����
				if(updateInterval == FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING){

					////test///
					android.os.Message tempMsg = android.os.Message.obtain();
					tempMsg.what = AppManager.SHOW_TOAST;
					tempMsg.obj = "1�� �Ÿ� �ȿ� ���ͼ� �ֱ⸦ �ٲߴϴ�";
					testBetweenHandler.sendMessage(tempMsg);
					////test///


					//��ġ Ʈ��ŷ�� �ߴ��Ѵ�.
					stopTrackingLocation();
					//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)
					updateInterval = FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING;
					fastestInterval = FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING;

					//��ġ Ʈ��ŷ�� �����Ѵ�.
					startTrackingLocation();

				}


				// Ʈ��ŷ �Ͻ������� ����ٸ� �簳
				if (trackingEndTimer != null)
					trackingEndTimer.cancel();
				// ���� ����
				// WiFi �о� �ֱ�
				// ���� ����

				// ������ ���� ��� �ְ� ����flag true��
				// ������� ���� ���� falg�� false��
				PRINT_LOG(TAG, "�Ÿ��� " + MINIMUM_TRACKING_DISTANCE
						+ " �����̹Ƿ� Ʈ��ŷ ����");

				// ����Ʈ�� �����ϴ� �����̸� ����Ʈ ��ü�� ���� ��������ϰ� ����Ʈ������ ����
				if (!isDating) {

					// ������ ������ �ϸ� start_time�� Ŀ�ð� client DB�� �ٸ��� ����� �� �ִ�.
					dateStart();

					PRINT_LOG(TAG, "myBatteryLevel : " + myBatteryLevel);
					PRINT_LOG(TAG, "loversBatteryLevel : "
							+ loversBatteryLevel);
					if (myBatteryLevel > loversBatteryLevel) {
						saveDataByMyLocation = true;
					} else if (myBatteryLevel == loversBatteryLevel) {
						// ���͸� ���°� ������ ������ �ڵ������� �ϴ°����� �Ѵ�.
					}
					// ����Ʈ ������Ҵ� ����� ����̹Ƿ� IS_SPECIAL�� �ѱ��
					insertLocationRoute("y");
				}
				// ����Ʈ ���϶��� ����� ��Ұ� �ƴϹǷ� ""�� �ѱ��
				// ���� ������������ �ƴ����� �Ǵ��ϴ� ����� �����Ѵ�.
				insertLocationRoute("");

				// int year =

				// DBPool.getInstance(context).insertDateStart(date);

			} else {// �Ÿ��� �հ��
				PRINT_LOG(TAG, "�Ÿ��� �־ Ʈ��ŷ ���� ����");
				//�Ÿ��� �հ�� ���� �Ǵ� �Ÿ��� CEILING ���� �ٲ۴�
				MINIMUM_TRACKING_DISTANCE = FollovSetUp.CEILING_TRACKING_DISTANCE;

				//��ġ ������Ʈ �ֱⰡ 1�������� ����ߴµ� else���� �ͼ� 2�������� ������� ���������
				if(updateInterval == FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING){

					////test///
					android.os.Message tempMsg = android.os.Message.obtain();
					tempMsg.what = AppManager.SHOW_TOAST;
					tempMsg.obj = "2�� ���� ���з� �ֱ⸦ �ø��ϴ�. ";
					testBetweenHandler.sendMessage(tempMsg);
					////test///


					//��ġ Ʈ��ŷ�� �ߴ��Ѵ�.
					stopTrackingLocation();
					//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)
					updateInterval = FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING;
					fastestInterval = FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING;

					//��ġ Ʈ��ŷ�� �����Ѵ�.
					startTrackingLocation();

				}


				// �Ÿ��� �յ� ����Ʈ�� �ϰ� �־�� ���
				if (isDating) {
					// �ð������� ����.

					trackingEndTimer = new Timer();
					trackingEndTimer.schedule(new TrackingEndTimer(),
							FollovSetUp.TRACKING_END_INTERVAL_MILLISECONDS);

				} else {
					// �Ÿ��� �ְ� ����Ʈ�� �ϰ� �ִ� ��찡 �ƴѰ�� �� �ƹ��ϵ� ���� �ʴ´�.
				}
			}

			android.os.Message msg = android.os.Message.obtain();
			msg.what = AppManager.DISTANCE_CHECK;
			msg.arg1 = between;
			testBetweenHandler.sendMessage(msg);

		}



		//		void func(){
		//			
		//			if(updateInterval == FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING){
		//				
		//				updateInterval = FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING;
		//				fastestInterval = FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING;
		//			}
		//			
		//		}

		boolean distanceCheck() {

			if (myLocation == null)
				return false;

			float result[] = new float[20];

			PRINT_LOG(TAG, "myLocation : " + myLocation.toString());
			PRINT_LOG(TAG, "loverslatitude : " + loversLatitude);
			PRINT_LOG(TAG, "loversLongitude : " + loversLongitude);

			Location.distanceBetween(myLocation.getLatitude(),
					myLocation.getLongitude(), loversLatitude, loversLongitude,
					result);

			if (BuildConfig.DEBUG) {
				// for(int i = 0; i < result.length; i++){
				PRINT_LOG(TAG, "result[0] = " + result[0]);
				// }
			}

			between = (int) result[0];
			if (result[0] < MINIMUM_TRACKING_DISTANCE) {

				return true;
			}

			return false;

		}

	}

	/**
	 * Ŀ�ð��� �Ÿ��� �־������� Ʈ��ŷ�� �����ϴ� TimerTask�̴�.
	 * 
	 * @author kimkibum
	 * 
	 */
	private class TrackingEndTimer extends TimerTask {
		public void run() {

			DBPool dbPool = DBPool.getInstance(context);

			SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.follov_date_format));
			dateFormatter.setLenient(false);
			String end_time = dateFormatter.format(new Date());

			dbPool.updateDateEnd(end_time, currentDateCode);
			dbPool.updateLocationRoute(currentDateCode, current_loc_no,
					"y");

			// ����Ʈ �ϰ��ִٴ� ������ false�� �ٲ۴�.
			isDating = false;
			saveDataByMyLocation = false;

			current_loc_no = 0;
			currentDateCode = 0;

			//����Ʈ�� �������Ƿ� �ٽ� ���͸� ���� ����ϱ� �����Ѵ�.
			registerBatteryReceiver();
		}
	}

	public void mOnClick(View v){
		Intent intent = new Intent();
		intent.setAction("com.example.boottest");
		sendBroadcast(intent);
	}
	// onCreate
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// mActivityIndicator = (ProgressBar)
		// findViewById(R.id.address_progress);
//		context = getApplicationContext();
//		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//
//		gcmInit();
//		initLocationClient();
//
//		AppManager.getInstance().setmActivity(this);


	}

	public void initLocationClient() {

		PRINT_LOG(TAG, "LocationClient ��");
		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
		.setInterval(updateInterval);

		// Use high accuracy
		mLocationRequest
		.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
		.setFastestInterval(fastestInterval);

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
	}

	public void gcmInit() {

		if (servicesConnected()) {

			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			PRINT_LOG(TAG, "gcmInit regid : " + regid);

			if (regid.equals("")) {
				PRINT_LOG(TAG, "regid�� �� ���� ����մϴ�");
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
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
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
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					PRINT_LOG(TAG, "��ϵ� regid : " + regid);

					sendRegistrationIdToBackend();

					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			protected void onProgressUpdate(Integer... progress) {

			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				PRINT_LOG(TAG, "register REG Id ��");
			}

			// ���� ������ regId�� ����ϴ� �޼ҵ�
			private void sendRegistrationIdToBackend() {

			}

			private void storeRegistrationId(Context context, String regId) {
				final SharedPreferences prefs = getGCMPreferences(context);
				int appVersion = getAppVersion(context);
				Log.i(TAG, "Saving regId on app version " + appVersion);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(PROPERTY_REG_ID, regId);
				editor.putInt(PROPERTY_APP_VERSION, appVersion);
				editor.commit();
			}

		}.execute(null, null, null);

	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	public void onStop() {
		//
//		// If the client is connected
//		if (mLocationClient.isConnected()) {
//			stopPeriodicUpdates();
//		}
//
//		// After disconnect() is called, the client is considered "dead".
//		mLocationClient.disconnect();
		//
		super.onStop();
	}

	/**
	 * ��ġ����� �����ϴ� �޼ҵ�
	 */
	public void stopTrackingLocation(){
		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();
	}

	public void tenseconds(View v){
		PRINT_LOG(TAG, "10�ʷ� ����");
		//��ġ Ʈ��ŷ�� �ߴ��Ѵ�.
		//stopTrackingLocation();



		mLocationClient.removeLocationUpdates(this);

		//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)

		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
		.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING);

		// Use high accuracy
		mLocationRequest
		.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
		.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING);

		mLocationClient.requestLocationUpdates(mLocationRequest, this);

		//��ġ Ʈ��ŷ�� �����Ѵ�.
		//startTrackingLocation();

	}
	public void thirtyseconds(View v){
		PRINT_LOG(TAG, "10�ʷ� ����");
		mLocationClient.removeLocationUpdates(this);

		//��ġ ������Ʈ �ֱ⸦ ����Ʈ������ �Ѵ�(���ֱ�)
		
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
		.setInterval(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);

		// Use high accuracy
		mLocationRequest
		.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
		.setFastestInterval(FollovSetUp.FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING);

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/**
	 * ��ġ����� �ٽ� �����ϴ� �޼ҵ�
	 * @param updateInterval
	 * @param fastestInterval
	 */
	public void startTrackingLocation(){
		initLocationClient();
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is going into the background. Parts of the UI
	 * may be visible, but the Activity is inactive.
	 */
	@Override
	public void onPause() {

//		unregisterReceiver(mBRBattery);
//		// Save the current setting for updates
//		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED,
//				mUpdatesRequested);
//		mEditor.commit();

		super.onPause();
	}

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {

		super.onStart();
		//
		//		/*
		//		 * Connect the client. Don't re-start any requests here; instead, wait
		//		 * for onResume()
		//		 */
		//mLocationClient.connect();

	}

	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();

//		registerBatteryReceiver();
//
//		// If the app already has a setting for getting location updates, get it
//		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
//			mUpdatesRequested = mPrefs.getBoolean(
//					LocationUtils.KEY_UPDATES_REQUESTED, false);
//
//			// Otherwise, turn off location updates until requested
//		} else {
//			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
//			mEditor.commit();
//		}
//		PRINT_LOG(TAG, "mUpdatesRequested : " + mUpdatesRequested + "");

	}

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
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public static void PRINT_LOG(String TAG, String msg) {

		if (BuildConfig.DEBUG) {
			Log.i(TAG, msg);
		}
	}

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

		startPeriodicUpdates();
		//}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void dropTable(View v) {
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
		//		mUpdatesRequested = true;
		//
		//		PRINT_LOG(TAG, "startUpdates()�޼ҵ� ȣ���");
		//		if (servicesConnected()) {
		//			startPeriodicUpdates();
		//		}
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

	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {

		this.myLocation = location;

		///////////////���� �׽��� ���߿��� ����κ� ����� �ؾ��� 

		//this.loversLatitude = location.getLatitude();
		//this.loversLongitude = location.getLongitude();



		///////////////���� �׽���
		PRINT_LOG(TAG, "onLocationChanged ȣ��");

		// Report to the UI that the location was updated
		Log.i(TAG, getString(R.string.location_updated));
		// mConnectionStatus.setText(R.string.location_updated);

		Log.i(TAG, LocationUtils.getLatLng(this, location));
		// In the UI, set the latitude and longitude to the value received
		// mLatLng.setText(LocationUtils.getLatLng(this, location));

		new GCMSenderThread(location).start();
		new LocationChecker().start();
		// GCMSend(location);
	}

	public void showMaps(View v) {
		PRINT_LOG(TAG, "test����");
		// Location currentLocation = mLocationClient.getLastLocation();

		Intent intent = new Intent(MainActivity.this, MapActivity.class);
		startActivity(intent);

		// new GCMSenderThread().start();

		// GCMSend("��� �����Դϴ�");
		PRINT_LOG(TAG, "test����");
	}

	class GCMSenderThread extends Thread {
		Location location;

		public GCMSenderThread() {
			super();
		}

		public GCMSenderThread(Location location) {
			super();
			this.location = location;

		}

		public void run() {

			sendGCM(regid);

		}

		public void sendGCM(String registrationId) {

			PRINT_LOG(TAG, "regid : \n");
			PRINT_LOG(TAG, registrationId);

			Sender sender = new Sender(apiKey);
			Message msg = new Message.Builder().addData("type", "location")
					.addData("lat", String.valueOf(location.getLatitude()))
					.addData("lng", String.valueOf(location.getLongitude()))
					.build();
			try {
				PRINT_LOG(TAG, "��ġ�� ���");
				sender.send(msg, sendTo, 3);

			} catch (IOException e) {
				Log.getStackTraceString(e);
			}
		}

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
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getSupportFragmentManager(),
						LocationUtils.APPTAG);
			}
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

		PRINT_LOG(TAG, getString(R.string.location_requested));
		mLocationClient.requestLocationUpdates(mLocationRequest, this);

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

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(),
					LocationUtils.APPTAG);
		}
	}

	public void registerBatteryReceiver() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBRBattery, filter);

	}

	BroadcastReceiver mBRBattery = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				PRINT_LOG(TAG, "���͸� ��ȭ Received ȣ��");
				onBatteryChanged(intent);
			}

		}

		public void onBatteryChanged(Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

			PRINT_LOG(TAG, "���� ���͸� : "+level);

			if(myBatteryLevel != level){
				myBatteryLevel = level;
				new BatteryGCMSenderThread(level).start();
			}

		}

		class BatteryGCMSenderThread extends Thread {

			int level;

			public BatteryGCMSenderThread() {
				super();
			}

			public BatteryGCMSenderThread(int level) {
				super();
				this.level = level;
			}

			public void run() {

				sendGCM();

			}

			public void sendGCM() {

				// MainActivity.PRINT_LOG(LOG_TAG, "regid : \n");
				// MainActivity.PRINT_LOG(LOG_TAG, registrationId);

				Sender sender = new Sender(apiKey);
				Message msg = new Message.Builder().addData("type", "battery")
						.addData("battery_level", String.valueOf(level))
						.build();

				try {

					MainActivity.PRINT_LOG(TAG, "���͸� �� ���");

					//test
					android.os.Message testMsg = android.os.Message.obtain();
					testMsg.what = AppManager.BATTERY_SEND;
					testMsg.arg1 = level;
					testBetweenHandler.sendMessage(testMsg);
					///

					sender.send(msg, sendTo, 3);

				} catch (IOException e) {
					Log.getStackTraceString(e);
				}

			}

		}

	};

	public int getLoversBatteryLevel() {
		return loversBatteryLevel;
	}

	public void setLoversBatteryLevel(int loversBatteryLevel) {
		//test
		android.os.Message testMsg = android.os.Message.obtain();
		testMsg.what = AppManager.BATTERY_RECEIVED;
		testMsg.arg1 = loversBatteryLevel;
		testBetweenHandler.sendMessage(testMsg);

		//
		this.loversBatteryLevel = loversBatteryLevel;
	}

}
