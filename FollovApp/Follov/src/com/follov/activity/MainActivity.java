
package com.follov.activity;

import com.follov.R;
import com.follov.page.MainHomeFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;



public class MainActivity extends FollovMainActivity implements OnClickListener {
	

	private Context mContext;
    private static MainActivity mInstance;
   
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.main);
         mContext = this;
         mInstance = this;
         setUi();
    }
    public void setUi()
    {
    	
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public static MainActivity getInstance() {
        return mInstance;
    }
    @Override
	public void onBackPressed() 
    {
		// TODO Auto-generated method stub
		super.onBackPressed();
    	Log.d("sf",getCurFragment().getClass().getName());
    	
		System.exit(0);
	}
	

	public void replaceFragment(Class<?> clss, Bundle bundle, boolean isAddStack)
	{
        Fragment fragment = Fragment.instantiate(this, clss.getName(), bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        if ( isAddStack ) {
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
        MainActivity.getInstance().showContent();
    }

    public Fragment getCurFragment()
    {
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        return frg;
    }
    public  void goHomeFragment(Context context) 
    {
        if ( context instanceof MainActivity )
        {
            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment frg = fm.findFragmentById(R.id.content_frame);
            if ( frg.getClass() != MainHomeFragment.class )
            {
                replaceFragment(MainHomeFragment.class, null, false);
            }
        }
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


 
}
