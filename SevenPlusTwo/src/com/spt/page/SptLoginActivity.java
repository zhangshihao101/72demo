package com.spt.page;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SptLoginActivity extends FragmentActivity {

	private ImageView iv_spt_back, iv_clear_urn, iv_clear_psw;
	private EditText et_urn, et_psw;
	private Button btn_dis_login;
	private ProgressDialog dialog;

	private SharedPreferences sp;
	private Editor editor;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_spt_login);
		super.onCreate(arg0);

		initViews();

		iv_spt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		iv_clear_urn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_urn.setText("");
			}
		});

		iv_clear_psw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_psw.setText("");
			}
		});

		et_urn.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (!"".equals(et_urn.getText().toString())) {
					iv_clear_urn.setVisibility(View.VISIBLE);
				} else {
					iv_clear_urn.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		et_psw.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!"".equals(et_psw.getText().toString())) {
					iv_clear_psw.setVisibility(View.VISIBLE);
				} else {
					iv_clear_psw.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		btn_dis_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(et_urn.getText().toString())) {
					Toast.makeText(SptLoginActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
				} else if ("".equals(et_psw.getText().toString())) {
					Toast.makeText(SptLoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
				} else {
					storeLogin();
				}
			}
		});
	}

	private void initViews() {
		iv_spt_back = (ImageView) findViewById(R.id.iv_spt_back);
		iv_clear_urn = (ImageView) findViewById(R.id.iv_clear_urn);
		iv_clear_psw = (ImageView) findViewById(R.id.iv_clear_psw);
		et_urn = (EditText) findViewById(R.id.et_urn);
		et_psw = (EditText) findViewById(R.id.et_psw);
		btn_dis_login = (Button) findViewById(R.id.btn_dis_login);

		dialog = ProgressDialog.show(SptLoginActivity.this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		sp = SptLoginActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		editor = sp.edit();
	}

	private void storeLogin() {
		dialog.show();
		String url = MyConstant.SERVICENAME + MyConstant.LOGIN;
		OkHttpManager.client
				.newCall(new Request.Builder().url(url)
						.post(new FormBody.Builder().add("user_name", et_urn.getText().toString()).add("version", "2.1")
								.add("password", MyUtil.stringToBase64(et_psw.getText().toString())).build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {

						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("======商城登录====" + jsonStr + "=====");
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									String error = object.optString("error");
									String msg = object.optString("msg");
									if ("0".equals(error)) {
										JSONObject obj = object.optJSONObject("data");
										String user_id = obj.getString("user_id");
										String token = obj.getString("token");

										editor.putString("user_id", user_id);
										editor.putString("token", token);
										editor.putBoolean("isUserDetailLoad", false);
										editor.commit();

										OkHttpManager.client.newCall(
												new Request.Builder().url(MyConstant.SERVICENAME + MyConstant.SHOPMSG)
														.post(new FormBody.Builder()
																.add("token", sp.getString("token", "")).build())
												.build()).enqueue(new Callback() {

											@Override
											public void onResponse(Call call, Response response) throws IOException {
												if (!response.isSuccessful()) {
													return;
												}
												final String jsonStr = response.body().string();
												System.out.println("======店铺信息===" + jsonStr + "====");
												new Handler(Looper.getMainLooper()).post(new Runnable() {

													@Override
													public void run() {
														dialog.dismiss();
														try {
															JSONObject jsonobject = new JSONObject(jsonStr);
															String error = jsonobject.optString("error");
															String msg = jsonobject.optString("msg");
															if (error.equals("1")) {
																Toast.makeText(SptLoginActivity.this, msg,
																		Toast.LENGTH_LONG).show();
															} else {
																JSONObject object = jsonobject.optJSONObject("data");
																String shopName = object.optString("store_name");
																String avatar = object.optString("store_logo");
																editor.putString("user_name", shopName);
																editor.putString("avatar", avatar);
																editor.commit();

																Intent homepage = new Intent(SptLoginActivity.this,
																		HomePageActivity.class);
																startActivity(homepage);
																finish();
															}
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
														Toast.makeText(SptLoginActivity.this, "网络错误，请检查网络",
																Toast.LENGTH_LONG).show();
													}
												});
											}
										});
									} else {
										Toast.makeText(SptLoginActivity.this, msg, Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
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
								Toast.makeText(SptLoginActivity.this, "网络错误，请检查网络", Toast.LENGTH_LONG).show();
							}
						});

					}
				});
	}

}
