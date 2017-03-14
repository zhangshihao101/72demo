package com.spt.page;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.spt.utils.SignUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

	private EditText etName;
	private EditText etPsw;
	private Button btnLogin;
	private TextView tvFind, tv_login_register;
	private HashMap<String, String> param_login, param_token;
	private ProgressDialog progressDialog;
	private CheckBox cbRemainName;
	private SharedPreferences sp;
	private Editor editor;
	private boolean isRemain = true;
	private boolean isSplash;
	private String name;
	private String psw;
	private String device_token;
	private String acc_token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.login);
		super.onCreate(savedInstanceState);

		acc_token = sp.getString("accessToken", "");
		if (!acc_token.equals("") && sp.contains("userName")) {
			checkIsLogin();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			progressDialog.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		sp = LoginActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		if (sp.contains("userName")) {
			name = sp.getString("userName", "");
			etName.setText(name);
		}
		if (sp.contains("userPsw")) {
			psw = sp.getString("userPsw", "");
			etPsw.setText(psw);
		}
		super.onStart();
	}

	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		this.etName = (EditText) findViewById(R.id.et_login_userName);
		this.etPsw = (EditText) findViewById(R.id.et_login_password);
		this.btnLogin = (Button) findViewById(R.id.btn_login_login);
		this.tv_login_register = (TextView) findViewById(R.id.tv_login_register);
		this.tvFind = (TextView) findViewById(R.id.tv_login_findPassword);
		this.progressDialog = ProgressDialog.show(LoginActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.param_login = new HashMap<String, String>();
		this.param_token = new HashMap<String, String>();
		this.cbRemainName = (CheckBox) findViewById(R.id.cb_login_remainName);
		this.sp = LoginActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		this.editor = sp.edit();
		if (sp.contains("userName")) {
			name = sp.getString("userName", "");
			etName.setText(name);
		}
		if (sp.contains("userPsw")) {
			psw = sp.getString("userPsw", "");
			etPsw.setText(psw);
		}
		if (sp.contains("device_token")) {
			device_token = sp.getString("device_token", "");
		}

		isSplash = sp.getBoolean("isSplash", false);
	}

	/**
	 * 添加点击事件
	 */
	@Override
	protected void addClickEvent() {
		btnLogin.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				name = etName.getText().toString();
				psw = etPsw.getText().toString();

				if (isRemain) {
					if (!"".equals(name)) {
						editor.putString("userName", name);
						editor.commit();
					}
					if (!"".equals(psw)) {
						editor.putString("userPsw", psw);
						editor.commit();
					}
				} else {
					if (sp.contains("userName")) {
						editor.remove("userName");
						editor.remove("userPsw");
						editor.commit();
					}
				}
				if ("".equals(device_token)) {
					for (int i = 0; i < 3; i++) {
						device_token = callUMengDeviceToken(LoginActivity.this);
					}
				}

				param_login.clear();
				param_login.put("client_id", "localhost");
				param_login.put("redirect_uri", "https://localhost:8443/");
				param_login.put("userLoginId", name);
				param_login.put("currentPassword", psw);

				progressDialog.show();
				// String uri =
				// "https://www.7jia2.com/index.php?pf=m_seller&app=member&act=login";
				String uri = MtsUrls.base + MtsUrls.sso_login + "?client_id=localhost&redirect_uri="
						+ URLEncoder.encode("https://localhost:8443/") + "&userLoginId=" + name + "&currentPassword="
						+ psw + "&sign=" + SignUtil.genSign(param_login, "localhost");

				OkHttpManager.client.newCall(new Request.Builder().url(uri).build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						progressDialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							public void run() {

								System.out.println("=====" + "结果" + "======" + jsonStr + "=======");
								try {
									JSONObject object = new JSONObject(jsonStr);
									String error = object.optString("error_code");
									if (error.equals("")) {
										String accessToken = object.optString("accessToken");
										String username = object.optString("user_login_id");
										String useremail = object.optString("email_address");
										String usertel = object.optString("telephone_number");
										String userid = object.optString("cas_uuid");

										editor.putString("accessToken", accessToken);
										editor.putString("username", username);
										editor.putString("useremail", useremail);
										editor.putString("usertel", usertel);
										editor.putString("userid", userid);
										editor.putBoolean("isUserDetailLoad", false);
										editor.putString("device_token", device_token);
										editor.commit();

										if (isSplash) {
											Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
											startActivity(intent);
										} else {
											Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
											startActivity(intent);
										}
									} else if (error.equals("108")) {
										Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
									} else if (error.equals("103")) {
										Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
									} else if (error.equals("102")) {
										Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
									}

									// String msg = object.optString("msg");
									// if ("0".equals(error)) {
									// JSONObject obj =
									// object.optJSONObject("data");
									// String user_id =
									// obj.getString("user_id");
									// String token = obj.getString("token");
									//
									// editor.putString("user_id", user_id);
									// editor.putString("token", token);
									// editor.putBoolean("isUserDetailLoad",
									// false);
									// editor.putString("device_token",
									// device_token);
									// editor.commit();
									//
									// boolean isSplash =
									// sp.getBoolean("isSplash", false);
									// if (isSplash) {
									// Intent intent = new
									// Intent(LoginActivity.this,
									// NewHomeActivity.class);
									// startActivity(intent);
									// } else {
									// Intent intent = new
									// Intent(LoginActivity.this,
									// WelcomeActivity.class);
									// startActivity(intent);
									// }
									// } else {
									// etPsw.setText("");
									// MyUtil.ToastMessage(LoginActivity.this,
									// msg);
									// }
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						progressDialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(LoginActivity.this, "请求失败，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

					}
				});

				// OkHttpManager.client
				// .newCall(new Request.Builder().url(uri)
				// .post(new FormBody.Builder().add("user_name", name)
				// .add("password", MyUtil.stringToBase64(psw)).build())
				// .build())
				// .enqueue(new Callback() {
				//
				// @Override
				// public void onResponse(Call call, Response response) throws
				// IOException {
				// if (!response.isSuccessful()) {
				// return;
				// }
				// final String jsonStr = response.body().string();
				// System.out.println("=======" + "登录结果" + "========" + jsonStr
				// + "=============");
				//
				// new Handler(Looper.getMainLooper()).post(new Runnable() {
				//
				// @Override
				// public void run() {
				// progressDialog.dismiss();
				// try {
				// JSONObject object = new JSONObject(jsonStr);
				// String error = object.optString("error");
				// String msg = object.optString("msg");
				// if ("0".equals(error)) {
				// JSONObject obj = object.optJSONObject("data");
				// String user_id = obj.getString("user_id");
				// String token = obj.getString("token");
				//
				// editor.putString("user_id", user_id);
				// editor.putString("token", token);
				// editor.putBoolean("isUserDetailLoad", false);
				// editor.putString("device_token", device_token);
				// editor.commit();
				//
				// boolean isSplash = sp.getBoolean("isSplash", false);
				// if (isSplash) {
				// Intent intent = new Intent(LoginActivity.this,
				// NewHomeActivity.class);
				// startActivity(intent);
				// } else {
				// Intent intent = new Intent(LoginActivity.this,
				// WelcomeActivity.class);
				// startActivity(intent);
				// }
				// } else {
				// etPsw.setText("");
				// MyUtil.ToastMessage(LoginActivity.this, msg);
				// }
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// }
				// });
				// }
				//
				// @Override
				// public void onFailure(Call arg0, IOException arg1) {
				// new Handler(Looper.getMainLooper()).post(new Runnable() {
				//
				// @Override
				// public void run() {
				// progressDialog.dismiss();
				// Toast.makeText(LoginActivity.this, "网络错误",
				// Toast.LENGTH_SHORT).show();
				// }
				// });
				// }
				// });
			}
		});

		tvFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent find = new Intent(LoginActivity.this, ValidatePhoneActivity.class);
				startActivity(find);
			}
		});

		cbRemainName.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isRemain = true;
				} else {
					isRemain = false;
				}
			}
		});

		tv_login_register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_login_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				etName.setText("");
				etPsw.setText("");
				editor.clear();
				editor.commit();
			}
		});

	}

	private void checkIsLogin() {
		param_token.clear();
		param_token.put("access_token", acc_token);
		param_token.put("client_id", "localhost");

		String url = MtsUrls.base + MtsUrls.sso_profile + "?access_token=" + acc_token + "&client_id=localhost&sign="
				+ SignUtil.genSign(param_token, "localhost");

		OkHttpManager.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String jsonStr = response.body().string();

				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						System.out.println("=======" + "跳过======" + jsonStr + "=====");
						try {
							JSONObject object = new JSONObject(jsonStr);
							String error = object.optString("error_code");
							if (error.equals("")) {
								String accessToken = object.optString("accessToken");
								String username = object.optString("user_login_id");
								String useremail = object.optString("email_address");
								String usertel = object.optString("telephone_number");
								String userid = object.optString("cas_uuid");

								editor.putString("accessToken", accessToken);
								editor.putString("username", username);
								editor.putString("useremail", useremail);
								editor.putString("usertel", usertel);
								editor.putString("userid", userid);
								editor.putBoolean("isUserDetailLoad", false);
								editor.putString("device_token", device_token);
								editor.commit();

								// if (isSplash) {
								Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
								startActivity(intent);
								// } else {
								// Intent intent = new
								// Intent(LoginActivity.this,
								// WelcomeActivity.class);
								// startActivity(intent);
								// }
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
						Toast.makeText(LoginActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});

			}
		});

	}

}
