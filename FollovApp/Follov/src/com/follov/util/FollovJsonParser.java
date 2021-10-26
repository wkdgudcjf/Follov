package com.follov.util;

import org.json.*;

import android.content.*;

import com.follov.*;
import com.follov.activity.*;
import com.follov.pref.*;

public class FollovJsonParser
{
	public static String join(String email,String pw,String phoneNum,String regid)
	{
		JSONObject join = new JSONObject();
		try
		{
			join.put("email", email);
			join.put("pw", pw);
			join.put("phone", phoneNum);
			join.put("regid", regid);
		} 
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}			
		return join.toString();
	}
	public static String login(String email,String pw)
	{
		JSONObject join = new JSONObject();
		try
		{
			join.put("email", email);
			join.put("pw", pw);
		} 
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}			
		return join.toString();
	}
	public static String profile(String email,int year, int month, int day,
			boolean isGirl)
	{
		JSONObject join = new JSONObject();
		try
		{
			join.put("email",email);
			join.put("year", year);
			join.put("month", month);
			join.put("day", day);
			join.put("isGirl", isGirl);
		} 
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}			
		return join.toString();
	}

	public static String requestCheck(String phoneNum, String phone)
	{
		JSONObject join = new JSONObject();
		try
		{
			join.put("myphone", phoneNum);
			join.put("yourphone", phone);
		} 
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}			
		return join.toString();
	}

	public static void coupleJson(String obj,String phone,Context context)
	{
		JSONTokener jtk = new JSONTokener(obj);
		try 
		{
			JSONObject jo = (JSONObject) jtk.nextValue();
			String email = jo.getString("email");
			String regid = jo.getString("regid");
			String coupleid = jo.getString("coupleid");
			FollovPref.saveInt("logincheck", 2, context); //커플매칭완료
    		FollovPref.saveString("coupleemail", email, context);
    		FollovPref.saveString("couplegcm", regid, context);
    		FollovPref.saveString("coupleid", coupleid, context);
    		FollovPref.saveString("couplephone",phone, context);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	public static void loginData(String obj, Context context) {
		JSONTokener jtk = new JSONTokener(obj);
		try 
		{
			JSONObject jo = (JSONObject) jtk.nextValue();
			int year = jo.getInt("birthYear");
			int month = jo.getInt("birthMonth");
			int day = jo.getInt("birthDay");
			String gender = jo.getString("cgender");
			FollovPref.saveInt("myyear", year, context); //로그인완료매칭완료
			FollovPref.saveInt("mymonth", month, context); 
			FollovPref.saveInt("myday", day, context); 
			if(gender.equals("f"))
			{
				FollovPref.saveBoolean("isGirl", false, context);
			}
			else
			{
				FollovPref.saveBoolean("isGirl", true, context);
			}
			String couple = jo.getString("couple");
			if(!couple.equals("x"))
			{
				int cyear = jo.getInt("cbirthYear");
				int cmonth = jo.getInt("cbirthMonth");
				int cday = jo.getInt("cbirthDay");
				String cgender = jo.getString("cgender");
				String cemail = jo.getString("cemail");
				String cphone = jo.getString("cphone");
				String cgcm = jo.getString("cgcm");
				String coupleid = jo.getString("coupleid");
				FollovPref.saveInt("coupleyear", cyear, context);
        		FollovPref.saveInt("couplemonth", cmonth, context);
        		FollovPref.saveInt("coupleday", cday, context);
				FollovPref.saveString("coupleemail", cemail, context);
	    		FollovPref.saveString("couplegcm", cgcm, context);
	    		FollovPref.saveString("coupleid", coupleid, context);
	    		FollovPref.saveString("couplephone",cphone, context);
				FollovPref.saveInt("logincheck", 3, context);
			}
			FollovPref.saveInt("logincheck", 1, context);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
}
