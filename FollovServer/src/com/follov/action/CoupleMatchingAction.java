package com.follov.action;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.parser.FollovJsonParser;
import com.follov.user.dto.MatchingMap;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;


public class CoupleMatchingAction implements ServletAction {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		JSONParser parse = new JSONParser();
		
		JSONObject jo=(JSONObject)parse.parse(ActionUtil.getInputString(request));
		
		String myphone = (String)jo.get("myphone");
		String yourphone = (String)jo.get("yourphone");
		
		boolean check = MatchingMap.getInstance().check(myphone, yourphone);
		
		JSONObject obj = new JSONObject();
		DBManager db = DBManager.getInstance();
		Connection conn = null;
		try
		{
			if(check==true)
			{
				conn = db.dbConn();
				ArrayList<String> myInfo = new ArrayList<String>();
				UserManager.getInstance().coupleInfomaion(conn,myphone,myInfo);
				ArrayList<String> yourInfo = new ArrayList<String>();
				UserManager.getInstance().coupleInfomaion(conn,yourphone,yourInfo);
				String Couple_Id = myInfo.get(0)+"_"+yourInfo.get(0);
				UserManager.getInstance().userCoupleIdUpdate(conn,myInfo.get(0),yourInfo.get(0),Couple_Id);
				UserManager.getInstance().couplematching(conn, myInfo.get(0),yourInfo.get(0),Couple_Id);
				sendGCM(yourInfo.get(1),myInfo.get(0),myInfo.get(1),Couple_Id);
				//상대방정보 result에 넣어서 보내주고, 내정보 gcm으로 보내야함.
				//디비에커플정보도 저장, 커플아이디도 같이 저장해서보내주자. gcm 에도 같이.
				obj.put("code", FollovCode.RESPONSE);
				obj.put("result", FollovJsonParser.parserCouple(yourInfo.get(0)
						, yourInfo.get(1),Couple_Id));
				request.setAttribute("result", obj.toString());
				MatchingMap.getInstance().delete(yourphone);
			}
			else
			{
				MatchingMap.getInstance().input(myphone, yourphone);
				obj.put("code", FollovCode.REQUEST);
				obj.put("result", "");
				request.setAttribute("result", obj.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			db.dbClose(conn);
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

