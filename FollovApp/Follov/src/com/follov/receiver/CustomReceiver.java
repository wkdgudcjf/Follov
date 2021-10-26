package com.follov.receiver;

import android.content.*;
import android.util.*;

import com.follov.*;

public class CustomReceiver extends BroadcastReceiver{

	public static final String TAG = "kimkibum";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "Follov�� ���۵˴ϴ�~~");

		//���ý���, �۱�½��� ��Ƽ� ���� ����.
		//if("com.example.boottest".equals(intent.getAction())){

		ComponentName comp = new ComponentName(context.getPackageName(), FollovLocationService.class.getName());

		ComponentName service = context.startService(new Intent().setComponent(comp));

		if(null == service){
			Log.e(TAG, "Service�� ã���� ����ϴ� : "+comp.toString());
		}

		//} else{
		//	Log.e(TAG, "Received unexpected intent"+intent.toString());
		//}

	}

}
