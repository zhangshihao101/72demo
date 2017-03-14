package com.spt.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyDate {
	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

	public static String getDate() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;
	}

	public static String getTime() {
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;
	}
	
	public static String getYesterday() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String date1 = format1.format(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));
		return date1;
	}

	public static String getSevenAgoDate() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String date1 = format1.format(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
		return date1;
	}
	
	public static String getThirtyAgoDate() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar=new GregorianCalendar();
		calendar.add(Calendar.DATE, -30);
		String date1 = format1.format(calendar.getTime());
		return date1;
	}

}
