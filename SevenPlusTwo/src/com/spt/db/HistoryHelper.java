package com.spt.db;

import com.spt.controler.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryHelper extends SQLiteOpenHelper{
	
	private static final int DB_VERSION = 1;

	public HistoryHelper(Context context) {
		super(context, Constant.DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Constant.CREATE_HISTORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
