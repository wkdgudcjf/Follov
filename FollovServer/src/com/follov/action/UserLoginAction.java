package com.follov.action;


import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.user.dto.*;

public class UserLoginAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		JSONParser parse = new JSONParser();
		
		JSONObject obj=(JSONObject)parse.parse(ActionUtil.getInputString(request));
		
		String email = (String)obj.get("email");
		String pwd = (String)obj.get("pw");
	
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try {
			conn = db.dbConn();
			boolean idCheck = UserManager.getInstance().dupIdCheck(conn,email);
			//JSONObject obj = new JSONObject();
			
			JSONObject responseObj = new JSONObject();
			if(idCheck == true) 
			{
				// 패스워드 확인하는 코드
				boolean pwdCheck = UserManager.getInstance().checkPassword(conn, email, pwd);
				if (pwdCheck == true) {
					// 클라이언트에게 응답(로그인 완료), 유저에 대한 데이터를 모두 담아서 보내줘야 하는가?
					// 나 또는 상대방의 전화번호도 여기서 저장해서 보내줘야하는가? 확인해서 코드 수정
//					User user = UserManager.getInstance().searchUser(conn, email, pwd);
//					responseObj.put("email", email);
//					responseObj.put("pwd", pwd);
//					responseObj.put("birthYear", user.getBirthYear());
//					responseObj.put("birthMonth", user.getBirthMonth());
//					responseObj.put("birthDay", user.getBirthDay());
//					responseObj.put("gender", user.getGender());
//					responseObj.put("gcmid", user.getGcmid());
//					
					// 상대방 데이터 가져와서 같이 보내야되는가?
										
					responseObj.put("code", FollovCode.LOGIN_COMPLETE);
				} else {
					// 클라이언트에게 응답(패스워드가 일치하지 않는다)
					responseObj.put("code", FollovCode.LOGIN_ERROR_PWD_NOT_VALID);
				}
			} else {
				// 클라이언트에게 응답(아이디가 존재하지 않는다)
				responseObj.put("code", FollovCode.LOGIN_ERROR_ID_NOT_FOUND);
			}
			responseObj.put("result", "");
			request.setAttribute("result", responseObj.toString());
		} finally {
			db.dbClose(conn);
		}
	}
}
