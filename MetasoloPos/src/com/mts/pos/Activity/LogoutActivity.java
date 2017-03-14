package com.mts.pos.Activity;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class LogoutActivity extends BaseActivity {

	private TextView tv_username, tv_order, tv_sale, tv_order_pay, tv_traffic, tv_conversion, tv_cash_pay,
			tv_alipay_pay, tv_wechat_pay, tv_visa_pay, tv_gift_pay, tv_logout_cancal, tv_logout_enter;
	private String orderCount = "", saleCount = "", orderSum = "", footfall = "", orderPercent = "", cashSum = "",
			alipaySum = "", wechatSum = "", visaSum = "", giftSum = "";

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_logout);
		super.onCreate(inState);

		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_order = (TextView) findViewById(R.id.tv_order);
		tv_sale = (TextView) findViewById(R.id.tv_sale);
		tv_order_pay = (TextView) findViewById(R.id.tv_order_pay);
		tv_traffic = (TextView) findViewById(R.id.tv_traffic);
		tv_conversion = (TextView) findViewById(R.id.tv_conversion);
		tv_cash_pay = (TextView) findViewById(R.id.tv_cash_pay);
		tv_alipay_pay = (TextView) findViewById(R.id.tv_alipay_pay);
		tv_wechat_pay = (TextView) findViewById(R.id.tv_wechat_pay);
		tv_visa_pay = (TextView) findViewById(R.id.tv_visa_pay);
		tv_gift_pay = (TextView) findViewById(R.id.tv_gift_pay);
		tv_logout_cancal = (TextView) findViewById(R.id.tv_logout_cancal);
		tv_logout_enter = (TextView) findViewById(R.id.tv_logout_enter);

		getLogoutInfo();

		tv_username.setText("当前登录用户：" + Localxml.search(LogoutActivity.this, "username"));

		tv_logout_cancal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tv_logout_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				setResult(112, intent);
				finish();

			}
		});

	}

	public void getLogoutInfo() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(LogoutActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("posTerminalId", Localxml.search(LogoutActivity.this, "posid")));
		Log.e("posID", "posId==" + Localxml.search(LogoutActivity.this, "posid"));
		getTask(LogoutActivity.this, Urls.base + Urls.logout_message, nameValuePair, "0");
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "换班信息==" + result);
			try {
				JSONObject ob = new JSONObject(result).optJSONObject("result");
				Log.e("LOOK", "下班数据==" + ob.toString());
				orderCount = ob.optString("ordersize");
				saleCount = ob.optString("quantity");
				orderSum = ob.optString("totle");
				footfall = ob.optString("totlePeople");
				orderPercent = ob.optString("conversion");
				cashSum = ob.optString("cash").equals("") ? "0" : ob.optString("cash");
				alipaySum = ob.optString("alipay").equals("") ? "0" : ob.optString("alipay");
				wechatSum = ob.optString("wechat").equals("") ? "0" : ob.optString("wechat");
				visaSum = ob.optString("creditCard").equals("") ? "0" : ob.optString("creditCard");
				giftSum = ob.optString("giftCard").equals("") ? "0" : ob.optString("giftCard");

				tv_order.setText(orderCount);
				tv_sale.setText(saleCount);
				tv_order_pay.setText("￥: " + orderSum);
				tv_traffic.setText(footfall);
				tv_conversion.setText(orderPercent + " %");
				tv_cash_pay.setText("￥: " + cashSum);
				tv_alipay_pay.setText("￥: " + alipaySum);
				tv_wechat_pay.setText("￥: " + wechatSum);
				tv_visa_pay.setText("￥: " + visaSum);
				tv_gift_pay.setText("￥: " + giftSum);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
