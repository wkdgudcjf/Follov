package com.follov.daoManager;

import java.sql.*;

import org.json.*;

import com.follov.db.vo.*;
import com.follov.jdbc.dao.*;



public class LocationManager {

	private static LocationManager sInstance = new LocationManager();

	private LocationManager(){}

	public static LocationManager getInstance(){
		return sInstance;
	}


	public boolean insertDate(Connection conn, JSONObject json){

		if(!UserManager.getInstance().checkPassword(conn, json)) return false;

		Date_tb_DAOImpl dao = Date_tb_DAOImpl.getInstance();
//		try{
//			if(dao.insert(conn, new Date_tb_VO(
//					Integer.valueOf(json.get("date_code").toString()), 
//					json.get("couple_id").toString(), 
//					Integer.valueOf(json.get("year").toString()),  
//					Integer.valueOf(json.get("month").toString()), 
//					Integer.valueOf(json.get("day").toString()), 
//					json.get("day_of_week").toString(), 
//					json.get("start_time").toString())) > 0) return true;
//		} catch (NumberFormatException | JSONException e){
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return false;
	}

	public boolean insertLoc_route(Connection conn, JSONObject json){

		if(!UserManager.getInstance().checkPassword(conn, json)) return false;


		Loc_route_tb_DAOImpl dao = Loc_route_tb_DAOImpl.getInstance();
		try{
			if(dao.insert(conn, new Loc_route_tb_VO(
					Integer.valueOf(json.get("date_code").toString()),
					Integer.valueOf(json.get("loc_no").toString()),
					Double.valueOf(json.get("latitude").toString()),
					Double.valueOf(json.get("longitude").toString()),
					json.get("isSpecial").toString(), json.get("loc_time").toString()), json.getString("couple_id")) > 0) return true;
		} catch (NumberFormatException | JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return false;
	}

	public boolean updateDate_End_Time(Connection conn, JSONObject json){

		if(!UserManager.getInstance().checkPassword(conn, json)) return false;


		Date_tb_DAOImpl dao = Date_tb_DAOImpl.getInstance();		
		try{
			if(dao.updateEnd_Time(
					conn, 
					json.get("couple_id").toString(), 
					Integer.valueOf(json.get("date_code").toString()),
					json.get("end_time").toString()) > 0) return true;
		} catch (NumberFormatException | JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return false;

	}





}
