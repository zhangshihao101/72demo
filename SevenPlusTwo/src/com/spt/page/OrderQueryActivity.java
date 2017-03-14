package com.spt.page;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 【订单查询】页
 * */
public class OrderQueryActivity extends BaseActivity {

	private Context mOrderQueryContext;
	private SharedPreferences spOrderQuery;
	private String token;
	private Intent iGetRequest;
	private MyTitleBar mtbOrderQuery;
	private TextView tvTitle;
	private ImageView ivLeft;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private TextView tvStart;
	private TextView tvEnd;
	private EditText etNo;
	private Button btnOrderQuery;
	private int iYear;
	private int iMonth;
	private int iDay;
	private Calendar calenderNow;
	private LinearLayout llOrderState;
	private LinearLayout llEvaluateState;
	private RadioGroup rgOrderState;
	private RadioButton rbOrderNone;
	private RadioButton rbNeedPay;
	private RadioButton rbNeedSend;
	private RadioButton rbSended;
	private RadioButton rbComplete;
	private RadioButton rbCancel;
	private RadioButton rbReturn;
	private RadioGroup rgEvaluateState;
	private RadioButton rbEvaluateNone;
	private RadioButton rbEvaluated;
	private RadioButton rbNeedEvaluate;
	private RadioGroup rgExtensionState;
	private RadioButton rbExtensionNone;
	private RadioButton rbNormal;
	private RadioButton rbTuan;
	private HashMap<String, Object> params;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp;
	private SharedPreferences spJSONQuery;
	private boolean isSuccess = false;
	private Intent itFrom;
	private String state;
	private ProgressDialog progressDialog;
	private String orderSn;
	private String orderStatus;
	private String evaluationStatus;
	private String extension;
	private String addTimeFrom;
	private String addTimeTo;
	private Intent resultIntent;
	private Intent jumpIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.orderquery);
		super.onCreate(savedInstanceState);
		getNow();

	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	protected void onResume() {
		resultIntent.removeExtra("orderSn");
		resultIntent.removeExtra("orderStatus");
		resultIntent.removeExtra("evaluationStatus");
		resultIntent.removeExtra("extension");
		resultIntent.removeExtra("addTimeFrom");
		resultIntent.removeExtra("addTimeTo");
		
		jumpIntent.removeExtra("orderSn");
		jumpIntent.removeExtra("orderStatus");
		jumpIntent.removeExtra("evaluationStatus");
		jumpIntent.removeExtra("extension");
		jumpIntent.removeExtra("addTimeFrom");
		jumpIntent.removeExtra("addTimeTo");
		super.onResume();
	}

	@Override
	protected void onStop() {
		this.mOrderQueryContext.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			resultIntent.putExtra("isSuccess", isSuccess);
//			if ("needSend".equals(state)) {
//				setResult(MyConstant.RESULTCODE_29, resultIntent);
//			} else if ("allorder".equals(state)) {
//				setResult(MyConstant.RESULTCODE_23, resultIntent);
//			}  else {
//				setResult(MyConstant.RESULTCODE_12, resultIntent);
//			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {

		setRadioButtonState(state);

		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				resultIntent.putExtra("isSuccess", isSuccess);
//				if ("needSend".equals(state)) {
//					setResult(MyConstant.RESULTCODE_29, resultIntent);
//				} else if ("allorder".equals(state)) {
//					setResult(MyConstant.RESULTCODE_23, resultIntent);
//				}  else {
//					setResult(MyConstant.RESULTCODE_12, resultIntent);
//				}
				finish();
			}
		});

		this.rgEvaluateState.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == rbEvaluateNone.getId()) {
					if (params.containsKey("evaluation_status")) {
						params.remove("evaluation_status");
					} 
					if (resultIntent.hasExtra("evaluationStatus")) {
						resultIntent.removeExtra("evaluationStatus");
						evaluationStatus = "";
					}
					if (jumpIntent.hasExtra("evaluationStatus")) {
						jumpIntent.removeExtra("evaluationStatus");
						evaluationStatus = "";
					}
				} else if (checkedId == rbEvaluated.getId()) {
					params.put("evaluation_status", 1);
					evaluationStatus = "1";
					resultIntent.putExtra("evaluationStatus", evaluationStatus);
					jumpIntent.putExtra("evaluationStatus", evaluationStatus);
				} else if (checkedId == rbNeedEvaluate.getId()) {
					params.put("evaluation_status", 0);
					evaluationStatus = "0";
					resultIntent.putExtra("evaluationStatus", evaluationStatus);
					jumpIntent.putExtra("evaluationStatus", evaluationStatus);
				}
			}
		});

		this.rgExtensionState.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == rbExtensionNone.getId()) {
					if (params.containsKey("extension")) {
						params.remove("extension");
					}
					if (resultIntent.hasExtra("extension")) {
						resultIntent.removeExtra("extension");
						extension = "";
					}
					if (jumpIntent.hasExtra("extension")) {
						jumpIntent.removeExtra("extension");
						extension = "";
					}
				} else if (checkedId == rbNormal.getId()) {
					params.put("extension", "normal");
					extension = "normal";
					resultIntent.putExtra("extension", extension);
					jumpIntent.putExtra("extension", extension);
				} else if (checkedId == rbTuan.getId()) {
					params.put("extension", "tuan");
					extension = "tuan";
					resultIntent.putExtra("extension", extension);
					jumpIntent.putExtra("extension", extension);
				}
			}
		});

		this.tvStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(mOrderQueryContext,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								String month = "";
								if ((monthOfYear + 1) < 10) {
									month = "0" + String.valueOf((monthOfYear + 1));
								} else {
									month = String.valueOf((monthOfYear + 1));
								}
								String day = "";
								if ((dayOfMonth + 1) < 10) {
									day = "0" + String.valueOf((dayOfMonth));
								} else {
									day = String.valueOf((dayOfMonth));
								}
								tvStart.setText(year + "-" + month + "-" + day);

								params.put("add_time_from", MyUtil.strToMilliseconds(year + "-" + month + "-" + day));
								addTimeFrom = String.valueOf(MyUtil.strToMilliseconds(year + "-" + month + "-" + day));
								resultIntent.putExtra("addTimeFrom", addTimeFrom);
								jumpIntent.putExtra("addTimeFrom", addTimeFrom);
							}
						}, iYear, iMonth, iDay);
				dpd.show();
			}
		});

		this.tvEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(mOrderQueryContext,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								String month = "";
								if ((monthOfYear + 1) < 10) {
									month = "0" + String.valueOf((monthOfYear + 1));
								} else {
									month = String.valueOf((monthOfYear + 1));
								}
								String day = "";
								if ((dayOfMonth + 1) < 10) {
									day = "0" + String.valueOf((dayOfMonth));
								} else {
									day = String.valueOf((dayOfMonth));
								}

								tvEnd.setText(year + "-" + month + "-" + day);
								params.put(
										"add_time_to",
										MyUtil.strToMilliseconds(year + "-" + month + "-"
												+ String.valueOf(Integer.parseInt(day) + 1)));
								addTimeTo = String.valueOf(MyUtil.strToMilliseconds(year + "-" + month + "-" + day));
								resultIntent.putExtra("addTimeTo", addTimeTo);
								jumpIntent.putExtra("addTimeTo", addTimeTo);
							}
						}, iYear, iMonth, iDay);
				dpd.show();
			}
		});

		this.btnOrderQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = etNo.getText().toString();
				if (!"".equals(name)) {
					orderSn = name;
					resultIntent.putExtra("orderSn", orderSn);
					jumpIntent.putExtra("orderSn", orderSn);
					params.put("order_sn", name);
					params.put("token", token);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
					progressDialog.show();
					iGetRequest.putExtra("uri", uri);
					iGetRequest.putExtra("param", params);
					iGetRequest.putExtra("type", "orderQuery");
					startService(iGetRequest);
					isGetServiceRunning = true;
					isSuccess = false;
				} else {
					if (params.containsKey("order_sn")) {
						params.remove("order_sn");
					}
					if (resultIntent.hasExtra("orderSn")) {
						resultIntent.removeExtra("orderSn");
						orderSn = "";
					}
					if (jumpIntent.hasExtra("orderSn")) {
						jumpIntent.removeExtra("orderSn");
						orderSn = "";
					}
					params.put("token", token);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
					progressDialog.show();
					iGetRequest.putExtra("uri", uri);
					iGetRequest.putExtra("param", params);
					iGetRequest.putExtra("type", "orderQuery");
					startService(iGetRequest);
					isGetServiceRunning = true;
					isSuccess = false;
				}
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mOrderQueryContext = OrderQueryActivity.this;
		this.spOrderQuery = mOrderQueryContext.getSharedPreferences("USERINFO", MODE_PRIVATE);
		this.token = spOrderQuery.getString("token", "");
		this.mtbOrderQuery = (MyTitleBar) findViewById(R.id.mtb_ordersQuery_title);
		this.tvTitle = mtbOrderQuery.getTvTitle();
		this.ivLeft = mtbOrderQuery.getIvLeft();
		this.tvTitle.setText("订单查询");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbOrderQuery.getLlLeft();
		this.llRight = mtbOrderQuery.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.itFrom = getIntent();
		this.tvStart = (TextView) findViewById(R.id.tv_ordersQuery_start);
		this.tvEnd = (TextView) findViewById(R.id.tv_ordersQuery_end);
		this.etNo = (EditText) findViewById(R.id.et_ordersQuery_orderNo);
		this.llOrderState = (LinearLayout) findViewById(R.id.ll_ordersQuery_OrderState);
		this.llEvaluateState = (LinearLayout) findViewById(R.id.ll_ordersQuery_evaluateState);
		this.rgOrderState = (RadioGroup) findViewById(R.id.rg_ordersQuery_OrderState);
		this.rbOrderNone = (RadioButton) findViewById(R.id.rb_ordersQuery_orderNone);
		this.rbNeedPay = (RadioButton) findViewById(R.id.rb_ordersQuery_needPay);
		this.rbSended = (RadioButton) findViewById(R.id.rb_ordersQuery_sended);
		this.rbNeedSend = (RadioButton) findViewById(R.id.rb_ordersQuery_needSend);
		this.rbComplete = (RadioButton) findViewById(R.id.rb_ordersQuery_complete);
		this.rbCancel = (RadioButton) findViewById(R.id.rb_ordersQuery_cancel);
		this.rbReturn = (RadioButton) findViewById(R.id.rb_ordersQuery_return);
		this.rgEvaluateState = (RadioGroup) findViewById(R.id.rg_ordersQuery_evaluateState);
		this.rbEvaluateNone = (RadioButton) findViewById(R.id.rb_ordersQuery_evaluateNone);
		this.rbEvaluated = (RadioButton) findViewById(R.id.rb_ordersQuery_evaluated);
		this.rbNeedEvaluate = (RadioButton) findViewById(R.id.rb_ordersQuery_needEvaluate);
		this.rgExtensionState = (RadioGroup) findViewById(R.id.rg_ordersQuery_extensionState);
		this.rbExtensionNone = (RadioButton) findViewById(R.id.rb_ordersQuery_extensionNone);
		this.rbNormal = (RadioButton) findViewById(R.id.rb_ordersQuery_normal);
		this.rbTuan = (RadioButton) findViewById(R.id.rb_ordersQuery_tuan);
		this.btnOrderQuery = (Button) findViewById(R.id.btn_ordersQuery_query);
		this.params = new HashMap<String, Object>();
		this.iGetRequest = new Intent(mOrderQueryContext, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver();
		this.spJSONQuery = OrderQueryActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		this.progressDialog = ProgressDialog.show(OrderQueryActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.state = this.itFrom.getStringExtra("state");
		if ("needSend".equals(state)) {
			this.llOrderState.setVisibility(View.GONE);
			this.llEvaluateState.setVisibility(View.GONE);
			this.rgOrderState.setVisibility(View.GONE);
			this.rgEvaluateState.setVisibility(View.GONE);
		}
		this.resultIntent = new Intent();
		this.jumpIntent = new Intent(OrderQueryActivity.this, OrderQueryResultActivity.class);
	}

	/**
	 * 设置【订单状态】是否可点击 如果是待发货订单，则只能选择待发货
	 * */
	private void setRadioButtonState(String state) {
		if ("needSend".equals(state)) {
			this.rbNeedSend.setChecked(true);
			this.rbOrderNone.setClickable(false);
			this.rbNeedPay.setClickable(false);
			this.rbSended.setClickable(false);
			this.rbComplete.setClickable(false);
			this.rbCancel.setClickable(false);
			this.rbReturn.setClickable(false);
			this.params.put("status", 20);
		} else {
			this.rbOrderNone.setChecked(true);
			this.rbOrderNone.setClickable(true);
			this.rbNeedSend.setClickable(true);
			this.rbNeedPay.setClickable(true);
			this.rbSended.setClickable(true);
			this.rbComplete.setClickable(true);
			this.rbCancel.setClickable(true);
			this.rbReturn.setClickable(true);

			this.rgOrderState.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {

					if (checkedId == rbOrderNone.getId()) {
						if (params.containsKey("status")) {
							params.remove("status");
						}
						if (resultIntent.hasExtra("orderStatus")) {
							resultIntent.removeExtra("orderStatus");
							orderStatus = "";
						}
						if (jumpIntent.hasExtra("orderStatus")) {
							jumpIntent.removeExtra("orderStatus");
							orderStatus = "";
						}
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					} else if (checkedId == rbNeedPay.getId()) {
						orderStatus = "11";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 11);
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					} else if (checkedId == rbNeedSend.getId()) {
						orderStatus = "20";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 20);
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					} else if (checkedId == rbSended.getId()) {
						orderStatus = "30";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 30);
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					} else if (checkedId == rbComplete.getId()) {
						orderStatus = "40";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 40);
						rgEvaluateState.setVisibility(View.VISIBLE);
						llEvaluateState.setVisibility(View.VISIBLE);
					} else if (checkedId == rbCancel.getId()) {
						orderStatus = "0";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 0);
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					} else if (checkedId == rbReturn.getId()) {
						orderStatus = "50";
						resultIntent.putExtra("orderStatus", orderStatus);
						jumpIntent.putExtra("orderStatus", orderStatus);
						params.put("status", 50);
						rgEvaluateState.setVisibility(View.GONE);
						llEvaluateState.setVisibility(View.GONE);
						if (params.containsKey("evaluation_status")) {
							params.remove("evaluation_status");
						}
					}
				}
			});
		}
	}

	/**
	 * 获取当前日期
	 * */
	private void getNow() {
		this.calenderNow = Calendar.getInstance(Locale.CHINA);
		Date date = new Date();
		this.calenderNow.setTime(date);
		this.iYear = calenderNow.get(Calendar.YEAR);
		this.iMonth = calenderNow.get(Calendar.MONTH);
		this.iDay = calenderNow.get(Calendar.DATE);
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		this.mOrderQueryContext.registerReceiver(brGetHttp, filterGetHttp);
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
		if ("orderQuery".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				JSONArray array = jsonReturn.getJSONArray("data");
				this.isSuccess = true;
				Editor editor = spJSONQuery.edit();
				if ("needSend".equals(state)) {
					editor.putString("OrderQueryBackNeed", jsonStr); // 将商品列表数据存入SharedPreferences
				} else if ("allorder".equals(state)) {
					editor.putString("AllOrderQueryBack", jsonStr); // 将商品列表数据存入SharedPreferences
				} else {
					editor.putString("OrderQueryBack", jsonStr); // 将商品列表数据存入SharedPreferences
				}
				editor.commit();
				resultIntent.putExtra("isSuccess", isSuccess);
				jumpIntent.putExtra("isSuccess", isSuccess);
				if ("needSend".equals(state)) {
					setResult(MyConstant.RESULTCODE_29, resultIntent);
					OrderQueryActivity.this.finish();
				} else if ("allorder".equals(state)) {
					setResult(MyConstant.RESULTCODE_23, resultIntent);
				} else if ("orderResult".equals(state)) {
					setResult(MyConstant.RESULTCODE_12, resultIntent);
					OrderQueryActivity.this.finish();
				} else {
					jumpIntent.putExtra("resultData", jsonStr);
					jumpIntent.putExtra("token", token);
					startActivity(jumpIntent);
					OrderQueryActivity.this.finish();
				}
				
				if (array.length() > 0) {
					MyUtil.ToastMessage(OrderQueryActivity.this, "查询成功");
				}
			} else {
				MyUtil.ToastMessage(OrderQueryActivity.this, msg);
			}

		}
	}

}
