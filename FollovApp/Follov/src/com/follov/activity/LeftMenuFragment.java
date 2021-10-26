package com.follov.activity;

import com.follov.R;
import com.follov.page.Btn1Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class LeftMenuFragment extends Fragment implements View.OnClickListener {
    private static Activity mActivity;
    private static LeftMenuFragment mInstance;
    private LinearLayout m_oBtn_home;
    public static LeftMenuFragment getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new LeftMenuFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
    	
        return inflater.inflate(R.layout.home_left_menu, container,
                false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();
        mInstance = this;
        setUi();
    }
    public void setUi()
    {
    	m_oBtn_home=(LinearLayout)getActivity().findViewById(R.id.btn_home);
    	m_oBtn_home.setOnClickListener(this);
    }

    // the meat of switching the above fragment
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof FollovMainActivity) {
            FollovMainActivity fca = (FollovMainActivity) getActivity();
            fca.switchContent(fragment);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    @Override
    public void onClick(View v) {
    	switch(v.getId())
    	{
	    	case R.id.btn_home : 
	    	{
	    		MainActivity.getInstance().replaceFragment(Btn1Fragment.class, null, false);
	    		break;
	    	}
    	
    	}
    }

}
