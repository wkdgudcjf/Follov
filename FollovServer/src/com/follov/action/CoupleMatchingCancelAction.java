package com.follov.action;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.user.dto.MatchingMap;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;


public class CoupleMatchingCancelAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		String phone = ActionUtil.getInputString(request);
		
		boolean check = MatchingMap.getInstance().delete(phone);
		
		JSONObject obj = new JSONObject();
			
		if(check==true)
		{
			//상대방정보 result에 넣어서 보내주고, 내정보 gcm으로 보내야함.
			obj.put("code", FollovCode.CANCELOK);
			obj.put("result", "");
			request.setAttribute("result", obj.toString());
			//sendGCM("APA91bHoOxzdvBJ4VlBMnsH1iK69YNBJL0YEbgix3p0X-Ubou0VB6s6l9RWwfIYE_OCgi-1pzJQn0TX79OejCPKaUHYfjfJSQ8pA3X8gIfuRFvqo2Xm_PdiW4FWLmYILZa5GD6OLZtr__rLpTSf1CgxNPhrl-lulKA"
			//		,"dkdk","이게모야","gmgm");
		}
		else
		{
			obj.put("code", FollovCode.CANCELOK);
			obj.put("result", "");
			request.setAttribute("result", obj.toString());
			//sendGCM("APA91bHoOxzdvBJ4VlBMnsH1iK69YNBJL0YEbgix3p0X-Ubou0VB6s6l9RWwfIYE_OCgi-1pzJQn0TX79OejCPKaUHYfjfJSQ8pA3X8gIfuRFvqo2Xm_PdiW4FWLmYILZa5GD6OLZtr__rLpTSf1CgxNPhrl-lulKA"
			//		,"dkdk","이게모야","gmgm");
		}
	}


	private void sendGCM(String registrationId, String email,String regid,String coupleid) {
		System.out.println(registrationId + "로보내자");
		Sender sender = new Sender("AIzaSyBlZQVIai6Odl7R8M4BGW0r1aOAPFFiv2g");
		Message msg = new Message.Builder().addData("type", "coupleinfo")
					.addData("email", email).addData("regid", regid)
					.addData("coupleid", coupleid).build();
		try {
				/* Result result = */sender.send(msg, registrationId, 3);
				// result 값으로 뭔가를 할 수 있을지....??
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

