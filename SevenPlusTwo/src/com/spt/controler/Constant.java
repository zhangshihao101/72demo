package com.spt.controler;

/**
 * 存放常量的类
 */
public class Constant {

	// 搜索历史的数据库以及表的创建
	public static final String DB_NAME = "history.db";
	public static final String TABLENAME = "history";
	public static final String CREATE_HISTORY = "create table " + TABLENAME
			+ "(_id integer primary key autoincrement, h_name text not null)";
	
	// 源一云商库存明细搜索历史的数据库以及表的创建
	public static final String DB_NAME2 = "mtsstkhistory.db";
	public static final String TABLENAME2 = "mtsstkhistory";
	public static final String CREATE_HISTORY2 = "create table " + TABLENAME2
			+ "(_id integer primary key autoincrement, h_name text not null)";

}
