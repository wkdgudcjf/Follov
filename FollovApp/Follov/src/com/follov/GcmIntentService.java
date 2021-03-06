package com.follov;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.follov.R;
import com.follov.Manager.AppManager;
import com.follov.activity.FollovFriendActivity;
import com.follov.activity.FollovJoinProfileActivity;
import com.follov.activity.FirstActivity;
import com.follov.pref.FollovPref;
import com.follov.sendpicture.*;
import com.follov.sendpicture.SendPictureService.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService
{
	private static final String TAG = GcmIntentService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;
  //  private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() 
    {
        super("GcmIntentService");
    }
    

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {

            }
            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType))
            {

            }
            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
            	
            	FollovApplication app = (FollovApplication) getApplication();
        		
        		
            	String type = extras.getString("type");
            	if(type.equals("location")){
            		
            		FollovLocationService follovService = app.getFollovIntentService();
            		
            		if(follovService != null){
                		FollovLocationService.PRINT_LOG(TAG, "follovIntentService null??? ???????????? ????????????");
            		}else{
            			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
            			return;
            		}
            		
            		
            		String lat = extras.getString("lat");
                	String lng = extras.getString("lng");
                	
                	Message msg = Message.obtain();
            		
            		follovService.locationReceived(lat, lng);
                	
            	} 
            	else if(type.equals("coupleinfo"))
            	{
            		String email = extras.getString("email");
            		String regid = extras.getString("regid");
            		String coupleid = extras.getString("coupleid");
            		FollovPref.saveInt("logincheck", 2, GcmIntentService.this); //??????????????????
            		FollovPref.saveString("coupleemail", email, GcmIntentService.this);
            		FollovPref.saveString("couplegcm", regid, GcmIntentService.this);
            		FollovPref.saveString("coupleid", coupleid, GcmIntentService.this);
            		FollovPref.saveString("couplephone", FollovPref.getString("tempp", GcmIntentService.this), GcmIntentService.this);
            		Log.i("gcm??????", "gcm??????"+FollovPref.getString("tempp", GcmIntentService.this));
            		
            		
            		FollovLocationService.PRINT_LOG("kkkk", "phone : "+FollovPref.getString("tempp", GcmIntentService.this));
            		FollovLocationService.PRINT_LOG("kkkk", "email : "+email);
            		FollovLocationService.PRINT_LOG("kkkk", "regid : "+regid);
            		FollovLocationService.PRINT_LOG("kkkk", "coupleid : "+coupleid);
            		if(isActivityTop())
            		{
            			
            			Log.i("gcm", "??????????????? ??????????");
            			setNotification(GcmIntentService.this,FollovPref.getString("couplephone", GcmIntentService.this)+"?????? ?????? ????????? ?????????????????????.","?????? ????????? ?????????!!");
            			Intent intent2 = new Intent(AppManager.getInstance().getActivity(),FollovJoinProfileActivity.class);
            			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    					startActivity(intent2);
    					AppManager.getInstance().getActivity().finish();
            		}
            		else
            		{
            			setNotification(GcmIntentService.this,FollovPref.getString("couplephone", GcmIntentService.this)+"?????? ?????? ????????? ?????????????????????.","?????? ????????? ?????????!!");           			
            		}
            		FollovPref.saveInt("logincheck", 2, GcmIntentService.this); //??????????????????.
            	}
            	else if(type.equals("coupleprofile"))
            	{
            		int year = Integer.valueOf(extras.getString("year"));
            		int month = Integer.valueOf(extras.getString("month"));
            		int day  = Integer.valueOf(extras.getString("day"));
            		FollovPref.saveInt("coupleyear", year, GcmIntentService.this);
            		FollovPref.saveInt("couplemonth", month, GcmIntentService.this);
            		FollovPref.saveInt("coupleday", day, GcmIntentService.this);
            		Log.i("gcm??????", "gcm??????"+year+"/"+month+"/"+day);
            	}
            	else if(type.equals("logininfo"))
            	{
            		String cregid = extras.getString("regid");
            		String cphone= extras.getString("phone");
            		FollovPref.saveString("couplegcm", cregid,  GcmIntentService.this);
            		FollovPref.saveString("couplephone", cphone,  GcmIntentService.this);
            		Log.i("gcm??????", "gcm??????"+cphone+"/"+cregid);
            	}
//            	else if(type.equals("photo_taken")){
//            		
//            		FollovLocationService follovService = app.getFollovIntentService();
//            		
//            		if(follovService != null){
//                		FollovLocationService.PRINT_LOG(TAG, "follovIntentService null??? ???????????? ????????????");
//            		}else{
//            			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
//            			return;
//            		}
//            		
//            		
//            		String photoUri = extras.getString("photoUri");
//            		
//            		follovService.sendPhotoTakenLocationInfoToLoverGCM(photoUri);
//            		
//            	} else if(type.equals("photo_taken_location_info")){
//            		
//            		FollovLocationService follovService = app.getFollovIntentService();
//            		
//            		if(follovService != null){
//                		FollovLocationService.PRINT_LOG(TAG, "follovIntentService null??? ???????????? ????????????");
//            		}else{
//            			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
//            			return;
//            		}
//            		
//            		
//            		//Uri photoUri = follovService.getPhotoUri();
//            		
//            		Uri photoUri = Uri.parse(extras.getString("photoUri"));
//            		
//            		if(photoUri == null) return;
//            		
//            		int date_code = Integer.valueOf(extras.getString("date_code"));
//            		int loc_no = Integer.valueOf(extras.getString("loc_no"));
//            		
//            		//new HttpSendThread(date_code, loc_no, photoUri, follovService).start();
//            		
//            	
//            	} else if(type.equals("photo_save_failed")){
//            		
//            		FollovLocationService follovService = app.getFollovIntentService();
//            		
//            		if(follovService != null){
//                		FollovLocationService.PRINT_LOG(TAG, "follovIntentService null??? ???????????? ????????????");
//            		}else{
//            			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
//            			return;
//            		}
//            		
//            		
//            		int date_code = Integer.valueOf(extras.getString("date_code"));
//            		int loc_no = Integer.valueOf(extras.getString("loc_no"));
//            		
//            		follovService.updateLocationNotSpecialPoint(date_code, loc_no);
//            		
//            	} else if(type.equals("battery")){//battery_level check
//            	
//            		
//            		FollovLocationService follovService = app.getFollovIntentService();
//            		
//            		if(follovService != null){
//                		FollovLocationService.PRINT_LOG(TAG, "follovIntentService null??? ???????????? ????????????");
//            		}else{
//            			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
//            			return;
//            		}
//            		
//            	
//            		String level = extras.getString("battery_level");
//    				follovService.setLoversBatteryLevel(Integer.parseInt(level));
//            		
//            	}
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private boolean isActivityTop()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
         List<RunningTaskInfo> info;
         info = activityManager.getRunningTasks(1);
         if(info.get(0).topActivity.getClassName().equals(FollovFriendActivity.class.getName()))
         {
              return true;
         }
         else
         {
              return false;
         }
    }
    private void setNotification(Context context, String title, String message) {
		NotificationManager notificationManager = null;
		Notification notification = null;
		try {
			notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notification = new Notification(R.drawable.ic_launcher,
					title, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.vibrate = new long[] {400,1500};
			notification.sound = Uri.parse("android.resource://com.follov/"+R.raw.ohyear);
			notification.number++;
		
			Intent intent = new Intent(context, FirstActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, message, title, pi);
			notificationManager.notify(0, notification);
		} catch (Exception e) {
			Log.d("kk", "[setNotification] Exception : " + e.getMessage());
		}
	}
}
//
//import android.app.Activity;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.util.Log;
//
//import com.follov.R;
//import com.follov.FollovGCM.FollovGCM;
//import com.follov.Manager.AppManager;
//import com.follov.activity.FristActivity;
//import com.follov.pref.FollovPref;
//import com.google.android.gcm.GCMBaseIntentService;
//
//public class GCMIntentService extends GCMBaseIntentService {
//	private static final String PROJECT_ID = "61576960038";
//
//	// ?????? api ????????? ?????? [https://code.google.com/apis/console/#project:??? ??????]
//	// #project: ????????? ????????? ?????? PROJECT_ID ?????? ????????????
//	// public ?????? ???????????? ????????? ???????????? ??????.
//
//	public GCMIntentService()
//	{
//		this(PROJECT_ID);
//	}
//
//	public GCMIntentService(String project_id) {
//		super(project_id);
//	}
//
//	/** ????????? ?????? ????????? */
//	@Override
//	protected void onMessage(Context context, Intent intent) {
//		Log.i("bundle", "????????????????????????");
//		String title = intent.getStringExtra("title");
//		String message = intent.getStringExtra("msg");
//		setNotification(context, title, message);
//	}
//	/** ???????????? GCM ????????? ?????? ?????? ??? ?????? id??? ????????? */
//	@Override
//	protected void onRegistered(Context context, String regId) {
//		FollovPref.saveString("mygcm", regId, context);
//		AppManager.getInstance().getUser().setMyGcm(regId);
//		Log.i("gcm", regId);
//		//FollovGCM.gcmregister(context, regId);
//	}
//
//	/** ???????????? GCM ????????? ?????? ????????? ?????? ????????? ?????? id??? ????????? */
//	@Override
//	protected void onUnregistered(Context context, String regId)
//	{
//		Log.d("kk", regId + "??? ????????????.");
//	}
//
//	@Override
//	protected void onError(Context arg0, String arg1)
//	{
//		Log.d("kk", arg1 + "??? ?????????");
//	}
//
//	private void setNotification(Context context, String title, String message) {
//		NotificationManager notificationManager = null;
//		Notification notification = null;
//		try {
//			notificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			notification = new Notification(R.drawable.ic_launcher,
//					title, System.currentTimeMillis());
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
//			notification.vibrate = new long[] {400,1500};
//			notification.sound = Uri.parse("android.resource://com.example.withschedule/"+R.raw.ohyear);
//			notification.number++;
//		
//			Intent intent = new Intent(context, FristActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
//			notification.setLatestEventInfo(context, message, title, pi);
//			notificationManager.notify(0, notification);
//		} catch (Exception e) {
//			Log.d("kk", "[setNotification] Exception : " + e.getMessage());
//		}
//	}
//}
