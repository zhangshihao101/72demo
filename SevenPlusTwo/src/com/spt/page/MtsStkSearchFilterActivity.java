package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsStkSearchFilterActivity extends FragmentActivity {

	private TextView tv_filter_storage, tv_filter_name, tv_filter_brand, tv_filter_code, tv_filter_number,
			tv_filter_search;
	private EditText  et_filter_name, et_filter_brand, et_filter_code, et_filter_number;
	
	private Spinner sp_filter_storage;
	private List<String> storeList;
	private ArrayAdapter<String> store_adapter;
	private HashMap<String, String> storeMap;

	private String facilityId = "";

	private static ProgressDialog dialog;

	private LinearLayout ll_filter, ll_filter_title;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_stk_filter);
		super.onCreate(inState);

		initView();

		initListener();
		
		initData();

	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(
						new Request.Builder().url(MtsUrls.base + MtsUrls.getFacilityInfo)
								.post(new FormBody.Builder().add("externalLoginKey",
										Localxml.search(MtsStkSearchFilterActivity.this, "externalloginkey")).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("facilityList");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.optJSONObject(i);
										storeList.add(obj.optString("facilityName"));
										storeMap.put(obj.optString("facilityName"), obj.optString("facilityId"));

									}

									storeList.add(0, "全部");
									storeMap.put("全部", "STORE_ALL");

									store_adapter = new ArrayAdapter<String>(MtsStkSearchFilterActivity.this,
											R.layout.simple_spinner_item, storeList);
									store_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
									sp_filter_storage.setAdapter(store_adapter);

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(MtsStkSearchFilterActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

	private void initListener() {
		
		sp_filter_storage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				facilityId = storeMap.get(storeList.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		tv_filter_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("facilityId", facilityId);
				intent.putExtra("productName", et_filter_name.getText().toString());
				intent.putExtra("brandName", et_filter_brand.getText().toString());
				intent.putExtra("unicode", et_filter_code.getText().toString());
				intent.putExtra("modelId", et_filter_number.getText().toString());
				setResult(0, intent);
				finish();
			}
		});

		ll_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});

		ll_filter_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});

	}

	private void initView() {
		tv_filter_storage = (TextView) findViewById(R.id.tv_filter_storage);
		tv_filter_name = (TextView) findViewById(R.id.tv_filter_name);
		tv_filter_brand = (TextView) findViewById(R.id.tv_filter_brand);
		tv_filter_code = (TextView) findViewById(R.id.tv_filter_code);
		tv_filter_number = (TextView) findViewById(R.id.tv_filter_number);
		tv_filter_search = (TextView) findViewById(R.id.tv_filter_search);
		sp_filter_storage = (Spinner) findViewById(R.id.sp_filter_storage);
		et_filter_name = (EditText) findViewById(R.id.et_filter_name);
		et_filter_brand = (EditText) findViewById(R.id.et_filter_brand);
		et_filter_code = (EditText) findViewById(R.id.et_filter_code);
		et_filter_number = (EditText) findViewById(R.id.et_filter_number);
		ll_filter = (LinearLayout) findViewById(R.id.ll_filter);
		ll_filter_title = (LinearLayout) findViewById(R.id.ll_filter_title);
		
		storeList = new ArrayList<String>();
		storeMap = new HashMap<String, String>();

		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(1);
		}
		return super.onKeyDown(keyCode, event);
	}
}
