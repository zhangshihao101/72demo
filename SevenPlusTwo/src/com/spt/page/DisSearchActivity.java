package com.spt.page;

import java.util.ArrayList;
import java.util.List;

import com.spt.adapter.SearchHistoryAdapter;
import com.spt.controler.Constant;
import com.spt.db.HistoryHelper;
import com.spt.sht.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 搜索分销商品
 * 
 * @author lihongxuan
 *
 */
public class DisSearchActivity extends BaseActivity {

	private ImageView iv_dis_search_back;
	private EditText et_dis_search;
	private TextView tv_dis_search;
	private ListView lv_dis_search;
	private List<String> searchList;// 存储搜索关键字集合
	private SearchHistoryAdapter mAdapter;
	HistoryHelper helper;// 初始化数据库打开器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_dis_search);
		super.onCreate(savedInstanceState);

		initData();

	}

	@Override
	protected void init() {
		iv_dis_search_back = (ImageView) findViewById(R.id.iv_dis_search_back);
		et_dis_search = (EditText) findViewById(R.id.et_dis_search);
		tv_dis_search = (TextView) findViewById(R.id.tv_dis_search);
		lv_dis_search = (ListView) findViewById(R.id.lv_dis_search);
	}

	@Override
	protected void addClickEvent() {
		// 返回按钮
		iv_dis_search_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				DisSearchActivity.this.finish();
			}
		});
		// 搜索按钮
		tv_dis_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String search = et_dis_search.getText().toString().trim();
				Intent intent = new Intent(DisSearchActivity.this, DisSearchResultActivity.class);
				intent.putExtra("search", search);
				startActivity(intent);

				// 把搜索的关键字插入数据库
				insertHistory(search);

			}
		});
	}

	private void initData() {
		searchList = queryHistoryData();
		mAdapter = new SearchHistoryAdapter(this, searchList);
		lv_dis_search.setAdapter(mAdapter);
		lv_dis_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String search = searchList.get(position);
				Intent intent = new Intent(DisSearchActivity.this, DisSearchResultActivity.class);
				intent.putExtra("search", search);
				startActivity(intent);
			}
		});
	}

	/*
	 * 点击搜索按钮插入搜索关键字到数据库
	 */
	private void insertHistory(String str) {
		helper = new HistoryHelper(this);
		// 获取数据库写权限
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = 0;
		// 查询数据库，判断搜索的关键字是否已存在，如果存在则不插入，不存在则插入
		// 取回存放在history表的list
		List<String> list = queryHistorySql();
		for (int i = 0; i < list.size(); i++) {
			// 获取输入框的关键字，和已存在的数据对比，如果一样，count++
			if (list.get(i).equals(str)) {
				count++;
			}
		}
		// 如果count == 0， 说明没有重复的数据，可以插入数据库
		if (count == 0) {
			db.execSQL("insert into " + Constant.TABLENAME + " values(?,?)", new Object[] { null, str });
		}
		// 关闭数据库，释放资源
		db.close();
	}

	/*
	 * 查询数据库，存到集合当中，判断是否插入数据库
	 */
	private List<String> queryHistorySql() {
		helper = new HistoryHelper(this);
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + Constant.TABLENAME, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndex("h_name"));
			list.add(name);
			cursor.moveToNext();
		}
		// 关闭数据库，释放资源
		db.close();
		return list;
	}

	/*
	 * 查询数据库，返回集合给adapter
	 */
	private List<String> queryHistoryData() {
		helper = new HistoryHelper(this);
		List<String> searchList = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + Constant.TABLENAME, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String h_name = cursor.getString(cursor.getColumnIndex("h_name"));
			searchList.add(h_name);
			cursor.moveToNext();
		}
		db.close();
		return searchList;
	}

}
