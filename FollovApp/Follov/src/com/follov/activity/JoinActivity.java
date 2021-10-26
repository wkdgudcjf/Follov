package com.follov.activity;


import java.io.*;
import java.net.*;
import java.util.regex.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.telephony.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.component.*;
import com.follov.network.*;
import com.follov.pref.*;
import com.follov.util.*;
import com.google.android.gms.common.*;
import com.google.android.gms.gcm.*;

public class JoinActivity extends Activity implements  View.OnClickListener
{
	
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	ConnectivityManager cm;
	WifiManager wifiManager;
	LinearLayout m_oBtnSignin;
	EditText join_pwd;
	EditText join_email;
	EditText join_pwd_check_txt;
	String email;
	String pw1;
	GoogleCloudMessaging gcm;
	String regid;
	//String SENDER_ID = "757348727071";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		setUi();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}

	public void setUi()
	{
		//email 입력 필터
		final InputFilter inputEmailFilter = new InputFilter(){
			public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend){
				Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
				if(!ps.matcher(source).matches()){
					return"";
				}
				return null;
			}
		};

		//영문 숫자 필터
		final InputFilter inputFilter = new InputFilter(){
			public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend){
				Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
				if(!ps.matcher(source).matches()){
					return"";
				}
				return null;
			}
		};
		m_oBtnSignin = (LinearLayout)findViewById(R.id.btn_sign);
		join_pwd = (EditText)findViewById(R.id.join_pwd);
		join_email = (EditText)findViewById(R.id.join_email);
		join_pwd_check_txt = (EditText)findViewById(R.id.join_pwd_check_txt);
		join_pwd.setFilters(new InputFilter[]{inputFilter});
		join_email.setFilters(new InputFilter[]{inputEmailFilter});
		join_pwd_check_txt.setFilters(new InputFilter[]{inputFilter});
		m_oBtnSignin.setOnClickListener(this);
		String email = FollovPref.getString("email", this);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}
	/**
	    * Verify that Google Play services is available before making a request.
	    * 
	    * @return true if Google Play services is available, otherwise false
	    */
    private boolean servicesConnected() {
       // Check that Google Play services is available
       int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

       // If Google Play services is available
       if (ConnectionResult.SUCCESS == resultCode) {
    	   // In debug mode, log the status
           //Log.d(");

    	   // Continue
    	   return true;
    	   // Google Play services was not available for some reason
       } else {
    	   // Display an error dialog
           return false;
       }
    }
	public void gcmInit() {
		if (servicesConnected()) {
			gcm = GoogleCloudMessaging.getInstance(this);
	        regid = getRegistrationId(this);
	 
	        if (regid.equals("")) {
	        	Log.i("reg", "regid가 없어서 새로 등록합니다");
	            registerInBackground();
	        }
	        Log.i("reg", " regid : " + regid);
	    } 
	    else
	    {
	    	//PRINT_LOG(LOG_TAG, "구글 플레이 서비스가 없습니다.");
	    }
	}
	
	 private void registerInBackground() {
	      new AsyncTask<Void, Integer, String>() {
	         @Override
	         protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	               Context context = getApplicationContext();
	               if (gcm == null) {
	                  gcm = GoogleCloudMessaging.getInstance(context);
	               }
	               regid = gcm.register(AppManager.getInstance().SENDER_ID);
	               msg = "Device registered, registration ID=" + regid;
	               Log.i("reg", "등록된 regid : " + regid);
	              // PRINT_LOG(LOG_TAG, "등록된 regid : " + regid);

	               sendRegistrationIdToBackend();

	               storeRegistrationId(context, regid);
	               TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
					String phoneNum = telManager.getLine1Number();
					if(phoneNum==null) phoneNum="0";
					
					if(phoneNum.startsWith("+82"))
					{
						phoneNum = phoneNum.replace("+82", "0");
					}
					
					// regid가 null이면
					if ("".equals(regid)) {
						msg = "네트워크 환경이 원활하지 않습니다. 다시 시도해 주십시오.";
						
						Message handlerMsg = Message.obtain();
						handlerMsg.what = FollovCode.GCM_NETWORK_ERROR;
						handlerMsg.obj = msg;
						handler.sendMessage(handlerMsg);
					}
					else 
					{
						FollovPref.saveString("mygcm", regid, JoinActivity.this);
		                HttpPostMethod hpm = new HttpPostMethod(handler,"join.do", FollovJsonParser.join(email,pw1,phoneNum,regid));
					    hpm.start();
					} 
				} catch (SocketTimeoutException ste) {
					msg = "네트워크 환경이 원활하지 않습니다. 다시 시도해 주십시오.";
					
					Message handlerMsg = Message.obtain();
					handlerMsg.what = FollovCode.GCM_NETWORK_ERROR;
					handlerMsg.obj = msg;
					handler.sendMessage(handlerMsg);
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
	           // PRINT_LOG(LOG_TAG, "register REG Id 끝");
	         }


	         // 나의 서버로 regId를 전송하는 메소드
	         private void sendRegistrationIdToBackend() {
	        
	         }

	         private void storeRegistrationId(Context context, String regId) {
	            final SharedPreferences prefs = getGCMPreferences(context);
	            int appVersion = getAppVersion(context);
	           // Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
	            SharedPreferences.Editor editor = prefs.edit();
	            editor.putString(PROPERTY_REG_ID, regId);
	            editor.putInt(PROPERTY_APP_VERSION, appVersion);
	            editor.commit();
	         }

	      }.execute(null, null, null);

	   }
	 
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
	 
	 private SharedPreferences getGCMPreferences(Context context) {
	      // This sample app persists the registration ID in shared preferences,
	      // but
	      // how you store the regID in your app is up to you.
	      return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	   }
	
	 private String getRegistrationId(Context context) {
	      final SharedPreferences prefs = getGCMPreferences(context);
	      String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	      if (registrationId.equals("")) {
	         //Log.i(LOG_TAG, "Registration not found.");
	         return "";
	      }
	      // Check if app was updated; if so, it must clear the registration ID
	      // since the existing regID is not guaranteed to work with the new
	      // app version.
	      int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
	            Integer.MIN_VALUE);
	      int currentVersion = getAppVersion(context);
	      if (registeredVersion != currentVersion) {
	        // Log.i(LOG_TAG, "App version changed.");
	         return "";
	      }
	      return registrationId;
	   }
	Handler handler = new Handler() 
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what)
			{
				case FollovCode.HTTP_ERROR:
					FollovDialog.showDialog("접속 에러 입니다.", JoinActivity.this);
					break;
				case FollovCode.JOIN_ERROR:
					FollovDialog.showDialog("회원가입 에러입니다.", JoinActivity.this);
					break;
				case FollovCode.EMAIl_CHECK_COMPLETION:
					gcmInit();
					break;
				case FollovCode.GCM_NETWORK_ERROR:
					FollovDialog.showDialog((String)msg.obj, JoinActivity.this);
					break;
				case FollovCode.JOIN_COMPLETE:
					FollovPref.saveInt("logincheck", 1, JoinActivity.this); //회원가입완료.
					FollovPref.saveString("email",email , JoinActivity.this); //email.
					FollovPref.saveString("pw", pw1, JoinActivity.this); //비밀번호
					AppManager.getInstance().getUser().setEmail(email);
					AppManager.getInstance().getUser().setPassword(pw1);
					Intent i = new Intent(JoinActivity.this, FollovFriendActivity.class);
					startActivity(i);
					finish();
					break;
				case FollovCode.EMAIl_CHECK_DUPLCATED:
					FollovDialog.showDialog("이메일 중복입니다.", JoinActivity.this);
					break;
			}
		}
	};
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_sign:
			{
				email = join_email.getText().toString();
				pw1 = join_pwd.getText().toString();
				String pw2 = join_pwd_check_txt.getText().toString();
			/*	if(!email.contains("@")||!email.contains(".")
						||email.lastIndexOf("@")!=email.indexOf("@")
						||email.lastIndexOf(".")!=email.indexOf(".")
						||email.lastIndexOf("@")>=email.indexOf("."))
				{		
					FollovDialog.showDialog("이메일 양식을 지켜주세요.", JoinActivity.this);
				}				
				else if((pw1.length()<6||pw1.length()>14)||(pw2.length()<6||pw2.length()>14))
				{
					FollovDialog.showDialog("패스워드를 6~14자로 해주세요.", JoinActivity.this);
				}
				else if(!(pw1.equals(pw2)))
				{
					FollovDialog.showDialog("패스워드를 확인과 다릅니다.", JoinActivity.this);
				}*/
				//else
				//{
					HttpPostMethod hpm = new HttpPostMethod(handler,"checkemail.do", email);
					hpm.start();
				//}
				break;
			}
		}
	}
}
