package com.follov.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.component.*;
import com.follov.pref.*;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;

public class FirstActivity extends Activity implements OnClickListener {
	 LoadingPopup loading ;
	 Handler myHandler = new Handler();
	 Runnable myRunnable = new Runnable() {
	
	  @Override
	  public void run()
	  {
	      myHandler.postDelayed(myRunnable, 2500);
	  }
	 };
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		Log.e("network","network init : "+cm.getNetworkPreference());
		
		
		setContentView(R.layout.first);
		ImageLoaderConfiguration config = AppManager.getInstance().getConfig();
		config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .threadPoolSize(3) // default
        .threadPriority(Thread.NORM_PRIORITY - 1) // default
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        .denyCacheImageMultipleSizesInMemory()
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        .writeDebugLogs()
        .build();
		ImageLoader.getInstance().init(config);
		
		DisplayImageOptions options = AppManager.getInstance().getOptions();
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.loading) // resource or drawable
        .showImageForEmptyUri(R.drawable.empty) // resource or drawable
        .showImageOnFail(R.drawable.fail) // resource or drawable
        .cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		AppManager.getInstance().setActivity(this);
		if(!AppManager.getInstance().isPossibleInternet())
		{
			FollovDialog.showDialog("인터넷 접속 상태를 확인해 주세요.", FirstActivity.this);
			System.exit(0);
		}
		else
		{
			startProgressDialog();
			switch(FollovPref.getInt("logincheck", FirstActivity.this))
			{
				case 0:
				{
					Intent i = new Intent(FirstActivity.this, LoginActivity.class);
					startActivity(i);
					stopProgressDialog();
					finish();
					break;
				}
				case 1:
				{
					AppManager.getInstance().getUser().setEmail(FollovPref.getString("email", FirstActivity.this));
					AppManager.getInstance().getUser().setPassword(FollovPref.getString("pw", FirstActivity.this));
					AppManager.getInstance().getUser().setMyGcm(FollovPref.getString("mygcm", FirstActivity.this));
					//이메일 패스워드세팅해주기.
					Intent i = new Intent(FirstActivity.this, FollovFriendActivity.class);
					startActivity(i);
					stopProgressDialog();
					finish();
					break;
				}
				case 2:
				{
					AppManager.getInstance().getUser().setEmail(FollovPref.getString("email", FirstActivity.this));
					AppManager.getInstance().getUser().setPassword(FollovPref.getString("pw", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleemail(FollovPref.getString("coupleemail", FirstActivity.this));
					AppManager.getInstance().getUser().setCouplePhone(FollovPref.getString("couplephone", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleGcm(FollovPref.getString("couplegcm", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleid(FollovPref.getString("coupleid", FirstActivity.this));
					AppManager.getInstance().getUser().setMyGcm(FollovPref.getString("mygcm", FirstActivity.this));
					//이메일 패스워드 커플이메일 커플번호 세팅 , gcm
					Intent i = new Intent(FirstActivity.this, FollovJoinProfileActivity.class);
					startActivity(i);
					stopProgressDialog();
					finish();
					break;
				}
				case 3:
				{
					AppManager.getInstance().getUser().setEmail(FollovPref.getString("email", FirstActivity.this));
					AppManager.getInstance().getUser().setPassword(FollovPref.getString("pw", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleemail(FollovPref.getString("coupleemail", FirstActivity.this));
					AppManager.getInstance().getUser().setCouplePhone(FollovPref.getString("couplephone", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleGcm(FollovPref.getString("couplegcm", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleid(FollovPref.getString("coupleid", FirstActivity.this));
					AppManager.getInstance().getUser().setMyGcm(FollovPref.getString("mygcm", FirstActivity.this));
					
					AppManager.getInstance().getUser().setMyyear(FollovPref.getInt("myyear", FirstActivity.this));
					AppManager.getInstance().getUser().setMymonth(FollovPref.getInt("mymonth", FirstActivity.this));
					AppManager.getInstance().getUser().setMyday(FollovPref.getInt("myday", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleyear(FollovPref.getInt("coupleyear", FirstActivity.this));
					AppManager.getInstance().getUser().setCouplemonth(FollovPref.getInt("couplemonth", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleday(FollovPref.getInt("coupleday", FirstActivity.this));
					
					AppManager.getInstance().getUser().setSex(FollovPref.getBoolean("gender", FirstActivity.this));
					//이메일 패스워드 커플이메일 커플번호 커플생일 내생일 내성별 세팅. gcm
					//Intent i = new Intent(FristActivity.this, FollovJoinMapActivity.class);//사생활 영역 등록 액티비티
					//Intent i = new Intent(FristActivity.this, )
					//startActivity(i);
					
					//test
					Intent i  = new Intent(FirstActivity.this, KibumTestActivity.class);
					startActivity(i);
					//
					
					stopProgressDialog();
					finish();
					break;
				}
				case 4: // 사생활 영역 정보는 어디서 어떻게 가져오는지 개발되면 추가
				{
					AppManager.getInstance().getUser().setEmail(FollovPref.getString("email", FirstActivity.this));
					AppManager.getInstance().getUser().setPassword(FollovPref.getString("pw", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleemail(FollovPref.getString("coupleemail", FirstActivity.this));
					AppManager.getInstance().getUser().setCouplePhone(FollovPref.getString("couplephone", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleGcm(FollovPref.getString("couplegcm", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleid(FollovPref.getString("coupleid", FirstActivity.this));
					AppManager.getInstance().getUser().setMyGcm(FollovPref.getString("mygcm", FirstActivity.this));
					
					AppManager.getInstance().getUser().setMyyear(FollovPref.getInt("myyear", FirstActivity.this));
					AppManager.getInstance().getUser().setMymonth(FollovPref.getInt("mymonth", FirstActivity.this));
					AppManager.getInstance().getUser().setMyday(FollovPref.getInt("myday", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleyear(FollovPref.getInt("coupleyear", FirstActivity.this));
					AppManager.getInstance().getUser().setCouplemonth(FollovPref.getInt("couplemonth", FirstActivity.this));
					AppManager.getInstance().getUser().setCoupleday(FollovPref.getInt("coupleday", FirstActivity.this));
					
					AppManager.getInstance().getUser().setSex(FollovPref.getBoolean("gender", FirstActivity.this));
					
					//사생활 영역 도 저장 해야함.
					
					Intent i = new Intent(FirstActivity.this, FollovMainActivity.class);//메인 액티비티
					startActivity(i);
					stopProgressDialog();
					finish();
					break;
				}
			}
		}
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
	public void startProgressDialog() 
	{
		if( loading == null )
		{
			loading = new LoadingPopup(this);
			loading.start();
		}
	}
	
	public void stopProgressDialog() 
	{
		if( loading != null )
		{
			loading.stop();
			loading = null;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
