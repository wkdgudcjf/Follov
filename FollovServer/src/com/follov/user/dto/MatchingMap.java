package com.follov.user.dto;

import java.util.HashMap;

public class MatchingMap
{
	private static MatchingMap mt = new MatchingMap();
	private MatchingMap()
	{
		couplemap = new HashMap<String,String>();
	}
	private HashMap<String,String> couplemap;
	public static MatchingMap getInstance()
	{
		return mt;
	}
	public boolean check(String myPhone,String yourPhone)
	{
		String str = couplemap.get(yourPhone);
		if(myPhone.equals(str))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public String getObject(String phone)
	{
		return couplemap.get(phone);
	}
	public void input(String myPhone,String yourPhone)
	{
		couplemap.put(myPhone, yourPhone);
	}
	public boolean delete(String phone)
	{
		if(couplemap.remove(phone)==null)
		{
			return false;
		}
		return true;
	}
}
