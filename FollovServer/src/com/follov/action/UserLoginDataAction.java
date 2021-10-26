package com.follov.action;


import java.io.*;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.user.dto.*;
import com.google.android.gcm.server.*;

public class UserLoginDataAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		JSONParser parse = new JSONParser();
		
		JSONObject obj=(JSONObject)parse.parse(ActionUtil.getInputString(request));
		
		String email = (String)obj.get("email");
		String pwd = (String)obj.get("pw");
		String phone = (String)obj.get("phone");
		String regid = (String)obj.get("regid");
	
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try {
			conn = db.dbConn();
			JSONObject jo = new JSONObject();
			JSONObject responseObj = new JSONObject();
					// 클라이언트에게 응답(로그인 완료), 유저에 대한 데이터를 모두 담아서 보내줘야 하는가?
					// 나 또는 상대방의 전화번호도 여기서 저장해서 보내줘야하는가? 확인해서 코드 수정
			UserManager.getInstance().updateLogin(conn,email,regid,phone);
			User user = UserManager.getInstance().searchUser(conn, email, pwd);
			responseObj.put("birthYear", user.getBirthYear());
			responseObj.put("birthMonth", user.getBirthMonth());
			responseObj.put("birthDay", user.getBirthDay());
			responseObj.put("gender", user.getGender());
			String coupleemail = UserManager.getInstance().selectCoupleEmail(conn, email);
			if(coupleemail==null)
			{
				responseObj.put("couple", "x");
			}
			else
			{
				User coupleuser = UserManager.getInstance().searchColpleUser(conn, coupleemail);
				responseObj.put("couple", "o");
				responseObj.put("cbirthYear", coupleuser.getBirthYear());
				responseObj.put("cbirthMonth", coupleuser.getBirthMonth());
				responseObj.put("cbirthDay", coupleuser.getBirthDay());
				responseObj.put("cgender", coupleuser.getGender());
				responseObj.put("cemail", coupleemail);
				responseObj.put("cphone", coupleuser.getPhone());
				responseObj.put("cgcm", coupleuser.getGcmid());
				
				String coupleid = UserManager.getInstance().searchColpleid(conn, email);
				
				responseObj.put("coupleid", coupleid);
			
				sendGCM(coupleuser.getGcmid(),regid,phone);
			}
					
			jo.put("code", FollovCode.LOGIN_DATA_COMPLETE);
			jo.put("result", responseObj.toString());
			request.setAttribute("result", jo.toString());
		} finally {
			db.dbClose(conn);
		}
	}

	private void sendGCM(String registrationId,String regid,String phone) {
		System.out.println(registrationId + "로보내자");
		Sender sender = new Sender("AIzaSyBlZQVIai6Odl7R8M4BGW0r1aOAPFFiv2g");
		Message msg = new Message.Builder().addData("type", "logininfo")
					.addData("regid", regid).addData("phone", phone)
					.build();
		try {
				/* Result result = */sender.send(msg, registrationId, 3);
				// result 값으로 뭔가를 할 수 있을지....??
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
