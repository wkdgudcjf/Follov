package com.follov.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.follov.R;

public class FollowFriendActivity extends Activity implements OnClickListener {
	
	RelativeLayout m_oBtnSignin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_friend);
		setUi();
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
		m_oBtnSignin = (RelativeLayout)findViewById(R.id.check_friend);
		m_oBtnSignin.setOnClickListener(this);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.check_friend:
			{
				Intent i = new Intent(FollowFriendActivity.this, FollowJoinProfileActivity.class);
				startActivity(i);
				finish();
				break;
			}
		}
	}

}
