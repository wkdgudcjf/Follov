package com.follov.activity;

import java.util.*;

import org.json.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.util.Log;
import android.view.*;

import com.follov.*;
import com.follov.Manager.*;
import com.follov.activityrecognition.*;
import com.follov.db.*;
import com.follov.db.vo.*;
import com.follov.network.*;
import com.follov.pref.*;
import com.follov.sendpicture.HttpSendThread;
import com.google.android.gms.maps.model.*;

@SuppressLint("UseSparseArrays")
public class KibumTestActivity extends Activity
{

	public static final String TAG = KibumTestActivity.class.getSimpleName();

	ConnectivityManager cm;
	WifiManager wifiManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		startFollovService();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		// cm.setNetworkPreference(1);
	}

	public void startFollovService()
	{
		Intent serviceStart = new Intent(KibumTestActivity.this,
				FollovLocationService.class);
		startService(serviceStart);
	}

	public final Handler mHandler = new Handler()
	{

		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{

			case FollovCode.GET_ALL_DATA_FROM_SERVER_SUCCESS:

				FollovLocationService.PRINT_LOG(TAG,
						"GET_ALL_DATA_FROM_SERVER_SUCCESS");

				insertAllDataToDB((String) msg.obj);
				showMaps();
				break;

			case FollovCode.NO_MODIFIED_DATA_FROM_SERVER:

				FollovLocationService.PRINT_LOG(TAG,
						"NO_MODIFIED_DATA_FROM_SERVER");
				sendNotUploadedDateInfoToServerAfterModify((String) msg.obj);

				break;

			case FollovCode.GET_MODIFIED_DATA_FROM_SERVER_SUCCESS:

				FollovLocationService.PRINT_LOG(TAG,
						"GET_MODIFIED_DATA_FROM_SERVER_SUCCESS");

				sendNotUploadedDateInfoToServerAfterModify((String) msg.obj);

				break;

			case FollovCode.MERGED_DATA_RECEIVED:

				RecentDataResponse mergedData = (RecentDataResponse) msg.obj;

				if (mergedData != null)
				{
					// test
					ArrayList<MergedLoc_Info> mergeList = mergedData
							.getMerged_list();
					ArrayList<Integer> uploadList = mergedData
							.getUpload_complete_list();

					FollovLocationService.PRINT_LOG(TAG, "Merged Info");
					for (MergedLoc_Info vo : mergeList)
					{

						FollovLocationService.PRINT_LOG(TAG,
								"date_code: " + vo.getDate_code());
						ArrayList<Loc_route_tb_VO> loc_route_list = vo
								.getLoc_route_list();
						ArrayList<Loc_photo_tb_VO> loc_photo_list = vo
								.getLoc_photo_list();

						for (Loc_route_tb_VO route : loc_route_list)
						{
							FollovLocationService.PRINT_LOG(TAG,
									route.toString());
						}
						for (Loc_photo_tb_VO photo : loc_photo_list)
						{
							FollovLocationService.PRINT_LOG(TAG,
									photo.toString());
						}

					}
					FollovLocationService.PRINT_LOG(TAG, "uploadList : ");
					for (Integer num : uploadList)
					{
						FollovLocationService.PRINT_LOG(TAG, "date_code : "
								+ num);
					}
					// test

					checkDateUploaded(mergedData.getUpload_complete_list());
					modifyDateInfoFromMergedData(mergedData.getMerged_list());
				} else
					FollovLocationService.PRINT_LOG(TAG,
							"merged data received : null");

				sendUnUploadedImg();
				//showMaps();
				break;

			case FollovCode.RECENT_SERVER_DATE_INFO_RECEIVED:

				// sendNotUploadedDateInfoToServer((String)msg.obj);

				break;
			case FollovCode.SERVER_DB_SYNCHRONIZE_TO_CLIENT_SUCCESS:

				FollovLocationService.PRINT_LOG(TAG,
						"SERVER_DB_SYNCHRONIZE_TO_CLIENT_SUCCESS");

				// synchronizeClientWithServer((String)msg.obj);

				// showMaps();
				break;
			case FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_SUCCESS:
				FollovLocationService.PRINT_LOG(TAG,
						"CLIENT_DB_SYNCHRONIZE_TO_SERVER_SUCCESS");
				showMaps();
				break;
			case FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_FAILED:
				FollovLocationService.PRINT_LOG(TAG,
						"CLIENT_DB_SYNCHRONIZE_TO_SERVER_FAILED");
				showMaps();
				break;
			case FollovCode.SHOW_MAPS:
				showMaps();
				break;
			}

		}

	};

	public void sendUnUploadedImg()
	{

		DBPool db = DBPool.getInstance(this);

		LinkedList<String> unUploadedImgList = 
				db.selectUnUploadedImg(FollovPref.getString("email", this));
		
		if(unUploadedImgList == null || unUploadedImgList.size() == 0){
			FollovLocationService.PRINT_LOG(TAG, "unUploadedImgList 없음");
			showMaps();
			return;
		}else{
			
			for(String name : unUploadedImgList){
				FollovLocationService.PRINT_LOG(TAG, "안올린 이미지 : "+name);
			}
			
		}
		
		
		HttpSendThread sendThread = new HttpSendThread(unUploadedImgList, ((FollovApplication) getApplication()).getFollovIntentService(), db, mHandler);
		sendThread.start();
		
		try
		{
			Thread.sleep(500);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sendThread.sendImgHandler.sendEmptyMessage(0);

	}

	private void checkDateUploaded(ArrayList<Integer> upload_complete_list)
	{

		DBPool db = DBPool.getInstance(this);

		for (Integer date_code : upload_complete_list)
		{
			db.openDatabase();
			try
			{
				db.checkDateIsUploaded(date_code);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

	}

	private void modifyDateInfoFromMergedData(
			ArrayList<MergedLoc_Info> merged_list)
	{

		DBPool db = DBPool.getInstance(this);
		JSONArray merged_complete = db
				.modifyDateInfoFromMergedData(merged_list);
		
		if(merged_complete == null || merged_complete.length() == 0) return;
		
		JSONObject json = new JSONObject();
		try
		{
			json.put("code", FollovCode.MERGED_COMPLETE);
			json.put("couple_id", FollovPref.getString("coupleid", this));
			json.put("pw", FollovPref.getString("pw", this));
			json.put("email", FollovPref.getString("email", this));
			json.put("merged_complete", merged_complete);
			new HttpPostMethod(mHandler, "SynchronizeAction.do",
					json.toString()).start();

		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// json.put("photo_no", photo_no);


	}

	private void selectLoc_routesAndPhotos(
			ArrayList<Date_tb_client_VO> newDate, DBPool db,
			UnUploadedClientData unUnloadedData)
	{

		if (newDate == null)
			return;

		HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_route_map = new HashMap<Integer, ArrayList<Loc_route_tb_VO>>();
		HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photo_map = new HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>>();

		for (int i = 0; i < newDate.size(); i++)
		{

			Date_tb_client_VO vo = newDate.get(i);
			int date_code = vo.getDate_code();

			ArrayList<Loc_route_tb_VO> loc_routes_list = db
					.selectLoc_routes(date_code);

			HashMap<Integer, ArrayList<Loc_photo_tb_VO>> temp_loc_photo_map = new HashMap<Integer, ArrayList<Loc_photo_tb_VO>>();
			if (loc_routes_list != null)
			{

				loc_route_map.put(date_code, loc_routes_list);

				for (Loc_route_tb_VO loc_route : loc_routes_list)
				{

					int loc_no = loc_route.getLoc_no();
					ArrayList<Loc_photo_tb_VO> loc_photos = db
							.selectLoc_photos(date_code, loc_no);
					

					if (loc_photos != null)
					{
						temp_loc_photo_map.put(loc_no, loc_photos);

					}

				}

			}

			loc_photo_map.put(date_code, temp_loc_photo_map);
		}

		unUnloadedData.setLoc_routes(loc_route_map);
		unUnloadedData.setLoc_photos(loc_photo_map);

	}

	private void insertAllDataToDB(String jsonString)
	{

		JSONTokener jtk = new JSONTokener(jsonString);
		JSONObject json = null;

		try
		{
			json = (JSONObject) jtk.nextValue();

			if (!insertDates(json))
				return;

			insertLoc_routes(json);
			insertLoc_photos(json);

		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean insertDates(JSONObject json)
	{

		DBPool db = DBPool.getInstance(this);

		try
		{
			JSONArray date = json.getJSONArray("date");

			if (date == null)
				return false;

			for (int i = 0; i < date.length(); i++)
			{

				JSONObject record = date.getJSONObject(i);

				db.insertDate(
						new Date_tb_client_VO(record.getInt("date_code"),
								record.getInt("year"), record.getInt("month"),
								record.getInt("day"), record
										.getString("day_of_week"), record
										.getString("start_time"), record
										.getString("end_time")), "y");

			}
			return true;

		} catch (JSONException e)
		{
			FollovLocationService.MyStackTrace(e);
			return false;
		}
	}

	private void insertLoc_routes(JSONObject json)
	{
		DBPool db = DBPool.getInstance(this);
		try
		{
			JSONArray loc_route = json.getJSONArray("loc_route");

			if (loc_route == null)
				return;

			for (int i = 0; i < loc_route.length(); i++)
			{

				JSONObject record = loc_route.getJSONObject(i);

				db.insertLocationRoute(new Loc_route_tb_VO(record
						.getInt("date_code"), record.getInt("loc_no"), record
						.getDouble("latitude"), record.getDouble("longitude"),
						record.getString("isSpecial"), record
								.getString("loc_time")));

			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void insertLoc_photos(JSONObject json)
	{

		DBPool db = DBPool.getInstance(this);

		try
		{
			JSONArray loc_photo = json.getJSONArray("loc_photo");

			if (loc_photo == null)
				return;

			for (int i = 0; i < loc_photo.length(); i++)
			{

				JSONObject record = loc_photo.getJSONObject(i);

				db.insertLocPhoto(record.getInt("date_code"),
						record.getInt("loc_no"), record.getString("name"),
						record.getString("date"),
						record.getString("photo_taken_email"),
						record.getString("is_img_uploaded"));

			}

		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private void sendNotUploadedDateInfoToServer(String jsonString){
	//
	// JSONTokener jtk = new JSONTokener(jsonString);
	// JSONObject json = null;
	//
	// try{
	//
	// json = (JSONObject)jtk.nextValue();
	//
	// DBPool db = DBPool.getInstance(this);
	// int server_date_code = json.getInt("server_recent_date_code");
	//
	// FollovLocationService.PRINT_LOG(TAG,
	// "server_date_code : "+server_date_code);
	//
	// JSONObject result = new JSONObject();
	//
	// ArrayList<Date_tb_client_VO> newDate = db.getNewDates(server_date_code);
	// JSONArray newLocRoute = new JSONArray();
	// JSONArray newLocPhoto = new JSONArray();
	//
	// if(newDate != null && newDate.size() != 0){
	//
	// selectLoc_routesAndPhotos(newDate, newLocRoute, newLocPhoto, db);
	//
	// }
	//
	// result.put("date", newDate);
	// result.put("loc_route", newLocRoute);
	// result.put("loc_photo", newLocPhoto);
	//
	// result.put("code", FollovCode.SEND_DATE_INFO_TO_SERVER_REQUEST);
	// result.put("couple_id", FollovPref.getString("coupleid", this));
	// result.put("email", FollovPref.getString("email", this));
	// result.put("pw", FollovPref.getString("pw", this));
	//
	//
	// if(newLocRoute.length() != 0){
	// new HttpPostMethod(mHandler,"SynchronizeAction.do", result.toString(),cm,
	// wifiManager).start();
	// //return true;
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//
	// }

	private ArrayList<Integer> modifyData(JSONObject json)
	{

		DBPool db = DBPool.getInstance(this);

		return db.modifyData(json);
	}
	
	private ArrayList<Integer> getDataAfterMerge(JSONObject json)
	{

		DBPool db = DBPool.getInstance(this);

		return db.getDataAfterMerge(json);
	}

	private void sendNotUploadedDateInfoToServerAfterModify(String jsonString)
	{

		JSONTokener jtk = new JSONTokener(jsonString);
		JSONObject json = null;
		JSONObject result = new JSONObject();

		try
		{

			json = (JSONObject) jtk.nextValue();

			UnUploadedClientData unUploadedData = new UnUploadedClientData();

			ArrayList<Integer> modified_complete = modifyData(json);
			ArrayList<Integer> get_data_after_merge = getDataAfterMerge(json);

			unUploadedData.setModified_complete(modified_complete);
			unUploadedData.setGet_data_after_merge(get_data_after_merge);

			DBPool db = DBPool.getInstance(this);
			int server_date_code = json.getInt("server_recent_date_code");

			FollovLocationService.PRINT_LOG(TAG, "server_date_code : "
					+ server_date_code);

			ArrayList<Date_tb_client_VO> newDate = db
					.getNewDates(server_date_code);
			unUploadedData.setDates(newDate);
			unUploadedData.setCouple_id(FollovPref.getString("coupleid", this));
			unUploadedData.setEmail(FollovPref.getString("email", this));
			unUploadedData.setPw(FollovPref.getString("pw", this));

			//새로운 데이트가 있거나, 서버로부터 수정한 데이터가 있을때
			if ((newDate != null && newDate.size() != 0)
					|| (modified_complete != null && modified_complete.size() != 0))
			{

				selectLoc_routesAndPhotos(newDate, db, unUploadedData);
				new HttpObjectPostSend(mHandler, unUploadedData).start();

			} else
			{
				
				
				
				FollovLocationService.PRINT_LOG(TAG, "여기들어오니???????????????????");
				
				sendUnUploadedImg();
				//showMaps();
			}

		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// private void synchronizeClientWithServer(String jsonString){
	// FollovLocationService.PRINT_LOG(TAG, "synchronizeClientWithServer");
	// insertServerDataToDeviceDB(jsonString);
	//
	// //서버에 DB를 보내어 동기화 한다. 동기화할 정보가 없으면 바로 맵을 보여준다.
	// if(!insertClientDataToServerDB(jsonString)) showMaps();
	// }

	// private boolean insertClientDataToServerDB(String jsonString){
	//
	// JSONTokener jtk = new JSONTokener(jsonString);
	// JSONObject json = null;
	//
	// try {
	// json = (JSONObject)jtk.nextValue();
	//
	// int date_code = json.getInt("serverRecentDate_code");
	// int loc_no = json.getInt("serverRecentLoc_no");
	// int photo_no = json.getInt("serverRecentPhoto_no");
	//
	// DBPool db = DBPool.getInstance(this);
	//
	// JSONObject result = new JSONObject();
	//
	// result.put("code", FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_REQUEST);
	// result.put("couple_id", FollovPref.getString("coupleid", this));
	// result.put("email", FollovPref.getString("email", this));
	// result.put("pw", FollovPref.getString("pw", this));
	//
	// JSONArray newDateArray = db.getNewDates(date_code);
	// JSONArray newLocRoute = db.selectLoc_routes(date_code, loc_no);
	// JSONArray newLocPhoto = db.selectLoc_photos(photo_no);
	//
	// if(newDateArray == null || newLocRoute == null || newLocPhoto == null)
	// return false;
	//
	// result.put("new_date", db.getNewDates(date_code));
	// result.put("new_loc_route", db.selectLoc_routes(date_code, loc_no));
	// result.put("new_loc_photo", db.selectLoc_photos(photo_no));
	//
	// if(newDateArray.length() != 0 || newLocRoute.length() != 0 ||
	// newLocPhoto.length() != 0){
	// new HttpPostMethod(mHandler,"SynchronizeAction.do", result.toString(),cm,
	// wifiManager).start();
	// return true;
	// }
	//
	//
	//
	// } catch (JSONException e) {
	// FollovLocationService.MyStackTrace(e);
	// }
	//
	// return false;
	// }

	private void insertServerDataToDeviceDB(String jsonString)
	{

		FollovLocationService.PRINT_LOG(TAG, "insertServerDataToDeviceDB");
		FollovLocationService.PRINT_LOG(TAG, "received jsonString : "
				+ jsonString);

		JSONTokener jtk = new JSONTokener(jsonString);
		JSONObject json = null;

		try
		{
			json = (JSONObject) jtk.nextValue();
		} catch (JSONException e)
		{
			FollovLocationService.MyStackTrace(e);
		}

		insertDateToDeviceDB(json);
		insertLoc_routeToDeviceDB(json);
		insertLoc_photoToDeviceDB(json);

		FollovLocationService.PRINT_LOG(TAG,
				"insertServerDataToDeviceDB Finish");

	}

	private void insertLoc_routeToDeviceDB(JSONObject json)
	{

		if (json == null)
			return;

		DBPool dbPool = DBPool.getInstance(this);

		try
		{

			JSONArray jsonArray = json.getJSONArray("new_loc_route");

			if (jsonArray == null)
				return;

			for (int i = 0; i < jsonArray.length(); i++)
			{

				JSONObject record = jsonArray.getJSONObject(i);

				dbPool.insertLocationRoute(new Loc_route_tb_VO(record
						.getInt("date_code"), record.getInt("loc_no"), record
						.getDouble("latitude"), record.getDouble("longitude"),
						record.getString("isSpecial"), record
								.getString("loc_time")));

			}

			FollovLocationService.PRINT_LOG(TAG,
					"synchronize loc_route Success");

		} catch (JSONException e)
		{
			FollovLocationService
					.PRINT_LOG(TAG,
							"insertLoc_routeToDeviceDB Failed 아마 해당하는 new_loc_route키가 없는듯");
			FollovLocationService.MyStackTrace(e);
		}
	}

	private void insertLoc_photoToDeviceDB(JSONObject json)
	{

		if (json == null)
			return;

		DBPool dbPool = DBPool.getInstance(this);

		try
		{
			JSONArray jsonArray = json.getJSONArray("new_loc_photo");

			if (jsonArray == null)
				return;

			for (int i = 0; i < jsonArray.length(); i++)
			{

				JSONObject record = jsonArray.getJSONObject(i);
				
				dbPool.insertLocPhoto(record.getInt("date_code"),
						record.getInt("loc_no"), record.getString("name"),
						record.getString("date"),
						record.getString("photo_taken_email"),
						record.getString("is_img_uploaded"));

			}

			FollovLocationService.PRINT_LOG(TAG,
					"synchronize loc_photo Success");

		} catch (JSONException e)
		{
			FollovLocationService
					.PRINT_LOG(TAG,
							"insertLoc_photoToDeviceDB Failed 아마 해당하는 new_loc_route키가 없는듯");
			FollovLocationService.MyStackTrace(e);
		}
	}

	private void insertDateToDeviceDB(JSONObject json)
	{

		FollovLocationService.PRINT_LOG(TAG, "insertDateToDeviceDB");

		if (json == null)
			return;

		DBPool dbPool = DBPool.getInstance(this);

		try
		{
			JSONArray jsonArray = json.getJSONArray("new_date");

			if (jsonArray == null)
				return;

			for (int i = 0; i < jsonArray.length(); i++)
			{

				JSONObject record = jsonArray.getJSONObject(i);

				dbPool.insertDateStart(new Date_tb_client_VO(record
						.getInt("date_code"), record.getInt("year"), record
						.getInt("month"), record.getInt("day"), record
						.getString("day_of_week"), record
						.getString("start_time")));

			}

			FollovLocationService.PRINT_LOG(TAG, "synchronize date Success");

		} catch (JSONException e)
		{
			FollovLocationService.PRINT_LOG(TAG,
					"insertDateToDeviceDB Failed 아마 해당하는 new_date키가 없는듯");
			FollovLocationService.MyStackTrace(e);
		}

	}

	private void requestServerDBSynchronizeToClient()
	{

		try
		{

			DBPool db = DBPool.getInstance(this);

			String email = FollovPref.getString("email", this);
			int date_code = db.recentDateCode();
			int loc_no = db.lastLocNo(date_code);
			int photo_no = db.recentPhotoNo(email);

			JSONObject json = new JSONObject();
			json.put("code", FollovCode.SERVER_DB_SYNCHRONIZE_TO_CLIENT_REQUEST);

			json.put("email", email);
			json.put("pw", FollovPref.getString("pw", this));
			json.put("couple_id", FollovPref.getString("coupleid", this));
			json.put("date_code", date_code);
			json.put("loc_no", loc_no);
			// json.put("photo_no", photo_no);

			FollovLocationService.PRINT_LOG(TAG, "synchronize request : "
					+ json.toString());

			new HttpPostMethod(mHandler, "SynchronizeAction.do",
					json.toString()).start();
		} catch (JSONException e)
		{
			FollovLocationService.MyStackTrace(e);
		}

	}

	private void synchronizeWithServer()
	{

		try
		{

			DBPool db = DBPool.getInstance(this);

			JSONObject json = new JSONObject();

			int date_code = db.recentDateCode();

			if (date_code == 0)
			{
				json.put("code", FollovCode.GET_ALL_DATA_FROM_SERVER_REQUEST);
				json.put("couple_id", FollovPref.getString("coupleid", this));
				json.put("email", FollovPref.getString("email", this));
				json.put("pw", FollovPref.getString("pw", this));

				new HttpPostMethod(mHandler, "SynchronizeAction.do",
						json.toString()).start();

				return;
			}

			json.put("code", FollovCode.GET_MODIFIED_DATA_FROM_SERVER_REQUEST);

			json.put("couple_id", FollovPref.getString("coupleid", this));
			json.put("email", FollovPref.getString("email", this));
			json.put("pw", FollovPref.getString("pw", this));

			new HttpPostMethod(mHandler, "SynchronizeAction.do",
					json.toString()).start();
		} catch (JSONException e)
		{
			FollovLocationService.MyStackTrace(e);
		}

	}

	public void mOnClick(View v)
	{
		switch (v.getId())
		{
		case R.id.showDateLog:
			// requestServerDBSynchronizeToClient();
			synchronizeWithServer();
			break;
		case R.id.droptable:
			break;

		}
	}

	public void dbInit(View v)
	{
		switch (v.getId())
		{
		case R.id.dbInit:

			DBPool db = DBPool.getInstance(this);
			db.dbInit();
			// dbInit
			break;
		}
	}

	public void showMaps()
	{

		Intent intent = new Intent(KibumTestActivity.this, MapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	public void dropTable()
	{
		FollovApplication app = (FollovApplication) getApplication();
		FollovLocationService follovService = app.getFollovIntentService();

		if (follovService != null)
		{
			follovService.dropTable();
		} else
		{
			FollovLocationService.PRINT_LOG(TAG, "follovIntentService null");
		}
	}
	
	
	public void mCycle(View v){
		switch(v.getId()){
		
		case R.id.fastCycle:
			AppManager.getInstance().getFollovService().fasterLocationTimer();
			break;
		case R.id.slowCycle:
			AppManager.getInstance().getFollovService().slowerLocationTimer();
			break;
		
		}
	}
	
	
	

}
