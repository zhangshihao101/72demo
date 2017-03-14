package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Constants;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyPostTask;
import com.mts.pos.Common.NetworkUtil;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.KeepBillsDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private EditText et_username, et_password;
	private Spinner spinner;
	private Button btn_login;
	private LinearLayout base_ll;
	private HashMap<String, String> posmap;
	private List<String> posName;
	private List<String> posTerminalId;
	private List<String> productStoreId;
	private String posnowname = "";
	private String productStorenowId = "";
	private String posTerminalnowId = "";
	private ArrayAdapter<String> arr_adapter;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		super.onCreate(inState);

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		spinner = (Spinner) findViewById(R.id.spinner);
		btn_login = (Button) findViewById(R.id.btn_login);
		base_ll = (LinearLayout) findViewById(R.id.base_ll);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(et_password.getWindowToken(), 1);
		et_password.setHintTextColor(0xffc9caca);
		et_username.setHintTextColor(0xffc9caca);
		// pos数据
		posmap = new HashMap<String, String>();
		posName = new ArrayList<String>();
		posTerminalId = new ArrayList<String>();
		productStoreId = new ArrayList<String>();

		// pos适配器
		arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, posName);
		// 设置样式
		arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinner.setAdapter(arr_adapter);
		base_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imm.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(et_password.getWindowToken(), 1);
			}
		});

		// 点击密码框，请求pos机
		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				loading.setVisibility(View.GONE);
				if (!et_username.getText().toString().equals("")) {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("USERNAME", et_username.getText().toString()));
					if (NetworkUtil.isConnected(LoginActivity.this)) {
						CommonTask commontask = new CommonTask(LoginActivity.this, Urls.base + Urls.search_pos,
								nameValuePair, "1");
						commontask.execute("");
					} else {
						Toast.makeText(LoginActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		// 监听spinner
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				posnowname = posName.get(position);
				productStorenowId = productStoreId.get(position);
				// TODO
				posTerminalnowId = posTerminalId.get(position);
				Localxml.remove(LoginActivity.this, "storeid");
				Localxml.save(LoginActivity.this, "storeid", productStorenowId);
				Localxml.save(LoginActivity.this, "pname", posnowname);

				Log.e("LOOK", "posnowname=" + posnowname);
				// MobclickAgent.onEvent(LoginActivity.this,
				// "LoginActivity_spinner_item");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkEdit()) {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("USERNAME", et_username.getText().toString()));
					nameValuePair.add(new BasicNameValuePair("PASSWORD", et_password.getText().toString()));
					getTask(LoginActivity.this, Urls.base + Urls.login, nameValuePair, "0");
				}
			}
		});
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "登陆==" + result);
			try {
				String code = new JSONObject(result).optString("_LOGIN_PASSED_");
				String message = new JSONObject(result).optString("_ERROR_MESSAGE_");
				String externalloginkey = new JSONObject(result).optString("externalLoginKey");
				// String externalloginkey = "EL738700464772";
				if (code.equals("TRUE")) {
					Localxml.save(LoginActivity.this, "username", et_username.getText().toString());
					Localxml.save(LoginActivity.this, "externalloginkey", externalloginkey);
					Localxml.save(LoginActivity.this, "posname", posnowname);
					Localxml.save(LoginActivity.this, "posid", posmap.get(posnowname));
					getStoreInfo();

					// Toast.makeText(LoginActivity.this, "登录成功",
					// Toast.LENGTH_SHORT).show();
					// Intent intent = new Intent(LoginActivity.this,
					// PayActivity.class);
					// startActivity(intent);
					// et_username.setText("");
					// et_password.setText("");
					// spinner.clearFocus();

				} else {
					if ("登录时发生下列错误：密码不正确。".equals(message)) {
						Toast.makeText(LoginActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
					} else if ("登录时发生下列错误：没有找到用户。".equals(message)) {
						Toast.makeText(LoginActivity.this, "没有此用户", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {

			Log.e("LOOK", "店铺==" + result);
			try {
				JSONArray resultlist = new JSONObject(result).optJSONArray("resultList");
				for (int i = 0; i < resultlist.length(); i++) {
					JSONObject jo = (JSONObject) resultlist.opt(i);
					Localxml.save(LoginActivity.this, "storename", jo.optString("storeName"));
					Localxml.save(LoginActivity.this, "storeaddress", jo.optString("address"));
					Localxml.save(LoginActivity.this, "storetelephone", jo.optString("telephone"));
					// Localxml.save(LoginActivity.this, "storeid",
					// jo.optString("productStoreId"));
				}
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this, PayActivity.class);
				startActivity(intent);
				et_username.setText("");
				et_password.setText("");
				spinner.clearFocus();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获得商店信息 作废
	 */
	public void getStoreInfo() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(LoginActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(LoginActivity.this, "storeid")));
		getTask(LoginActivity.this, Urls.base + Urls.store_info, nameValuePair, "1");
	}

	/**
	 * 判断两个输入框输入的东西符不符合规则
	 */
	private boolean checkEdit() {
		if (et_username.getText().toString().equals("")) {
			Toast.makeText(LoginActivity.this, "用户名输入不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (et_password.getText().toString().equals("")) {
			Toast.makeText(LoginActivity.this, "密码输入不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (posnowname.equals("")) {
			Toast.makeText(LoginActivity.this, "请选择pos机！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	class CommonTask extends MyPostTask {
		String which;

		public CommonTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("LOOK", "搜索pos机==" + result);
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {

			} else {
				posmap.clear();
				posName.clear();
				try {
					JSONArray resultlist = new JSONObject(result).optJSONArray("resultList");
					if (resultlist != null && resultlist.length() != 0) {
						for (int i = 0; i < resultlist.length(); i++) {
							JSONObject jo = (JSONObject) resultlist.opt(i);
							posmap.put(jo.optString("storeName"), jo.optString("posTerminalId"));
							posName.add(jo.optString("storeName"));
							productStoreId.add(jo.optString("productStoreId"));
							posTerminalId.add(jo.optString("posTerminalId"));
						}
						// posnowname = posName.get(0);
						// productStorenowId = productStoreId.get(0);
						// posTerminalnowId = posTerminalId.get(0);
						arr_adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
		// MobclickAgent.onPageStart("LoginActivity");
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
		// MobclickAgent.onPageEnd("LoginActivity");
	}

}
