package com.mts.pos.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

	static final String KEY_ROWID = "_id";
	static final String KEY_MONEY = "money";
	static final String KEY_NUM = "num";
	static final String KEY_USERID = "userid";
	static final String KEY_NAME = "name";
	static final String KEY_SIGN = "sign";
	static final String KEY_LOGO = "logo";
	static final String KEY_SORT = "sort";
	static final String TAG = "DBAdapter";

	static final String DATABASE_NAME = "MyDB";
	static final String DATABASE_TABLE = "contacts";
	static final int DATABASE_VERSION = 1;

	static final String DATABASE_CREATE = "create table contacts( _id integer primary key , money text, sign text, logo text, num text, sort text, name text, userid text);";
	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DBAdapter(Context cxt) {
		this.context = cxt;
		DBHelper = new DatabaseHelper(context);
	}

	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS contacts");
			onCreate(db);

		}

	}

	// open the database
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// close the database
	public void close() {
		DBHelper.close();
	}

	// 插入数据
	public long insertContact(String userid, String name, String sign, String logo, String money, int num, String sort) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERID, userid);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_SIGN, sign);
		initialValues.put(KEY_LOGO, logo);
		initialValues.put(KEY_MONEY, money + "");
		initialValues.put(KEY_NUM, num + "");
		initialValues.put(KEY_SORT, sort);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// 查询
	public Cursor get(String sql) {
		Cursor mCursor = db.rawQuery(sql, null);
		// 如果指针存在，就把指针移到第一个条目上
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	// 删除指定数据
	public boolean deleteContact(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// 删除所有数据
	public boolean deleteAllContact() {
		db.delete(DATABASE_TABLE, null, null);
		return false;

	}

	// 遍历所有数据
	public Cursor getAllContacts() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_USERID, KEY_NAME, KEY_SIGN, KEY_LOGO, KEY_MONEY, KEY_NUM, KEY_SORT }, null, null, null, null, KEY_SORT);
	}

	// 浏览指定数据
	public Cursor getContact(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_USERID, KEY_NAME, KEY_SIGN, KEY_LOGO, KEY_MONEY, KEY_NUM, KEY_SORT }, KEY_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	// updates a contact
	public boolean updateContact(long rowId, String userid, String name, String sign, String logo, int money, int num, String sort) {
		ContentValues args = new ContentValues();
		args.put(KEY_USERID, userid);
		args.put(KEY_NAME, name);
		args.put(KEY_SIGN, sign);
		args.put(KEY_LOGO, logo);
		args.put(KEY_MONEY, money + "");
		args.put(KEY_NUM, num + "");
		args.put(KEY_SORT, sort);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

}