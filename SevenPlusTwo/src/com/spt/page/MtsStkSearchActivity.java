package com.spt.page;

import java.util.ArrayList;
import java.util.List;
import com.spt.adapter.MtsStkHistoryAdapter;
import com.spt.controler.Constant;
import com.spt.db.MtsStkHistoryHelper;
import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MtsStkSearchActivity extends FragmentActivity {

	private TextView tv_mts_stk_search_screen, tv_search_clear;
	private EditText et_mts_stk_search;
	private ImageView iv_mts_stk_search_back;
	private ListView lv_search_history;
	private List<String> mList;
	private MtsStkHistoryAdapter historyAdapter;
	private MtsStkHistoryHelper helper;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 点击清除记录按钮，刷新界面
				historyAdapter.refresh(queryHistoryData());
				break;
			// 点击删除指定位置，刷新界面
			case 2:
				historyAdapter.refresh(queryHistoryData());
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_stk_search);
		super.onCreate(arg0);

		initView();

		initData();

		initListener();

	}

	private void initData() {
		mList = queryHistoryData();
		historyAdapter = new MtsStkHistoryAdapter(this, mList);
		lv_search_history.setAdapter(historyAdapter);
		lv_search_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String search = mList.get(position);
				Intent intent = new Intent(MtsStkSearchActivity.this, MtsStkSearchResultActivity.class);
				intent.putExtra("search", search);
				startActivity(intent);
			}
		});
	}

	private void initListener() {
		iv_mts_stk_search_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		tv_mts_stk_search_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String search = et_mts_stk_search.getText().toString().trim();
				Intent intent = new Intent(MtsStkSearchActivity.this, MtsStkSearchResultActivity.class);
				intent.putExtra("search", search);
				startActivity(intent);

				// 把搜索的关键字插入数据库
				insertHistory(search);
			}
		});

		tv_search_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteHistory();
			}
		});

		historyAdapter.setDeleteOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// int position = (int) v.getTag();
				int position = Integer.parseInt(v.getTag().toString());
				helper = new MtsStkHistoryHelper(getApplicationContext());
				SQLiteDatabase db = helper.getWritableDatabase();
				db.execSQL("delete from " + Constant.TABLENAME2 + " where h_name=?",
						new Object[] { mList.get(position).toString() });

				new Thread(new Runnable() {

					@Override
					public void run() {
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);
					}
				}).start();
				db.close();
			}
		});

	}

	private void initView() {
		tv_search_clear = (TextView) findViewById(R.id.tv_search_clear);
		tv_mts_stk_search_screen = (TextView) findViewById(R.id.tv_mts_stk_search_screen);
		et_mts_stk_search = (EditText) findViewById(R.id.et_mts_stk_search);
		iv_mts_stk_search_back = (ImageView) findViewById(R.id.iv_mts_stk_search_back);
		lv_search_history = (ListView) findViewById(R.id.lv_search_history);
	}

	/*
	 * 删除历史记录
	 */
	private void deleteHistory() {
		helper = new MtsStkHistoryHelper(getApplicationContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		// 删除表。
		db.execSQL("delete from " + Constant.TABLENAME2);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 此处handler发送一个message，用来更新ui
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}).start();
		db.close();
	}

	/*
	 * 点击搜索按钮插入搜索关键字到数据库
	 */
	private void insertHistory(String str) {
		helper = new MtsStkHistoryHelper(this);
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
			db.execSQL("insert into " + Constant.TABLENAME2 + " values(?,?)", new Object[] { null, str });
		}
		// 关闭数据库，释放资源
		db.close();
	}

	/*
	 * 查询数据库，存到集合当中，判断是否插入数据库
	 */
	private List<String> queryHistorySql() {
		helper = new MtsStkHistoryHelper(this);
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + Constant.TABLENAME2, null);
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
		helper = new MtsStkHistoryHelper(this);
		List<String> searchList = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + Constant.TABLENAME2, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String h_name = cursor.getString(cursor.getColumnIndex("h_name"));
			searchList.add(h_name);
			cursor.moveToNext();
		}
		if (searchList.size() == 0) {
			tv_search_clear.setVisibility(View.GONE);
		}
		db.close();
		return searchList;
	}

}
