package com.follov.action;

import java.sql.*;

import javax.servlet.http.*;

import org.json.*;

import com.follov.daoManager.*;
import com.follov.db.*;
import com.follov.db.vo.*;

public class RecentDataAction implements ServletAction
{

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		UnUploadedClientData unUploadedClientData = ActionUtil.getUnUploadedClientData(request);
//		JSONObject responseJSON = new JSONObject();
		
		updateIsModifiedToN(unUploadedClientData);
		updateGet_data_after_mergeToY(unUploadedClientData);
		
		//result 가 널인지 아닌지에 따라서 insert_success code failed code
		RecentDataResponse result = insertClientNewData(unUploadedClientData);
		

		request.setAttribute("result", result);

	}
	
	private void updateGet_data_after_mergeToY(UnUploadedClientData data)
	{

		DBManager db = DBManager.getInstance();
		Connection conn = db.dbConn();

		SynchronizeManager synchroManager = SynchronizeManager.getInstance();

		synchroManager.updateGet_data_after_mergeToY(conn, data);

		db.dbClose(conn);

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
