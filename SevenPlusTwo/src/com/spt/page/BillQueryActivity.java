package com.spt.page;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MySearchEditText;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 【对账单查询】页
 * */
public class BillQueryActivity extends BaseActivity {

	private MyTitleBar mtb_billQuery;
	private TextView tvTitle;
	private ImageView ivLeft;
	private MySearchEditText mset;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private Button btnQuery;
	private int year;
	private int month;
	private int day;
//	private String billName;
//	private String startTime;
//	private String endTime;
	private RadioGroup rgState;
	private RadioButton rbNoState;
	private RadioButton rbUnConfirm;
	private RadioButton rbConfirmed;
	private RadioButton rbPayed;
	private RadioGroup rgBelong;
	private RadioButton rbNoBelong;
	private RadioButton rbTuan;
	private RadioButton rbNormal;
	private boolean isGetServiceRunning = false;
	private Intent iGetRequest;
	private BroadcastReceiver brGetHttp; // post方法广播
	private SharedPreferences spBillQuery;
	private SharedPreferences spJSONQuery;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private String state;
	private String belong;
	private ProgressDialog progressDialog;
	private boolean isSuccess = false;
	private Intent resultIntent;
	private String sta_status;
	private String sta_plat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.billquery);
		super.onCreate(savedInstanceState);
		// 获取当前时间
		getCurrent();
	}
	
	@Override
    public void onResume() {
		resultIntent.removeExtra("sta_status");
		resultIntent.removeExtra("sta_plat");
		super.onResume();
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		BillQueryActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			resultIntent.putExtra("isSuccess", isSuccess);
			setResult(MyConstant.RESULTCODE_14, resultIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resultIntent.putExtra("isSuccess", isSuccess);
				setResult(MyConstant.RESULTCODE_14, resultIntent);
				finish();
			}
		});

		this.rgState.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int idNoState = rbNoState.getId();
				int idUnConfirm = rbUnConfirm.getId();
				int idPayed = rbPayed.getId();
				int idConfirmed = rbConfirmed.getId();

				if (idNoState == checkedId) {
					state = rbNoState.getText().toString();
				} else if (idUnConfirm == checkedId) {
					state = rbUnConfirm.getText().toString();
				} else if (idConfirmed == checkedId) {
					state = rbConfirmed.getText().toString();
				} else if (idPayed == checkedId) {
					state = rbPayed.getText().toString();
				}
			}
		});

		this.rgBelong.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int idNoBelong = rbNoBelong.getId();
				int idTuan = rbTuan.getId();
				int idNormal = rbNormal.getId();

				if (idNoBelong == checkedId) {
					belong = rbNoState.getText().toString();
				} else if (idTuan == checkedId) {
					belong = rbTuan.getText().toString();
				} else if (idNormal == checkedId) {
					belong = rbNormal.getText().toString();
				}
			}
		});

		this.tvStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(BillQueryActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int pyear, int pmonthOfYear, int pdayOfMonth) {

						tvStartTime.setText(pyear + "-" + (pmonthOfYear + 1) + "-" + pdayOfMonth);
					}
				}, year, month, day);

				dpd.show();
			}
		});

		this.tvEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(BillQueryActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int pyear, int pmonthOfYear, int pdayOfMonth) {

						tvEndTime.setText(pyear + "-" + (pmonthOfYear + 1) + "-" + pdayOfMonth);
					}
				}, year, month, day);

				dpd.show();
			}
		});

		this.btnQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				billName = mset.getMyEditText();
//				startTime = tvStartTime.getText().toString();
//				endTime = tvEndTime.getText().toString();

				HashMap<String, Object> param = new HashMap<String, Object>();
				param.clear();
				param.put("token", spBillQuery.getString("token", ""));
				if (!"".equals(state)) {
					param.put("sta_status", str2Code(state));
					sta_status = str2Code(state);
					resultIntent.putExtra("sta_status", sta_status);
				} else {
					if (resultIntent.hasExtra("sta_status")) {
						sta_status = "";
						resultIntent.removeExtra("sta_status");
					}
				}

				if (!"".equals(belong)) {
					param.put("sta_plat", str2Code(belong));
					sta_plat = str2Code(belong);
					resultIntent.putExtra("sta_plat", sta_plat);
				} else {
					if (resultIntent.hasExtra("sta_plat")) {
						sta_plat = "";
						resultIntent.removeExtra("sta_plat");
					}
				}

				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance";
				String type = "billQuery";
				progressDialog.show();
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtb_billQuery = (MyTitleBar) findViewById(R.id.mtb_billQuery_title);
		this.tvTitle = mtb_billQuery.getTvTitle();
		this.tvTitle.setText("对账单搜索");
		this.ivLeft = mtb_billQuery.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtb_billQuery.getLlLeft();
		this.llRight = mtb_billQuery.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.mset = (MySearchEditText) findViewById(R.id.mset_billQuery_search);
		this.mset.setMyEditHint("对账单名称");
		this.rgState = (RadioGroup) findViewById(R.id.rg_billQuery_state);
		this.rbNoState = (RadioButton) findViewById(R.id.rb_billQuery_noState);
		this.rbUnConfirm = (RadioButton) findViewById(R.id.rb_billQuery_unconfirm);
		this.rbConfirmed = (RadioButton) findViewById(R.id.rb_billQuery_confirmed);
		this.rbPayed = (RadioButton) findViewById(R.id.rb_billQuery_payed);
		this.rgBelong = (RadioGroup) findViewById(R.id.rg_billQuery_belong);
		this.rbNoBelong = (RadioButton) findViewById(R.id.rb_billQuery_noBelong);
		this.rbTuan = (RadioButton) findViewById(R.id.rb_billQuery_tuan);
		this.rbNormal = (RadioButton) findViewById(R.id.rb_billQuery_normal);
		this.tvStartTime = (TextView) findViewById(R.id.tv_billQuery_startTime);
		this.tvEndTime = (TextView) findViewById(R.id.tv_billQuery_endTime);
		this.btnQuery = (Button) findViewById(R.id.btn_billQuery_query);
		this.btnQuery.setText(R.string.orderquery_query);
		this.btnQuery.setClickable(true);
		this.iGetRequest = new Intent(BillQueryActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver(); // POST广播对象
		this.spBillQuery = BillQueryActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE); // 获取sp对象
		this.spJSONQuery = BillQueryActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		this.progressDialog = ProgressDialog.show(BillQueryActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.resultIntent = new Intent();
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		BillQueryActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 获取当前日期
	 * */
	private void getCurrent() {
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
	}

	/**
	 * 内部广播类
	 * */
	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parseData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * 解析返回数据
	 * */
	private void parseData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("billQuery".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Editor editor = spJSONQuery.edit();
				editor.putString("BillList", data); // 将商品列表数据存入SharedPreferences
				editor.commit();
				resultIntent.putExtra("isSuccess", isSuccess);
				setResult(MyConstant.RESULTCODE_14, resultIntent);
				BillQueryActivity.this.finish();
			} else {
				MyUtil.ToastMessage(BillQueryActivity.this, msg);
			}

		}
	}

	private String str2Code(String str) {
		String code = "";
		if ("未确认".equals(str)) {
			code = "0";
		} else if ("已确认".equals(str)) {
			code = "1";
		} else if ("已结账".equals(str)) {
			code = "2";
		} else if ("商城".equals(str)) {
			code = "1";
		} else if ("团购".equals(str)) {
			code = "2";
		}

		return code;
	}

}
