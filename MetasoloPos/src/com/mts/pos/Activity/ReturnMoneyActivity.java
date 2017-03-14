package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnMoneyActivity extends BaseActivity {

	private TextView tv_can_return_sum;
	private EditText et_return_sum;
	private Spinner sp_return_sum_type;
	private Button btn_cancel, btn_enter;
	private Intent intent;
	private String orderId = "", paymentMethod = "", invoiceId = "";
	private Double returnSum = 0.00;
	private List<String> paymentList;
	private HashMap<String, String> paymentMap;
	private ArrayAdapter<String> payment_adapter;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_return_money);
		super.onCreate(inState);

		intiView();

		intent = getIntent();
		orderId = intent.getExtras().getString("orderId");
		returnSum = intent.getExtras().getDouble("returnSum");

		tv_can_return_sum.setText("可退金额：￥ " + returnSum);

		// 获取店铺支付方式
		getPayment();

		sp_return_sum_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				paymentMethod = paymentList.get(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("swhich", "NO");
				setResult(1001, intent);
				finish();
			}
		});

		btn_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_return_sum.getText().toString().trim().equals("")) {
					Toast.makeText(ReturnMoneyActivity.this, "请输入退款金额", Toast.LENGTH_SHORT).show();
				} else if ((!et_return_sum.getText().toString().trim().equals(""))
						&& Double.valueOf(et_return_sum.getText().toString()) > returnSum) {
					Toast.makeText(ReturnMoneyActivity.this, "退款金额不能大于订单总金额", Toast.LENGTH_SHORT).show();
				} else {
					doReturnMoney();
				}
			}
		});

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "查看支付方式==" + result);
			paymentList.clear();
			paymentMap.clear();
			try {
				JSONObject object = new JSONObject(result);
				JSONObject objectdetail = object.optJSONObject("storePaymentMethods");

				if (objectdetail.optString("EFT_ACCOUNT") != null
						&& !"".equals(objectdetail.optString("EFT_ACCOUNT"))) {
					paymentList.add(objectdetail.optString("EFT_ACCOUNT"));
					paymentMap.put(objectdetail.optString("EFT_ACCOUNT"), "EFT_ACCOUNT");
				}

				if (objectdetail.optString("GIFT_CARD") != null && !"".equals(objectdetail.optString("GIFT_CARD"))) {
					paymentList.add(objectdetail.optString("GIFT_CARD"));
					paymentMap.put(objectdetail.optString("GIFT_CARD"), "GIFT_CARD");
				}

				if (objectdetail.optString("EXT_WECHAT") != null && !"".equals(objectdetail.optString("EXT_WECHAT"))) {
					paymentList.add(objectdetail.optString("EXT_WECHAT"));
					paymentMap.put(objectdetail.optString("EXT_WECHAT"), "EXT_WECHAT");
				}

				if (objectdetail.optString("EXT_ALIPAY") != null && !"".equals(objectdetail.optString("EXT_ALIPAY"))) {
					paymentList.add(objectdetail.optString("EXT_ALIPAY"));
					paymentMap.put(objectdetail.optString("EXT_ALIPAY"), "EXT_ALIPAY");
				}

				if (objectdetail.optString("CASH") != null && !"".equals(objectdetail.optString("CASH"))) {
					paymentList.add(objectdetail.optString("CASH"));
					paymentMap.put(objectdetail.optString("CASH"), "CASH");
				}

				payment_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, paymentList);
				payment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_return_sum_type.setAdapter(payment_adapter);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "查看退款结果==" + result);
			try {
				JSONObject object = new JSONObject(result);
				invoiceId = object.optString("invoiceId");
//				String message = object.optString("_ERROR_MESSAGE_");
				if ((!invoiceId.equals("")) && invoiceId != null) {
					Toast.makeText(ReturnMoneyActivity.this, "退款成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("swhich", "OK");
					setResult(1001, intent);
					finish();
				} else {
//					Toast.makeText(ReturnMoneyActivity.this, message, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void intiView() {

		tv_can_return_sum = (TextView) findViewById(R.id.tv_can_return_sum);
		et_return_sum = (EditText) findViewById(R.id.et_return_sum);
		sp_return_sum_type = (Spinner) findViewById(R.id.sp_return_sum_type);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_enter = (Button) findViewById(R.id.btn_enter);

		paymentList = new ArrayList<String>();
		paymentMap = new HashMap<String, String>();
	}

	// 获取支付方式
	private void getPayment() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnMoneyActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", orderId));

		getTask(ReturnMoneyActivity.this, Urls.base + Urls.get_paymentmethods, nameValuePair, "0");
	}

	// 退款
	private void doReturnMoney() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnMoneyActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", orderId));
		nameValuePair.add(new BasicNameValuePair("paymentMethodTypeIds", paymentMap.get(paymentMethod)));
		nameValuePair.add(new BasicNameValuePair("paymentAmounts", et_return_sum.getText().toString()));

		getTask(ReturnMoneyActivity.this, Urls.base + Urls.return_money, nameValuePair, "1");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("key", "NO");
			setResult(1001, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
