package com.follov.action;


import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;

public class UserJoinAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		JSONParser parse = new JSONParser();
		
		JSONObject obj=(JSONObject)parse.parse(ActionUtil.getInputString(request));
		
		String email = (String)obj.get("email");
		String phone = (String)obj.get("phone");
		String password = (String)obj.get("pw");
		String regid = (String)obj.get("regid");
	
		if(phone.contains("+82"))
		{
			phone = phone.replace("+82", "0");
		}
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try
		{
			conn = db.dbConn();
			int check = UserManager.getInstance().insertUser(conn,email,password,phone,regid);
			JSONObject jo = new JSONObject();
			
			if(check==1)
			{
				jo.put("code", FollovCode.JOIN_COMPLETE);
				jo.put("result", "");
				request.setAttribute("result", jo.toString());
			}
			else
			{
				jo.put("code", FollovCode.JOIN_ERROR);
				jo.put("result", "");
				request.setAttribute("result", jo.toString());
			}
		}
		finally
		{
			db.dbClose(conn);
		}
	}
}
