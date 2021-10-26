package com.follov.main;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.follov.R;

public class FollowJoinProfileActivity extends Activity implements OnClickListener {
	
	LinearLayout m_oBtnNext;
	LinearLayout m_oBtnYearTop;
	LinearLayout m_oBtnYearBottom;
	LinearLayout m_oBtnMonthTop;
	LinearLayout m_oBtnMonthBottom;
	LinearLayout m_oBtnDayTop;
	LinearLayout m_oBtnDayBottom;
	LinearLayout m_oBtnMan;
	LinearLayout m_oBtnWoman;
	ImageView img_man;
	ImageView img_woman;
	boolean checkGender = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_profile);
		setUi();
	}

	public void setUi()
	{
		m_oBtnNext = (LinearLayout)findViewById(R.id.next_btn);
		m_oBtnYearTop = (LinearLayout)findViewById(R.id.year_top);
		m_oBtnYearBottom = (LinearLayout)findViewById(R.id.year_bottom);
		m_oBtnMonthTop = (LinearLayout)findViewById(R.id.month_top);
		m_oBtnMonthBottom = (LinearLayout)findViewById(R.id.month_bottom);
		m_oBtnDayTop = (LinearLayout)findViewById(R.id.day_top);
		m_oBtnDayBottom = (LinearLayout)findViewById(R.id.day_bottom);
		m_oBtnMan = (LinearLayout)findViewById(R.id.gender_man);
		m_oBtnWoman = (LinearLayout)findViewById(R.id.gender_woman);
		
		m_oBtnNext.setOnClickListener(this);
		m_oBtnYearTop.setOnClickListener(this);
		m_oBtnYearBottom.setOnClickListener(this);
		m_oBtnMonthTop.setOnClickListener(this);
		m_oBtnMonthBottom.setOnClickListener(this);
		m_oBtnDayTop.setOnClickListener(this);
		m_oBtnDayBottom.setOnClickListener(this);
		m_oBtnMan.setOnClickListener(this);
		m_oBtnWoman.setOnClickListener(this);
		
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.next_btn:
			{
				
				break;
			}
			case R.id.year_top:
			{
				
				break;
			}
			case R.id.year_bottom:
			{
				
				break;
			}
			case R.id.month_top:
			{
				
				break;
			}
			case R.id.month_bottom:
			{
				
				break;
			}
			case R.id.day_top:
			{
				
				break;
			}
			case R.id.day_bottom:
			{
				
				break;
			}
			case R.id.gender_man :
			{
				if(checkGender)
				{
					img_man.setBackgroundResource(R.drawable.boyaf);
					img_woman.setBackgroundResource(R.drawable.girlbf);
					checkGender= false;
				}
				else
				{
					
				}
				break;
			}
			case R.id.gender_woman :
			{
				if(checkGender)
				{
					//checkGender= false;
				}
				else
				{
					img_man.setBackgroundResource(R.drawable.boybf);
					img_woman.setBackgroundResource(R.drawable.girlaf);
					checkGender = true;
				}
				break;
			}
		}
	}

}
