package com.follov.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;


public class CheckEmailAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		String email = ActionUtil.getInputString(request);
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try
		{
			conn = db.dbConn();
			boolean check = UserManager.getInstance().dupIdCheck(conn,email);
			JSONObject obj = new JSONObject();
			
			if(check==true)
			{
				obj.put("code", FollovCode.EMAIl_CHECK_DUPLCATED);
				obj.put("result", "");
				request.setAttribute("result", obj.toString());
			}
			else
			{
				obj.put("code", FollovCode.EMAIl_CHECK_COMPLETION);
				obj.put("result", "");
				request.setAttribute("result", obj.toString());
			}
		}
		finally
		{
			db.dbClose(conn);
		}
	}
}
	
//	private void sendGCM(String registrationId, String title, String message) {
//		System.out.println(registrationId + "로보내자");
//		Sender sender = new Sender("AIzaSyBj2AHYqcI8DTRmP1_Y7ufbCg1gWoDtwIg");
//		Message msg = new Message.Builder().addData("title", title)
//				.addData("msg", message).build();
//		try {
//			/* Result result = */sender.send(msg, registrationId, 3);
//			// result 값으로 뭔가를 할 수 있을지....??
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

