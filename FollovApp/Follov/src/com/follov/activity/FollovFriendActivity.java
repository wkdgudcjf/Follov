package com.follov.activity;

import java.util.*;
import java.util.regex.*;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.telephony.*;
import android.text.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.*;
import android.widget.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.component.*;
import com.follov.network.*;
import com.follov.pref.*;
import com.follov.util.*;

public class FollovFriendActivity extends Activity implements OnClickListener {
	RelativeLayout m_oBtnSignin;
	EditText edit_text_follow_firend;
	String phone;
	
	ConnectivityManager cm;
	
	WifiManager wifiManager;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_friend);
		setUi();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public void setUi()
	{
		//번호 필터
		AppManager.getInstance().setActivity(this);
		final InputFilter inputPhoneFilter = new InputFilter(){
			public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend){
				Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
				if(!ps.matcher(source).matches()){
					return"";
				}
				return null;
			}
		};
		edit_text_follow_firend = (EditText)findViewById(R.id.edit_text_follow_firend);
		edit_text_follow_firend.setFilters(new InputFilter[]{inputPhoneFilter});
		m_oBtnSignin = (RelativeLayout)findViewById(R.id.check_friend);
		m_oBtnSignin.setOnClickListener(this);
		if(!(FollovPref.getBoolean("tempr", FollovFriendActivity.this)))
		{
			m_oBtnSignin.setBackgroundResource(R.xml.btn_follow_friend_cancel);
			edit_text_follow_firend.setText(FollovPref.getString("tempp", FollovFriendActivity.this));
			edit_text_follow_firend.setClickable(false);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}


	public Handler handler = new Handler() 
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what)
			{
				case FollovCode.HTTP_ERROR:
				{
					FollovDialog.showDialog("접속 에러입니다.", FollovFriendActivity.this);
					break;
				}
				case FollovCode.REQUEST:
				{
					FollovPref.saveBoolean("tempr", false, FollovFriendActivity.this);
					FollovPref.saveString("tempp", phone, FollovFriendActivity.this);
					m_oBtnSignin.setBackgroundResource(R.xml.btn_follow_friend_cancel);
					edit_text_follow_firend.setClickable(false);
					kakaoDialog();
					break;
				}
				case FollovCode.RESPONSE:
				{
					FollovJsonParser.coupleJson((String)msg.obj,phone,FollovFriendActivity.this);
					Intent i = new Intent(FollovFriendActivity.this, FollovJoinProfileActivity.class);
					startActivity(i);
					finish();
					break;
				}
				case FollovCode.CANCELOK: //코드바까야함 요청 취소 됬을때.
				{
					m_oBtnSignin.setBackgroundResource(R.xml.btn_follow_friend);
					FollovPref.saveBoolean("tempr", true, FollovFriendActivity.this);
					edit_text_follow_firend.setClickable(true);
					break;
				}
				case FollovCode.CANCELNO: //코드바까야함 요청 취소 됬을때.
				{
					FollovDialog.showDialog("요청취소가 안됩니다.", FollovFriendActivity.this);
					break;
				}
			}
		}
	};
	@Override
	public void onClick(View v) {
		phone = edit_text_follow_firend.getText().toString();
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.check_friend:
			{
				if(FollovPref.getBoolean("tempr", FollovFriendActivity.this))
				{
					TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
					String phoneNum = telManager.getLine1Number();
					if(phoneNum==null) phoneNum="0";
					
					if(phoneNum.startsWith("+82"))
					{
						phoneNum = phoneNum.replace("+82", "0");
					}
					HttpPostMethod hpm = new HttpPostMethod(handler,"couplematching.do", FollovJsonParser.requestCheck(phoneNum,phone));
					hpm.start();
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);   
					imm.hideSoftInputFromWindow(edit_text_follow_firend.getWindowToken(),0); 
				}
				else
				{
					TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
					String phoneNum = telManager.getLine1Number();
					if(phoneNum==null) phoneNum="0";
					
					if(phoneNum.startsWith("+82"))
					{
						phoneNum = phoneNum.replace("+82", "0");
					}
					HttpPostMethod hpm = new HttpPostMethod(handler,"couplematchingcancel.do", phoneNum);
					hpm.start();
					//요청취소버튼눌렸을때 해줄것.
				}
				break;
			}
		}
	}
	
	private void kakaoDialog()
	{
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
	    alt_bld.setMessage("카카오톡으로 전송 하시겠습니까?").setCancelable(
	        false).setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id)
	        {
	        	KakaoLink kakao = KakaoLink.getLink(FollovFriendActivity.this); 
				if(!kakao.isAvailableIntent())
				{
					Toast.makeText(getApplicationContext(), "카카오톡을 설치해주세요", 1000).show();
				}
				else
				{
					ArrayList<Map<String,String>> metaInfoArray = new ArrayList<Map<String,String>>();
					
					Map<String,String> metaInfoAndroid = new Hashtable<String,String>(1);
					metaInfoAndroid.put("os", "android");
					metaInfoAndroid.put("devicetype", "phone");
					metaInfoAndroid.put("installurl", "market://details?id=com.kakao.talk");
					metaInfoAndroid.put("executeurl", "FollovLink://startActivity");

					Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
					metaInfoIOS.put("os", "ios");
					metaInfoIOS.put("devicetype", "phone");
					metaInfoIOS.put("installurl", "your iOS app install url");
					metaInfoIOS.put("executeurl", "FollovLink://startActivity");


					metaInfoArray.add(metaInfoAndroid);
					metaInfoArray.add(metaInfoIOS);

					String strMessage = "FollovApp 에서 커플요청을 하셨습니다.";
					String strURL = "http://www.naver.com";
					String strAppId = getPackageName();
					String strAppVer="1.0";
					try
					{
						strAppVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
					} catch (NameNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strAppName = "Follov";
					
					kakao.openKakaoAppLink(FollovFriendActivity.this
							, strURL, strMessage, strAppId, strAppVer, strAppName, "UTF-8", metaInfoArray);
				}
	        }
	        }).setNegativeButton("No",
	        new DialogInterface.OnClickListener()
	        {
	        	public void onClick(DialogInterface dialog, int id)
	        	{
	        		dialog.cancel();
	        	}
	        });
	    AlertDialog alert = alt_bld.create();
	    alert.setTitle("FollovApp");
	    alert.setIcon(R.drawable.kakao);
	    alert.show();
	}
}
