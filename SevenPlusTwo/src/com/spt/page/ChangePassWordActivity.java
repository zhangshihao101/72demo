package com.spt.page;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 【设定新密码】页
 */
public class ChangePassWordActivity extends BaseActivity {
	private MyTitleBar mtbChangePsw;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Button btnNext;
	private HashMap<String, Object> param;
	private Intent iGetRequest; // get方法请求
	private boolean isGetServiceRunning;
	private BroadcastReceiver brGetHttp; // get方法广播
	private TextView tvCurrentAccount;
	private EditText etCurrentPsw;
	private EditText etNewPsw;
	private EditText etConfirmNewPsw;
	private Intent itFrom;
	private LinearLayout llLeft;
	private ProgressDialog progressDialog;
	private LinearLayout llRight;
	private SharedPreferences sp;

	/**
	 * 正则表达式:验证密码(不包含特殊字符)
	 */
	public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.changephone);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 添加点击事件
	 */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		this.btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// String strPhone = "";
				String strCurrentPsw = "";
				String strNew = "";
				String strConfirmNew = "";
				// strPhone = itFrom.getStringExtra("phoneNo");
				strCurrentPsw = etCurrentPsw.getText().toString();
				strNew = etNewPsw.getText().toString();
				strConfirmNew = etConfirmNewPsw.getText().toString();
				String strPsw = sp.getString("userPsw", "");
				System.out.println("strPsw    |" + strPsw);
				System.out.println("strCurrentPsw    |" + strCurrentPsw);

				if ("".equals(strCurrentPsw) || strCurrentPsw == null) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "请输入当前密码！");
				} else if (!strPsw.equals(strCurrentPsw)) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "当前密码输入错误！");
				} else if ("".equals(strNew) || strNew == null) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "请输入新密码！");
				} else if (!isPassword(strNew) || (!isPassword(strConfirmNew))) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "新密码格式错误！");
				} else if ("".equals(strConfirmNew) || strConfirmNew == null) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "请再次输入新密码！");
				} else if (!strNew.equals(strConfirmNew)) {
					MyUtil.ToastMessage(ChangePassWordActivity.this, "两次输入的密码不一致，请重新输入");
				} else {
					// param.clear();
					// param.put("token", itFrom.getStringExtra("token"));
					// param.put("pwd", strNew);
					// String uri = MyConstant.SERVICENAME +
					// "/index.php?pf=m_seller&app=member&act=change_user_password";
					// progressDialog.show();
					// iGetRequest.putExtra("uri", uri);
					// iGetRequest.putExtra("param", param);
					// iGetRequest.putExtra("type", "change_user_password");
					// startService(iGetRequest);
					// isGetServiceRunning = true;

					changePsw();
				}

			}
		});
	}

	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		this.mtbChangePsw = (MyTitleBar) findViewById(R.id.mtb_changePsw_title);
		this.tvTitle = mtbChangePsw.getTvTitle();
		this.ivLeft = mtbChangePsw.getIvLeft();
		this.tvTitle.setText("修改密码");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbChangePsw.getLlLeft();
		this.llRight = mtbChangePsw.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.btnNext = (Button) findViewById(R.id.btn_changePsw_next);
		this.param = new HashMap<String, Object>();
		this.iGetRequest = new Intent(ChangePassWordActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.itFrom = getIntent();
		this.tvCurrentAccount = (TextView) findViewById(R.id.tv_changePsw_currentAccountshow);
		this.tvCurrentAccount.setText(itFrom.getStringExtra("account"));
		// this.etCurrentAccount.setMyEditInputType(InputType.TYPE_CLASS_PHONE);
		this.etCurrentPsw = (EditText) findViewById(R.id.et_changePsw_currentPsw);
		// this.metCurrentPsw.setMyEditInputType(InputType.TYPE_CLASS_TEXT
		// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		this.etNewPsw = (EditText) findViewById(R.id.et_changePsw_inputNewPsw);
		// this.metNewPsw.setMyEditInputType(InputType.TYPE_CLASS_TEXT
		// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		this.etConfirmNewPsw = (EditText) findViewById(R.id.et_changePsw_confirmNewPsw);
		// this.metConfirmNewPsw.setMyEditInputType(InputType.TYPE_CLASS_TEXT
		// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		this.progressDialog = ProgressDialog.show(ChangePassWordActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.sp = ChangePassWordActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
	}

	public void changePsw() {
		progressDialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_psw)
						.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
								.add("oldPassword", sp.getString("userPsw", ""))
								.add("newPassword", etNewPsw.getText().toString()).build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									String success = object.optString("success");
									String error = object.optString("error");
									if (error.equals("")) {
										Toast.makeText(ChangePassWordActivity.this, "密码修改成功，请重新登录", Toast.LENGTH_SHORT)
												.show();
										setResult(MyConstant.RESULTCODE_10);
										ChangePassWordActivity.this.finish();
									} else {
										Toast.makeText(ChangePassWordActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
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
						progressDialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(ChangePassWordActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

					}
				});
	}

	/**
	 * 校验密码
	 * 
	 * @param password
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPassword(String password) {
		return Pattern.matches(REGEX_PASSWORD, password);
	}

}
