package com.follov.FollovGCM;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.follov.network.HttpPostMethod;
import com.follov.pref.FollovPref;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import android.app.Activity;
import android.content.Context;
public class FollovGCM 
{
	public static void gcmregister(Context context,String regId)
	{
		JSONObject memberInfo = new JSONObject();
		String email = FollovPref.getString("email", (Activity) context);

		try {
			memberInfo.put("email", email);
			memberInfo.put("key", regId);
		} catch (JSONException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//	String result = HttpPostMethod.excuteHttpPostMethod("gcm셋팅유알엘", memberInfo.toString());
	}
	public static void sendGCM(String registrationId, String title, String message) {
		//System.out.println(registrationId + "로보내자");
		Sender sender = new Sender("AIzaSyDhd2X7bBG-atobqCuKVZca_Bc1BZbB13I");
		Message msg = new Message.Builder().addData("title", title)
				.addData("msg", message).build();
		try {
			/* Result result = */sender.send(msg, registrationId, 3);
			// result 값으로 뭔가를 할 수 있을지....??
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
