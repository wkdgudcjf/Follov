package com.follov;

import com.follov.activity.KibumTestActivity;

import android.content.*;
import android.util.*;

public class BootReceiver extends BroadcastReceiver{

	public static final String TAG = "kimkibum";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Boot Receiver 시작 follov");
		
		context.startService(new Intent(context, FollovLocationService.class));
		
		
//		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//			
//			ComponentName comp = new ComponentName(context.getPackageName(), FollovLocationService.class.getName());
//			
//			ComponentName service = context.startService(new Intent().setComponent(comp));
//			
//			if(null == service){
//				Log.e(TAG, "Service를 찾을수 없습니다 : "+comp.toString());
//			}
//			
//		} else{
//			Log.e(TAG, "Received unexpected intent"+intent.toString());
//		}
		
	}
	

}
