package com.follov.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class FollovPref 
{
	public static void saveString(String key,String value,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString(key, value);
	    edit.commit();//저장 시작
	}
	public static void saveBoolean(String key,Boolean value,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		SharedPreferences.Editor edit = pref.edit();
		edit.putBoolean(key, value);
	    edit.commit();//저장 시작
	}
	public static void saveInt(String key,int value,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		SharedPreferences.Editor edit = pref.edit();
		edit.putInt(key, value);
	    edit.commit();//저장 시작
	}
	public static String getString(String key,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		String value = pref.getString(key, "");
		return value;
	}
	public static int getInt(String key,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		int value = pref.getInt(key, 0);
		return value;
	}
	public static boolean getBoolean(String key,Context context)
	{
		SharedPreferences pref = context.getSharedPreferences("FollovApp", 0);
		boolean value = pref.getBoolean(key, true);
		return value;
	}
}
