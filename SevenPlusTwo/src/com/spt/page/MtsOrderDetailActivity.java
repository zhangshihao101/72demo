package com.spt.page;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.OrderDetailAdapter;
import com.spt.bean.OrderDetailInfo;
import com.spt.common.BaseMtsActivity;
import com.spt.common.Constants;
import com.spt.common.MyPostTask;
import com.spt.common.NetworkUtil;
import com.spt.controler.ListViewForScrollView;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MtsOrderDetailActivity extends BaseMtsActivity {

	private ImageView iv_back;
	private TextView tv_order_no, tv_order_date, tv_channel_no, tv_channel, tv_store, tv_client, tv_order_state,
			tv_order_sum, tv_payed, tv_nopay, tv_promo, tv_returned, tv_pay_state, tv_product_name, tv_style, tv_size,
			tv_price, tv_color, tv_brand, tv_count, tv_sended, tv_nosend, tv_return;
	private ListViewForScrollView lv_order_item;

	private OrderDetailInfo detailInfo;
	private List<OrderDetailInfo> detailData;
	private OrderDetailAdapter detailAdapter;

	private Intent intent;
	DecimalFormat df;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detail);
		super.onCreate(inState);

		intent = getIntent();

		initView();

		getOrderDetail();

		getOrderItemDetail();

		df = new DecimalFormat("0.00");

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getOrderItemDetail() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderDetailActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", intent.getStringExtra("orderId")));

		if (NetworkUtil.isConnected(MtsOrderDetailActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderDetailActivity.this,
					MtsUrls.base + MtsUrls.get_orderitemdetails, nameValuePair, "1");
			promotask.execute("");
		} else {
			Toast.makeText(MtsOrderDetailActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}

	}

	private void getOrderDetail() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderDetailActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", intent.getStringExtra("orderId")));

		if (NetworkUtil.isConnected(MtsOrderDetailActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderDetailActivity.this, MtsUrls.base + MtsUrls.get_orderdetails,
					nameValuePair, "0");
			promotask.execute("");
		} else {
			Toast.makeText(MtsOrderDetailActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "订单详情==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONObject obj = object.optJSONObject("order");

				tv_order_no.setText("订单：" + obj.optString("orderId"));
				JSONObject time = obj.optJSONObject("orderDate");
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
						+ "-" + date;

				tv_order_date.setText("订单日期：" + stamp);
				tv_channel_no.setText(
						"渠道订单：" + (obj.optString("externalId").equals("null") ? "未填写" : obj.optString("externalId")));
				String channel = "";
				if (obj.optString("salesChannelEnumId").equals("POS_SALES_CHANNEL")) {
					channel = "POS零售";
				} else if (obj.optString("salesChannelEnumId").equals("WHOLES_CHANNEL")) {
					channel = "批发";
				} else if (obj.optString("salesChannelEnumId").equals("DISTRI_CHANNEL")) {
					channel = "分销";
				} else if (obj.optString("salesChannelEnumId").equals("72_SALES_CHANNEL")) {
					channel = "七加二商城";
				}

				tv_channel.setText("销售渠道：" + channel);
				tv_store.setText("店铺：" + obj.optString("storeName"));
				tv_client.setText(
						"客户：" + (object.optString("storeName").equals("") ? "未填写" : object.optString("storeName")));

				if (obj.optString("paymentStatusId").equals("PMNT_NOPAY_RECV")) {
					tv_pay_state.setText("x未收款");
					tv_pay_state.setTextColor(0xffff0000);
				} else if (obj.optString("paymentStatusId").equals("PMNT_PARTIAL_RECV")) {
					tv_pay_state.setText("√部分收款");
					tv_pay_state.setTextColor(0xfffe7c43);
				} else if (obj.optString("paymentStatusId").equals("PMNT_RETURN_CUSTOMER")) {
					tv_pay_state.setText("√已退款");
					tv_pay_state.setTextColor(0xff00ff00);
				} else if (obj.optString("paymentStatusId").equals("PMNT_TOTAL_RECV")) {
					tv_pay_state.setText("√已结清");
					tv_pay_state.setTextColor(0xffb8da7b);
				}

				tv_order_sum.setText("￥" + df.format(obj.optDouble("grandTotal")));
				tv_payed.setText("已收款：￥" + df.format(object.optDouble("orderPaymentReceived")));
				tv_nopay.setText("未收款：￥" + df.format(object.optDouble("orderPaymentRemaining")));
				tv_promo.setText("优惠金额：￥" + df.format(object.optDouble("favorableAmount")));
				tv_returned.setText("已退款：￥" + df.format(object.optDouble("orderPaymentReturned")));

				if (obj.optString("statusId").equals("ORDER_CREATED")) {
					tv_order_state.setText("√已创建");
				} else if (obj.optString("statusId").equals("ORDER_APPROVED")) {
					tv_order_state.setText("√已批准");
				} else if (obj.optString("statusId").equals("ORDER_HOLD")) {
					tv_order_state.setText("√已保留");
				} else if (obj.optString("statusId").equals("ORDER_COMPLETED")) {
					tv_order_state.setText("√已完成");
				} else if (obj.optString("statusId").equals("ORDER_CANCELLED")) {
					tv_order_state.setText("√已取消");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "查看订单商品详情==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray itemsarray = object.getJSONArray("listOrderItems");
				JSONArray quantitysarray = object.getJSONArray("listProductQuantitys");

				for (int i = 0; i < itemsarray.length(); i++) {
					JSONObject obj = (JSONObject) itemsarray.get(i);
					detailInfo = new OrderDetailInfo();

					detailInfo.setProductName(obj.optString("itemDescription"));
					detailInfo.setStyleNo(obj.optString("viewModelId"));
					detailInfo.setProductColor(obj.optString("viewColorName"));
					detailInfo.setProductSize(obj.optString("viewSizeName"));
					detailInfo.setProductBrand(obj.optString("viewBrandName"));
					detailInfo.setProductPrice(obj.optDouble("unitPrice"));
					detailInfo.setProductCount(obj.optInt("quantity"));

					String orderId = obj.optString("productId");
					for (int j = 0; j < quantitysarray.length(); j++) {
						JSONObject obj2 = (JSONObject) quantitysarray.get(i);
						JSONObject sendobj = obj2.optJSONObject(orderId);

						detailInfo.setSendedCount(sendobj.optInt("shippedQuantity"));
						detailInfo.setSendingCount(sendobj.optInt("remainingQuantity"));
						detailInfo.setReturnedCount(sendobj.optInt("returnQuantity"));
					}
					detailData.add(detailInfo);
					detailAdapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_order_no = (TextView) findViewById(R.id.tv_order_no);
		tv_order_date = (TextView) findViewById(R.id.tv_order_date);
		tv_channel_no = (TextView) findViewById(R.id.tv_channel_no);
		tv_channel = (TextView) findViewById(R.id.tv_channel);
		tv_store = (TextView) findViewById(R.id.tv_store);
		tv_client = (TextView) findViewById(R.id.tv_client);
		tv_order_state = (TextView) findViewById(R.id.tv_order_state);
		tv_order_sum = (TextView) findViewById(R.id.tv_order_sum);
		tv_payed = (TextView) findViewById(R.id.tv_payed);
		tv_nopay = (TextView) findViewById(R.id.tv_nopay);
		tv_promo = (TextView) findViewById(R.id.tv_promo);
		tv_returned = (TextView) findViewById(R.id.tv_returned);
		tv_pay_state = (TextView) findViewById(R.id.tv_pay_state);
		// tv_product_name = (TextView) findViewById(R.id.tv_product_name);
		// tv_style = (TextView) findViewById(R.id.tv_style);
		// tv_size = (TextView) findViewById(R.id.tv_size);
		// tv_color = (TextView) findViewById(R.id.tv_color);
		// tv_brand = (TextView) findViewById(R.id.tv_brand);
		// tv_count = (TextView) findViewById(R.id.tv_count);
		// tv_sended = (TextView) findViewById(R.id.tv_sended);
		// tv_nosend = (TextView) findViewById(R.id.tv_nosend);
		// tv_return = (TextView) findViewById(R.id.tv_return);
		lv_order_item = (ListViewForScrollView) findViewById(R.id.lv_order_item);

		detailData = new ArrayList<OrderDetailInfo>();
		detailAdapter = new OrderDetailAdapter(MtsOrderDetailActivity.this, detailData);
		lv_order_item.setAdapter(detailAdapter);
	}

	class PromoTask extends MyPostTask {
		String which;

		public PromoTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				Toast.makeText(MtsOrderDetailActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}

}
