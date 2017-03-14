package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.controler.MyTitleBar;
import com.spt.utils.DBManager;
import com.spt.utils.MyUtil;

/**
 * 【地址管理】页
 * */
public class AdressManageActivity extends BaseActivity {

	private String dbName = "city.db"; // 保存的数据库文件名
	private String packageName = "com.example.sevenplustwo"; // 包名
	private String tableName = "lym_region"; // 待查询的表名
	private int resourceId = R.raw.city; // 待查询的表名
	private MyTitleBar mtbAdressManage;
	private TextView tvTitle;
	private ImageView ivLeft;
	private StringBuffer sbArea;
	private StringBuffer sbRegionId;
	private ListView lvContent;
	private DBManager dbHelper;
	private String[] columns = new String[] { "region_id", "region_name" };
	private String selection = " parent_id = ?";
	private String[] selectionArgs;
	private ArrayList<String> lstData;
	private HashMap<String, String> mapData;
	private TextView tvTip;
	private int times = 1;
	private LinearLayout llLeft;
	private String regionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.adressmanage);
		super.onCreate(savedInstanceState);
		initContent();
	}

	/**
	 * 初始化内容 获取一级城市
	 * */
	private void initContent() {
		initListView();
		callDB("1");
		refreshListView();
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbAdressManage = (MyTitleBar) findViewById(R.id.mtb_adress_title);
		this.tvTitle = mtbAdressManage.getTvTitle();
		this.ivLeft = mtbAdressManage.getIvLeft();
		this.tvTitle.setText("区域管理");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbAdressManage.getLlLeft();
		this.lvContent = (ListView) findViewById(R.id.lv_adress_content);
		this.lstData = new ArrayList<String>();
		this.mapData = new HashMap<String, String>();
		this.tvTip = (TextView) findViewById(R.id.tv_adress_tip);
		this.dbHelper = new DBManager(AdressManageActivity.this, dbName, packageName, tableName, resourceId);
		this.dbHelper.openDatabase();
		this.selectionArgs = new String[1];
		this.sbArea = new StringBuffer();
		this.sbRegionId = new StringBuffer();
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String tip = tvTip.getText().toString();
				if (!"".equals(tip)) {
					Intent intent = new Intent();
					intent.putExtra("area", tip);
					intent.putExtra("region_id", regionId);
					setResult(116, intent);
					finish();
				} else {
					finish();
				}

			}
		});
	}

	/**
	 * 访问数据库
	 * */
	private void callDB(String selectionArg) {
		this.mapData.clear();
		this.lstData.clear();
		ArrayList<String> ids = new ArrayList<String>();
		this.selectionArgs[0] = selectionArg;
		Cursor cursor = dbHelper.select(this.dbHelper.getDatabase(), this.columns, this.selection, this.selectionArgs);
		int count = cursor.getCount();
		int nameIndex = cursor.getColumnIndex("region_name");
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String name = cursor.getString(nameIndex);
			this.lstData.add(name);
		}
		int idIndex = cursor.getColumnIndex("region_id");
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String id = cursor.getString(idIndex);
			ids.add(id);
		}

		for (int i = 0; i < count; i++) {
			this.mapData.put(this.lstData.get(i), ids.get(i));
		}

	}

	/**
	 * 初始化适配器
	 * */
	private void initListView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdressManageActivity.this,
				android.R.layout.simple_list_item_1, this.lstData);
		this.lvContent.setAdapter(adapter);

		this.lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!(times == 3)) {
					String key = parent.getItemAtPosition(position).toString();
					tvTip.setText(sbArea.append(key + " "));
					String region_id = mapData.get(key);
					sbRegionId.append(region_id + " ");
					callDB(region_id); // 根据region_id获取二三级城市
					refreshListView();
					times++;
				} else {
					String key = parent.getItemAtPosition(position).toString();
					tvTip.setText(sbArea.append(key));
					String region_id = mapData.get(key);
					sbRegionId.append(region_id);
					regionId = sbRegionId.toString();
					callDB("1"); // 当选择第三级城市后，加载第一级城市，可重新选择城市
					refreshListView();
					times = 1;
					int length = sbArea.length();
					int length1 = sbRegionId.length();
					sbArea.delete(0, length);
					sbRegionId.delete(0, length1);
					String tip = tvTip.getText().toString();
					if (!"".equals(tip)) {
						Intent intent = new Intent();
						intent.putExtra("area", tip);
						intent.putExtra("region_id", regionId);
						setResult(116, intent);
						AdressManageActivity.this.finish();
					} else {
						MyUtil.ToastMessage(AdressManageActivity.this, "请选择区域");
					}
				}

			}
		});
	}

	/**
	 * 刷新适配器
	 * */
	@SuppressWarnings("unchecked")
	private void refreshListView() {
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) this.lvContent.getAdapter();
		adapter.notifyDataSetChanged();
	}
}
