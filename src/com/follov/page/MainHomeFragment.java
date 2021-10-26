package com.follov.page;

import com.follov.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainHomeFragment extends Fragment implements OnClickListener{
    private FragmentActivity mContext;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_home, container, false);
        
    }
    @Override
	public void onResume() {
        super.onResume();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        clearUi();

    }
    public void clearUi()
    {
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
 
    @Override
    public synchronized void onClick(View v) {
        switch (v.getId()) {
       
        }
    }
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
		System.gc();
    }
  
    
}
