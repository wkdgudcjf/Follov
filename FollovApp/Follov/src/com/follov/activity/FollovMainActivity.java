
package com.follov.activity;

import android.os.*;
import android.support.v4.app.*;
import android.widget.*;

import com.follov.R;
import com.follov.Manager.*;
import com.follov.page.*;
import com.nostra13.universalimageloader.core.*;
import com.slidingmenu.lib.*;

public class FollovMainActivity extends ListSlidingActivity {

    private Fragment mainFg;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Above View
        if ( savedInstanceState != null )
            mainFg = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if ( mainFg == null )
            mainFg = Fragment.instantiate(getBaseContext(), MainHomeFragment.class.getName(), null);

        // set the Above View
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFg).commit();

        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftMenuFragment()).commit();

        // customize the SlidingMenu
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        getSlidingMenu().setFadeDegree(1.0f);
      //  ImageLoader.getInstance().displayImage("", new ImageView(FollovMainActivity.this), AppManager.getInstance().getOptions());
    }
    
   
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void switchContent(Fragment fragment) {
//        mainFg = fragment;
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
        getSlidingMenu().showContent();
    }
}
