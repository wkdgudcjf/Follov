package com.follov.daoManager;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import org.json.*;

import sun.util.resources.*;

import com.follov.*;
import com.follov.daoManager.dto.*;
import com.follov.db.*;
import com.follov.db.vo.*;
import com.follov.jdbc.dao.*;
import com.follov.jdbc.dto.location.*;

public class SynchronizeManager
{

	private static SynchronizeManager sInstance = new SynchronizeManager();

	Date_tb_DAOImpl dateDAO = Date_tb_DAOImpl.getInstance();
	Loc_route_tb_DAOImpl loc_route_DAO = Loc_route_tb_DAOImpl.getInstance();
	Loc_photo_tb_DAOImpl loc_photo_DAO = Loc_photo_tb_DAOImpl.getInstance();
	Data_input_user_tb_DAOImpl dataInputDAO = Data_input_user_tb_DAOImpl.getInstance();

	private SynchronizeManager(){}

	public static SynchronizeManager getInstance(){
		return sInstance;
	}

	public boolean isAlreadySyncronizedDate(Connection conn, JSONObject json){

		try
		{
			String couple_id = json.getString("couple_id");
			int date_code = json.getInt("date_code");

			String clientSync_time = json.getString("sync_time");

			if(clientSync_time.equals("")) return false;

			String serverSync_time = dateDAO.getSyncTime(conn, couple_id, date_code);

			if(serverSync_time.equals(clientSync_time)){
				return true;
			} 

		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public String getDate()
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yy-MM-dd_hh:mm:ss.SSS");
		dateFormatter.setLenient(false);
		return dateFormatter.format(new Date());
	}




	public boolean synchronizeDataBase(Connection conn, JSONObject json)
	{
		//이 메소드가 불린다는 것은 이미 sync_time 이 다름을 의미함.	
		return false;
	}

	
	public void updateIsModifiedToN(Connection conn, UnUploadedClientData data){

		ArrayList<Integer> modifiedDates = data.getModified_complete();

		if(modifiedDates == null) return ;

		if(!UserManager.getInstance().checkPassword(conn, data.getEmail(), data.getPw())) return;

		String couple_id = data.getCouple_id();


		for(int i = 0; i < modifiedDates.size(); i++){

			dateDAO.updateDateModified(conn, couple_id, modifiedDates.get(i), "n");
		}

	}
	
	
	
	public void updateGet_data_after_mergeToY(Connection conn, UnUploadedClientData data){

		ArrayList<Integer> modifiedDates = data.getGet_data_after_merge();

		if(modifiedDates == null) return ;

		if(!UserManager.getInstance().checkPassword(conn, data.getEmail(), data.getPw())) return;

		String couple_id = data.getCouple_id();

		for(int i = 0; i < modifiedDates.size(); i++){

			dateDAO.updateGet_data_after_mergeToY(conn, couple_id, modifiedDates.get(i), "n");
		}

	}

	public void updateIsModifiedToN(Connection conn, JSONObject json)
	{
		if(!UserManager.getInstance().checkPassword(conn, json)) return;

		String couple_id = "";
		try
		{
			couple_id = json.getString("couple_id");

			JSONArray modifiedDates = json.getJSONArray("date");

			if(modifiedDates == null) return ;

			for(int i = 0; i < modifiedDates.length(); i++){

				int date_code = modifiedDates.getJSONObject(i).getInt("date_code");

				dateDAO.updateDateModified(conn, couple_id, date_code, "n");
			}


		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		//return result;
	}


	public void updateGet_Data_After_MergeToY(Connection conn, JSONObject json){
		
		if(!UserManager.getInstance().checkPassword(conn, json)) return;

		String couple_id = "";
		try
		{
			couple_id = json.getString("couple_id");
			
			JSONArray merged_complete = json.getJSONArray("merged_complete");
			
			if(merged_complete == null || merged_complete.length() == 0) return;
			
			for(int i = 0; i < merged_complete.length(); i++){
				
				int date_code = merged_complete.getJSONObject(i).getInt("date_code");
				
				dateDAO.updateGet_Data_After_Merge(conn, couple_id, date_code, "y");
				
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public JSONObject getAllData(Connection conn, JSONObject json){
		if(!UserManager.getInstance().checkPassword(conn, json)) return null;

		JSONObject result = new JSONObject();

		String couple_id = "";
		try
		{
			couple_id = json.getString("couple_id");

			result.put("date", dateDAO.selectAllDates(conn, couple_id));
			result.put("loc_route", loc_route_DAO.selectAllLoc_routes(conn, couple_id));
			result.put("loc_photo", loc_photo_DAO.selectAllLoc_photos(conn, couple_id));

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	

	private void insertLoc_routesAndLoc_photos(Connection conn, String couple_id, int date_code, 
			HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_route_map,
			HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photo_map){
		
		if(loc_route_map != null){
			ArrayList<Loc_route_tb_VO> loc_route_list = loc_route_map.get(date_code);

			if(loc_route_list != null){
				
				for(Loc_route_tb_VO loc_route : loc_route_list){
					int loc_no = loc_route.getLoc_no();
					try{
						loc_route_DAO.insert(conn, loc_route, couple_id);
					} catch (SQLException e1){
						e1.printStackTrace();
					}

					ArrayList<Loc_photo_tb_VO> loc_photo_list = loc_photo_map.get(date_code).get(loc_no);
					
					if(DBManager.DEBUG_MODE){
						System.out.println(loc_no+"의 사진 개수 : "+loc_photo_list);
					}
					

					if(loc_photo_list != null){
						for(Loc_photo_tb_VO loc_photo : loc_photo_list){
							try{
								loc_photo_DAO.insert(conn, loc_photo, couple_id);
							} catch (SQLException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				
			}

		}
		
	}
	
	private void insertDate(Connection conn, String couple_id, String email, int date_code, Date_tb_client_VO date, ArrayList<Integer> upload_complete_list){
		
		if(dateDAO.insert(conn, new Date_tb_VO(
				date_code, date.getYear(), date.getMonth(), 
				date.getDay(), date.getDay_of_week(), date.getStart_time(), 
				email, "y", "n"	), couple_id) > 0){

			upload_complete_list.add(date_code);
//			JSONObject tempJSON = null;
//			try{
//				upload_complete_list.paddut(tempJSON);
//			} catch (JSONException e){
//				e.printStackTrace();
//			}

		}
		
	}

	@SuppressWarnings("deprecation")
	public RecentDataResponse insertClientNewData(Connection conn, UnUploadedClientData data){

		//JSONObject result = new JSONObject();
		RecentDataResponse result = new RecentDataResponse();

		if(data == null) return result;

		String couple_id = data.getCouple_id();
		String email = data.getEmail();

		if(!UserManager.getInstance().checkPassword(conn, email, data.getPw())) return null;

		HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_route_map = data.getLoc_routes();
		HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photo_map = data.getLoc_photos();

		try
		{

			ArrayList<Date_tb_client_VO> dates = data.getDates();

			if(dates == null) return result;

			
			ArrayList<Integer> upload_complete_list = new ArrayList<Integer>();
			ArrayList<MergedLoc_Info> merged_list = new ArrayList<MergedLoc_Info>();
			

//			JSONArray merged_data = new JSONArray();

			for(int i = 0 ; i < dates.size(); i++){

				Date_tb_client_VO date = dates.get(i);

				int date_code = date.getDate_code();

				ModifiedInfo info = dateDAO.selectUnMergedDate(conn, couple_id, date_code);

				//없는 데이트 정보이면 insert
				if(info == null){

					insertDate(conn, couple_id, email, date_code, date, upload_complete_list);
					
					insertLoc_routesAndLoc_photos(conn, couple_id, date_code, loc_route_map, loc_photo_map);

				//있는 데이트 정보이고, 내가 올린 데이트 정보가 아니고, merged가 되지 않았다면 merge
				} else if(!info.getLast_sync_email().equals(email) && info.getIs_merged().equals("n")){

					//모듈화 하기
					if(loc_route_map != null){
						ArrayList<Loc_route_tb_VO> server_loc_routeList = loc_route_DAO.selectAllToArrayList(conn, couple_id, date_code);
						ArrayList<Loc_route_tb_VO> client_loc_routeList = loc_route_map.get(date_code);


						ArrayList<Loc_photo_tb_VO> server_loc_photoList = loc_photo_DAO.selectAllToArrayList(conn, couple_id, date_code);
						HashMap<Integer, ArrayList<Loc_photo_tb_VO>> client_loc_photo_map = loc_photo_map.get(date_code);

						ArrayList<InsertNewLoc_routeAfterMerge> insertAfterMergeList = new ArrayList<InsertNewLoc_routeAfterMerge>();

						int client_loc_route_size = client_loc_routeList.size();
						int server_loc_route_size = server_loc_routeList.size();

						for(int j = 0; j < client_loc_route_size; j++){

							Loc_route_tb_VO client_loc_route = client_loc_routeList.get(j);

							if(client_loc_route.getIsSpecial().equals("")) continue; //isSpecial 장소가 아니면 다음 장소로

							int closest_distance = Integer.MAX_VALUE;
							int closest_server_loc_route_idx = -1;
							int closest_server_loc_no = -1;

							double client_loc_route_latitude = client_loc_route.getLatitude();
							double client_loc_route_longitude = client_loc_route.getLongitude();
							int client_loc_no = client_loc_route.getLoc_no();

							for(int k = 0; k < server_loc_route_size; k++){

								Loc_route_tb_VO server_loc_route = server_loc_routeList.get(k);

								if(server_loc_route.getIsSpecial().equals("")) continue;

								int distance = (int)DistanceCalculator.getDistanceFromLatLonInM(
										client_loc_route_latitude, client_loc_route_longitude, 
										server_loc_route.getLatitude(), server_loc_route.getLongitude());

								if(closest_distance >= distance){
									closest_distance = distance;
									closest_server_loc_route_idx = k;
									closest_server_loc_no = server_loc_route.getLoc_no();
								}

							}

							if(closest_distance <= (int)AppManager.SPECIAL_POINT_CHECK_RADIUS){
								
								if(DBManager.DEBUG_MODE){
									System.out.println("서버의 "+closest_server_loc_no+"와 클라의"+client_loc_no+"의 거리가 "
											+closest_distance+"이므로 그대로 합병");
								}
								
								
								addClientPhotoListToServerList(client_loc_photo_map.get(client_loc_no), 
										server_loc_photoList, closest_server_loc_no);
								
								//여기 엘스문을 나중에 밑에 주석 친 부분으로 옴겨야 한다.
							} else {

								if(DBManager.DEBUG_MODE){
									System.out.println("서버의 "+closest_server_loc_no+"와 클라의"+client_loc_no+"의 거리가 "
											+closest_distance+"이므로 쉬프트 ");
								}
								insertAfterMergeList.add(new InsertNewLoc_routeAfterMerge(
										client_loc_route, server_loc_routeList.get(closest_server_loc_route_idx)));

							}


						}
						//이쪽 부분으로 옴겨야 한다 이미있는거 머지하는거 끝낸 이후 새로운 머지작업을 넣는거를 처리해야한다.
						//그래야 중복이 되지 않기 때문.
						for(InsertNewLoc_routeAfterMerge obj : insertAfterMergeList){
							Loc_route_tb_VO client_loc_route = obj.getClient_loc_route();
							Loc_route_tb_VO server_loc_route = obj.getServer_loc_route();
						
							int server_loc_no = server_loc_route.getLoc_no();
							
							if(DBManager.DEBUG_MODE){
								System.out.println("client loc time : "+client_loc_route.getLoc_time());
								System.out.println("server loc time : "+server_loc_route.getLoc_time());
								
							}
							
							SimpleDateFormat loc_time_format = new SimpleDateFormat("hh:mm:ss");
							
							Date clientDate = loc_time_format.parse(client_loc_route.getLoc_time());
							Date serverDate = loc_time_format.parse(server_loc_route.getLoc_time());
							//client loc_route의 시간이 느리면
							if(clientDate.after(serverDate)){
								//server_loc_no를 하나 올린다. 
								server_loc_no++;
								
							} 
							//server_loc_no 이상의 loc_no를 가진 loc_photo 와 loc_route의 loc_no를 한개씩 증가시킨다(shift)
							increaseLoc_noGreaterOrEqualThan(server_loc_no, server_loc_photoList, server_loc_routeList);
							addClientPhotoListToServerList(client_loc_photo_map.get(client_loc_route.getLoc_no()), server_loc_photoList, server_loc_no);
							
							//client의 loc_route에 기존의 server_loc_no를 넣는다. 
							client_loc_route.setLoc_no(server_loc_no);
							//한개씩 밀고 난 자리에 client의 loc_route를 삽입한다.
							server_loc_routeList.add(client_loc_route);
							
						}

						try{
							conn.setAutoCommit(false);
							
							loc_photo_DAO.delete(conn, couple_id, date_code);
							loc_route_DAO.delete(conn, couple_id, date_code);
							
							for(Loc_route_tb_VO loc_route : server_loc_routeList){
								loc_route_DAO.insert(conn, loc_route, couple_id);
							}
							
							for(Loc_photo_tb_VO loc_photo : server_loc_photoList){
								loc_photo_DAO.insert(conn, loc_photo, couple_id);
							}
							
							merged_list.add(new MergedLoc_Info(date_code, server_loc_routeList, server_loc_photoList));
							
							dateDAO.updateDateIsMerged(conn, couple_id, email, date_code);
							
							conn.commit();
						}catch (SQLException e){
							
							try{
								conn.rollback();
							} catch (SQLException e1){
								e1.printStackTrace();
							}
							
						}
					
					}
					
					
					//db transaction 막고 delete & insert
					//insert후 merged_data 키에 JSONArray  값을 넣는다.

				}
			}

			result.setUpload_complete_list(upload_complete_list);
			result.setMerged_list(merged_list);


		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	private void increaseLoc_noGreaterOrEqualThan(int server_loc_no, ArrayList<Loc_photo_tb_VO> server_loc_photoList, ArrayList<Loc_route_tb_VO> server_loc_routeList){
		
		for(Loc_photo_tb_VO server_photo : server_loc_photoList){
			
			if(server_photo.getLoc_no() >= server_loc_no){
				server_photo.increaseLoc_no();
			}
			
		}
		
		for(Loc_route_tb_VO server_route : server_loc_routeList){
			
			if(server_route.getLoc_no() >= server_loc_no){
				server_route.increaseLoc_no();
			}
			
		}
		
	}
	
	private void addClientPhotoListToServerList(ArrayList<Loc_photo_tb_VO> client_loc_photoList, 
			ArrayList<Loc_photo_tb_VO> server_loc_photoList, int closest_server_loc_no){
		
		if(client_loc_photoList != null){
			
			for(Loc_photo_tb_VO loc_photo : client_loc_photoList){
				loc_photo.setLoc_no(closest_server_loc_no);
			}
			server_loc_photoList.addAll(client_loc_photoList);
		}
	}


	public JSONObject insertClientNewData(Connection conn, JSONObject json){
		if(!UserManager.getInstance().checkPassword(conn, json)) return null;

		JSONObject result = new JSONObject();
		String couple_id = "";
		String email = "";
		try
		{
			couple_id = json.getString("couple_id");
			email = json.getString("email");

			JSONArray dates = json.getJSONArray("date");
			JSONArray loc_routes = json.getJSONArray("loc_route");
			JSONArray loc_photos = json.getJSONArray("loc_photo");

			HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_route_map 
			= convertLoc_routeJSONArrayToHashMap(loc_routes, couple_id);
			HashMap<Integer, ArrayList<Loc_photo_tb_VO>> loc_photo_map 
			= convertLoc_photoJSONArrayToHashMap(loc_photos, couple_id);


			if(dates == null) return result;

			JSONArray upload_complete_list = new JSONArray();
			JSONArray merged_data = new JSONArray();

			for(int i = 0 ; i < dates.length(); i++){

				JSONObject jo = dates.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				ModifiedInfo info = dateDAO.selectUnMergedDate(conn, couple_id, date_code);

				if(info == null){

					if(dateDAO.insert(conn, new Date_tb_VO(
							date_code, jo.getInt("year"), jo.getInt("month"), 
							jo.getInt("day"), jo.getString("day_of_week"), jo.getString("start_time"), 
							email, "y", "n"	), couple_id) > 0){

						JSONObject tempJSON = new JSONObject().put("date_code", date_code);
						upload_complete_list.put(tempJSON);

					}

					ArrayList<Loc_route_tb_VO> loc_route_list = loc_route_map.get(date_code);

					if(loc_route_list != null){
						for(Loc_route_tb_VO vo : loc_route_list){
							try{
								loc_route_DAO.insert(conn, vo, couple_id);
							} catch (SQLException e){
								e.printStackTrace();
							}
						}
					}

					ArrayList<Loc_photo_tb_VO> loc_photo_list = loc_photo_map.get(date_code);

					if(loc_photo_list != null){
						for(Loc_photo_tb_VO vo : loc_photo_list){
							try{
								loc_photo_DAO.insert(conn, vo, couple_id);
							} catch (SQLException e){
								e.printStackTrace();
							}
						}
					}

					//insert
				}
				else if(!info.getLast_sync_email().equals(email) && info.getIs_merged().equals("n"))
				{
					//모듈화 하기

					ArrayList<Loc_route_tb_VO> server_loc_routeList = loc_route_DAO.selectAllToArrayList(conn, couple_id, date_code);
					ArrayList<Loc_route_tb_VO> client_loc_routeList = loc_route_map.get(date_code);

					//나중에 시간남으면 loc_no 를 key로 하는 HashMap을 만들면 더 좋을듯
					ArrayList<Loc_photo_tb_VO> client_Loc_photoList = loc_photo_map.get(date_code);
					ArrayList<Loc_photo_tb_VO> server_loc_photoList = loc_photo_DAO.selectAllToArrayList(conn, couple_id, date_code);

					ArrayList<InsertNewLoc_routeAfterMerge> insertAfterMergeList = new ArrayList<InsertNewLoc_routeAfterMerge>();

					int client_loc_route_size = client_loc_routeList.size();
					int server_loc_route_size = server_loc_routeList.size();

					for(int j = 0; j < client_loc_route_size; j++){

						Loc_route_tb_VO client_loc_route = client_loc_routeList.get(j);

						if(client_loc_route.getIsSpecial().equals("n")) continue; //isSpecial 장소가 아니면 다음 장소로

						double closest_distance = Double.MAX_VALUE;
						int closest_server_loc_route_idx = -1;

						double client_loc_route_latitude = client_loc_route.getLatitude();
						double client_loc_route_longitude = client_loc_route.getLongitude();
						int client_loc_no = client_loc_route.getLoc_no();

						for(int k = 0; k < server_loc_route_size; k++){

							Loc_route_tb_VO server_loc_route = server_loc_routeList.get(k);

							if(server_loc_route.getIsSpecial().equals("n")) continue;

							double distance = DistanceCalculator.getDistanceFromLatLonInM(
									client_loc_route_latitude, client_loc_route_longitude, 
									server_loc_route.getLatitude(), server_loc_route.getLongitude());

							if(closest_distance > distance){
								closest_distance = distance;
								closest_server_loc_route_idx = k;
							}

						}
						if(closest_distance < AppManager.SPECIAL_POINT_CHECK_RADIUS)
						{
							for(int l = 0; l < client_Loc_photoList.size(); l++){
								Loc_photo_tb_VO client_loc_photo = client_Loc_photoList.get(i);
								if(client_loc_no != client_loc_photo.getLoc_no()) continue;
								client_loc_photo.setLoc_no(closest_server_loc_route_idx);
								server_loc_photoList.add(client_loc_photo);
							}
							//여기 엘스문을 나중에 밑에 주석 친 부분으로 옴겨야 한다.
						}
						else
						{
							insertAfterMergeList.add(new InsertNewLoc_routeAfterMerge(
									client_loc_route, server_loc_routeList.get(closest_server_loc_route_idx)));
						}
					}
					//이쪽 부분으로 옴겨야 한다 이미있는거 머지하는거 끝낸 이후 새로운 머지작업을 넣는거를 처리해야한다.
					//그래야 중복이 되지 않기 때문.
				}
			}

			result.put("upload_complete", upload_complete_list);
			result.put("merged_data", merged_data);


		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;
	}



	public HashMap<Integer, ArrayList<Loc_route_tb_VO>> convertLoc_routeJSONArrayToHashMap(JSONArray loc_routes, String couple_id){

		HashMap<Integer, ArrayList<Loc_route_tb_VO>> map = new HashMap<Integer, ArrayList<Loc_route_tb_VO>>();

		if(loc_routes == null) return map;

		int temp_date_code = 0;

		ArrayList<Loc_route_tb_VO> list = new ArrayList<Loc_route_tb_VO>();

		int length = loc_routes.length();

		for(int i = 0; i < length; i++){

			try{

				JSONObject jo = loc_routes.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				if(temp_date_code != date_code && temp_date_code != 0){
					map.put(date_code, list);
					list = new ArrayList<Loc_route_tb_VO>();
				}

				temp_date_code = date_code;

				list.add(new Loc_route_tb_VO(
						date_code,
						jo.getInt("loc_no"),
						jo.getDouble("latitude"),
						jo.getDouble("longitude"),
						jo.getString("isSpecial"),
						jo.getString("loc_time")));

				if(i == length - 1) map.put(date_code, list);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return map;
	}



	public HashMap<Integer, ArrayList<Loc_photo_tb_VO>> convertLoc_photoJSONArrayToHashMap(JSONArray loc_photos, String couple_id){

		HashMap<Integer, ArrayList<Loc_photo_tb_VO>> map = new HashMap<Integer, ArrayList<Loc_photo_tb_VO>>();

		if(loc_photos == null) return map;

		int temp_date_code = 0;

		ArrayList<Loc_photo_tb_VO> list = new ArrayList<Loc_photo_tb_VO>();

		int length = loc_photos.length();

		for(int i = 0; i < length; i++){

			try{

				JSONObject jo = loc_photos.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				if(temp_date_code != date_code && temp_date_code != 0){
					map.put(date_code, list);
					list = new ArrayList<Loc_photo_tb_VO>();
				}

				temp_date_code = date_code;

				list.add(new Loc_photo_tb_VO(
						date_code,
						jo.getInt("loc_no"),
						jo.getString("name"),
						jo.getString("date"),
						jo.getString("photo_taken_email"),
						jo.getString("is_img_uploaded")));

				if(i == length - 1) map.put(date_code, list);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return map;
	}

	public JSONObject get_UnSynchronizedDataAfterMerge(Connection conn, JSONObject json){

		JSONObject result = new JSONObject();

		String couple_id = "";
		String email = "";

		try
		{
			couple_id = json.getString("couple_id");
			email = json.getString("email");


			JSONArray dates= dateDAO.selectUnSynchronizedDataAfterMerge(conn, couple_id, email);

			if(DBManager.DEBUG_MODE){
				System.out.println("get_UnSynchronizedDataAfterMerge : ");
				System.out.println("dates : "+dates.toString());
			}
			
			
			if(dates == null || dates.length() == 0) return null;

			result.put("date", dates);

			JSONArray loc_routes = new JSONArray();
			JSONArray loc_photos = new JSONArray();

			for(int i = 0; i < dates.length(); i++){

				JSONObject jo = dates.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				loc_route_DAO.selectLoc_routes(conn, loc_routes, couple_id, date_code);
				loc_photo_DAO.selectLoc_photos(conn, loc_photos, couple_id, date_code);

			}

			result.put("loc_route", loc_routes);
			result.put("loc_photo", loc_photos);

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	public JSONObject getModifiedData(Connection conn, JSONObject json){
		

		JSONObject result = new JSONObject();

		String couple_id = "";
		String email = "";

		try
		{
			couple_id = json.getString("couple_id");
			email = json.getString("email");


			JSONArray dates= dateDAO.selectModifiedDates(conn, couple_id, email);
			
			
//			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//			System.out.println(dates.toString());
//			
			
			

			if(DBManager.DEBUG_MODE){
				System.out.println("getModifiedData : ");
				System.out.println("dates : "+dates.toString());
			}
			
			
			if(dates == null || dates.length() == 0) return null;

			result.put("date", dates);

			JSONArray loc_routes = new JSONArray();
			JSONArray loc_photos = new JSONArray();

			for(int i = 0; i < dates.length(); i++){

				JSONObject jo = dates.getJSONObject(i);

				int date_code = jo.getInt("date_code");

				loc_route_DAO.selectLoc_routes(conn, loc_routes, couple_id, date_code);
				loc_photo_DAO.selectLoc_photos(conn, loc_photos, couple_id, date_code);

			}

			result.put("loc_route", loc_routes);
			result.put("loc_photo", loc_photos);

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	




	public int recentDate(Connection conn, JSONObject json){

		JSONObject result = new JSONObject();

		String couple_id = "";

		int date_code = 0;

		try
		{
			couple_id = json.getString("couple_id");
			date_code = dateDAO.recentDateCode(conn, couple_id);


		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return date_code;
	}


	public JSONObject getDBDatas(Connection conn, JSONObject json){

		if(!UserManager.getInstance().checkPassword(conn, json)) return null;

		JSONObject result = new JSONObject();

		String couple_id = "";
		int date_code = 0;
		int loc_no = 0 ;

		try{
			couple_id = json.get("couple_id").toString();
			date_code = Integer.valueOf(json.get("date_code").toString());
			loc_no = Integer.valueOf(json.get("loc_no").toString());

			JSONArray new_date =
					dateDAO.getNewDates(conn, couple_id, date_code);

			if(new_date != null && new_date.length() > 0 ) {
				if(DBManager.DEBUG_MODE) System.out.println("new date : "+new_date.toString());

				result.put("new_date", new_date);
			} else {
				if(DBManager.DEBUG_MODE)System.out.println("new date == null or size = 0");
			}

			JSONArray new_loc_route = 
					loc_route_DAO.selectLoc_routes(conn, couple_id, date_code, loc_no);

			//새로운 loc_route가 있을경우
			if(new_loc_route != null && new_loc_route.length() > 0 ){

				if(DBManager.DEBUG_MODE)System.out.println("new loc_route : "+new_loc_route.toString());
				else{
					if(DBManager.DEBUG_MODE)System.out.println("new loc_route == null or size = 0");
				}


				result.put("new_loc_route", new_loc_route);

			}

			int photo_no = Integer.valueOf(json.get("photo_no").toString());

			JSONArray new_loc_photo = 
					loc_photo_DAO.selectLoc_photos(conn, couple_id, photo_no);


			if(new_loc_photo != null && new_loc_photo.length() > 0 ){
				if(DBManager.DEBUG_MODE) System.out.println("new loc_photo : "+new_loc_photo.toString());
				else{
					if(DBManager.DEBUG_MODE)System.out.println("new loc_photo == null or size = 0");
				}
				result.put("new_loc_photo", new_loc_photo);
			}

			getRecentDatas(conn, couple_id, result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return result;


	}

	private void getRecentDatas(Connection conn, String couple_id, JSONObject result){
		//추가 부분

		int serverRecentDate_code = dateDAO.recentDateCode(conn, couple_id);
		int serverRecentLoc_no = loc_route_DAO.recentLoc_no(conn, couple_id, serverRecentDate_code);
		int serverRecentPhoto_no = loc_photo_DAO.recentPhoto_no(conn, couple_id);


		try{
			result.put("serverRecentDate_code", serverRecentDate_code);
			result.put("serverRecentLoc_no", serverRecentLoc_no);
			result.put("serverRecentPhoto_no", serverRecentPhoto_no);
		} catch (JSONException e){
			e.printStackTrace();
		}


	}


	public boolean insertLoc_routeToDeviceDB(Connection conn, JSONObject json){

		if(json == null) return false;

		String couple_id = "";

		try
		{
			couple_id = json.get("couple_id").toString();
			JSONArray jsonArray = (JSONArray)json.get("new_loc_route");

			if(jsonArray == null) return false;

			for(int i = 0; i < jsonArray.length(); i++){

				JSONObject record = (JSONObject)jsonArray.get(i);

				try{
					loc_route_DAO.insert(conn, 
							new Loc_route_tb_VO( 
									record.getInt("date_code"), 
									record.getInt("loc_no"), 
									record.getDouble("latitude"), 
									record.getDouble("longitude"), 
									record.getString("isSpecial"),
									record.getString("loc_time")), couple_id);
				} catch (SQLException e){
					e.printStackTrace();
				}

			}


		} catch (JSONException e){
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertLoc_photoToDeviceDB(Connection conn, JSONObject json){

		if(json == null) return false;

		String couple_id = "";

		try
		{
			couple_id = json.get("couple_id").toString();
			JSONArray jsonArray = (JSONArray)json.get("new_loc_photo");

			if(jsonArray == null) return false;

			for(int i = 0; i < jsonArray.length(); i++){

				JSONObject record = (JSONObject)jsonArray.get(i);

				try{
					loc_photo_DAO.insert(conn, 
							new Loc_photo_tb_VO(
									record.getInt("date_code"), 
									record.getInt("loc_no"), 
									record.getString("name"), 
									record.getString("date"),
									record.getString("photo_taken_email"),
									record.getString("is_img_uploaded")), couple_id);
				} catch (SQLException e){
					e.printStackTrace();
				}

			}


		} catch (JSONException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public boolean insertDateToDeviceDB(Connection conn, JSONObject json) {

		if(json == null) return false;

		String couple_id = "";

		try
		{
			couple_id = json.get("couple_id").toString();
			JSONArray jsonArray = (JSONArray)json.get("new_date");

			if(jsonArray == null) return false;

			for(int i = 0; i < jsonArray.length(); i++){

				JSONObject record = (JSONObject)jsonArray.get(i);

				//				dateDAO.insert(conn,
				//						new Date_tb_VO(
				//								record.getInt("date_code"), 
				//								couple_id,
				//								record.getInt("year"), 
				//								record.getInt("month"), 
				//								record.getInt("day"), 
				//								record.getString("day_of_week"),
				//								record.getString("start_time")));

			}



		} catch (JSONException e){
			e.printStackTrace();
			return false;
		}

		return true;


	}



}
