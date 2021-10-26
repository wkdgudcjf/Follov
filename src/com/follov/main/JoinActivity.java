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

import com.follov.R;

public class JoinActivity extends Activity implements  View.OnClickListener {
	
	LinearLayout m_oBtnSignin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		setUi();
	}

	public void setUi()
	{
		m_oBtnSignin = (LinearLayout)findViewById(R.id.btn_sign);
		m_oBtnSignin.setOnClickListener(this);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btn_sign:
			{
				Intent i = new Intent(JoinActivity.this, FollowFriendActivity.class);
				startActivity(i);
				finish();

				break;
			}
		}
	}

}
