package com.follov.db;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.json.*;

import android.content.*;
import android.database.*;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.*;

import com.follov.*;
import com.follov.db.vo.*;
import com.google.android.gms.maps.model.*;

public class DBPool {

	private static final String TAG = DBPool.class.getSimpleName();

	private String DB_FILE_PATH;
	private static DBPool instance;

	private SQLiteDatabase db;

	public static DBPool getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new DBPool(context);
		}
		return instance;
	}
	
	public void createImageFolder(){
		File imageFolder = new File(DBConfig.IMAGE_FILE_DIR);
		
		if(imageFolder.exists()) return;
		imageFolder.mkdirs();
	}

	public DBPool(Context context)
	{
		createImageFolder();
		DB_FILE_PATH = DBConfig.DB_FILE_DIR + DBConfig.DB_FILE_NAME;

		File file = new File(DBConfig.DB_FILE_DIR);
		//DB������ ������ ����� ������ �� (���� �� ��ġ�� SDī�忡 follov/DB ��� ����� �����Ƿ� �����ش�.
		if(!file.exists() && !file.mkdirs())
			return;
		
		
		
		
		
		
		file = new File(DB_FILE_PATH);
		if(!file.exists())
		{
			try
			{
				InputStream is = context.getAssets().open(DBConfig.DB_FILE_NAME);
				OutputStream os = new FileOutputStream(DB_FILE_PATH);

				byte[] buffer = new byte[1024];
				int len;

				while((len = is.read(buffer)) > 0 )
					os.write(buffer, 0 , len);

				os.flush();
				os.close();
				is.close();
			}
			catch(IOException e){MyStackTrace(e); return;}
		}
		try
		{
			db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch(Exception e){MyStackTrace(e);}

	}
	
	public void dbInit(){
		
		try
		{
			Log.i(TAG, "dbInit");
			
			String tempPath = DBConfig.DB_FILE_DIR + "backup/follov.sqlite";
			Log.i(TAG, "tempPath : "+tempPath);
			InputStream is = new FileInputStream(new File(tempPath));
			OutputStream os = new FileOutputStream(DB_FILE_PATH);
			
			Log.i(TAG, DB_FILE_PATH);

			byte[] buffer = new byte[1024];
			int len;

			while((len = is.read(buffer)) > 0 )
				os.write(buffer, 0 , len);

			os.flush();
			os.close();
			is.close();
		}catch(IOException e){MyStackTrace(e); return;}
		
	}

	public void openDatabase(){
		if(db == null){
			try
			{
				db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			}
			catch(Exception e){MyStackTrace(e);}

		}
	}

	public void dbClose(){
		db.close();

	}

	public void beginTransaction(){
		openDatabase();

		db.beginTransaction();
	}

	public void setTransactionSuccessful(){


	}

	public void deleteDate(int date_code) throws SQLException{

		db.execSQL("DELETE FROM date_tb WHERE date_code = ?;",
				new String[]{String.valueOf(date_code)});


	}

	public void deleteLoc_route(int date_code) throws SQLException{

		db.execSQL("DELETE FROM loc_route_tb WHERE date_code = ?;",
				new String[]{String.valueOf(date_code)});


	}

	public void deleteLoc_photo(int date_code) throws SQLException{

		db.execSQL("DELETE FROM loc_photo_tb WHERE date_code = ?;",
				new String[]{String.valueOf(date_code)});


	}


	public JSONArray modifyDateInfoFromMergedData(ArrayList<MergedLoc_Info> merged_list){

		JSONArray retArray = new JSONArray();
		openDatabase();
		
		for(MergedLoc_Info info : merged_list){

			int date_code = info.getDate_code();

			ArrayList<Loc_photo_tb_VO> loc_photo_list = info.getLoc_photo_list();
			ArrayList<Loc_route_tb_VO> loc_route_list = info.getLoc_route_list();

			//트랜잭션 막고 delete & insert
			db.beginTransaction();
			
			try{
				
				checkDateIsUploaded(date_code);
				
				deleteLoc_photo(date_code);
				deleteLoc_route(date_code);
				
				for(Loc_route_tb_VO route : loc_route_list){
					insertLocationRouteAndThrowException(route);
				}
				for(Loc_photo_tb_VO photo : loc_photo_list){
					insertLocPhoto(photo);
				}
				
				
				JSONObject obj = new JSONObject();
				obj.put("date_code", date_code);
				retArray.put(obj);
				
				db.setTransactionSuccessful();
			} catch (SQLException e){
				FollovLocationService.PRINT_LOG(TAG, "SQLException on modifyDateInfoFromMergedData");
				FollovLocationService.MyStackTrace(e);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
			

		}

		return retArray;
	} 

	
	public ArrayList<Integer> modifyData(JSONObject json){

		ArrayList<Integer> modifiedDate = new ArrayList<Integer>();

		openDatabase();
		db.beginTransaction();
		
		JSONObject modified = null;
		JSONArray dates = null;
		try {
			modified = json.getJSONObject("getModifiedData");
			dates = modified.getJSONArray("date");
			
			Log.i("kkkkkk", "여기오냐"+dates.toString());

			if(dates == null) return null;

			for(int i = 0; i < dates.length(); i++){

				JSONObject jo = dates.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				deleteLoc_photo(date_code);
				deleteLoc_route(date_code);
				deleteDate(date_code);

				modifiedDate.add(date_code);
			}

			insertDates(modified);
			insertLoc_routes(modified);
			insertLoc_photos(modified);

			db.setTransactionSuccessful();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SQLException e){
			FollovLocationService.PRINT_LOG(TAG, "SQLException on modifyData");
			FollovLocationService.MyStackTrace(e);
			modifiedDate = null;

		} catch(NullPointerException e){
			FollovLocationService.PRINT_LOG(TAG, "NullPointer on modifyData");
			modifiedDate = null;
		} finally {
			db.endTransaction();
		}

		return modifiedDate;
	}

	public ArrayList<Integer> getDataAfterMerge(JSONObject json){

		ArrayList<Integer> modifiedDate = new ArrayList<Integer>();

		openDatabase();
		db.beginTransaction();

		JSONArray dates = null;
		JSONObject modified = null;
		try {
			modified = json.getJSONObject("get_data_after_merge");
			dates = modified.getJSONArray("date");

			if(dates == null) return null;

			for(int i = 0; i < dates.length(); i++){

				JSONObject jo = dates.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				deleteLoc_photo(date_code);
				deleteLoc_route(date_code);
				deleteDate(date_code);

				modifiedDate.add(date_code);
			}

			insertDates(modified);
			insertLoc_routes(modified);
			insertLoc_photos(modified);

			db.setTransactionSuccessful();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SQLException e){
			FollovLocationService.PRINT_LOG(TAG, "SQLException on modifyData");
			FollovLocationService.MyStackTrace(e);
			modifiedDate = null;

		} catch(NullPointerException e){
			FollovLocationService.PRINT_LOG(TAG, "NullPointer on modifyData");
			modifiedDate = null;
		} finally {
			db.endTransaction();
		}

		return modifiedDate;
	}

	private boolean insertDates(JSONObject json) throws SQLException{


		try{
			JSONArray date = json.getJSONArray("date");

			if(date == null) return false;

			for(int i = 0; i < date.length(); i++){

				JSONObject record = date.getJSONObject(i);


				db.execSQL("INSERT INTO date_tb (date_code, year, month, day, day_of_week, start_time, end_time, is_uploaded) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
						new String[]{String.valueOf(record.getInt("date_code")), String.valueOf(record.getInt("year"))
						,String.valueOf(record.getInt("month")), String.valueOf(record.getInt("day"))
						,record.getString("day_of_week"), record.getString("start_time"), record.getString("end_time"), "y"});

			}
			return true;

		} catch (JSONException e){
			FollovLocationService.MyStackTrace(e);
			return false;
		}
	}

	private void insertLoc_routes(JSONObject json) throws SQLException{
		try {
			JSONArray loc_route = json.getJSONArray("loc_route");

			if(loc_route == null) return;

			for(int i = 0; i < loc_route.length(); i++){

				JSONObject record = loc_route.getJSONObject(i);

				db.execSQL("INSERT INTO loc_route_tb " +
						"VALUES (?, ?, ?, ?, ?, ?);",
						new String[]{ String.valueOf(record.getInt("date_code"))
						,String.valueOf(record.getInt("loc_no")), String.valueOf(record.getDouble("latitude"))
						,String.valueOf(record.getDouble("longitude")), record.getString("isSpecial"), record.getString("loc_time") });

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	

	private void insertLoc_photos(JSONObject json) throws SQLException{


		try {
			JSONArray loc_photo = json.getJSONArray("loc_photo");


			if(loc_photo == null) return;

			for(int i = 0; i < loc_photo.length(); i++){

				JSONObject record = loc_photo.getJSONObject(i);

				db.execSQL("INSERT INTO loc_photo_tb (date_code, loc_no, name , date, photo_taken_email, is_img_uploaded)"  +
						"VALUES (?, ?, ?, ?, ?, ?);",
						new String[]{String.valueOf(record.getInt("date_code")), 
						String.valueOf(record.getInt("loc_no")), 
						record.getString("name"), 
						record.getString("date"),
						record.getString("photo_taken_email"),
						record.getString("is_img_uploaded")});


			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public boolean insertDate(Date_tb_client_VO date, String is_uploaded){
		openDatabase();
		try{
			db.execSQL("INSERT INTO date_tb (date_code, year, month, day, day_of_week, start_time, end_time, is_uploaded) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
					new String[]{String.valueOf(date.getDate_code()), String.valueOf(date.getYear())
					,String.valueOf(date.getMonth()), String.valueOf(date.getDay())
					,date.getDay_of_week(), date.getStart_time(), date.getEnd_time(), is_uploaded});
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;
	}

	/**
	 * date_tb�� insert�ϴ� �޼ҵ� 
	 * @param date
	 * @return
	 */
	public boolean insertDateStart(Date_tb_client_VO date){

		openDatabase();
		try{
			db.execSQL("INSERT INTO date_tb (date_code, year, month, day, day_of_week, start_time) " +
					"VALUES (?, ?, ?, ?, ?, ?);",
					new String[]{String.valueOf(date.getDate_code()), String.valueOf(date.getYear())
					,String.valueOf(date.getMonth()), String.valueOf(date.getDay())
					,date.getDay_of_week(), date.getStart_time()});
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;
	}

	/**
	 * date_tb�� ������Ʈ ��Ű�� �޼ҵ� ����Ʈ ����ð�(end_time)�÷��� ������Ʈ ��Ų��.
	 * @param end_time
	 * @param date_code
	 * @return
	 */
	public boolean updateDateEnd(String end_time, int date_code){

		openDatabase();
		try{
			db.execSQL("UPDATE date_tb " +
					"SET end_time = ? " +
					"WHERE date_code = ?;",
					new String[]{end_time, String.valueOf(date_code) });
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;

	}

	public boolean checkDateIsUploaded(int date_code) throws SQLException{
	
		db.execSQL("UPDATE date_tb " +
				"SET is_uploaded = 'y' " +
				"WHERE date_code = ?;",
				new String[]{String.valueOf(date_code) });
	

		return true;
	}

	/**
	 * loc_route_tb�� ������Ʈ ��Ű�� �޼ҵ�
	 * @param date_code
	 * @param loc_no
	 * @param isSpecial
	 * @return
	 */
	public boolean updateLocationRoute(int date_code, int loc_no, String isSpecial){

		openDatabase();
		try{
			db.execSQL("UPDATE loc_route_tb " +
					"SET isSpecial = ? " +
					"WHERE date_code = ? AND loc_no = ?;",
					new String[]{isSpecial , String.valueOf(date_code), String.valueOf(loc_no)});
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;

	}
	
	public boolean updateLoc_photo(String imgName, String is_img_uploaded){
		openDatabase();
		
		try{
			db.execSQL("UPDATE loc_photo_tb " +
					"SET is_img_uploaded = ?" +
					"WHERE name = ? ;",
					new String[]{is_img_uploaded , imgName});
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;
	}
	
	public synchronized void insertLocPhoto(Loc_photo_tb_VO vo) throws SQLException{
		db.execSQL("INSERT INTO loc_photo_tb (date_code, loc_no, name , date, photo_taken_email, is_img_uploaded)"  +
				"VALUES (?, ?, ?, ?, ?, ?);",
				new String[]{String.valueOf(vo.getDate_code()), 
				String.valueOf(vo.getLoc_no()), vo.getName(), vo.getDate(), 
				vo.getPhoto_taken_email(), vo.getIs_img_uploaded()});
	}

	
	public synchronized LinkedList<String> selectUnUploadedImg(String email){
		
		LinkedList<String> unUploadedImgList = new LinkedList<String>();
		openDatabase();

		Cursor cursor = db.rawQuery("SELECT name "
				+ "FROM loc_photo_tb " +
				"WHERE photo_taken_email = ? AND is_img_uploaded = 'n' ", 
				new String[]{email});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				unUploadedImgList.offer(cursor.getString(0));

				cursor.moveToNext();
			}

		}

		cursor.close();

		return unUploadedImgList;
	}
	


	public synchronized boolean insertLocPhoto(int date_code, int loc_no, String name, String date,
			String email, String is_img_uploaded){
		openDatabase();
		try{
			db.execSQL("INSERT INTO loc_photo_tb (date_code, loc_no, name , date, photo_taken_email, is_img_uploaded)"  +
					"VALUES (?, ?, ?, ?, ?, ?);",
					new String[]{String.valueOf(date_code), String.valueOf(loc_no), name, date, email, is_img_uploaded});
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;
	}
	
	public void insertLocationRouteAndThrowException(Loc_route_tb_VO locRoute) throws SQLException{

		db.execSQL("INSERT INTO loc_route_tb " +
				"VALUES (?, ?, ?, ?, ?, ?);",
				new String[]{ String.valueOf(locRoute.getDate_code())
				,String.valueOf(locRoute.getLoc_no()), String.valueOf(locRoute.getLatitude())
				,String.valueOf(locRoute.getLongitude()), locRoute.getIsSpecial(), locRoute.getLoc_time() });
		
	}


	/**
	 * loc_route_tb�� insert�ϴ� �޼ҵ�
	 * @param locRoute
	 * @return
	 */
	public boolean insertLocationRoute(Loc_route_tb_VO locRoute){

		openDatabase();
		try{
			db.execSQL("INSERT INTO loc_route_tb " +
					"VALUES (?, ?, ?, ?, ?, ?);",
					new String[]{ String.valueOf(locRoute.getDate_code())
					,String.valueOf(locRoute.getLoc_no()), String.valueOf(locRoute.getLatitude())
					,String.valueOf(locRoute.getLongitude()), locRoute.getIsSpecial(), locRoute.getLoc_time() });
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}

		return true;
	}


	public ArrayList<Date_tb_client_VO> getNewDates(int date_code){

		openDatabase();
		ArrayList<Date_tb_client_VO> dates = new ArrayList<Date_tb_client_VO>();

		String date_code_str = String.valueOf(date_code);

		Cursor cursor = db.rawQuery("SELECT date_code, year, month, day, day_of_week, start_time, end_time "
				+ "FROM date_tb " 
				+ "WHERE (is_uploaded = 'n') ", null);

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				Date_tb_client_VO vo = new Date_tb_client_VO(
						cursor.getInt(0),cursor.getInt(1), cursor.getInt(2), 
						cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

				dates.add(vo);

				cursor.moveToNext();
			}

			cursor.close();
		}

		cursor.close();

		return dates;

	}

//	public ArrayList<Date_tb_client_VO> getNewDates(int date_code){
//
//		openDatabase();
//		ArrayList<Date_tb_client_VO> dates = new ArrayList<Date_tb_client_VO>();
//
//		String date_code_str = String.valueOf(date_code);
//
//		Cursor cursor = db.rawQuery("SELECT date_code, year, month, day, day_of_week, start_time, end_time "
//				+ "FROM date_tb " 
//				+ "WHERE ( date_code >= ? ) AND (is_uploaded = 'n') ", 
//				new String[]{date_code_str});
//
//		if(cursor.moveToFirst()){
//			while(!cursor.isAfterLast())
//			{
//				Date_tb_client_VO vo = new Date_tb_client_VO(
//						cursor.getInt(0),cursor.getInt(1), cursor.getInt(2), 
//						cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
//
//				dates.add(vo);
//
//				cursor.moveToNext();
//			}
//
//			cursor.close();
//		}
//
//		cursor.close();
//
//		return dates;
//
//	}


	//	public JSONArray selectLoc_routes(int date_code, int loc_no){
	//
	//		JSONArray jsonArray = new JSONArray();
	//		openDatabase();
	//
	//		Cursor cursor = db.rawQuery("SELECT date_code, loc_no , latitude , longitude , isSpecial "
	//				+ "FROM loc_route_tb " +
	//				"WHERE ( (date_code > ?) OR (date_code = ? AND loc_no > ?))", 
	//				new String[]{String.valueOf(date_code), String.valueOf(date_code), String.valueOf(loc_no)});
	//
	//		if(cursor.moveToFirst()){
	//			while(!cursor.isAfterLast())
	//			{
	//				JSONObject jo = new JSONObject();
	//
	//				int size = cursor.getColumnCount();
	//
	//				for(int i = 0; i < size; i++){
	//					String columnName = cursor.getColumnName(i);
	//					try {
	//						jo.put(columnName, cursor.getString(i));
	//					} catch (JSONException e) {
	//						e.printStackTrace();
	//					}
	//				}
	//
	//				jsonArray.put(jo);
	//
	//				cursor.moveToNext();
	//			}
	//
	//			cursor.close();
	//		}
	//
	//		cursor.close();
	//
	//
	//		return jsonArray;
	//
	//	}


	public ArrayList<Loc_route_tb_VO> selectLoc_routes(int date_code){

		ArrayList<Loc_route_tb_VO> loc_routes = new ArrayList<Loc_route_tb_VO>();
		openDatabase();

		Cursor cursor = db.rawQuery("SELECT date_code, loc_no , latitude , longitude , isSpecial, loc_time "
				+ "FROM loc_route_tb " +
				"WHERE date_code = ?", 
				new String[]{String.valueOf(date_code)});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				loc_routes.add(new Loc_route_tb_VO(
						cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2),
						cursor.getDouble(3), cursor.getString(4), cursor.getString(5)));

				cursor.moveToNext();
			}

		}

		cursor.close();

		return loc_routes;

	}

	public Date_tb_client_VO getDate(int date_code){
		openDatabase();

		Date_tb_client_VO date = new Date_tb_client_VO();

		Cursor cursor = db.rawQuery("SELECT date_code, year, month, day, day_of_week, start_time, end_time, weather "
				+ "FROM date_tb WHERE date_code = ?", 
				new String[]{String.valueOf(date_code)});

		if(cursor.moveToFirst()){
			date.setDate_code(cursor.getInt(0));
			date.setYear(cursor.getInt(1));
			date.setMonth(cursor.getInt(2));
			date.setDay(cursor.getInt(3));
			date.setDay_of_week(cursor.getString(4));
			date.setStart_time(cursor.getString(5));
			date.setEnd_time(cursor.getString(6));
		}

		cursor.close();

		return date;
	}


	public String getSync_time(int date_code){
		openDatabase();

		String sync_time = "";

		Cursor cursor = null;
		if(db != null)
			cursor = db.rawQuery("SELECT sync_time FROM date_tb WHERE date_code = ?", 
					new String[]{String.valueOf(date_code)});
		else{
			Log.e(TAG, "db : null");
			return sync_time;
		}


		if(cursor.moveToFirst()){
			sync_time = cursor.getString(0);
		}
		return sync_time;
	}

	//	public JSONArray selectLoc_photos(int photo_no){
	//
	//		JSONArray jsonArray = new JSONArray();
	//		openDatabase();
	//
	//		Cursor cursor = db.rawQuery("SELECT photo_no , date_code, loc_no, name, date  "
	//				+ "FROM loc_photo_tb " +
	//				"WHERE photo_no > ?", 
	//				new String[]{String.valueOf(photo_no)});
	//
	//		if(cursor.moveToFirst()){
	//			while(!cursor.isAfterLast())
	//			{
	//				JSONObject jo = new JSONObject();
	//
	//				int size = cursor.getColumnCount();
	//
	//				for(int i = 0; i < size; i++){
	//					String columnName = cursor.getColumnName(i);
	//					try {
	//						jo.put(columnName, cursor.getString(i));
	//					} catch (JSONException e) {
	//						e.printStackTrace();
	//					}
	//				}
	//
	//				jsonArray.put(jo);
	//
	//				cursor.moveToNext();
	//			}
	//
	//			cursor.close();
	//		}
	//
	//		cursor.close();
	//
	//
	//		return jsonArray;
	//
	//	}

	public ArrayList<Loc_photo_tb_VO> selectLoc_photos(int date_code, int loc_no){

		ArrayList<Loc_photo_tb_VO> loc_photos = new ArrayList<Loc_photo_tb_VO>();
		openDatabase();

		Cursor cursor = db.rawQuery("SELECT date_code, loc_no, name, date, photo_taken_email, is_img_uploaded  "
				+ "FROM loc_photo_tb " +
				"WHERE date_code = ? AND loc_no = ?", 
				new String[]{String.valueOf(date_code), String.valueOf(loc_no)});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				loc_photos.add(new Loc_photo_tb_VO(cursor.getInt(0), cursor.getInt(1), 
						cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));

				cursor.moveToNext();
			}

		}

		cursor.close();

		return loc_photos;

	}






	public boolean deleteAllDate_tb(){

		openDatabase();
		try{
			db.execSQL("DELETE FROM date_tb;");
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}
		return true;
	}


	public boolean deleteAllLoc_Route_tb(){

		openDatabase();
		try{
			db.execSQL("DELETE FROM loc_route_tb;");
		}catch(SQLException e){
			MyStackTrace(e);
			return false;
		}
		return true;

	}


	public int recentDateCode(){

		openDatabase();
		int maxDateCode = 0;

		Cursor cursor = null;
		if(db != null)
			cursor = db.rawQuery("SELECT max(date_code) FROM date_tb", null);
		else{
			Log.e(TAG, "db : null");
			return 0;
		}


		if(cursor.moveToFirst()){
			maxDateCode = cursor.getInt(0);
		}
		return maxDateCode;
	}
	
	
	public int recentLoc_no(int date_code){

		openDatabase();
		int recentLoc_no = 0;

		Cursor cursor = null;
		if(db != null)
			cursor = db.rawQuery("SELECT max(loc_no) FROM loc_route_tb " +
					"WHERE date_code = ? ", new String[]{String.valueOf(date_code)});
		else{
			Log.e(TAG, "db : null");
			return 0;
		}


		if(cursor.moveToFirst()){
			recentLoc_no = cursor.getInt(0);
		}
		return recentLoc_no;
	}
	
	
	
	public boolean isCurrentDateEnd(int date_code){
		openDatabase();

		String end_time = "";
		
		Cursor cursor = null;
		if(db != null){
			cursor = db.rawQuery("SELECT end_time FROM date_tb " +
					"WHERE date_code = ? ", 
					new String[]{String.valueOf(date_code)});
		} 

		if(cursor.moveToFirst()){
			end_time = cursor.getString(0);

			if(end_time == null || end_time.equals(""))
				return false;
		}
		
		
		return true;
	}


	public int lastLocNo(int date_code){
		openDatabase();
		int lastLocNo = 0;

		Cursor cursor = null;
		if(db != null)
			cursor = db.rawQuery("SELECT max(loc_no) FROM loc_route_tb WHERE date_code = ?", 
					new String[]{String.valueOf(date_code)});
		else{
			Log.e(TAG, "db : null");
			return 0;
		}


		if(cursor.moveToFirst()){
			lastLocNo = cursor.getInt(0);
		}
		return lastLocNo;
	}

	public synchronized int recentPhotoNo(String email){
		openDatabase();
		int maxPhotoNo = 0;

		Cursor cursor = null;
		if(db != null)
			cursor = db.rawQuery("SELECT max(photo_no) FROM loc_photo_tb WHERE email = ?", new String[]{String.valueOf(email)});
		else{
			Log.e(TAG, "db : null");
			return 0;
		}


		if(cursor.moveToFirst()){
			maxPhotoNo = cursor.getInt(0);
		}
		return maxPhotoNo;
	}


	public ArrayList<LatLng> getLocationPoints(int date_code){

		openDatabase();

		ArrayList<LatLng> locations = new ArrayList<LatLng>();
		Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM loc_route_tb WHERE date_code = ?", 
				new String[]{String.valueOf(date_code)});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				//���� �κп��� db���� �ش��ϴ� �÷��� �о�ͼ� �װ��� ��ü�� ����� �κ�
				//2��° 3��° �÷��� latitude, longitude
				LatLng point = new LatLng(cursor.getDouble(0), cursor.getDouble(1));

				//�� �����о Mission ��ü�� ArrayList�� ����
				locations.add(point);
				cursor.moveToNext();
			}

			cursor.close();
			//����� ��� ������ ArrayList�� ��Ƽ� �����ϴºκ�.
		}

		cursor.close();
		return locations;



	}

	public ArrayList<Loc_route_tb_VO> getLocationRoutes(int date_code){

		openDatabase();

		ArrayList<Loc_route_tb_VO> locations = new ArrayList<Loc_route_tb_VO>();
		Cursor cursor = db.rawQuery("SELECT date_code, loc_no, latitude, longitude, isSpecial, loc_time FROM loc_route_tb WHERE date_code = ?", 
				new String[]{String.valueOf(date_code)});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				Loc_route_tb_VO point = new Loc_route_tb_VO(
						cursor.getInt(0), cursor.getInt(1),
						cursor.getDouble(2), cursor.getDouble(3),
						cursor.getString(4), cursor.getString(5));
				//LatLng point = new LatLng(cursor.getDouble(0), cursor.getDouble(1));

				//�� �����о Mission ��ü�� ArrayList�� ����
				locations.add(point);
				cursor.moveToNext();
			}

			cursor.close();
			//����� ��� ������ ArrayList�� ��Ƽ� �����ϴºκ�.
		}

		cursor.close();
		return locations;



	}


	//	public ArrayList<Mission> getMissions(int content, int page)
	//	{
	//		ArrayList<Mission> missions = new ArrayList<Mission>();
	//		Cursor cursor = db.rawQuery("SELECT * FROM Missions WHERE content = ? and page = ?", new String[]{String.valueOf(content), String.valueOf(page)});
	//		
	//		if(cursor.moveToFirst())
	//		{
	//			while(!cursor.isAfterLast())
	//			{
	//				//���� �κп��� db���� �ش��ϴ� �÷��� �о�ͼ� �װ��� ��ü�� ����� �κ�
	//				Mission mission = new Mission(cursor.getInt(0),
	//						cursor.getInt(1),
	//						cursor.getInt(2), cursor.getInt(3), 
	//						cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9));
	//				
	//				//�� �����о Mission ��ü�� ArrayList�� ����
	//				missions.add(mission);
	//				cursor.moveToNext();
	//			}
	//			
	//			cursor.close();
	//			//����� ��� ������ ArrayList�� ��Ƽ� �����ϴºκ�.
	//			return missions;
	//		}
	//		
	//		cursor.close();
	//		return null;
	//	}


	//insert�ϴ� �޼ҵ�� ��� �����


	//delete�ϴ� �޼ҵ嵵 ��� �������

	synchronized public boolean updateSimple(int day)
	{
		ContentValues values = new ContentValues();
		values.put("done", 1);

		return db.update("Simple", values, "day = ?", new String[]{String.valueOf(day)}) >= 0;
	}

	synchronized public boolean resetSimple()
	{
		ContentValues values = new ContentValues();
		values.put("done", 0);

		return db.update("Simple", values, null, null) >= 0;
	}

	public static void MyStackTrace(Throwable e){
		String msg;
		msg = "Error :" + e.getMessage() +"\n";
		StackTraceElement[] elem = e.getStackTrace();

		for(int i = 0; i < elem.length; i++){
			msg += elem[i].toString()+"\n";
		}
		Log.e(TAG,msg);
	}

	public ArrayList<Loc_photo_tb_VO> getLocationPhotos(int date_code) {
		openDatabase();

		ArrayList<Loc_photo_tb_VO> photos = new ArrayList<Loc_photo_tb_VO>();
		Cursor cursor = db.rawQuery("SELECT date_code, loc_no, name, date, photo_taken_email, is_img_uploaded  " +
				"FROM loc_photo_tb " +
				"WHERE date_code = ?", 
				new String[]{String.valueOf(date_code)});

		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast())
			{
				Loc_photo_tb_VO point = new Loc_photo_tb_VO(
						cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2),cursor.getString(3), cursor.getString(4), cursor.getString(5));
				//LatLng point = new LatLng(cursor.getDouble(0), cursor.getDouble(1));

				//�� �����о Mission ��ü�� ArrayList�� ����
				photos.add(point);
				cursor.moveToNext();
			}

			cursor.close();
			//����� ��� ������ ArrayList�� ��Ƽ� �����ϴºκ�.
		}

		cursor.close();
		return photos;

	}
}
