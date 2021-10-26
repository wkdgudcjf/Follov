package com.follov.activity;


import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.component.*;
import com.follov.network.*;
import com.follov.pref.*;
import com.follov.util.*;

public class FollovJoinProfileActivity extends Activity implements OnClickListener {
	
	LinearLayout m_oBtnNext;
	LinearLayout m_oBtnMan;
	LinearLayout m_oBtnWoman;
	LinearLayout birthday;
	ImageView img_man;
	ImageView img_woman;
	TextView edit_text_year;
	TextView edit_text_month;
	TextView edit_text_day;
	GregorianCalendar gc;
	boolean isGirl = false;
	
	ConnectivityManager cm;
	WifiManager wifiManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_profile);
		setUi();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}

	public void setUi()
	{
		gc = new GregorianCalendar();
		
		edit_text_year = (TextView)findViewById(R.id.edit_text_year);
		edit_text_month = (TextView)findViewById(R.id.edit_text_month);
		edit_text_day = (TextView)findViewById(R.id.edit_text_day);
		
		m_oBtnNext = (LinearLayout)findViewById(R.id.next_btn);
		m_oBtnMan = (LinearLayout)findViewById(R.id.gender_man);
		m_oBtnWoman = (LinearLayout)findViewById(R.id.gender_woman);
		birthday = (LinearLayout)findViewById(R.id.layout_birth);
		
		m_oBtnNext.setOnClickListener(this);
		m_oBtnMan.setOnClickListener(this);
		m_oBtnWoman.setOnClickListener(this);
		birthday.setOnClickListener(this);
		
		img_man = (ImageView)findViewById(R.id.gender_img_boy);
		img_woman = (ImageView)findViewById(R.id.gender_img_woman);
	}
	@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}
	Handler handler = new Handler() 
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what)
			{
				case FollovCode.HTTP_ERROR:
				{
					FollovDialog.showDialog("접속 에러입니다.", FollovJoinProfileActivity.this);
					break;
				}
				case FollovCode.PROFILE_COMPLETE:
				{
					FollovPref.saveInt("logincheck", 3, FollovJoinProfileActivity.this); //회원가입완료.
					FollovPref.saveInt("myyear",gc.get(Calendar.YEAR) , FollovJoinProfileActivity.this);
					FollovPref.saveInt("mymonth",gc.get(Calendar.MONTH)+1 , FollovJoinProfileActivity.this);
					FollovPref.saveInt("myday",gc.get(Calendar.DATE) , FollovJoinProfileActivity.this);
					FollovPref.saveBoolean("gender", isGirl,FollovJoinProfileActivity.this);
					AppManager.getInstance().getUser().setSex(isGirl);
					AppManager.getInstance().getUser().setMyyear(gc.get(Calendar.YEAR));
					AppManager.getInstance().getUser().setMymonth(gc.get(Calendar.MONTH)+1);
					AppManager.getInstance().getUser().setMyday(gc.get(Calendar.DATE));
//					Intent i = new Intent(FollovJoinProfileActivity.this, FollovMainActivity.class);
//					startActivity(i);
					Intent testKibumActivity = new Intent(FollovJoinProfileActivity.this, KibumTestActivity.class);
					startActivity(testKibumActivity);
					finish();
					break;
				}
				case FollovCode.PROFILE_ERROR:
				{
					FollovDialog.showDialog("프로필 수정 에러입니다.", FollovJoinProfileActivity.this);
					break;
				}
			}
		}
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.next_btn:
			{
				String year = edit_text_year.getText().toString();
				if(year.equals(""))
				{
					//생일 설정 안했을경우.
					FollovDialog.showDialog("생일을 설정하세요.", FollovJoinProfileActivity.this);
				}
				else
				{
					HttpPostMethod hpm = new HttpPostMethod(handler,"profile.do", FollovJsonParser.profile(FollovPref.getString("email", FollovJoinProfileActivity.this),gc.get(Calendar.YEAR),gc.get(Calendar.MONTH)+1,gc.get(Calendar.DATE),isGirl));
					hpm.start();
				}
				break;
			}
			case R.id.layout_birth:
			{
			    final DatePickerDialog datePicker;
			    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
		    	  @Override
		    	  public void onDateSet(DatePicker view, int year, int monthOfYear,
		    	    int dayOfMonth) {
		    		   gc.set(year, monthOfYear, dayOfMonth);
		    		   edit_text_year.setText(""+gc.get(Calendar.YEAR));
					   edit_text_month.setText(""+(gc.get(Calendar.MONTH)+1));
					   if(gc.get(Calendar.DATE)<10)
					   {
						   edit_text_day.setText("0"+gc.get(Calendar.DATE));
					   }
					   else
					   {
						   edit_text_day.setText(""+gc.get(Calendar.DATE));
					   }
					   
		    	  }
        	    };
        	    datePicker = new DatePickerDialog(FollovJoinProfileActivity.this, listener,gc.get(Calendar.YEAR) , gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
				datePicker.show();
				break;
			}
			case R.id.gender_man:
			{
				if(isGirl)
				{
					img_man.setBackgroundResource(R.drawable.boyaf);
					img_woman.setBackgroundResource(R.drawable.girlbf);
					isGirl= false;
				}
				else
				{
					;
				}
				break;
			}
			case R.id.gender_woman:
			{
				if(isGirl)
				{
					;
				}
				else
				{
					img_man.setBackgroundResource(R.drawable.boybf);
					img_woman.setBackgroundResource(R.drawable.girlaf);
					isGirl = true;
				}
				break;
			}
		}
	}

}
