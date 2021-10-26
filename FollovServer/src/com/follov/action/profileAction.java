package com.follov.action;


import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class profileAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		JSONParser parse = new JSONParser();
		
		JSONObject obj=(JSONObject)parse.parse(ActionUtil.getInputString(request));
		
		String email = (String)obj.get("email");
		int year = Integer.valueOf(obj.get("year").toString());
		int month = Integer.valueOf(obj.get("month").toString());
		int day = Integer.valueOf(obj.get("day").toString());
		boolean zender = (Boolean)obj.get("isGirl");
	
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try
		{
			conn = db.dbConn();
			UserManager.getInstance().updateProfile(conn,email,year,month,day,zender);
			JSONObject jo = new JSONObject();
			int check = 1;
			if(check==1)
			{
				//프로필도 gcm으로 상대방에게 보내줘야한다. 저장시킬거니까. 생활영역도 보내줘야함!.
				jo.put("code", FollovCode.PROFILE_COMPLETE);
				jo.put("result", "");
				request.setAttribute("result", jo.toString());
				String coupleEmail = UserManager.getInstance().selectCoupleEmail(conn,email);
				String coupleGcm = UserManager.getInstance().selectGcm(conn,coupleEmail);
				sendGCM(coupleGcm,year,month,day);
			}
			else
			{
				jo.put("code", FollovCode.PROFILE_ERROR);
				jo.put("result", "");
				request.setAttribute("result", jo.toString());
			}
		}
		finally
		{
			db.dbClose(conn);
		}
	}

	private void sendGCM(String registrationId, int year,int month,int day) {
		System.out.println(registrationId + "로보내자");
		Sender sender = new Sender("AIzaSyBlZQVIai6Odl7R8M4BGW0r1aOAPFFiv2g");
		Message msg = new Message.Builder().addData("type", "coupleprofile")
					.addData("year", String.valueOf(year)).addData("month", String.valueOf(month))
					.addData("day", String.valueOf(day)).build();
		try {
				/* Result result = */sender.send(msg, registrationId, 3);
				// result 값으로 뭔가를 할 수 있을지....??
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
