package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.LogisticsAdapter;
import com.spt.bean.LogisticsInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 物流信息类
 * 
 * @author lihongxuan
 *
 */
public class LogisticsInfoActivity extends BaseActivity {

	private ImageView iv_logistics_back;
	private TextView tv_logistics_name, tv_logistics_number;
	private ListView lv_logistics;
	private List<LogisticsInfo> mList;
	private LogisticsAdapter mAdapter;

	private String order_id;

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_logistics_info);
		super.onCreate(savedInstanceState);

		order_id = getIntent().getStringExtra("order_id");

		initData();

	}

	private void initData() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("数据正在加载,请稍等...");
		dialog.show();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.LOGISTICSINFO + "&order_id=" + order_id, params,
				new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						dialog.dismiss();
						Log.e("data", data);
						try {

							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							if (error.equals("0")) {
								String string = object.optString("data");
								JSONObject obj = new JSONObject(string);
								tv_logistics_name.setText(obj.optString("expTextName"));
								tv_logistics_number.setText(obj.optString("mailNo"));

								JSONArray array = obj.optJSONArray("data");
								for (int i = 0; i < array.length(); i++) {
									LogisticsInfo info = new LogisticsInfo();
									JSONObject obj2 = array.optJSONObject(i);
									info.setTime(obj2.optString("time"));
									info.setMsg(obj2.optString("context"));
									mList.add(info);
									mAdapter.notifyDataSetChanged();
								}

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						dialog.dismiss();
						Toast.makeText(LogisticsInfoActivity.this, "网络不好,请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected void init() {

		iv_logistics_back = (ImageView) findViewById(R.id.iv_logistics_back);
		tv_logistics_name = (TextView) findViewById(R.id.tv_logistics_name);
		tv_logistics_number = (TextView) findViewById(R.id.tv_logistics_number);
		lv_logistics = (ListView) findViewById(R.id.lv_logistics);

		mList = new ArrayList<LogisticsInfo>();
		mAdapter = new LogisticsAdapter(this, mList);
		lv_logistics.setAdapter(mAdapter);

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");

	}

	@Override
	protected void addClickEvent() {
		iv_logistics_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
