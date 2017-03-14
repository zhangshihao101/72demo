package com.spt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * DBManager工具类
 * */
public class DBManager {
	private final int mBufferSize = 400000;
	private SQLiteDatabase mDatabase;
	private Context mContext;
	private String mDBName;
	private String mPackageName;
	private String mDBPath;
	private String mTableName;
	private int mResourceId;

	/**
	 * 构造方法
	 * */
	public DBManager(Context context, String dbName, String packageName, String tableName, int resourceId) {
		super();
		this.mContext = context;
		this.mDBName = dbName;
		this.mPackageName = packageName;
		this.mDBPath = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + mPackageName;// 在手机里存放数据库的位置(/data/data/mPackageName/mDBName)
		this.mTableName = tableName;
		this.mResourceId = resourceId;
	}

	/**
	 * 获取SQLiteDatabase对象
	 * */
	public SQLiteDatabase getDatabase() {
		return mDatabase;
	}

	/**
	 * 打开SQLiteDatabase
	 * */
	public void openDatabase() {
		this.mDatabase = this.openDatabase(mDBPath + "/" + mDBName);
	}

	/**
	 * 关闭SQLiteDatabase
	 * */
	public void closeDatabase() {
		this.mDatabase.close();
	}

	/**
	 * 查询数据库
	 * 
	 * @param db
	 *            表名
	 * @param columns
	 *            返回的列名
	 * @param selection
	 *            WHERE语句
	 * @param selectionArgs
	 *            WHERE语句限制条件
	 * @return Cursor 结果集游标
	 * */
	public Cursor select(SQLiteDatabase db, String[] columns, String selection, String[] selectionArgs) {
		Cursor cursor = db.query(mTableName, columns, selection, selectionArgs, null, null, null);
		return cursor;
	}

	/**
	 * 打开db
	 * 
	 * @param dbfile
	 *            db储存路径
	 * @return SQLiteDatabase
	 * */
	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			if (!isFileExist(dbfile)) {
				// 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
				InputStream is = this.mContext.getResources().openRawResource(mResourceId);// 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[mBufferSize];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}

			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return db;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 查询db是否存在
	 * 
	 * @param path
	 *            db储存路径
	 * @return true存在，false不存在
	 * */
	private boolean isFileExist(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}

		return false;
	}
}
