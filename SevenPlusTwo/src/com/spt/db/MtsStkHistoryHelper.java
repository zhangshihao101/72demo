package com.spt.db;

import com.spt.controler.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MtsStkHistoryHelper extends SQLiteOpenHelper{

	private static final int DB_VERSION2 = 1;

	public MtsStkHistoryHelper(Context context) {
		super(context, Constant.DB_NAME2, null, DB_VERSION2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Constant.CREATE_HISTORY2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
