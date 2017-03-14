package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.TagAdapter;
import com.spt.bean.BankInfo;
import com.spt.controler.FlowTagLayout;
import com.spt.interfac.OnTagSelectListener;
import com.spt.sht.R;
import com.spt.utils.ListUtils;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;
import com.spt.utils.NoDoubleClickUtils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * 申请分销
 * 
 * @author lihongxuan
 *
 */
public class ApplyDisActivity extends BaseActivity {

	private EditText et_apply_name, et_apply_phone, et_apply_qq, et_apply_wechat, et_apply_email, et_apply_msg,
			et_apply_alipay, et_apply_alipay_name, et_apply_bank, et_apply_open_name;
	private TextView tv_apply_open;
	private FlowTagLayout mChannelLayout, mIdentityLayout;
	private Button btn_apply_commit;
	private RadioGroup rg_apply_sex;
	private RadioButton rb_apply_man, rb_apply_woman;
	private ImageView iv_apply_back;
	private TagAdapter mChannelTagAdapter, mIdentityTagAdapter;
	private String disGender;
	private String channel, identity;
	private ProgressDialog progressdialog;
	private SharedPreferences spHome;
	private HashMap<String, Object> params;// 参数集合
	private String token;// 必须传的参数
	private BroadcastReceiver brGetHttp; // get方法广播
	private BroadcastReceiver brPostHttp; // post方法广播
	private boolean isGetServiceRunning = false;
	private boolean isPostServiceRunning = false;
	private Intent iPostRequest; // post方法请求
	private Intent iGetRequest; // get方法请求

	private CharSequence temp;// 监听前的文本

	/**
	 * 电话号码正则匹配表达式（11位手机号码）^0\d{2,3}(\-)?\d{7,8}$
	 * (13[0-9]|15[0-9]|18[0-9])\\d{8}$
	 */
	public static final String TEL_CHECK = "^(13[0-9]|15[0-9]|18[0-9])\\d{8}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_applydis);
		super.onCreate(savedInstanceState);

		String phone = spHome.getString("usertel", "");
		if (phone != null && !phone.equals("")) {
			et_apply_phone.setText(phone);
			et_apply_phone.setFocusable(false);
		} else {
			et_apply_phone.setFocusable(true);
		}

		initData();

		initChannelData();

		initIdentityData();

		// 默认选中男
		rg_apply_sex.check(R.id.rb_apply_man);
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	protected void onStop() {
		ApplyDisActivity.this.unregisterReceiver(brGetHttp);
		ApplyDisActivity.this.unregisterReceiver(brPostHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	private void initData() {

		// 销售渠道
		mChannelTagAdapter = new TagAdapter(ApplyDisActivity.this);
		mChannelLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
		mChannelLayout.setAdapter(mChannelTagAdapter);

		// 所属身份
		mIdentityTagAdapter = new TagAdapter(ApplyDisActivity.this);
		mIdentityLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
		mIdentityLayout.setAdapter(mIdentityTagAdapter);
	}

	// 销售渠道添加数据
	private void initChannelData() {
		List<Object> dataSource = new ArrayList<Object>();
		dataSource.add("淘宝");
		dataSource.add("京东");
		dataSource.add("微店");
		dataSource.add("朋友圈");
		dataSource.add("实体店");
		dataSource.add("其他");
		mChannelTagAdapter.onlyAddAll(dataSource);
	}

	// 所属身份添加数据
	private void initIdentityData() {
		List<Object> dataSource = new ArrayList<Object>();
		dataSource.add("领队");
		dataSource.add("俱乐部");
		dataSource.add("社团");
		dataSource.add("网店店主");
		dataSource.add("实体店主");
		dataSource.add("其他");
		mIdentityTagAdapter.onlyAddAll(dataSource);
	}

	// 初始化
	@Override
	protected void init() {
		et_apply_name = (EditText) findViewById(R.id.et_apply_name);
		et_apply_phone = (EditText) findViewById(R.id.et_apply_phone);
		et_apply_qq = (EditText) findViewById(R.id.et_apply_qq);
		et_apply_wechat = (EditText) findViewById(R.id.et_apply_wechat);
		et_apply_email = (EditText) findViewById(R.id.et_apply_email);
		et_apply_msg = (EditText) findViewById(R.id.et_apply_msg);
		et_apply_alipay = (EditText) findViewById(R.id.et_apply_alipay);
		et_apply_alipay_name = (EditText) findViewById(R.id.et_apply_alipay_name);
		et_apply_bank = (EditText) findViewById(R.id.et_apply_bank);
		et_apply_open_name = (EditText) findViewById(R.id.et_apply_open_name);
		tv_apply_open = (TextView) findViewById(R.id.tv_apply_open);
		iv_apply_back = (ImageView) findViewById(R.id.iv_apply_back);
		rg_apply_sex = (RadioGroup) findViewById(R.id.rg_apply_sex);
		rb_apply_man = (RadioButton) findViewById(R.id.rb_apply_man);
		rb_apply_woman = (RadioButton) findViewById(R.id.rb_apply_woman);
		mChannelLayout = (FlowTagLayout) findViewById(R.id.ftl_apply_dischannel);
		mIdentityLayout = (FlowTagLayout) findViewById(R.id.ftl_apply_identity);
		btn_apply_commit = (Button) findViewById(R.id.btn_apply_commit);
		spHome = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		progressdialog = ProgressDialog.show(ApplyDisActivity.this, "请稍候。。。", "获取数据中。。。", true);
		progressdialog.dismiss();
		token = spHome.getString("accessToken", "");
		brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		params = new HashMap<String, Object>(); // 调用接口参数
		iPostRequest = new Intent(ApplyDisActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POST
																	// Action
		iGetRequest = new Intent(ApplyDisActivity.this, MyHttpGetService.class); // 启动GET服务Intent对象
		iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置GET Action

	}

	// 点击事件
	@Override
	protected void addClickEvent() {

		// 返回
		iv_apply_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				ApplyDisActivity.this.finish();
				setResult(100);
			}
		});

		et_apply_bank.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() > 5) {
					String string = s.toString();
					char[] array = string.toCharArray();
					String bankName = BankInfo.getNameOfBank(array, 0);
					tv_apply_open.setText(bankName);
				} else if (temp.length() == 0) {
					tv_apply_open.setText("");
				}
			}
		});

		// 提交
		btn_apply_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					if (checkEdit()) {
						progressdialog = ProgressDialog.show(ApplyDisActivity.this, "请稍等...", "数据获取中...", true);
						params.clear();
						params.put("token", token);
						params.put("version", "2.1");
						params.put("real_name", et_apply_name.getText().toString());
						params.put("gender", disGender);
						params.put("im_qq", et_apply_qq.getText().toString());
						params.put("email", et_apply_email.getText().toString());
						params.put("phone_mob", et_apply_phone.getText().toString());
						params.put("im_wechat", et_apply_wechat.getText().toString());
						params.put("channel", channel);
						params.put("identity", identity);
						params.put("other_identity", et_apply_msg.getText().toString());
						params.put("alipay_account", et_apply_alipay.getText().toString());
						params.put("alipay_name", et_apply_alipay_name.getText().toString());
						params.put("account_holder", et_apply_open_name.getText().toString());
						params.put("bank_account", et_apply_bank.getText().toString());
						params.put("bank_name", tv_apply_open.getText().toString());
						String uri = MyConstant.SERVICENAME + MyConstant.COMMITDIS;
						iPostRequest.putExtra("param", params);
						iPostRequest.putExtra("uri", uri);
						iPostRequest.putExtra("type", "commit_dis");
						startService(iPostRequest);
						isPostServiceRunning = true;
					}
				}
			}
		});

		// 选择性别
		rg_apply_sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rb_apply_man.getId()) {
					disGender = "1";
				} else if (checkedId == rb_apply_woman.getId()) {
					disGender = "2";
				}
			}
		});

		mChannelLayout.setOnTagSelectListener(new OnTagSelectListener() {

			@Override
			public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
				if (selectedList.size() != 0) {
					channel = ListUtils.listToString2(selectedList);
				}
			}
		});

		mIdentityLayout.setOnTagSelectListener(new OnTagSelectListener() {

			@Override
			public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
				if (selectedList.size() != 0) {
					identity = ListUtils.listToString2(selectedList);
				}
			}
		});

	}

	/**
	 * 判断用户提交内容是否正确
	 * 
	 * @return
	 */
	public boolean checkEdit() {

		if (et_apply_name.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_phone.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!checkStringStyle(et_apply_phone.getText().toString().replace(" ", ""), TEL_CHECK)) {
			Toast.makeText(ApplyDisActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_qq.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入QQ号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_wechat.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入微信号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_email.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_alipay.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入支付宝账号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_bank.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入银行卡号", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!et_apply_bank.getText().toString().equals("")) {
			if (!MyUtil.checkBankCard(et_apply_bank.getText().toString())) {
				Toast.makeText(ApplyDisActivity.this, "请输入正确银行卡号", Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		if (et_apply_alipay_name.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入支付宝真实姓名", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (et_apply_open_name.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(ApplyDisActivity.this, "请输入开户行姓名", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (TextUtils.isEmpty(channel)) {
			Toast.makeText(ApplyDisActivity.this, "请选择分销渠道", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (TextUtils.isEmpty(identity)) {
			Toast.makeText(ApplyDisActivity.this, "请选择身份", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	public static boolean checkStringStyle(String str, String style) {
		Pattern REGEX = Pattern.compile(style);
		// 非空判断
		if (null == str) {
			return false;
		}
		Matcher matcher = REGEX.matcher(str);
		return matcher.matches();
	}

	/**
	 * 注册广播
	 */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		ApplyDisActivity.this.registerReceiver(brPostHttp, filterPostHttp);

		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		ApplyDisActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 内部广播类
	 */
	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parseDataGet(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parseDataPost(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * 解析get返回数据
	 */
	public void parseDataGet(String type, String result) throws JSONException {

	}

	/**
	 * 解析post返回数据
	 */
	public void parseDataPost(String type, String result) throws JSONException {
		if (type.equals("commit_dis")) {
			JSONObject obj = new JSONObject(result);
			String error = obj.optString("error");
			if (error.equals("0")) {
				progressdialog.dismiss();
				Toast.makeText(ApplyDisActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				progressdialog.dismiss();
				Toast.makeText(ApplyDisActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(100);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
