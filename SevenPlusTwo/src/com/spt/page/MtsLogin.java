package com.spt.page;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.utils.Log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsLogin extends BaseActivity {

	private ImageView iv_mts_back, iv_clear, iv_clear_urn;
	private EditText et_urn, et_psw;
	private Button btn_mts_login;
	private CheckBox cb_remind;
	private String mtsUserName;
	private String mtsPas;
	private boolean flag;
	private SharedPreferences spHome;
	private String SPTUserName;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_mts_login);
		super.onCreate(savedInstanceState);

//		Intent intent = getIntent();
//		mtsUserName = intent.getStringExtra("userName");
//		mtsPas = intent.getStringExtra("pas");
//		if (mtsUserName != null && mtsPas != null) {
//			dialog.show();
//			et_urn.setText(mtsUserName);
//			et_psw.setText(mtsPas);
//			String uri = MtsUrls.base + MtsUrls.login;
//			OkHttpManager.client
//					.newCall(
//							new Request.Builder().url(uri)
//									.post(new FormBody.Builder().add("USERNAME", mtsUserName).add("PASSWORD", mtsPas)
//											.add("terminalInfo", getTerminalInfo()).build())
//									.build())
//					.enqueue(new Callback() {
//
//						@Override
//						public void onResponse(Call call, Response response) throws IOException {
//							if (!response.isSuccessful()) {
//								return;
//							}
//							final String jsonStr = response.body().string();
//							new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//								@Override
//								public void run() {
//									dialog.dismiss();
//									try {
//										JSONObject jsonobject = new JSONObject(jsonStr);
//										String externalloginkey = jsonobject.optString("externalLoginKey");
//										String message = jsonobject.optString("_ERROR_MESSAGE_");
//										if (externalloginkey != null && !"".equals(externalloginkey)) {
//											Localxml.save(MtsLogin.this, "externalloginkey", externalloginkey);
//											Intent intent = new Intent(MtsLogin.this, MtsMainActivity.class);
//											startActivity(intent);
//										} else {
//											Toast.makeText(MtsLogin.this, message, Toast.LENGTH_SHORT).show();
//										}
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
//
//								}
//							});
//						}
//
//						@Override
//						public void onFailure(Call arg0, IOException arg1) {
//							dialog.dismiss();
//							new Handler(Looper.getMainLooper()).post(new Runnable() {
//								@Override
//								public void run() {
//									Toast.makeText(MtsLogin.this, "网络异常", Toast.LENGTH_LONG).show();
//								}
//							});
//
//						}
//					});
//		} else {
//
//		}

	}

	@Override
	protected void init() {

		spHome = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		SPTUserName = spHome.getString("userName", "");

		iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear_urn = (ImageView) findViewById(R.id.iv_clear_urn);
		et_urn = (EditText) findViewById(R.id.et_urn);
		et_psw = (EditText) findViewById(R.id.et_psw);
		btn_mts_login = (Button) findViewById(R.id.btn_mts_login);
		cb_remind = (CheckBox) findViewById(R.id.cb_remind);

		dialog = ProgressDialog.show(MtsLogin.this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
	}

	private String getTerminalInfo() {

		JSONObject allObj = new JSONObject();
		try {
			allObj.putOpt("terminalType", "PHONE");
			allObj.putOpt("isCoexist", "N");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return allObj.toString();
	}

	@Override
	protected void addClickEvent() {

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		iv_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_psw.setText("");
			}
		});

		iv_clear_urn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_urn.setText("");
			}
		});

		btn_mts_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				if (flag) {
//					if (MtsUrls.base.equals("https://www.metasolo.cn/")) {
//						String uri = MtsUrls.base + MtsUrls.create_metasolo;
//						dialog.show();
//						OkHttpManager.client
//								.newCall(
//										new Request.Builder().url(uri)
//												.post(new FormBody.Builder().add("username", "test_retail")
//														.add("password", "metasolo").add("mappingType", "Admin")
//														.add("metasoloValue",
//																et_urn.getText().toString() + "&"
//																		+ et_psw.getText().toString())
//								.add("sevenPlusTwoValue", SPTUserName).build()).build()).enqueue(new Callback() {
//
//							@Override
//							public void onResponse(Call call, Response response) throws IOException {
//								if (!response.isSuccessful()) {
//									return;
//								}
//								final String jsonStr = response.body().string();
//								new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//									@Override
//									public void run() {
//										dialog.dismiss();
//										try {
//											JSONObject object = new JSONObject(jsonStr);
//											String errorMsg = object.optString("_ERROR_MESSAGE_");
//											if (!"".equals(errorMsg)) {
//												Toast.makeText(MtsLogin.this, errorMsg, Toast.LENGTH_SHORT).show();
//											}
//										} catch (JSONException e) {
//											e.printStackTrace();
//										}
//
//									}
//								});
//							}
//
//							@Override
//							public void onFailure(Call arg0, IOException arg1) {
//								dialog.dismiss();
//								new Handler(Looper.getMainLooper()).post(new Runnable() {
//									@Override
//									public void run() {
//										Toast.makeText(MtsLogin.this, "网络异常", Toast.LENGTH_LONG).show();
//									}
//								});
//
//							}
//						});
//					} else {
//						String uri = MtsUrls.base + MtsUrls.create_metasolo;
//						dialog.show();
//						OkHttpManager.client
//								.newCall(
//										new Request.Builder().url(uri)
//												.post(new FormBody.Builder().add("username", "qje")
//														.add("password", "123456").add("mappingType", "Admin")
//														.add("metasoloValue",
//																et_urn.getText().toString() + "&"
//																		+ et_psw.getText().toString())
//								.add("sevenPlusTwoValue", SPTUserName).build()).build()).enqueue(new Callback() {
//
//							@Override
//							public void onResponse(Call call, Response response) throws IOException {
//								if (!response.isSuccessful()) {
//									return;
//								}
//								final String jsonStr = response.body().string();
//								new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//									@Override
//									public void run() {
//										dialog.dismiss();
//										try {
//											JSONObject object = new JSONObject(jsonStr);
//											String errorMsg = object.optString("_ERROR_MESSAGE_");
//											if (!"".equals(errorMsg)) {
//												Toast.makeText(MtsLogin.this, errorMsg, Toast.LENGTH_SHORT).show();
//											}
//										} catch (JSONException e) {
//											e.printStackTrace();
//										}
//
//									}
//								});
//							}
//
//							@Override
//							public void onFailure(Call arg0, IOException arg1) {
//								dialog.dismiss();
//								new Handler(Looper.getMainLooper()).post(new Runnable() {
//									@Override
//									public void run() {
//										Toast.makeText(MtsLogin.this, "网络异常", Toast.LENGTH_LONG).show();
//									}
//								});
//
//							}
//						});
//					}
//				}

				userLogin();
			}
		});

		et_psw.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!"".equals(et_psw.getText().toString())) {
					iv_clear.setVisibility(View.VISIBLE);
				} else {
					iv_clear.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

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

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		cb_remind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					flag = true;
				} else {
					flag = false;
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void userLogin() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.login)
				.post(new FormBody.Builder().add("USERNAME", et_urn.getText().toString())
						.add("PASSWORD", et_psw.getText().toString()).add("terminalInfo", getTerminalInfo()).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "登录" + "========" + jsonStr + "=============");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {

								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									String externalloginkey = jsonobject.optString("externalLoginKey");
									String message = jsonobject.optString("_ERROR_MESSAGE_");
									if (externalloginkey != null && !"".equals(externalloginkey)) {
										Localxml.save(MtsLogin.this, "externalloginkey", externalloginkey);
										Intent intent = new Intent(MtsLogin.this, MtsMainActivity.class);
										startActivity(intent);
									} else {
										Toast.makeText(MtsLogin.this, message, Toast.LENGTH_SHORT).show();
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
								//
								Toast.makeText(MtsLogin.this, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

}
