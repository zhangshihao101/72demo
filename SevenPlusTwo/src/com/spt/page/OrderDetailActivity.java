package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 【订单详情】页
 */
public class OrderDetailActivity extends BaseActivity {

	private Context mOrderDetailContext;
	private MyTitleBar mtbOrderDetail;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Button btnOrderDetailSendOrder; // 发货
	// private ImageView ivOrderDetailReceiveInfo; // 更改收货信息
	// private ImageView ivOrderDetailTransInfo; // 更改物流信息
	private ImageView ivOtherInfo; // 查看操作日志
	private ImageView ivOrderDetailLog; // 查看操作日志
	private Button btnOrderDetailCancel; // 取消订单
	private ImageView ivOrderDetailGoodChange; // 取消订单
	private Button btnQueryTransInfo; // 查看物流
	// private LinearLayout llOrderDetailReceiveInfo;
	// private LinearLayout llOrderDetailTransInfo;
	private LinearLayout llOrderDetailLog;
	private LinearLayout llOrderDetailGoodChange;
	private LinearLayout llGoodsList;
	private LinearLayout llGoodsInfo;
	private LinearLayout llTransInfo;
	private LinearLayout llTransInfoInput;
	// private LinearLayout llReceiveInfo;
	private LinearLayout llOtherInfo;
	private TextView tvOrderNo;
	private TextView tvOrderState;
	// private TextView tvOrderDate;
	private TextView tvOrderMoney;
	private TextView tvPayType;
	private TextView tvPayDate;
	private TextView tvTransPrice;
	private TextView tvSumPrice;
	private TextView tvReceiver;
	private TextView tvReceivePhone;
	private TextView tvReceiveAddress;
	private TextView tvTransCompany;
	private TextView tvTransNo;
	private TextView tvTransTime;
	private TextView tvOtherInfo;
	private TextView tvInsurance;
	private TextView tvTransOtherInfo;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Spinner spinCompany;
	private List<HashMap<String, String>> lstCompany;
	private TextView tvChangeMoney;
	private EditText etTransNo;
	private EditText etTransOtherInfo;
	private LinearLayout llScanTransInfo;
	private LinearLayout llTransInfoContent;
	private LinearLayout llTransInfoContentImage;
	private ImageView ivTransInfoContent;
	private LinearLayout llOrderTransInfoContent;
	private TextView tvEvaluateState;

	private String detailData; // 订单详情数据
	private String logInfo;
	private SharedPreferences spOrderDetail;
	private String token;
	private String order_id;
	private String order_sn;
	private String invoice_no;
	// private String invoice_code;
	private String invoice_inc;
	private String order_state;
	private String ship_time;
	private String evaluation_status;
	// private String trans_otherInfo;
	private String goods_amount;
	private HashMap<String, Object> param;
	private Intent iGetOrderDetail;
	private Intent iPostOrderDetail;
	private boolean isGetServiceRunning = false;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brGetHttp;
	private BroadcastReceiver brPostHttp;
	private String consignee;
	private String phone_tel;
	private String phone_mob;
	private String address;
	private String extension;
	private ProgressDialog progressDialog;
	// private LinearLayout llPayContent;
	// private LinearLayout llGoodContent;
	// private LinearLayout llReceiveContent;
	// private LinearLayout llTransInfoContent;
	private LinearLayout llOtherInfoContent;
	// private LinearLayout llLogContent;
	private LinearLayout llLogList;
	private String region_name;
	private String shipping_insurance;
	private String ext_activity_type_name;
	private String strFromPage;
	private String final_amount;
	private String shipping_fee;
	private String order_amount;
	private TextView tvLogo;
	private TextView tvChanged;
	private TextView tvSumpriceChanged;
	private String pageCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.orderdetail);
		super.onCreate(savedInstanceState);
		detailData = getIntent().getStringExtra("data"); // 获取数据
		callCompanyData();
		try {
			parseData(detailData); // 解析数据
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		OrderDetailActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetOrderDetail);
			isGetServiceRunning = false;
		}

		OrderDetailActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostOrderDetail);
			isPostServiceRunning = false;
		}

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case MyConstant.RESULTCODE_31:
			// String company = data.getStringExtra("company");
			// String transNo = data.getStringExtra("transNo");
			// String transOtherInfo = data.getStringExtra("transOtherInfo");
			// if (!"".equals(company)) {
			// tvTransCompany.setText(company);
			// invoice_inc = company;
			// }
			// if (!"".equals(transNo)) {
			// tvTransNo.setText(transNo);
			// invoice_no = transNo;
			// }
			// if (!"".equals(transOtherInfo)) {
			// tvTransOtherInfo.setText(transOtherInfo);
			// // trans_otherInfo = transOtherInfo;
			// }
			param.clear();
			param.put("token", token);
			param.put("order_id", order_id);
			String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
			String type = "orderDetail_refresh";
			iGetOrderDetail.putExtra("uri", uri);
			iGetOrderDetail.putExtra("param", param);
			iGetOrderDetail.putExtra("type", type);
			startService(iGetOrderDetail);
			isGetServiceRunning = true;
			param.clear();
			break;
		case MyConstant.RESULTCODE_32:
			boolean isCancel = data.getBooleanExtra("isCancel", false);
			if (isCancel) {
				Intent it = new Intent();
				data.putExtra("isCancel", isCancel);
				if ("orderResult".equals(pageCode)) {
					setResult(MyConstant.RESULTCODE_39, it);
				} else {
					setResult(MyConstant.RESULTCODE_15, it);
				}
				finish();
			}
			break;
		case MyConstant.RESULTCODE_33:
			// String shipping_fee_back = data.getStringExtra("shipping_fee");
			// if (!"".equals(shipping_fee_back) &&
			// !"null".equals(shipping_fee_back)) {
			// shipping_fee = shipping_fee_back;
			// tvTransPrice.setText("￥" + shipping_fee_back);
			// }
			// String order_amount_back = data.getStringExtra("goods_amount");
			// if (!"".equals(order_amount_back) &&
			// !"null".equals(order_amount_back)) {
			// float sum = Float.parseFloat(order_amount_back) +
			// Float.parseFloat(shipping_fee);
			// DecimalFormat decimalFormat=new
			// DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足
			// order_amount = decimalFormat.format(sum);
			//
			// tvSumPrice.setText("￥" + order_amount);
			// tvOrderMoney.setText("￥" + final_amount);
			// }
			param.clear();
			param.put("token", token);
			param.put("order_id", order_id);
			String uri_ = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
			String type_ = "orderDetail_refresh";
			iGetOrderDetail.putExtra("uri", uri_);
			iGetOrderDetail.putExtra("param", param);
			iGetOrderDetail.putExtra("type", type_);
			startService(iGetOrderDetail);
			isGetServiceRunning = true;
			param.clear();
			break;
		}
	}

	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		this.mOrderDetailContext = OrderDetailActivity.this;
		this.mtbOrderDetail = (MyTitleBar) findViewById(R.id.mtb_orderdetail_title);
		this.tvTitle = mtbOrderDetail.getTvTitle();
		this.ivLeft = mtbOrderDetail.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbOrderDetail.getLlLeft();
		this.llRight = mtbOrderDetail.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.tvChangeMoney = (TextView) findViewById(R.id.tv_orderdetail_changeMoney);
		this.tvLogo = (TextView) findViewById(R.id.tv_orderdetail_logo);
		this.tvChanged = (TextView) findViewById(R.id.tv_orderdetail_changed);
		this.tvSumpriceChanged = (TextView) findViewById(R.id.tv_orderdetail_sumpriceChanged);
		// this.llOrderDetailReceiveInfo = (LinearLayout)
		// findViewById(R.id.ll_orderdetail_receiveChange);
		// this.llOrderDetailTransInfo = (LinearLayout)
		// findViewById(R.id.ll_orderdetail_transChange);
		this.tvTransOtherInfo = (TextView) findViewById(R.id.tv_orderdetail_transOtherInfo);
		this.etTransNo = (EditText) findViewById(R.id.et_orderdetail_transNo);
		this.etTransOtherInfo = (EditText) findViewById(R.id.et_orderdetail_transOtherInfo);
		this.llOrderDetailLog = (LinearLayout) findViewById(R.id.ll_orderdetail_log);
		this.llOrderDetailGoodChange = (LinearLayout) findViewById(R.id.ll_orderdetail_goodChange);
		this.llOtherInfo = (LinearLayout) findViewById(R.id.ll_orderdetail_otherInfo);
		this.btnOrderDetailSendOrder = (Button) findViewById(R.id.btn_orderdetail_sendOrder);
		this.btnQueryTransInfo = (Button) findViewById(R.id.btn_orderdetail_queryTransInfo);
		this.llScanTransInfo = (LinearLayout) findViewById(R.id.ll_orderdetail_scanTransInfo);
		this.llTransInfoContent = (LinearLayout) findViewById(R.id.ll_transInfoContent);
		this.llTransInfoContentImage = (LinearLayout) findViewById(R.id.ll_orderdetail_transInfoContentImage);
		this.ivTransInfoContent = (ImageView) findViewById(R.id.iv_orderdetail_transInfoContent);
		this.ivTransInfoContent.setImageResource(R.drawable.down);
		this.llOrderTransInfoContent = (LinearLayout) findViewById(R.id.ll_orderdetail_transInfoContent);
		// this.ivOrderDetailReceiveInfo = (ImageView)
		// findViewById(R.id.iv_orderdetail_receiveChange);
		// this.ivOrderDetailReceiveInfo.setImageResource(R.drawable.down);
		// this.ivOrderDetailTransInfo = (ImageView)
		// findViewById(R.id.iv_orderdetail_transChange);
		// this.ivOrderDetailTransInfo.setImageResource(R.drawable.down);
		this.ivOrderDetailLog = (ImageView) findViewById(R.id.iv_orderdetail_log);
		this.ivOrderDetailLog.setImageResource(R.drawable.down);
		this.ivOtherInfo = (ImageView) findViewById(R.id.iv_orderdetail_otherInfo);
		this.tvInsurance = (TextView) findViewById(R.id.tv_orderdetail_insurance);
		this.ivOtherInfo.setImageResource(R.drawable.down);
		this.tvTransPrice = (TextView) findViewById(R.id.tv_orderdetail_transprice);
		this.btnOrderDetailCancel = (Button) findViewById(R.id.btn_orderdetail_cancel);
		this.ivOrderDetailGoodChange = (ImageView) findViewById(R.id.iv_orderdetail_goodChange);
		this.ivOrderDetailGoodChange.setImageResource(R.drawable.down);
		this.llGoodsList = (LinearLayout) findViewById(R.id.ll_orderdetail_goods);
		this.llGoodsInfo = (LinearLayout) findViewById(R.id.ll_orderdetail_goodsInfo);
		// this.llReceiveInfo = (LinearLayout)
		// findViewById(R.id.ll_orderdetail_receiveInfo);
		this.llTransInfo = (LinearLayout) findViewById(R.id.ll_orderdetail_transInfo);
		this.llTransInfoInput = (LinearLayout) findViewById(R.id.ll_orderdetail_transInfoInput);
		this.tvOrderNo = (TextView) findViewById(R.id.tv_orderdetail_orderNoShow);
		this.tvOrderState = (TextView) findViewById(R.id.tv_orderdetail_orderState);
		this.spinCompany = (Spinner) findViewById(R.id.spin_orderdetail_transCompany);
		this.lstCompany = new ArrayList<HashMap<String, String>>();
		SimpleAdapter sa = new SimpleAdapter(OrderDetailActivity.this, lstCompany, R.layout.companyitem,
				new String[] { "tv_company_title" }, new int[] { R.id.tv_company_title });
		this.spinCompany.setAdapter(sa);
		this.tvEvaluateState = (TextView) findViewById(R.id.tv_orderdetail_evaluateState);
		// this.tvOrderDate = (TextView)
		// findViewById(R.id.tv_orderdetail_orderDate);
		this.tvOrderMoney = (TextView) findViewById(R.id.tv_orderdetail_orderMoney);
		this.tvPayType = (TextView) findViewById(R.id.tv_orderdetail_payType);
		this.tvPayDate = (TextView) findViewById(R.id.tv_orderdetail_payDate);
		this.tvSumPrice = (TextView) findViewById(R.id.tv_orderdetail_sumprice);
		this.tvReceiver = (TextView) findViewById(R.id.tv_orderdetail_receiverShow);
		this.tvReceivePhone = (TextView) findViewById(R.id.tv_orderdetail_receiverPhone);
		this.tvReceiveAddress = (TextView) findViewById(R.id.tv_orderdetail_receiveAdress);
		this.tvTransCompany = (TextView) findViewById(R.id.tv_orderdetail_transCompany);
		this.tvTransNo = (TextView) findViewById(R.id.tv_orderdetail_transNo);
		this.tvTransTime = (TextView) findViewById(R.id.tv_orderdetail_transTime);
		this.tvOtherInfo = (TextView) findViewById(R.id.tv_orderdetail_otherInfo);
		this.param = new HashMap<String, Object>();
		this.spOrderDetail = getSharedPreferences("USERINFO", MODE_PRIVATE);
		this.token = spOrderDetail.getString("token", "");
		this.iGetOrderDetail = new Intent(OrderDetailActivity.this, MyHttpGetService.class);
		this.iGetOrderDetail.setAction(MyConstant.HttpGetServiceAciton);
		this.iPostOrderDetail = new Intent(OrderDetailActivity.this, MyHttpPostService.class);
		this.iPostOrderDetail.setAction(MyConstant.HttpPostServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver();
		this.brPostHttp = new MyBroadCastReceiver();
		this.progressDialog = ProgressDialog.show(OrderDetailActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		// this.llPayContent = (LinearLayout) findViewById(R.id.ll_payContent);
		// this.llGoodContent = (LinearLayout)
		// findViewById(R.id.ll_goodContent);
		// this.llReceiveContent = (LinearLayout)
		// findViewById(R.id.ll_receiveContent);
		// this.llTransInfoContent = (LinearLayout)
		// findViewById(R.id.ll_transInfoContent);
		this.llOtherInfoContent = (LinearLayout) findViewById(R.id.ll_otherInfoContent);
		// this.llLogContent = (LinearLayout) findViewById(R.id.ll_logContent);
		this.llLogList = (LinearLayout) findViewById(R.id.ll_orderdetail_logContent);
		this.strFromPage = getIntent().getStringExtra("frompage");
		this.pageCode = getIntent().getStringExtra("pageCode");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("AllOrder".equals(strFromPage)) {
				setResult(99);
			} else {
				setResult(98);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加点击事件
	 */
	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("AllOrder".equals(strFromPage)) {
					setResult(99);
				} else {
					setResult(98);
				}
				finish();
			}
		});
		// 发货按钮
		btnOrderDetailSendOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String transNo = etTransNo.getText().toString();
				String transOtherInfo = etTransOtherInfo.getText().toString();

				if (!"".equals(transNo) && param.containsKey("invoice_code") && param.containsKey("invoice_inc")) {
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=shipped";
					progressDialog.show();
					param.put("token", token);
					param.put("order_id", order_id);
					param.put("invoice_no", transNo);
					if (!"".equals(transOtherInfo)) {
						param.put("remark", transOtherInfo);
					}
					iPostOrderDetail.putExtra("uri", uri);
					iPostOrderDetail.putExtra("param", param);
					iPostOrderDetail.putExtra("type", "shipped_OrderDetail");
					startService(iPostOrderDetail);
					isPostServiceRunning = true;
					param.clear();
				} else {
					MyUtil.ToastMessage(OrderDetailActivity.this, "快递单号不能为空");
				}

			}
		});

		spinCompany.setOnItemSelectedListener(new OnItemSelectedListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				String invoice_code = map.get("express_code");
				String invoice_inc = map.get("tv_company_title");
				param.put("invoice_code", invoice_code);
				param.put("invoice_inc", invoice_inc);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		llGoodsInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		llTransInfoContentImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int visibility = llOrderTransInfoContent.getVisibility();
				if (visibility == View.GONE) {
					llOrderTransInfoContent.setVisibility(View.VISIBLE);
					ivTransInfoContent.setImageResource(R.drawable.up);
				} else if (visibility == View.VISIBLE) {
					llOrderTransInfoContent.setVisibility(View.GONE);
					ivTransInfoContent.setImageResource(R.drawable.down);
				}
			}
		});

		llOrderDetailGoodChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int visibility = llGoodsInfo.getVisibility();
				if (visibility == View.GONE) {
					llGoodsInfo.setVisibility(View.VISIBLE);
					ivOrderDetailGoodChange.setImageResource(R.drawable.up);
				} else if (visibility == View.VISIBLE) {
					llGoodsInfo.setVisibility(View.GONE);
					ivOrderDetailGoodChange.setImageResource(R.drawable.down);
				}
			}
		});
		// 点击修改【物流信息】
		llScanTransInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("20".equals(order_state) || "11".equals(order_state) || "30".equals(order_state)) {
					param.clear();
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=default&act=expresslist";
					progressDialog.show();
					iGetOrderDetail.putExtra("uri", uri);
					iGetOrderDetail.putExtra("param", param);
					iGetOrderDetail.putExtra("type", "expresslist");
					startService(iGetOrderDetail);
					isGetServiceRunning = true;
				} else {
					MyUtil.ToastMessage(mOrderDetailContext, "该订单不能修改物流信息");
				}
			}
		});
		// 点击隐藏【备注信息】
		llOtherInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int visibility = tvOtherInfo.getVisibility();
				if (visibility == View.GONE) {
					tvOtherInfo.setVisibility(View.VISIBLE);
					ivOtherInfo.setImageResource(R.drawable.up);
				} else if (visibility == View.VISIBLE) {
					tvOtherInfo.setVisibility(View.GONE);
					ivOtherInfo.setImageResource(R.drawable.down);
				}
			}
		});
		llOrderDetailLog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int visibility = llLogList.getVisibility();
				if (visibility == View.GONE) {
					llLogList.setVisibility(View.VISIBLE);
					ivOrderDetailLog.setImageResource(R.drawable.up);
				} else if (visibility == View.VISIBLE) {
					llLogList.setVisibility(View.GONE);
					ivOrderDetailLog.setImageResource(R.drawable.down);
				}
			}
		});

		btnOrderDetailCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("11".equals(order_state)) {
					Intent it = new Intent(mOrderDetailContext, CancelOrderActivity.class);
					it.putExtra("token", token);
					it.putExtra("order_id", order_id);
					startActivityForResult(it, MyConstant.RESULTCODE_32);
				} else {
					MyUtil.ToastMessage(mOrderDetailContext, "亲，该订单还不能取消哦！");
				}
			}
		});
		// 查看物流按钮
		btnQueryTransInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				param.clear();
				param.put("token", token);
				param.put("order_id", order_id);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=express";
				progressDialog.show();
				iGetOrderDetail.putExtra("uri", uri);
				iGetOrderDetail.putExtra("param", param);
				iGetOrderDetail.putExtra("type", "express_orderDetail");
				startService(iGetOrderDetail);
				isGetServiceRunning = true;
			}
		});
		// 调整费用按钮
		tvChangeMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(OrderDetailActivity.this, ChangeMoneyActivity.class);
				it.putExtra("token", token);
				it.putExtra("order_id", order_id);
				it.putExtra("consignee", consignee);
				it.putExtra("order_sn", order_sn);
				it.putExtra("order_amount", order_amount);
				it.putExtra("goods_amount", goods_amount);
				it.putExtra("shipping_fee", shipping_fee);
				it.putExtra("shipping_insurance", shipping_insurance);
				it.putExtra("final_amount", final_amount);
				startActivityForResult(it, MyConstant.RESULTCODE_33);
			}
		});
	}

	private void addGoodsList(JSONArray goodList) throws JSONException {
		LayoutInflater inflater = (LayoutInflater) mOrderDetailContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llGoodsList.removeAllViews();
		int length = goodList.length();
		if (length > 0) {

			for (int i = 0; i < length; i++) {
				View goodsView = inflater.inflate(R.layout.ordergoodsitem, null);
				ImageView ivImg = (ImageView) goodsView.findViewById(R.id.iv_ordergoods_img);
				TextView tvName = (TextView) goodsView.findViewById(R.id.tv_ordergoods_goodsName);
				TextView tvBelong = (TextView) goodsView.findViewById(R.id.tv_ordergoods_belong);
				TextView tvColor = (TextView) goodsView.findViewById(R.id.tv_ordergoods_colorShow);
				TextView tvNum = (TextView) goodsView.findViewById(R.id.tv_ordergoods_numberShow);
				TextView tvPrice = (TextView) goodsView.findViewById(R.id.tv_ordergoods_price);

				JSONObject obj = goodList.getJSONObject(i);
				String goods_name = obj.getString("goods_name");
				String goods_image = obj.getString("goods_image");
				String specification = obj.getString("specification");
				String price = obj.getString("price");
				String quantity = obj.getString("quantity");
				ext_activity_type_name = obj.getString("ext_activity_type_name");

				if (!"".equals(specification)) {
					tvColor.setText(specification);
				} else {
					tvColor.setText("无");
				}

				String url = MyUtil.getImageURL(goods_image, "280", "280");
				AsynImageLoader asynImageLoader = new AsynImageLoader();
				asynImageLoader.showImageAsyn(ivImg, url, R.drawable.test280280);
				tvBelong.setText(ext_activity_type_name);
				tvName.setText(goods_name);
				tvNum.setText(quantity);
				tvPrice.setText("￥" + price);

				llGoodsList.addView(goodsView);
			}
		}
	}

	/**
	 * 加载页面数据
	 */
	private void parseData(String pdata) throws JSONException {
		Log.e("CCCCC", pdata);
		JSONTokener jasonParser = new JSONTokener(pdata);
		JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();

		JSONArray goods_list = jsonReturn.getJSONArray("goods_list");
		JSONArray order_logs = jsonReturn.getJSONArray("order_logs");

		order_id = jsonReturn.getString("order_id");
		order_sn = jsonReturn.getString("order_sn");
		order_state = jsonReturn.getString("status");
		extension = MyUtil.dealNullString(jsonReturn.getString("extension"));
		evaluation_status = jsonReturn.getString("evaluation_status");
		String is_change = jsonReturn.getString("is_change");
		if ("0".equals(is_change)) {
			tvChanged.setVisibility(View.GONE);
			tvSumpriceChanged.setVisibility(View.GONE);
		} else if ("1".equals(is_change)) {
			tvChanged.setVisibility(View.VISIBLE);
			tvSumpriceChanged.setVisibility(View.VISIBLE);
		}

		// String order_date = jsonReturn.getString("add_time");
		// MyUtil.dealNullString(jsonReturn.getString("goods_amount"));
		order_amount = MyUtil.dealNullString(jsonReturn.getString("order_amount"));
		goods_amount = MyUtil.dealNullString(jsonReturn.getString("goods_amount"));
		final_amount = MyUtil.dealNullString(jsonReturn.getString("final_amount"));
		String payment_name = MyUtil.dealNullString(jsonReturn.getString("payment_name"));
		Object obj = jsonReturn.get("order_extm");
		String str1 = obj.toString();
		if (!"".equals(str1)) {
			JSONTokener jasonParser1 = new JSONTokener(pdata);
			JSONObject jsonReturn1 = (JSONObject) jasonParser1.nextValue();
			JSONObject order_extm = jsonReturn1.getJSONObject("order_extm");
			consignee = MyUtil.dealNullString(order_extm.getString("consignee"));
			shipping_fee = MyUtil.dealNullString(order_extm.getString("shipping_fee"));
			phone_tel = MyUtil.dealNullString(order_extm.getString("phone_tel"));
			phone_mob = MyUtil.dealNullString(order_extm.getString("phone_mob"));
			region_name = MyUtil.dealNullString(order_extm.getString("region_name"));
			address = MyUtil.dealNullString(order_extm.getString("address"));

			if (!"无".equals(phone_tel) && !"无".equals(phone_mob)) {
				if (!phone_tel.equals(phone_mob)) {
					phone_tel = phone_tel + ", " + phone_mob;
				}
			} else if ("无".equals(phone_tel) && !"无".equals(phone_mob)) {
				phone_tel = phone_mob;
			} else if ("无".equals(phone_tel) && "无".equals(phone_mob)) {
				phone_tel = "无";
			}

			tvTransPrice.setText("￥" + shipping_fee);
			tvInsurance.setText("￥" + shipping_insurance);
			tvSumPrice.setText("￥" + order_amount);
			tvReceiver.setText(consignee);
			tvReceivePhone.setText(phone_tel);
			tvReceiveAddress.setText(region_name + address);
		}
		invoice_inc = MyUtil.dealNullString(jsonReturn.getString("invoice_inc"));
		invoice_no = MyUtil.dealNullString(jsonReturn.getString("invoice_no"));
		shipping_insurance = MyUtil.dealNullString(jsonReturn.getString("shipping_insurance"));
		// invoice_code = jsonReturn.getString("invoice_code");
		ship_time = MyUtil.millisecondsToStr(jsonReturn.getString("ship_time"));
		String postscript = jsonReturn.getString("postscript");
		String pay_time = MyUtil.dealNullString(jsonReturn.getString("pay_time"));

		tvOrderNo.setText(order_sn);
		tvOrderState.setText(MyUtil.codeToString(order_state));
		tvOrderState.setTextColor(MyUtil.stateColor(OrderDetailActivity.this, MyUtil.codeToString(order_state)));
		// tvOrderDate.setText(MyUtil.millisecondsToStr(order_date));
		tvOrderMoney.setText("￥" + final_amount);
		tvPayType.setText(payment_name);

		if (!"无".equals(pay_time)) {
			tvPayDate.setText(MyUtil.millisecondsToStr(pay_time));
		} else {
			tvPayDate.setText("无");
		}
		addGoodsList(goods_list);

		if ("".equals(invoice_inc) || "null".equals(invoice_inc)) {
			tvTransCompany.setText("无");
		} else {
			tvTransCompany.setText(invoice_inc);
		}
		if ("".equals(invoice_no) || "null".equals(invoice_no)) {
			tvTransNo.setText("无");
		} else {
			tvTransNo.setText(invoice_no);
		}
		if ("".equals(ship_time) || "null".equals(ship_time)) {
			tvTransTime.setText("无");
		} else {
			tvTransTime.setText(ship_time);
		}
		if ("".equals(postscript) || "null".equals(postscript)) {
			tvOtherInfo.setText("无");
			tvTransOtherInfo.setText("无");
		} else {
			tvOtherInfo.setText(postscript);
			tvTransOtherInfo.setText(postscript);
		}

		logInfo = order_logs.toString();
		showLogInfo(logInfo);
		setContentShow();// 根据订单状态判断显示内容
	}

	/**
	 * 展示操作日志内容
	 */
	private void showLogInfo(String log) throws JSONException {
		if (!"".equals(log)) {
			JSONTokener jasonParser = new JSONTokener(log);
			JSONArray array = (JSONArray) jasonParser.nextValue();
			int length = array.length();
			if (length > 0) {
				llLogList.removeAllViews();
				for (int i = 0; i < length; i++) {
					View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.operationlogitem, null);
					TextView tvLogContent = (TextView) view.findViewById(R.id.tv_logItem_content);
					TextView tvLogTime = (TextView) view.findViewById(R.id.tv_logItem_time);
					TextView tvLogOperator = (TextView) view.findViewById(R.id.tv_logItem_operator);
					JSONObject obj = array.getJSONObject(i);
					String operator = obj.getString("operator");
					String remark = obj.getString("remark");
					String log_time = obj.getString("log_time");
					String order_status = obj.getString("order_status");
					String changed_status = obj.getString("changed_status");

					CharSequence csOrder_status = Html.fromHtml(order_status);
					CharSequence csChanged_status = Html.fromHtml(changed_status);

					StringBuilder sb = new StringBuilder();
					sb.append("将订单状态从");
					if (MyUtil.isContainHTML(order_status)) {
						sb.append(Html.fromHtml(order_status));
					} else {
						sb.append(order_status);
					}
					sb.append("改变为");
					if (MyUtil.isContainHTML(changed_status)) {
						sb.append(Html.fromHtml(changed_status));
					} else {
						sb.append(changed_status);
					}
					if (!"".equals(remark)) {
						sb.append(" 原因：").append(remark);
					}
					String contentStr = sb.toString();
					SpannableString style = new SpannableString(contentStr);
					style = MyUtil.changePartOfStringColor(OrderDetailActivity.this, style,
							String.valueOf(csOrder_status));
					style = MyUtil.changePartOfStringColor(OrderDetailActivity.this, style,
							String.valueOf(csChanged_status));

					tvLogContent.setText(style);
					tvLogTime.setText(MyUtil.millisecondsToStr(log_time));
					tvLogOperator.setText(operator);
					llLogList.addView(view);
				}
			} else {
				llLogList.removeAllViews();
				View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.operationlogitem, null);
				TextView tvLogContent = (TextView) view.findViewById(R.id.tv_logItem_content);
				tvLogContent.setText("该订单暂无操作日志");
				llLogList.addView(view);
			}
		}
	}

	/**
	 * 获取物流公司数据
	 */
	private void callCompanyData() {
		param.clear();
		String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=default&act=expresslist";
		progressDialog.show();
		iGetOrderDetail.putExtra("uri", uri);
		iGetOrderDetail.putExtra("param", param);
		iGetOrderDetail.putExtra("type", "expresslist_orderDetail");
		startService(iGetOrderDetail);
		isGetServiceRunning = true;
	}

	/**
	 * 根据订单状态判断显示内容
	 */
	private void setContentShow() {
		if ("11".equals(order_state)) { // 待付款
			this.tvTitle.setText("订单详情");
			llTransInfoInput.setVisibility(View.GONE); // 可输入物流信息
			llTransInfo.setVisibility(View.GONE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.VISIBLE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.GONE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.GONE);// 发货按钮
			llScanTransInfo.setVisibility(View.GONE);// 查看物流公司
			llTransInfoContent.setVisibility(View.GONE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.VISIBLE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.waitpay1_logo);
		} else if ("20".equals(order_state)) {// 待发货
			this.tvTitle.setText("确认订单");
			llTransInfoInput.setVisibility(View.VISIBLE); // 可输入物流信息
			llTransInfo.setVisibility(View.GONE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.GONE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.VISIBLE);// 发货按钮
			llScanTransInfo.setVisibility(View.GONE);// 查看物流公司
			llTransInfoContent.setVisibility(View.GONE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.GONE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.waitpay_logo);
		} else if ("30".equals(order_state)) {// 已发货
			this.tvTitle.setText("订单详情");
			llTransInfoInput.setVisibility(View.GONE); // 可输入物流信息
			llTransInfo.setVisibility(View.VISIBLE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.VISIBLE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.GONE);// 发货按钮
			llScanTransInfo.setVisibility(View.VISIBLE);// 查看物流公司
			llTransInfoContent.setVisibility(View.GONE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.GONE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.other_logo);
		} else if ("40".equals(order_state)) {// 已完成
			this.tvTitle.setText("订单详情");
			llTransInfoInput.setVisibility(View.GONE); // 可输入物流信息
			llTransInfo.setVisibility(View.VISIBLE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.VISIBLE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.GONE);// 发货按钮
			llScanTransInfo.setVisibility(View.GONE);// 查看物流公司
			llTransInfoContent.setVisibility(View.GONE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.GONE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.other_logo);
			String str = tvOrderState.getText().toString();
			System.out.println("evaluation_status   " + evaluation_status);
			if ("0".equals(evaluation_status)) {
				tvEvaluateState.setText("未评价");
				tvEvaluateState
						.setTextColor(MyUtil.stateColor(OrderDetailActivity.this, MyUtil.codeToString(order_state)));
			} else if ("1".equals(evaluation_status)) {
				tvEvaluateState.setText("已评价");
				tvEvaluateState
						.setTextColor(MyUtil.stateColor(OrderDetailActivity.this, MyUtil.codeToString(order_state)));
			}
			tvOrderState.setText(str);
		} else if ("50".equals(order_state)) {// 已退款
			this.tvTitle.setText("订单详情");
			llTransInfoInput.setVisibility(View.GONE); // 可输入物流信息
			llTransInfo.setVisibility(View.GONE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.GONE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.GONE);// 发货按钮
			llScanTransInfo.setVisibility(View.GONE);// 查看物流公司
			llTransInfoContent.setVisibility(View.VISIBLE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.GONE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.other_logo);
		} else if ("0".equals(order_state)) {// 已取消
			this.tvTitle.setText("订单详情");
			llTransInfoInput.setVisibility(View.GONE); // 可输入物流信息
			llTransInfo.setVisibility(View.GONE); // 查看物流信息
			llOtherInfoContent.setVisibility(View.VISIBLE); // 备注信息
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
			btnQueryTransInfo.setVisibility(View.GONE);// 查看物流按钮
			btnOrderDetailSendOrder.setVisibility(View.GONE);// 发货按钮
			llScanTransInfo.setVisibility(View.GONE);// 查看物流公司
			llTransInfoContent.setVisibility(View.GONE);// 物流信息
			btnOrderDetailCancel.setVisibility(View.GONE);// 取消订单按钮
			tvLogo.setBackgroundResource(R.drawable.other_logo);
		}
		if ("tuan".equals(extension)) {
			tvChangeMoney.setVisibility(View.GONE);// 调整费用按钮
		}
	}

	/**
	 * 注册广播
	 */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		OrderDetailActivity.this.registerReceiver(brGetHttp, filterGetHttp);

		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		OrderDetailActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
						parseGetData(strReturnType, result);
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
						parsePostData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 解析post返回数据
	 */
	private void parsePostData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("shipped_OrderDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(mOrderDetailContext, "发货成功");
				if ("need".equals(pageCode)) {
					setResult(MyConstant.RESULTCODE_30);
				} else if ("home".equals(pageCode)) {
					setResult(MyConstant.RESULTCODE_15);
				} else if ("orderResult".equals(pageCode)) {
					setResult(MyConstant.RESULTCODE_38);
				}
				OrderDetailActivity.this.finish();
			} else {
				MyUtil.ToastMessage(mOrderDetailContext, msg);
			}
		}
	}

	/**
	 * 解析get返回数据
	 */
	private void parseGetData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("expresslist".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mOrderDetailContext, TransInfoActivity.class);
				System.out.println("invoice_no" + invoice_no);
				it.putExtra("token", token);
				it.putExtra("data", data);
				it.putExtra("invoice_no", invoice_no);
				it.putExtra("invoice_inc", invoice_inc);
				it.putExtra("order_id", order_id);
				startActivityForResult(it, MyConstant.RESULTCODE_31);
			} else {
				MyUtil.ToastMessage(mOrderDetailContext, msg);
			}

		} else if ("expresslist_orderDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				lstCompany.clear();
				JSONTokener jsonParser1 = new JSONTokener(data);
				JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
				JSONArray arrayNormal = jsonReturn1.getJSONArray("normal");
				JSONArray arrayOther = jsonReturn1.getJSONArray("other");
				int lengthNormal = arrayNormal.length();
				int lengthOther = arrayOther.length();
				for (int i = 0; i < lengthNormal; i++) {
					JSONObject jsonReturn2 = arrayNormal.getJSONObject(i);
					String express_inc = jsonReturn2.getString("express_inc");
					String express_code = jsonReturn2.getString("express_code");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("express_code", express_code);
					map.put("tv_company_title", express_inc);
					lstCompany.add(map);
				}

				for (int i = 0; i < lengthOther; i++) {
					JSONObject jsonReturn2 = arrayOther.getJSONObject(i);
					String express_inc = jsonReturn2.getString("express_inc");
					String express_code = jsonReturn2.getString("express_code");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("express_code", express_code);
					map.put("tv_company_title", express_inc);
					if (!lstCompany.contains(map)) {
						lstCompany.add(map);
					}
				}
				SimpleAdapter sa = (SimpleAdapter) spinCompany.getAdapter();
				sa.notifyDataSetChanged();
			} else {
				MyUtil.ToastMessage(mOrderDetailContext, msg);
			}

		} else if ("express_orderDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				String data = jsonReturn.getString("data");
				Intent it = new Intent(mOrderDetailContext, ScanTransInfoActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				it.putExtra("order_id", order_id);
				startActivity(it);
			} else {
				MyUtil.ToastMessage(OrderDetailActivity.this, msg);
			}

		} else if ("orderDetail_refresh".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				parseData(data);
			} else {
				MyUtil.ToastMessage(OrderDetailActivity.this, msg);
			}
		}
	}

}
