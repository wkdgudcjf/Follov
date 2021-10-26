package com.follov.main;

import com.follov.R;
import com.follov.component.LoadingPopup;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class FristActivity extends Activity implements OnClickListener {
	 LoadingPopup loading ;
	 Handler myHandler = new Handler();
	 Runnable myRunnable = new Runnable() {
	
	  @Override
	  public void run() {
	 
	   myHandler.postDelayed(myRunnable, 2500);
	   
	 
	  }
	 };
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		startProgressDialog() ;
		  Intent i = new Intent(FristActivity.this, JoinActivity.class);
		  startActivity(i);
		  stopProgressDialog();
		   finish();
		// myHandler.postDelayed(myRunnable, 2500);
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
