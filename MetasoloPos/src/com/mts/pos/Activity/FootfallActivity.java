package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyDate;
import com.mts.pos.Common.Urls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FootfallActivity extends BaseActivity {

	private TextView tv_time;
	private Spinner sp_weather;
	private EditText et_footfall, et_remark;
	private Button btn_footfall_cancel, btn_footfall_confirm;
	private Intent intent;
	private String hour = "", time = "", weather = "", weatherNo = "", timeY = "";
	private Integer iHour = 0;
	private List<String> weatherList;
	private HashMap<String, String> weatherMap;
	private ArrayAdapter<String> weather_adapter;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_footfall);
		super.onCreate(inState);

		initView();

		intent = getIntent();
		hour = intent.getStringExtra("hour");

		iHour = Integer.valueOf(hour);

		time = (iHour - 1) + ":00 ~ " + iHour + ":00";
		timeY = (iHour - 1) + ":00~" + iHour + ":00";

		tv_time.setText(time);

		weatherList = new ArrayList<String>();
		weatherMap = new HashMap<String, String>();

		weatherList.add("晴");
		weatherList.add("多云");
		weatherList.add("阴");
		weatherList.add("雨");
		weatherList.add("雪");
		weatherList.add("雾霾");

		weatherMap.put("晴", "sunny");
		weatherMap.put("多云", "cloudy");
		weatherMap.put("阴", "lunar");
		weatherMap.put("雨", "rainy");
		weatherMap.put("雪", "snowy");
		weatherMap.put("雾霾", "Haze");

		weather_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, weatherList);
		weather_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_weather.setAdapter(weather_adapter);

		sp_weather.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				weather = weatherList.get(position);
				weatherNo = weatherMap.get(weather);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		btn_footfall_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_footfall.getText().toString().trim().equals("")) {
					Toast.makeText(FootfallActivity.this, "请输入客流量信息", Toast.LENGTH_SHORT).show();
				} else {
					statisticClient();
					finish();
				}

			}
		});

		btn_footfall_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "保存客流量信息==" + result);
			if (!result.equals("")) {
				Toast.makeText(FootfallActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void initView() {
		tv_time = (TextView) findViewById(R.id.tv_time);
		sp_weather = (Spinner) findViewById(R.id.sp_weather);
		et_footfall = (EditText) findViewById(R.id.et_footfall);
		et_remark = (EditText) findViewById(R.id.et_remark);
		btn_footfall_cancel = (Button) findViewById(R.id.btn_footfall_cancel);
		btn_footfall_confirm = (Button) findViewById(R.id.btn_footfall_confirm);
	}

	private void statisticClient() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(FootfallActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("createdDate", MyDate.getDate()));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(FootfallActivity.this, "storeid")));
		nameValuePair.add(new BasicNameValuePair("timeSlot", timeY));
		nameValuePair.add(new BasicNameValuePair("weatherInfo", weatherNo));
		nameValuePair.add(new BasicNameValuePair("numberOfPeople", et_footfall.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("commentText", et_remark.getText().toString()));
		
		nameValuePair.add(new BasicNameValuePair("storeName", Localxml.search(FootfallActivity.this, "storename")));
		nameValuePair.add(new BasicNameValuePair("userLoginId", Localxml.search(FootfallActivity.this, "username")));

		getTask(FootfallActivity.this, Urls.base + Urls.statistic_client, nameValuePair, "0");
	}

}
