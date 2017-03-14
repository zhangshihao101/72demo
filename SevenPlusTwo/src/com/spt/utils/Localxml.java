package com.spt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Localxml {
	public static String share = "shtmts";
	public static SharedPreferences per;
	public static SharedPreferences.Editor editor;
	public static void save(Context context,String key,String content){
		editor = context.getSharedPreferences(Localxml.share, Activity.MODE_PRIVATE).edit();
		editor.putString(key, content);
		editor.commit();
	}
	public static String search(Context context,String key){
		per = context.getSharedPreferences(Localxml.share, Activity.MODE_PRIVATE);
		return per.getString(key, "");
	}
	
	public static void remove(Context context,String key){
		editor = context.getSharedPreferences(Localxml.share, Activity.MODE_PRIVATE).edit();
		editor.remove(key);
		editor.commit();
	}
}
