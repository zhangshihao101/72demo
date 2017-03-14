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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ChangeWeatherActivity extends BaseActivity {

	private Spinner sp_weather;
	private Button btn_cancel, btn_enter;
	private Intent it;
	private List<String> weatherList;
	private HashMap<String, String> weatherMap;
	private ArrayAdapter<String> weather_adapter;
	private int position;
	private String wea = "", weather = "", weatherNo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_changeweather);
		super.onCreate(savedInstanceState);

		sp_weather = (Spinner) findViewById(R.id.sp_weather);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_enter = (Button) findViewById(R.id.btn_enter);

		it = getIntent();

		weatherList = new ArrayList<String>();
		weatherMap = new HashMap<String, String>();

		weatherMap.put("晴", "sunny");
		weatherMap.put("多云", "cloudy");
		weatherMap.put("阴", "lunar");
		weatherMap.put("雨", "rainy");
		weatherMap.put("雪", "snowy");
		weatherMap.put("雾霾", "Haze");

		weatherList.add("晴");
		weatherList.add("多云");
		weatherList.add("阴");
		weatherList.add("雨");
		weatherList.add("雪");
		weatherList.add("雾霾");

		weather_adapter = new ArrayAdapter<String>(ChangeWeatherActivity.this, R.layout.spinner_dropdown_item,
				weatherList);
		weather_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_weather.setAdapter(weather_adapter);

		for (int i = 0; i < weatherList.size(); i++) {
			if (weatherList.get(i).equals(it.getStringExtra("weather"))) {
				position = i;
			}
		}

		sp_weather.setSelection(position, true);

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

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				ClientActivity.clientData.get(Integer.valueOf(it.getStringExtra("position"))).setWeatherNo(weatherNo);
				ClientActivity.clientData.get(Integer.valueOf(it.getStringExtra("position"))).setWeatherNo(weatherMap.get(weatherList.get(position)));
				ClientActivity.clientData.get(Integer.valueOf(it.getStringExtra("position"))).setWeather(weather);
				ClientActivity.clientadapter.notifyDataSetChanged();
				
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(
						new BasicNameValuePair("externalLoginKey", Localxml.search(ChangeWeatherActivity.this, "externalloginkey")));
				nameValuePair.add(new BasicNameValuePair("createdDate", MyDate.getDate()));
				nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(ChangeWeatherActivity.this, "storeid")));
				nameValuePair.add(new BasicNameValuePair("timeSlot", it.getStringExtra("time")));
				nameValuePair.add(new BasicNameValuePair("weatherInfo", weatherNo));
				nameValuePair.add(new BasicNameValuePair("numberOfPeople", it.getStringExtra("people")));
				nameValuePair.add(new BasicNameValuePair("commentText", it.getStringExtra("remark")));

				getTask(ChangeWeatherActivity.this, Urls.base + Urls.statistic_client, nameValuePair, "0");
				
				finish();
			}
		});

	}
	
	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "保存客流量信息==" + result);
			if (!result.equals("")) {
				Toast.makeText(ChangeWeatherActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
