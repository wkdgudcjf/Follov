package com.follov.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.LocationManager;
import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.jdbc.dao.Date_tb_DAOImpl;

public class LocationAction implements ServletAction{

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		JSONTokener jtk = new JSONTokener(ActionUtil.getInputString(request));
		
		//org.json.simple.parser.JSONParser parse = new org.json.simple.parser.JSONParser();

		JSONObject requestJSON = (JSONObject) jtk.nextValue();

		JSONObject responseJSON = new JSONObject();

		switch(Integer.valueOf(requestJSON.get("code").toString())){

			case FollovCode.LOCATION_INSERT_REQUEST:
				responseJSON = insertLoc_route(request, response, requestJSON);
				break;
			case FollovCode.DATE_INSERT_REQUEST:
				responseJSON = insertDate(request, response, requestJSON);
				break;
			case FollovCode.DATE_END_TIME_UPDATE_REQUEST:
				responseJSON = updateDate_End_Time(request, response, requestJSON);
				break;
		}
		responseJSON.put("result", "");
		request.setAttribute("result", responseJSON.toString());
	}
	private JSONObject insertLoc_route(HttpServletRequest request, HttpServletResponse response, JSONObject json){

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();
		
		JSONObject responseJSON = new JSONObject();

		if(LocationManager.getInstance().insertLoc_route(conn, json)){
			try{
				responseJSON.put("code", FollovCode.LOCATION_INSERT_SUCCESS);
			} catch (JSONException e){
				e.printStackTrace();
			}

		}else{
			try{
				responseJSON.put("code", FollovCode.LOCATION_INSERT_FAILED);
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
		

		db.dbClose(conn);

		return responseJSON;

	}

	private JSONObject insertDate(HttpServletRequest request, HttpServletResponse response, JSONObject json){

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();


		JSONObject responseJSON = new JSONObject();

		if(LocationManager.getInstance().insertDate(conn, json)){
			try{
				responseJSON.put("code", FollovCode.DATE_INSERT_SUCCESS);
			} catch (JSONException e){
				e.printStackTrace();
			}

		}else{
			try{
				responseJSON.put("code", FollovCode.DATE_INSERT_FAILED);
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
		
		
		try{
			responseJSON.put("result", "");
		} catch (JSONException e){
			e.printStackTrace();
		}
		request.setAttribute("result", responseJSON.toString());


		db.dbClose(conn);

		return responseJSON;
	}
	
	public JSONObject updateDate_End_Time(HttpServletRequest request, HttpServletResponse response, JSONObject json){

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();


		JSONObject responseJSON = new JSONObject();

		if(LocationManager.getInstance().updateDate_End_Time(conn, json)){
			
			try{
				responseJSON.put("code", FollovCode.DATE_END_TIME_UPDATE_SUCCESS);
			} catch (JSONException e){
				e.printStackTrace();
			}

		}else{
			
			try{
				responseJSON.put("code", FollovCode.DATE_END_TIME_UPDATE_FAILED);
			} catch (JSONException e){
				e.printStackTrace();
			}
			
		}
		
		try{
			responseJSON.put("result", "");
		} catch (JSONException e){
			e.printStackTrace();
		}
		request.setAttribute("result", responseJSON.toString());


		db.dbClose(conn);

		return responseJSON;

	}
	
	

}
