package com.follov;

import android.content.*;
import android.support.v4.content.*;
import android.util.*;

import com.follov.Manager.*;
import com.follov.activity.*;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{
	private static String TAG = GcmBroadcastReceiver.class.getSimpleName();
	 @Override
	    public void onReceive(Context context, Intent intent) {
		 if("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())){
				String regId = intent.getExtras().getString("registration_id");
				
				if(regId != null && !regId.equals("")){
					
					AppManager appManager = AppManager.getInstance();
					
					if(appManager != null){
						Log.i("GcmRegIDRegisterReceiver", "regid receiver불리나");
						//appManager.getFollovService().storeRegistrationId(context, regId);
					}
				}else{
					Log.i("GcmRegIDRegisterReceiver", "이거불림");
				}
			}else{
				//MainActivity.PRINT_LOG(TAG, "GCM Broadcast received");
				ComponentName comp = new ComponentName(context.getPackageName(),
						GcmIntentService.class.getName());
				startWakefulService(context, (intent.setComponent(comp)));
			}
	    }
}
 