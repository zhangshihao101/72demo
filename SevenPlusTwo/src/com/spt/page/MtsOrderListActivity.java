package com.spt.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.OrderAdapter;
import com.spt.bean.OrderInfo;
import com.spt.common.BaseMtsActivity;
import com.spt.common.Constants;
import com.spt.common.MyPostTask;
import com.spt.common.NetworkUtil;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MtsOrderListActivity extends BaseMtsActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private ImageView iv_back, iv_clear;
	private EditText et_search;
	private TextView tv_filter, tv_needSendOrdersPage_tip;
	private ListView prlv_order_list;

	private OrderInfo info;
	private List<OrderInfo> Orderdata;
	private OrderAdapter orderAdapter;

	private ProgressDialog progressDialog;

	private int showFlag = 0;// 搜索类型
	private int page = 1;// 页数
	private int totalPage;// 总页数
	private boolean isBottom;// 判断是否到达底部
	private int isOnly; // 搜出的结果是否为1

	private PullToRefreshView mPullToRefreshView;

	private String orderId = "", storeId = "", channelId = "", orderState = "", payState = "", startDay = "",
			endDay = "";

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_orderlist);
		super.onCreate(inState);

		intiView();
		getAllOrder();

		Orderdata = new ArrayList<OrderInfo>();
		orderAdapter = new OrderAdapter(MtsOrderListActivity.this, Orderdata);

		prlv_order_list.setAdapter(orderAdapter);

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Orderdata.clear();
				orderAdapter.notifyDataSetChanged();
				searchOrder();

				showFlag = 1;
				page = 1;

				return true;
			}
		});

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!"".equals(et_search.getText().toString())) {
					iv_clear.setVisibility(View.VISIBLE);
					showFlag = 1;
				} else {
					iv_clear.setVisibility(View.INVISIBLE);
					showFlag = 0;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		iv_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_search.setText("");
				Orderdata.clear();
				orderAdapter.notifyDataSetChanged();
				getAllOrder();
			}
		});

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		tv_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MtsOrderListActivity.this, MtsOrderFilterActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		prlv_order_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MtsOrderListActivity.this, MtsOrderDetailActivity.class);

				intent.putExtra("orderId", Orderdata.get(position).getOrderId());
				startActivity(intent);
			}
		});

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				page++;
				if (showFlag == 0) {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("externalLoginKey",
							Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
					nameValuePair.add(new BasicNameValuePair("viewIndex", page + ""));
					nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
					nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));

					if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
						PromoTask promotask = new PromoTask(MtsOrderListActivity.this,
								MtsUrls.base + MtsUrls.get_orders, nameValuePair, "0");
						promotask.execute("");
					} else {
						Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
					}
				} else if (showFlag == 1) {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("externalLoginKey",
							Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
					nameValuePair.add(new BasicNameValuePair("viewIndex", page + ""));
					nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
					nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));
					nameValuePair.add(new BasicNameValuePair("keywords", et_search.getText().toString()));

					if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
						PromoTask promotask = new PromoTask(MtsOrderListActivity.this,
								MtsUrls.base + MtsUrls.get_orders, nameValuePair, "4");
						promotask.execute("");
					} else {
						Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
					}
				} else if (showFlag == 2) {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("externalLoginKey",
							Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
					nameValuePair.add(new BasicNameValuePair("viewIndex", page + ""));
					nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
					nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));

					if (orderId != null && !"".equals(orderId)) {
						nameValuePair.add(new BasicNameValuePair("orderId", orderId));
						Log.e("orderId", "orderId==" + orderId);
					} else {

					}
					if (storeId != null && !"".equals(storeId) && !"STORE_ALL".equals(storeId)) {
						nameValuePair.add(new BasicNameValuePair("productStoreId", storeId));
						Log.e("storeId", "storeId==" + storeId);
					} else {

					}

					if (channelId != null && !"".equals(channelId) && !"CHANNEL_ALL".equals(channelId)) {
						nameValuePair.add(new BasicNameValuePair("salesChannelEnumId", channelId));
						Log.e("channelId", "channelId==" + channelId);
					} else {

					}
					if (orderState != null && !"".equals(orderState) && !"ORDER_ALL".equals(orderState)) {
						nameValuePair.add(new BasicNameValuePair("orderStatusId", orderState));
						Log.e("orderState", "orderState==" + orderState);
					} else {

					}
					if (payState != null && !"".equals(payState) && !"PMNT_ALL".equals(payState)) {
						nameValuePair.add(new BasicNameValuePair("paymentStatusId", payState));
						Log.e("payState", "payState==" + payState);
					} else {

					}
					if (startDay != null && !"".equals(startDay)) {
						nameValuePair.add(new BasicNameValuePair("minDate", startDay));
						Log.e("startDay", "startDay==" + startDay);
					} else {

					}

					if (endDay != null && !"".equals(endDay)) {
						nameValuePair.add(new BasicNameValuePair("maxDate", endDay));
						Log.e("endDay", "endDay==" + endDay);
					} else {

					}

					if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
						PromoTask promotask = new PromoTask(MtsOrderListActivity.this,
								MtsUrls.base + MtsUrls.get_orders, nameValuePair, "2");
						promotask.execute("");
					} else {
						Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
					}
				}
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page = 1;
				if (showFlag == 0) {
					Orderdata.clear();
					orderAdapter.notifyDataSetChanged();
					getNewOrder();
				} else if (showFlag == 1) {
					Orderdata.clear();
					orderAdapter.notifyDataSetChanged();
					searchOrder();
				} else if (showFlag == 2) {
					Orderdata.clear();
					orderAdapter.notifyDataSetChanged();
					getFilterOrder();
				}
				mPullToRefreshView.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
			}
		}, 1000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == 10) {
				orderId = data.getExtras().getString("orderId");
				storeId = data.getExtras().getString("store");
				channelId = data.getExtras().getString("channel");
				orderState = data.getExtras().getString("orderState");
				payState = data.getExtras().getString("payState");
				startDay = data.getExtras().getString("startDay");
				endDay = data.getExtras().getString("endDay");

				showFlag = 2;

				Orderdata.clear();
				orderAdapter.notifyDataSetChanged();
				getFilterOrder();
			}
		}
	}

	private void intiView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_filter = (TextView) findViewById(R.id.tv_filter);
		tv_needSendOrdersPage_tip = (TextView) findViewById(R.id.tv_needSendOrdersPage_tip);
		prlv_order_list = (ListView) findViewById(R.id.prlv_order_list);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		progressDialog = ProgressDialog.show(MtsOrderListActivity.this, "请稍候。。。", "获取数据中。。。", true);
		progressDialog.dismiss();

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
	}

	private void getFilterOrder() {
		progressDialog.show();
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("viewIndex", "1"));
		nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
		nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));

		if (orderId != null && !"".equals(orderId)) {
			nameValuePair.add(new BasicNameValuePair("orderId", orderId));
			Log.e("orderId", "orderId==" + orderId);
		} else {

		}
		if (storeId != null && !"".equals(storeId) && !"STORE_ALL".equals(storeId)) {
			nameValuePair.add(new BasicNameValuePair("productStoreId", storeId));
			Log.e("storeId", "storeId==" + storeId);
		} else {

		}

		if (channelId != null && !"".equals(channelId) && !"CHANNEL_ALL".equals(channelId)) {
			nameValuePair.add(new BasicNameValuePair("salesChannelEnumId", channelId));
			Log.e("channelId", "channelId==" + channelId);
		} else {

		}
		if (orderState != null && !"".equals(orderState) && !"ORDER_ALL".equals(orderState)) {
			nameValuePair.add(new BasicNameValuePair("orderStatusId", orderState));
			Log.e("orderState", "orderState==" + orderState);
		} else {

		}
		if (payState != null && !"".equals(payState) && !"PMNT_ALL".equals(payState)) {
			nameValuePair.add(new BasicNameValuePair("paymentStatusId", payState));
			Log.e("payState", "payState==" + payState);
		} else {

		}
		if (startDay != null && !"".equals(startDay)) {
			nameValuePair.add(new BasicNameValuePair("minDate", startDay));
			Log.e("startDay", "startDay==" + startDay);
		} else {

		}

		if (endDay != null && !"".equals(endDay)) {
			nameValuePair.add(new BasicNameValuePair("maxDate", endDay));
			Log.e("endDay", "endDay==" + endDay);
		} else {

		}

		if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderListActivity.this, MtsUrls.base + MtsUrls.get_orders,
					nameValuePair, "2");
			promotask.execute("");
		} else {
			progressDialog.dismiss();
			Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}

	}

	private void getAllOrder() {
		progressDialog.show();
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("viewIndex", "1"));
		nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
		nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));

		if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderListActivity.this, MtsUrls.base + MtsUrls.get_orders,
					nameValuePair, "0");
			promotask.execute("");
		} else {
			progressDialog.dismiss();
			Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	private void getNewOrder() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("viewIndex", "1"));
		nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
		nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));

		if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderListActivity.this, MtsUrls.base + MtsUrls.get_orders,
					nameValuePair, "0");
			promotask.execute("");
		} else {
			Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	private void searchOrder() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(MtsOrderListActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("viewIndex", "1"));
		nameValuePair.add(new BasicNameValuePair("viewSize", "20"));
		nameValuePair.add(new BasicNameValuePair("orderTypeId", "SALES_ORDER"));
		nameValuePair.add(new BasicNameValuePair("keywords", et_search.getText().toString()));

		if (NetworkUtil.isConnected(MtsOrderListActivity.this)) {
			PromoTask promotask = new PromoTask(MtsOrderListActivity.this, MtsUrls.base + MtsUrls.get_orders,
					nameValuePair, "1");
			promotask.execute("");
		} else {
			Toast.makeText(MtsOrderListActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		progressDialog.dismiss();
		if (whichtask.equals("0")) {
			Log.e("LOOK", "所有订单信息==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.optJSONArray("orderList");
				Log.e("长度", "长度==" + array.length());
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.get(i);

					info = new OrderInfo();
					info.setOrderId(obj.optString("orderId"));
					info.setChannel(obj.optString("salesChannelEnumId"));
					info.setIsFinish(obj.optString("statusId"));
					info.setIsPayment(obj.optString("paymentStatusId"));

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

					String stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-"
							+ month + "-" + date;
					info.setOrderDate(stamp);
					info.setShopName(obj.optString("storeName"));
					info.setTotle(obj.optDouble("grandTotal"));

					Orderdata.add(info);
					orderAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "条件查询==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.optJSONArray("orderList");
				Log.e("长度", "长度==" + array.length());

				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.get(i);

					info = new OrderInfo();
					info.setOrderId(obj.optString("orderId"));
					info.setChannel(obj.optString("salesChannelEnumId"));
					info.setIsFinish(obj.optString("statusId"));
					info.setIsPayment(obj.optString("paymentStatusId"));

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

					String stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-"
							+ month + "-" + date;
					info.setOrderDate(stamp);
					info.setShopName(obj.optString("storeName"));
					info.setTotle(obj.optDouble("grandTotal"));

					Orderdata.add(info);
					orderAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (whichtask.equals("2")) {
			Log.e("LOOK", "筛选结果==" + result);
			Log.e("LOOK", "showFlag==" + showFlag);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.optJSONArray("orderList");
				Log.e("长度", "长度==" + array.length());
				if (array.length() == 0) {
					Toast.makeText(MtsOrderListActivity.this, "没有符合条件商品", Toast.LENGTH_SHORT).show();
				}
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.get(i);

					info = new OrderInfo();
					info.setOrderId(obj.optString("orderId"));
					info.setChannel(obj.optString("salesChannelEnumId"));
					info.setIsFinish(obj.optString("statusId"));
					info.setIsPayment(obj.optString("paymentStatusId"));

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

					String stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-"
							+ month + "-" + date;
					info.setOrderDate(stamp);
					info.setShopName(obj.optString("storeName"));
					info.setTotle(obj.optDouble("grandTotal"));

					Orderdata.add(info);
					orderAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (whichtask.equals("4")) {
			Log.e("LOOK", "条件搜索加载==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.optJSONArray("orderList");
				Log.e("长度", "长度==" + array.length());

				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.get(i);

					info = new OrderInfo();
					info.setOrderId(obj.optString("orderId"));
					info.setChannel(obj.optString("salesChannelEnumId"));
					info.setIsFinish(obj.optString("statusId"));
					info.setIsPayment(obj.optString("paymentStatusId"));

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

					String stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-"
							+ month + "-" + date;
					info.setOrderDate(stamp);
					info.setShopName(obj.optString("storeName"));
					info.setTotle(obj.optDouble("grandTotal"));

					Orderdata.add(info);
					orderAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				Toast.makeText(MtsOrderListActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}

}
