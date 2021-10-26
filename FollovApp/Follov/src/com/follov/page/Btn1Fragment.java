package com.follov.page;

import com.follov.R;
import com.follov.activity.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;


public class Btn1Fragment extends Fragment implements OnClickListener{
    private FragmentActivity mContext;
    private LinearLayout m_oLeftListUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	setUi();
        return inflater.inflate(R.layout.main_home, container, false);
        
    }
    public void setUi()
    {
    	m_oLeftListUp = (LinearLayout)getActivity().findViewById(R.id.list);
    	m_oLeftListUp.setOnClickListener(this);
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
        int k = v.getId();
        if(k== R.id.list)
	    {
        	MainActivity.getInstance().showMenu();
	    }
    }
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
		System.gc();
    }
  
    
}
