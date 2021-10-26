package com.follov.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

import com.follov.daoManager.LocationManager;
import com.follov.daoManager.SynchronizeManager;
import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;

public class SynchronizeAction implements ServletAction
{

	// @Override
	// public void execute(HttpServletRequest request, HttpServletResponse
	// response)
	// throws Exception
	// {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		JSONTokener jtk = new JSONTokener(ActionUtil.getInputString(request));

		// org.json.simple.parser.JSONParser parse = new
		// org.json.simple.parser.JSONParser();

		JSONObject requestJSON = (JSONObject) jtk.nextValue();

		JSONObject responseJSON = new JSONObject();
		JSONObject result = new JSONObject();
		switch (Integer.valueOf(requestJSON.get("code").toString()))
		{

			case FollovCode.GET_ALL_DATA_FROM_SERVER_REQUEST:

				result = getAllData(requestJSON);
				responseJSON.put("code",
						FollovCode.GET_ALL_DATA_FROM_SERVER_SUCCESS);
				responseJSON.put("result", result.toString());

				break;

			case FollovCode.GET_MODIFIED_DATA_FROM_SERVER_REQUEST:

				result = getModifiedData(requestJSON);
				

				int recent_date_code = recentDate(requestJSON);

//				if (result == null)
//				{
//					responseJSON.put("code",
//							FollovCode.NO_MODIFIED_DATA_FROM_SERVER);
//					result = new JSONObject();
//					result.put("server_recent_date_code", recent_date_code);
//
//					responseJSON.put("result", result.toString());
//				}
//				else
//				{
				responseJSON.put("code",
						FollovCode.GET_MODIFIED_DATA_FROM_SERVER_SUCCESS);
				result.put("server_recent_date_code", recent_date_code);
				responseJSON.put("result", result.toString());

//				}

				break;

			case FollovCode.MERGED_COMPLETE:
				
				updateGet_Data_After_MergeToY(requestJSON);

				responseJSON.put("code",
						FollovCode.MERGED_COMPLETE_RECEIVED);
				
				result = new JSONObject();
				responseJSON.put("result", result.toString());
			
				break;
			case FollovCode.SEND_DATE_INFO_TO_SERVER_REQUEST:

				updateIsModifiedToN(requestJSON);

				result = insertClientNewData(requestJSON);

				break;

			case FollovCode.RECENT_SERVER_DATE_INFO_REQUEST:
				// result = recentDate(requestJSON);
				responseJSON.put("code",
						FollovCode.RECENT_SERVER_DATE_INFO_RECEIVED);
				responseJSON.put("result", result.toString());
				break;
			case FollovCode.SYNCHRONIZE_TO_SERVER_REQUEST:

				break;

			case FollovCode.SERVER_DB_SYNCHRONIZE_TO_CLIENT_REQUEST:

				int clientDate_code = Integer.valueOf(requestJSON.get(
						"date_code").toString());
				result = getDBDatas(request, response, requestJSON);

				if (result != null)
				{
					if (DBManager.DEBUG_MODE)
					{
						System.out.println("synchronize success");
						System.out.println("responseJSON : "
								+ result.toString());
					}
					responseJSON.put("code",
							FollovCode.SERVER_DB_SYNCHRONIZE_TO_CLIENT_SUCCESS);
					responseJSON.put("result", result.toString());
				}
				else
				{
					if (DBManager.DEBUG_MODE)
					{
						System.out.println("synchronize failed");
					}
					responseJSON.put("code",
							FollovCode.SERVER_DB_SYNCHRONIZE_TO_CLIENT_FAILED);
					responseJSON.put("result", "");
				}
				break;
			case FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_REQUEST:
				if (insertClientDataToServerDB(requestJSON))
				{
					responseJSON.put("code",
							FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_SUCCESS);
				}
				else
				{
					responseJSON.put("code",
							FollovCode.CLIENT_DB_SYNCHRONIZE_TO_SERVER_FAILED);
				}
				responseJSON.put("result", "");
				break;

		}

		request.setAttribute("result", responseJSON.toString());

	}
	
	private void updateGet_Data_After_MergeToY(JSONObject json){
		
		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();
		
		SynchronizeManager synchroManager = SynchronizeManager.getInstance();
		
		synchroManager.updateGet_Data_After_MergeToY(conn, json);
		
		db.dbClose(conn);
	}

	// isModified 를 전부 n로 바꾼다.
	private void updateIsModifiedToN(JSONObject json)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		synchroManager.updateIsModifiedToN(conn, json);

		db.dbClose(conn);

		// return result;
	}

	private JSONObject insertClientNewData(JSONObject json)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		JSONObject result = synchroManager.insertClientNewData(conn, json);

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
		
		if(!UserManager.getInstance().checkPassword(conn, json)) return null;
		

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		JSONObject getModifiedData = synchroManager.getModifiedData(conn, json);
		JSONObject get_data_after_merge = synchroManager.get_UnSynchronizedDataAfterMerge(conn, json);

		JSONObject returnJSON = new JSONObject();
		try
		{
			returnJSON.put("getModifiedData", getModifiedData);
			returnJSON.put("get_data_after_merge", get_data_after_merge);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		db.dbClose(conn);

		return returnJSON;
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
