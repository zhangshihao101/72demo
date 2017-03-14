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
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 【发货】页
 * */
public class SendOrderActivity extends BaseActivity {

	private MyTitleBar mtbSendOrder;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Button btnSendOrderOk;
	private EditText etTransNo;
	private Spinner spinTransCompany;
	private Intent itFrom;
	private Intent iPostSend;
	private boolean isPostServiceRunning = false;
	private HashMap<String, Object> param;
	private String token;
	private String order_id;
	private String invoice_no;
	private String invoice_code;
	private String invoice_inc;
	private String remark;
	private BroadcastReceiver brPostHttp;
	private LinearLayout llReceiveInfo;
	private TextView tvReceiver;
	private TextView tvReceivePhone;
	private TextView tvReceiveAddress;
	private LinearLayout llLeft;
	private ProgressDialog progressDialog;
	private EditText etOtherInfo;
	private boolean isSuccess = false;
	private String phone_tel;
	private String region_name;
	private Intent iGetSend;
	private BroadcastReceiver brGetHttp;
	private boolean isGetServiceRunning = false;
	private List<String> lstCompany;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.sendorder);
		super.onCreate(savedInstanceState);
		callExpresslist();
		try {
			initContent();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	protected void onStop() {

		SendOrderActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostSend);
			isPostServiceRunning = false;
		}
		SendOrderActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetSend);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSendOrderOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				invoice_no = etTransNo.getText().toString();
				remark = etOtherInfo.getText().toString();
				param.clear();
				param.put("token", token);
				param.put("order_id", order_id);
				param.put("invoice_no", invoice_no);
				param.put("invoice_code", invoice_code);
				param.put("invoice_inc", invoice_inc);
				param.put("remark", remark);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=shipped";
				progressDialog.show();
				iPostSend.putExtra("uri", uri);
				iPostSend.putExtra("param", param);
				iPostSend.putExtra("type", "sendOrder");
				startService(iPostSend);
				isPostServiceRunning = true;
			}
		});

		// 点击内容请求【收货信息】
		llReceiveInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(SendOrderActivity.this, ReceiveInfoActivity.class);
				it.putExtra("token", token);
				it.putExtra("order_id", order_id);
				it.putExtra("Receiver", tvReceiver.getText().toString());
				it.putExtra("ReceivePhone", tvReceivePhone.getText().toString());
				it.putExtra("ReceiveAddress", tvReceiveAddress.getText().toString());
				it.putExtra("region_name", region_name);
				it.putExtra("state", "send");
				startActivityForResult(it, MyConstant.RESULTCODE_35);
			}
		});

		spinTransCompany.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				invoice_inc = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	protected void init() {
		this.mtbSendOrder = (MyTitleBar) findViewById(R.id.mtb_sendorder_title);
		this.tvTitle = mtbSendOrder.getTvTitle();
		this.ivLeft = mtbSendOrder.getIvLeft();
		this.tvTitle.setText("发货");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbSendOrder.getLlLeft();
		this.btnSendOrderOk = (Button) findViewById(R.id.btn_sendorder_ok);
		this.param = new HashMap<String, Object>();
		this.itFrom = getIntent();
		this.lstCompany = new ArrayList<String>();
		this.token = itFrom.getStringExtra("token");
		this.order_id = itFrom.getStringExtra("order_id");
		this.invoice_no = itFrom.getStringExtra("invoice_no");
		this.invoice_code = itFrom.getStringExtra("invoice_code");
		this.invoice_inc = itFrom.getStringExtra("invoice_inc");
		this.brPostHttp = new MyBroadCastReceiver();
		this.etTransNo = (EditText) findViewById(R.id.et_sendorder_transNo);
		this.spinTransCompany = (Spinner) findViewById(R.id.spin_sendorder_transCompany);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(SendOrderActivity.this, android.R.layout.simple_list_item_1,
				lstCompany);
		this.spinTransCompany.setAdapter(aa);
		this.etTransNo.setText(invoice_no);
		this.etOtherInfo = (EditText) findViewById(R.id.et_sendorder_transOther);
		this.llReceiveInfo = (LinearLayout) findViewById(R.id.ll_sendorder_receiveInfo);
		this.tvReceiver = (TextView) findViewById(R.id.tv_sendorder_receiverShow);
		this.tvReceivePhone = (TextView) findViewById(R.id.tv_sendorder_receiverPhone);
		this.tvReceiveAddress = (TextView) findViewById(R.id.tv_sendorder_receiveAdress);
		this.iPostSend = new Intent(SendOrderActivity.this, MyHttpPostService.class);
		this.iPostSend.setAction(MyConstant.HttpPostServiceAciton);
		this.progressDialog = ProgressDialog.show(SendOrderActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.iGetSend = new Intent(SendOrderActivity.this, MyHttpGetService.class);
		this.iGetSend.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver();

	}

	/**
	 * 获取物流公司列表
	 * */
	private void callExpresslist() {
		param.clear();
		String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=default&act=expresslist";
		progressDialog.show();
		iGetSend.putExtra("uri", uri);
		iGetSend.putExtra("param", param);
		iGetSend.putExtra("type", "expresslist1");
		startService(iGetSend);
		isGetServiceRunning = true;
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() throws JSONException {
		String page = itFrom.getStringExtra("page");
		if ("orderDetail".equals(page)) {
			order_id = itFrom.getStringExtra("order_id");
			invoice_no = itFrom.getStringExtra("invoice_no");
			invoice_code = itFrom.getStringExtra("invoice_code");
			invoice_inc = itFrom.getStringExtra("invoice_inc");
			region_name = itFrom.getStringExtra("region_name");
			tvReceiver.setText(itFrom.getStringExtra("consignee"));
			tvReceivePhone.setText(itFrom.getStringExtra("phone_tel"));
			tvReceiveAddress.setText(region_name + itFrom.getStringExtra("address"));
			if ("null".equals(invoice_no)) {
				etTransNo.setText("");
			} else {
				etTransNo.setText(invoice_no);
			}

			spinTransCompany.setSelection(findSpinIndex(invoice_inc));

		} else {
			String data = itFrom.getStringExtra("data");
			JSONTokener jsonParser1 = new JSONTokener(data);
			JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
			order_id = jsonReturn1.getString("order_id");
			invoice_no = jsonReturn1.getString("invoice_no");
			invoice_code = jsonReturn1.getString("invoice_code");
			invoice_inc = jsonReturn1.getString("invoice_inc");

			JSONObject order_extm = jsonReturn1.getJSONObject("order_extm");
			JSONTokener jsonParser2 = new JSONTokener(order_extm.toString());
			JSONObject jsonReturn2 = (JSONObject) jsonParser2.nextValue();
			region_name = MyUtil.dealNullString(jsonReturn2.getString("region_name"));
			String phone_mob = jsonReturn2.getString("phone_mob");
			phone_tel = MyUtil.dealNullString(jsonReturn2.getString("phone_tel"));
			if (!"无".equals(phone_tel) && !"无".equals(phone_mob)) {
				if (!phone_tel.equals(phone_mob)) {
					phone_tel = phone_tel + ", " + phone_mob;
				}
			} else if ("无".equals(phone_tel) && !"无".equals(phone_mob)) {
				phone_tel = phone_mob;
			} else if ("无".equals(phone_tel) && "无".equals(phone_mob)) {
				phone_tel = "无";
			}
			tvReceiver.setText(jsonReturn2.getString("consignee"));
			tvReceivePhone.setText(phone_tel);
			tvReceiveAddress.setText(region_name + jsonReturn2.getString("address"));
		}

		if ("null".equals(invoice_no)) {
			etTransNo.setText("");
		} else {
			etTransNo.setText(invoice_no);
		}

		spinTransCompany.setSelection(findSpinIndex(invoice_inc));

		if ("null".equals(remark)) {
			etOtherInfo.setText("");
		} else {
			etOtherInfo.setText(remark);
		}
	}

	/**
	 * 找出字符串在spinner中的位置，若不存在则返回0
	 * */
	private int findSpinIndex(String str) {
		int index = 0;
		int size = lstCompany.size();
		if (size > 0) {
			if ("".equals(invoice_inc)) {
				index = 0;
			} else if ("null".equals(invoice_inc)) {
				index = 0;
			} else {
				index = lstCompany.indexOf(invoice_inc);
			}
		} else {
			if ("".equals(invoice_inc)) {
				lstCompany.add("无");
				index = 0;
			} else if ("null".equals(invoice_inc)) {
				lstCompany.add("无");
				index = 0;
			} else {
				lstCompany.add(invoice_inc);
				index = 0;
			}
		}

		return index;
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {

		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		SendOrderActivity.this.registerReceiver(brPostHttp, filterPostHttp);

		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		SendOrderActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 内部广播类
	 * */
	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
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
			} else if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
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
			}

		}

	}

	/**
	 * 解析Post返回数据
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("sendOrder".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(SendOrderActivity.this, "发货成功");
				isSuccess = true;
				Intent it = new Intent();
				it.putExtra("isSuccess", isSuccess);
				it.putExtra("order_id", order_id);
				setResult(501, it);
				SendOrderActivity.this.finish();
			} else {
				MyUtil.ToastMessage(SendOrderActivity.this, msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseDataGet(String type, String jsonStr) throws JSONException {
		if ("expresslist1".equals(type)) {
			progressDialog.dismiss();
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
					lstCompany.add(express_inc);
				}

				for (int i = 0; i < lengthOther; i++) {
					JSONObject jsonReturn2 = arrayOther.getJSONObject(i);
					String express_inc = jsonReturn2.getString("express_inc");
					if (!lstCompany.contains(express_inc)) {
						lstCompany.add(express_inc);
					}
				}
				ArrayAdapter<String> aa = (ArrayAdapter<String>) spinTransCompany.getAdapter();
				aa.notifyDataSetChanged();
				spinTransCompany.setSelection(findSpinIndex(invoice_inc));
			} else {
				MyUtil.ToastMessage(SendOrderActivity.this, msg);
			}
		}
	}

}
