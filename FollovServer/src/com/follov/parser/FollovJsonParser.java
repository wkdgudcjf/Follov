package com.follov.parser;

import org.json.simple.JSONObject;

public class FollovJsonParser
{
	public static String parserCouple(String email,String regid,String coupleid)
	{
		JSONObject obj = new JSONObject();
		obj.put("email", email);
		obj.put("regid", regid);
		obj.put("coupleid", coupleid);
		return obj.toString();
	}
}
