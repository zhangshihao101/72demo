package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class HavenoCompanyActivity extends FragmentActivity {

	private ImageView iv_edit_back;
	private AutoCompleteTextView atv_company;
	private TextView tv_creat_company;
	private SharedPreferences sp;
	private ArrayAdapter<String> arrayAdapter;
	private List<String> listCompanyName;
	private HashMap<String, String> mapCompany;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_haveno_company);
		super.onCreate(arg0);

		initViews();

		getCompany();

		atv_company.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				// 公司ID传给公司详情
				String companyId = mapCompany.get(atv_company.getText().toString());
				if (companyId == null) {
					Toast.makeText(HavenoCompanyActivity.this, "没有您搜索的公司", Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(HavenoCompanyActivity.this, CompanyDetailActivity.class);
					intent.putExtra("partyId", companyId);
					startActivity(intent);
				}

				return true;
			}
		});

		tv_creat_company.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_creat_company.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					Intent intent = new Intent(HavenoCompanyActivity.this, MtsPerfectionMessageActivity.class);
					startActivityForResult(intent, 500);
					finish();
				}
			}
		});

		iv_edit_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 500) {
			if (resultCode == 100) {
				// Intent intent = new Intent(HavenoCompanyActivity.this,
				// MyCompanyActivity.class);
				// startActivity(intent);
				setResult(100);

				finish();
			}
		}
	}

	private void getCompany() {
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_company)
				.post(new FormBody.Builder().add("partyId", sp.getString("partyId", ""))
						.add("accessToken", sp.getString("accessToken", "")).add("viewIndex", "1").add("viewSize", "5")
						.add("isPage", "333").build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "获取公司" + "========" + jsonStr + "=============");
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("list");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = (JSONObject) array.get(i);
										String companyName = obj.optString("companyName");
										String companyId = obj.optString("partyId");
										listCompanyName.add(companyName);
										mapCompany.put(companyName, companyId);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(HavenoCompanyActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

					}
				});

	}

	private void initViews() {
		iv_edit_back = (ImageView) findViewById(R.id.iv_edit_back);
		atv_company = (AutoCompleteTextView) findViewById(R.id.atv_company);
		tv_creat_company = (TextView) findViewById(R.id.tv_creat_company);
		sp = HavenoCompanyActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		listCompanyName = new ArrayList<String>();
		arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, listCompanyName);
		atv_company.setAdapter(arrayAdapter);
		mapCompany = new HashMap<String, String>();
	}

}
