package com.follov.servlet;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.json.*;

import com.follov.action.*;
import com.follov.daoManager.*;
import com.follov.db.*;
import com.follov.db.vo.*;

public class RecentDataAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		super.doGet(request, response);
		doPost(request, response);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		request.setCharacterEncoding("UTF-8");
//		response.setContentType("text/html;charset=UTF-8");
		//이쪽 다시한번보기 이것때매 object가 제대로 안가는듯
		response.setContentType("application/octet-stream");

	
		UnUploadedClientData unUploadedClientData = ActionUtil.getUnUploadedClientData(request);
//		JSONObject responseJSON = new JSONObject();
		
		updateIsModifiedToN(unUploadedClientData);
		
		//result 가 널인지 아닌지에 따라서 insert_success code failed code
		RecentDataResponse result = insertClientNewData(unUploadedClientData);
		
//		if(DBManager.DEBUG_MODE){
//			ArrayList<MergedLoc_Info> mergeList = result.getMerged_list();
//			ArrayList<Integer> uploadList = result.getUpload_complete_list();
//			
//			System.out.println("Merged Info");
//			for(MergedLoc_Info vo : mergeList){
//				
//				System.out.println("date_code: "+vo.getDate_code());
//				ArrayList<Loc_route_tb_VO> loc_route_list = vo.getLoc_route_list();
//				ArrayList<Loc_photo_tb_VO> loc_photo_list = vo.getLoc_photo_list();
//				
//				for(Loc_route_tb_VO route : loc_route_list){
//					System.out.println(route.toString());
//				}
//				for(Loc_photo_tb_VO photo : loc_photo_list){
//					System.out.println(photo.toString());
//				}
//
//			}
//			System.out.println("uploadList : ");
//			for(Integer num : uploadList){
//				System.out.println("date_code : "+num);
//			}
//		}
		
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		
		out.writeObject(result);
		out.flush();
		out.close();

	}

	// isModified 를 전부 n로 바꾼다.
	private void updateIsModifiedToN(UnUploadedClientData data)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		synchroManager.updateIsModifiedToN(conn, data);

		db.dbClose(conn);

	}

	private RecentDataResponse insertClientNewData(UnUploadedClientData data)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		RecentDataResponse result = synchroManager.insertClientNewData(conn, data);

		db.dbClose(conn);

		return result;
	}

	private JSONObject getAllData(JSONObject json)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		JSONObject result = synchroManager.getAllData(conn, json);

		db.dbClose(conn);

		return result;
	}

	private JSONObject getModifiedData(JSONObject json)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		JSONObject result = synchroManager.getModifiedData(conn, json);

		db.dbClose(conn);

		return result;
	}

	private int recentDate(JSONObject json)
	{
		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		int date_code = synchroManager.recentDate(conn, json);

		db.dbClose(conn);

		return date_code;

	}

	private boolean synchronizeDataBase(JSONObject json)
	{
		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		if (!UserManager.getInstance().checkPassword(conn, json))
		{
			db.dbClose(conn);
			return false;
		}

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		if (synchroManager.insertDateToDeviceDB(conn, json)
				&& synchroManager.insertLoc_routeToDeviceDB(conn, json)
				&& synchroManager.insertLoc_photoToDeviceDB(conn, json))
		{
			db.dbClose(conn);
			return true;
		}

		db.dbClose(conn);
		return false;
	}

	private boolean isAlreadySyncronizedDate(JSONObject json)
	{
		DBManager db = DBManager.getInstance();

		Connection conn = db.dbConn();

		if (!UserManager.getInstance().checkPassword(conn, json))
		{
			db.dbClose(conn);
			return false;
		}

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		boolean result = synchroManager.isAlreadySyncronizedDate(conn, json);

		db.dbClose(conn);

		return result;
	}

	private boolean insertClientDataToServerDB(JSONObject json)
	{

		if (DBManager.DEBUG_MODE)
		{
			System.out.println("received jsonString from client\n "
					+ json.toString());
		}

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		if (!UserManager.getInstance().checkPassword(conn, json))
		{
			db.dbClose(conn);
			return false;
		}

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		if (synchroManager.insertDateToDeviceDB(conn, json)
				&& synchroManager.insertLoc_routeToDeviceDB(conn, json)
				&& synchroManager.insertLoc_photoToDeviceDB(conn, json))
		{
			db.dbClose(conn);
			return true;
		}

		db.dbClose(conn);
		return false;

	}

	// private JSONArray

	private JSONObject getDBDatas(HttpServletRequest request,
			HttpServletResponse response, JSONObject json)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		JSONObject result = synchroManager.getDBDatas(conn, json);

		db.dbClose(conn);

		return result;
	}


}
