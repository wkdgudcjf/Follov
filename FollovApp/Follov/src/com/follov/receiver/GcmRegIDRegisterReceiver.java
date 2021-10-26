package com.follov.receiver;

import android.content.*;
import android.util.*;

import com.follov.Manager.*;

public class GcmRegIDRegisterReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String regId = intent.getExtras().getString("registration_id");
		
		if(regId != null && !regId.equals("")){
			
			AppManager appManager = AppManager.getInstance();
			
			if(appManager != null){
				Log.i("GcmRegIDRegisterReceiver", "regid receive");
				appManager.getFollovService().storeRegistrationId(context, regId);
			}
		}else{
			Log.i("GcmRegIDRegisterReceiver", "sdlkfjsdflkj");
		}
		
	}
	
	
	

}
