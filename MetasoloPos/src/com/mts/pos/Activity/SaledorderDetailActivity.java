package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.ListViewForScrollView;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.SaledOrderProductAdapter;
import com.mts.pos.listview.SaledOrderProductInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SaledorderDetailActivity extends BaseActivity {

	private Button btn_back, btn_return;
	private TextView tv_client_name, tv_order_time, tv_alipay, tv_cash, tv_gift, tv_visa, tv_wechat, tv_order_sum,
			tv_order_payed, tv_order_return;
	private ListViewForScrollView lv_saled_detail, lv_saled_promo_detail;
	private SaledOrderProductInfo saledorderproductInfo;
	private List<SaledOrderProductInfo> saledorderproductData;
	private SaledOrderProductAdapter adapter;
	private Intent intent;
	private String orderId;
	private int RETURN_RESULT = 1000;
	private Double orderSum = 0.00, cashvalue = 0.00, giftvalue = 0.00, wechatvalue = 0.00, alipayvalue = 0.00,
			visavalue = 0.00, unReturn = 0.00;
	private Boolean cash = false, gift = false, wechat = false, alipay = false, visa = false;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_saledorder_detail);
		super.onCreate(inState);

		initView();

		intent = getIntent();
		orderId = intent.getStringExtra("orderId");

		getOrderDetail();

		saledorderproductData = new ArrayList<SaledOrderProductInfo>();
		adapter = new SaledOrderProductAdapter(SaledorderDetailActivity.this, saledorderproductData, orderId);
		lv_saled_detail.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SaledorderDetailActivity.this, ReturnMoneyActivity.class);
				Bundle b = new Bundle();
				b.putString("orderId", orderId);
				b.putDouble("returnSum", unReturn);
				intent.putExtras(b);

				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "订单详情==" + result);
			try {
				JSONObject Obj = new JSONObject(result);
				JSONObject objDetail = Obj.optJSONObject("orderDetails");
				tv_client_name.setText("#" + objDetail.optString("orderId") + "-" + objDetail.optString("firstName"));

				JSONObject time = objDetail.optJSONObject("orderDate");

				Integer monthI = Integer.valueOf(time.getString("month")) + 1;
				String month;
				if (monthI < 10) {
					month = "0" + monthI.toString();
				} else {
					month = monthI.toString();
				}

				Integer secondI = Integer.valueOf(time.getString("seconds"));
				String second;
				if (secondI < 10) {
					second = "0" + secondI.toString();
				} else {
					second = secondI.toString();
				}

				Integer minuteI = Integer.valueOf(time.getString("minutes"));
				String minute;
				if (minuteI < 10) {
					minute = "0" + minuteI.toString();
				} else {
					minute = minuteI.toString();
				}

				Integer dateI = Integer.valueOf(time.getString("date"));
				String date;
				if (dateI < 10) {
					date = "0" + dateI.toString();
				} else {
					date = dateI.toString();
				}

				Integer hoursI = Integer.valueOf(time.getString("hours"));
				String hours;
				if (hoursI < 10) {
					hours = "0" + hoursI.toString();
				} else {
					hours = hoursI.toString();
				}

				String stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-" + month
						+ "-" + date + "  " + hours + ":" + minute + ":" + second;

				tv_order_time.setText("订单时间：" + stamp);
				tv_order_sum.setText("订单金额：￥" + objDetail.optDouble("grandTotal"));
				orderSum = objDetail.optDouble("grandTotal");
				
				if ("null".equals(objDetail.optString("orderPaymentReturned"))) {
					unReturn = orderSum;
				} else {
					unReturn = orderSum - objDetail.optDouble("orderPaymentReturned");
				}

				tv_order_payed.setText("已收款：￥" + objDetail.optString("orderPaymentReceived"));

				if (!objDetail.optString("orderPaymentReturned").equals("null")) {
					tv_order_return.setText("已退款：￥" + objDetail.optString("orderPaymentReturned"));
				} else {
					tv_order_return.setText("已退款：￥ 0");
				}

				if (objDetail.optString("orderPaymentReceived").equals(objDetail.optString("orderPaymentReturned"))) {
					btn_return.setEnabled(false);
					btn_return.setBackgroundColor(0x7f070017);
				}

				JSONArray jsonarrayProduct = Obj.optJSONArray("orderItems");

				for (int i = 0; i < jsonarrayProduct.length(); i++) {
					JSONObject joItem = (JSONObject) jsonarrayProduct.opt(i);
					saledorderproductInfo = new SaledOrderProductInfo();
					saledorderproductInfo.setColor(joItem.optString("colorDesc"));
					saledorderproductInfo.setCount(joItem.optInt("quantity"));
					saledorderproductInfo.setId(joItem.optString("productId"));
					saledorderproductInfo.setIsReturn(joItem.optInt("returnQuantity"));
					saledorderproductInfo.setName(joItem.optString("productName"));
					saledorderproductInfo.setPrice(joItem.optDouble("unitPrice"));
					saledorderproductInfo.setSize(joItem.optString("dimensionDesc"));
					saledorderproductInfo.setStyle(joItem.optString("modelId"));
					saledorderproductInfo.setUrl(joItem.optString("skuImageUrl"));

					saledorderproductData.add(saledorderproductInfo);
					adapter.notifyDataSetChanged();

				}

				if (Obj.optJSONArray("adjustmentDetails").length() == 0
						|| Obj.optJSONArray("adjustmentDetails") == null) {
					lv_saled_promo_detail.setVisibility(View.GONE);
				} else {
					lv_saled_promo_detail.setVisibility(View.VISIBLE);
				}

				JSONArray jsonarrayPayment = Obj.optJSONArray("paymentDetails");
				for (int i = 0; i < jsonarrayPayment.length(); i++) {
					JSONObject jo = (JSONObject) jsonarrayPayment.opt(i);

					if (jo.optString("paymentMethodTypeId").equals("CASH")) {
						cash = true;
						cashvalue = jo.optDouble("maxAmount");
					} else if (jo.optString("paymentMethodTypeId").equals("GIFT_CARD")) {
						gift = true;
						giftvalue = jo.optDouble("maxAmount");
					} else if (jo.optString("paymentMethodTypeId").equals("CREDIT_CARD")) {
						visa = true;
						visavalue = jo.optDouble("maxAmount");
					} else if (jo.optString("paymentMethodTypeId").equals("EXT_ALIPAY")) {
						alipay = true;
						alipayvalue = jo.optDouble("maxAmount");
					} else if (jo.optString("paymentMethodTypeId").equals("EXT_WECHAT")) {
						wechat = true;
						wechatvalue = jo.optDouble("maxAmount");
					}
				}

				Log.e("查看结果", "查看结果==" + cash.toString() + "||" + wechat.toString() + "||" + gift.toString() + "||"
						+ visa.toString() + "||" + alipay.toString());

				if (cash) {
					tv_cash.setVisibility(View.VISIBLE);
					tv_cash.setText("现金：￥" + cashvalue);
				} else {
					tv_cash.setVisibility(View.GONE);
				}

				if (gift) {
					tv_gift.setVisibility(View.VISIBLE);
					tv_gift.setText("礼品卡：￥" + giftvalue);
				} else {
					tv_gift.setVisibility(View.GONE);
				}

				if (alipay) {
					tv_alipay.setVisibility(View.VISIBLE);
					tv_alipay.setText("支付宝：￥" + alipayvalue);
				} else {
					tv_alipay.setVisibility(View.GONE);
				}

				if (wechat) {
					tv_wechat.setVisibility(View.VISIBLE);
					tv_wechat.setText("微信：￥" + wechatvalue);
				} else {
					tv_wechat.setVisibility(View.GONE);
				}

				if (visa) {
					tv_visa.setVisibility(View.VISIBLE);
					tv_visa.setText("银行卡：￥" + visavalue);
				} else {
					tv_visa.setVisibility(View.GONE);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {

			if (data.getStringExtra("key").equals("OK")) {
				saledorderproductData.clear();
				getOrderDetail();
				adapter = new SaledOrderProductAdapter(SaledorderDetailActivity.this, saledorderproductData, orderId);
				lv_saled_detail.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} else {

			}

		} else if (requestCode == 1) {
			if (data.getStringExtra("swhich").equals("OK")) {
				saledorderproductData.clear();
				getOrderDetail();
				adapter = new SaledOrderProductAdapter(SaledorderDetailActivity.this, saledorderproductData, orderId);
				lv_saled_detail.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} else {

			}

		}
	}

	private void initView() {
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_return = (Button) findViewById(R.id.btn_return);
		tv_client_name = (TextView) findViewById(R.id.tv_client_name);
		tv_order_time = (TextView) findViewById(R.id.tv_order_time);
		tv_alipay = (TextView) findViewById(R.id.tv_alipay);
		tv_cash = (TextView) findViewById(R.id.tv_cash);
		tv_gift = (TextView) findViewById(R.id.tv_gift);
		tv_wechat = (TextView) findViewById(R.id.tv_wechat);
		tv_visa = (TextView) findViewById(R.id.tv_wechat);
		tv_order_sum = (TextView) findViewById(R.id.tv_order_sum);
		tv_order_payed = (TextView) findViewById(R.id.tv_order_payed);
		tv_order_return = (TextView) findViewById(R.id.tv_order_return);
		lv_saled_detail = (ListViewForScrollView) findViewById(R.id.lv_saled_detail);
		lv_saled_promo_detail = (ListViewForScrollView) findViewById(R.id.lv_saled_promo_detail);
	}

	private void getOrderDetail() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(SaledorderDetailActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", orderId));

		getTask(SaledorderDetailActivity.this, Urls.base + Urls.get_orderdetails, nameValuePair, "0");
	}

}
