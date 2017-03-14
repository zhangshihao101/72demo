package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.DisOrderDetailAdapter;
import com.spt.alipay.AliPayHelper;
import com.spt.alipay.AliPayHelper.AliPayCallback;
import com.spt.bean.OrderGoodsInfo;
import com.spt.controler.CustomListView;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.NetUtils;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;
import com.spt.wechat.WXHelper;
import com.spt.wechat.WXPayModel;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 分销订单详情
 * 
 * @author lihongxuan
 *
 */
public class DisOrderDetailActivity extends BaseActivity {

	private TextView tv_disorder_detail_number, tv_disorder_detail_status, tv_disorder_detail_time,
			tv_disorder_detail_freight, tv_disorder_detail_total, tv_disorder_detail_consignee,
			tv_disorder_detail_phone, tv_disorder_detail_address, tv_pop_cancel_goods_reason,
			tv_pop_cancel_order_reason, tv_pop_cancel_buyer_reason, tv_pop_cancel_other_reason, tv_predict, TextViewb;
	private ImageView iv_dis_order_detail_back;
	private Button btn_disorder_detail_cancel, btn_disorder_detail_pay, btn_disorder_detail_confirm,
			btn_disorder_detail_logistics;
	private LinearLayout ll_disorder_detail, ll_disorder_detail2;
	private CustomListView lv_disorder_detail;
	private List<OrderGoodsInfo> mList;
	private DisOrderDetailAdapter mAdapter;
	private String order_id;// 订单id
	private String payment;// 支付方式
	private String order_sn;// 订单号

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_dis_order_detail);
		super.onCreate(savedInstanceState);

		order_id = getIntent().getStringExtra("order_id");

	}

	@Override
	protected void onResume() {
		super.onResume();
		dialog = new ProgressDialog(this);
		dialog.setMessage("数据加载中,请稍等...");
		dialog.show();
		mList = new ArrayList<OrderGoodsInfo>();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISORDERDETAIL + "&order_id=" + order_id, params,
				new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						dialog.dismiss();
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							if (error.equals("0")) {
								JSONObject object2 = object.optJSONObject("data");
								order_sn = object2.optString("order_sn");
								tv_disorder_detail_number.setText(order_sn);
								if (!object2.optString("agent_id").equals("0")) {
									TextViewb.setVisibility(View.VISIBLE);
									tv_predict.setVisibility(View.VISIBLE);
									ll_disorder_detail.setVisibility(View.GONE);
									tv_predict.setText("￥" + object2.optString("profit"));
								} else {
									TextViewb.setVisibility(View.GONE);
									tv_predict.setVisibility(View.GONE);
								}

								// if (object2.optString("status").equals("11")
								// &&
								// !object2.optString("agent_id").equals("0")) {
								// ll_disorder_detail.setVisibility(View.VISIBLE);
								// btn_disorder_detail_pay.setVisibility(View.GONE);
								// }

								if (object2.optString("status").equals("11")
										&& object2.optString("agent_id").equals("0")) {
									tv_disorder_detail_status.setText("待付款");
									ll_disorder_detail2.setVisibility(View.GONE);
									ll_disorder_detail.setVisibility(View.VISIBLE);
								} else if (object2.optString("status").equals("20")) {
									tv_disorder_detail_status.setText("待发货");
									tv_disorder_detail_status.setTextColor(Color.GRAY);
									ll_disorder_detail2.setVisibility(View.GONE);
									ll_disorder_detail.setVisibility(View.GONE);
								} else if (object2.optString("status").equals("30")) {
									tv_disorder_detail_status.setText("已发货");
									tv_disorder_detail_status.setTextColor(Color.GRAY);
									ll_disorder_detail2.setVisibility(View.VISIBLE);
									ll_disorder_detail.setVisibility(View.GONE);
								} else if (object2.optString("status").equals("40")) {
									tv_disorder_detail_status.setText("交易成功");
									tv_disorder_detail_status.setTextColor(Color.GRAY);
									ll_disorder_detail2.setVisibility(View.GONE);
									ll_disorder_detail.setVisibility(View.GONE);
								} else if (object2.optString("status").equals("50")) {
									tv_disorder_detail_status.setText("已退款");
									tv_disorder_detail_status.setTextColor(Color.GRAY);
									ll_disorder_detail2.setVisibility(View.GONE);
									ll_disorder_detail.setVisibility(View.GONE);
									TextViewb.setVisibility(View.GONE);
									tv_predict.setVisibility(View.GONE);
								} else if (object2.optString("status").equals("0")) {
									tv_disorder_detail_status.setText("交易取消");
									ll_disorder_detail2.setVisibility(View.GONE);
									tv_disorder_detail_status.setTextColor(Color.GRAY);
									ll_disorder_detail.setVisibility(View.GONE);
								}

								if (object2.optString("pay_time").equals("0")) {
									tv_disorder_detail_time.setText("未支付");
								} else {
									tv_disorder_detail_time
											.setText(MyUtil.millisecondsToStr(object2.optString("pay_time")));
								}

								tv_disorder_detail_total.setText("￥" + object2.optString("final_amount"));

								JSONObject object3 = object2.optJSONObject("order_extm");
								tv_disorder_detail_consignee.setText(object3.optString("consignee"));
								tv_disorder_detail_freight.setText("￥" + object3.optString("shipping_fee"));
								tv_disorder_detail_phone.setText(object3.optString("phone_mob"));
								tv_disorder_detail_address.setText(object3.optString("address"));

								JSONArray array2 = object2.optJSONArray("goods_list");
								for (int i = 0; i < array2.length(); i++) {
									OrderGoodsInfo info = new OrderGoodsInfo();
									JSONObject object4 = array2.optJSONObject(i);
									info.setGoods_id(object4.optString("goods_id"));
									info.setGoods_image(object4.optString("goods_image"));

									info.setGoods_name(object4.optString("goods_name"));
									info.setSpecification(object4.optString("specification"));
									info.setPrice(object4.optString("price"));
									info.setQuantity(object4.optString("quantity"));
									mList.add(info);
									mAdapter.notifyDataSetChanged();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						dialog.dismiss();
						Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});

		mAdapter = new DisOrderDetailAdapter(this, mList);
		lv_disorder_detail.setAdapter(mAdapter);

	}

	@Override
	protected void init() {
		tv_disorder_detail_number = (TextView) findViewById(R.id.tv_disorder_detail_number);
		tv_disorder_detail_status = (TextView) findViewById(R.id.tv_disorder_detail_status);
		tv_disorder_detail_time = (TextView) findViewById(R.id.tv_disorder_detail_time);
		tv_disorder_detail_freight = (TextView) findViewById(R.id.tv_disorder_detail_freight);
		tv_disorder_detail_total = (TextView) findViewById(R.id.tv_disorder_detail_total);
		tv_disorder_detail_consignee = (TextView) findViewById(R.id.tv_disorder_detail_consignee);
		tv_disorder_detail_phone = (TextView) findViewById(R.id.tv_disorder_detail_phone);
		tv_disorder_detail_address = (TextView) findViewById(R.id.tv_disorder_detail_address);
		tv_predict = (TextView) findViewById(R.id.tv_predict);
		TextViewb = (TextView) findViewById(R.id.TextViewb);
		iv_dis_order_detail_back = (ImageView) findViewById(R.id.iv_dis_order_detail_back);
		btn_disorder_detail_cancel = (Button) findViewById(R.id.btn_disorder_detail_cancel);
		btn_disorder_detail_pay = (Button) findViewById(R.id.btn_disorder_detail_pay);
		btn_disorder_detail_confirm = (Button) findViewById(R.id.btn_disorder_detail_confirm);
		btn_disorder_detail_logistics = (Button) findViewById(R.id.btn_disorder_detail_logistics);
		ll_disorder_detail = (LinearLayout) findViewById(R.id.ll_disorder_detail);
		ll_disorder_detail2 = (LinearLayout) findViewById(R.id.ll_disorder_detail2);
		lv_disorder_detail = (CustomListView) findViewById(R.id.lv_disorder_detail);

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");
	}

	@Override
	protected void addClickEvent() {
		// 返回键
		iv_dis_order_detail_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DisOrderDetailActivity.this.finish();
			}
		});

		lv_disorder_detail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(DisOrderDetailActivity.this, DisGoodsDetailsActivity.class);
				intent.putExtra("goodsId", mList.get(position).getGoods_id());
				startActivity(intent);
			}
		});

		// 取消订单按钮
		btn_disorder_detail_cancel.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint({ "InflateParams", "InlinedApi" })
			@Override
			public void onClick(View v) {
				View reason_view = LayoutInflater.from(DisOrderDetailActivity.this).inflate(R.layout.pop_cancel_order,
						null);
				tv_pop_cancel_goods_reason = (TextView) reason_view.findViewById(R.id.tv_pop_cancel_goods_reason);
				tv_pop_cancel_order_reason = (TextView) reason_view.findViewById(R.id.tv_pop_cancel_order_reason);
				tv_pop_cancel_buyer_reason = (TextView) reason_view.findViewById(R.id.tv_pop_cancel_buyer_reason);
				tv_pop_cancel_other_reason = (TextView) reason_view.findViewById(R.id.tv_pop_cancel_other_reason);
				final PopupWindow reason_pop = new PopupWindow(reason_view, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				// 需要设置一下此参数，点击外边可消失
				reason_pop.setBackgroundDrawable(new BitmapDrawable());
				// 设置点击窗口外边窗口消失
				reason_pop.setOutsideTouchable(true);
				// 设置此参数获得焦点，否则无法点击
				reason_pop.setFocusable(true);
				// 弹出位置
				reason_pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);

				// 无法备齐货物
				tv_pop_cancel_goods_reason.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						params.clear();
						params.put("token", token);
						params.put("version", "2.1");
						params.put("order_id", order_id);
						params.put("cancel_reason", "无法备齐货物");
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CANCELORDER, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									String msg = object.optString("msg");
									if (error.equals("0")) {
										Toast.makeText(DisOrderDetailActivity.this, "订单取消成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									} else {
										Toast.makeText(DisOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

						reason_pop.dismiss();
					}
				});

				// 不是有效订单
				tv_pop_cancel_order_reason.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						params.clear();
						params.put("token", token);
						params.put("version", "2.1");
						params.put("order_id", order_id);
						params.put("cancel_reason", "不是有效订单");
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CANCELORDER, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									String msg = object.optString("msg");
									if (error.equals("0")) {
										Toast.makeText(DisOrderDetailActivity.this, "订单取消成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									} else {
										Toast.makeText(DisOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

						reason_pop.dismiss();
					}
				});

				// 买家主动要求
				tv_pop_cancel_buyer_reason.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						params.clear();
						params.put("token", token);
						params.put("version", "2.1");
						params.put("order_id", order_id);
						params.put("cancel_reason", "买家主动要求");
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CANCELORDER, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									String msg = object.optString("msg");
									if (error.equals("0")) {
										Toast.makeText(DisOrderDetailActivity.this, "订单取消成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									} else {
										Toast.makeText(DisOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

						reason_pop.dismiss();
					}
				});

				// 其他原因
				tv_pop_cancel_other_reason.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						params.clear();
						params.put("token", token);
						params.put("version", "2.1");
						params.put("order_id", order_id);
						params.put("cancel_reason", "其他原因");
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CANCELORDER, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									String msg = object.optString("msg");
									if (error.equals("0")) {
										Toast.makeText(DisOrderDetailActivity.this, "订单取消成功", Toast.LENGTH_SHORT)
												.show();
										finish();
									} else {
										Toast.makeText(DisOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

						reason_pop.dismiss();
					}
				});

			}
		});
		// 立即支付按钮
		btn_disorder_detail_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DisOrderDetailActivity.this, PayMethodActivity.class);
				intent.putExtra("tag", 1);
				startActivityForResult(intent, 0);
			}
		});

		// 确认收货按钮
		btn_disorder_detail_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				params.clear();
				params.put("token", token);
				params.put("version", "2.1");
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CONFIRMGOODS + "&order_id=" + order_id, params,
						new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							String msg = object.optString("msg");
							if (error.equals("0")) {
								Toast.makeText(DisOrderDetailActivity.this, "确认收货成功", Toast.LENGTH_SHORT).show();
								finish();
							} else {
								Toast.makeText(DisOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		// 查看物流按钮
		btn_disorder_detail_logistics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DisOrderDetailActivity.this, LogisticsInfoActivity.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == 1) {
				payment = data.getStringExtra("payment");
				VolleyHelper.post(
						MyConstant.SERVICENAME + MyConstant.GOTOPAY + "&order_ids=" + order_id + "&payment=" + payment,
						params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									if (error.equals("0")) {
										JSONObject object2 = object.optJSONObject("data");
										AliPayHelper.getInstance(DisOrderDetailActivity.this).pay(
												object2.optString("out_trade_sn"), object2.optString("final_amount"),
												order_sn, new AliPayCallback() {

											@Override
											public void onPay(int status) {
												switch (status) {
												case 9000:// 支付成功
													openOrderList();
													finish();
													break;
												default:// 支付失败或取消
													openOrderList();
													finish();
													break;
												}
											}
										});
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
			} else if (resultCode == 2) {
				payment = data.getStringExtra("payment");
				String ip = "";
				if (NetUtils.isWifiConnected(DisOrderDetailActivity.this)) {
					ip = "192.168.1.1";
				} else if (NetUtils.isGPRSConnected(DisOrderDetailActivity.this)) {
					ip = NetUtils.getLocalIpAddress();
				}
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.GOTOPAY + "&order_ids=" + order_id + "&payment="
						+ payment + "&ip=" + ip, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject object = new JSONObject(data);
									String error = object.optString("error");
									if (error.equals("0")) {
										JSONObject obj = object.optJSONObject("data");
										WXPayModel model = new WXPayModel();
										model.setAppId(obj.optString("appId"));
										model.setPartnerId(obj.optString("partnerId"));
										model.setPrepayId(obj.optString("prepayId"));
										model.setPackageStr(obj.optString("packageStr"));
										model.setNoncestr(obj.optString("noncestr"));
										model.setTimestamp(obj.optString("timestamp"));
										model.setSign(obj.optString("sign"));
										if (model != null) {
											payWX(model);
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisOrderDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
			} else if (resultCode == 3) {
				Toast toast = Toast.makeText(DisOrderDetailActivity.this, "请尽快完成交易,我们会在交易结束后及时发货！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}

	/**
	 * 微信支付
	 * 
	 * @param model
	 */
	private void payWX(WXPayModel model) {
		IWXAPI mIwxapi = WXHelper.registeAppId(this);
		PayReq req = WXHelper.payReq(model);
		if (mIwxapi.sendReq(req)) {

		} else {
			Toast.makeText(this, "请安装登录微信", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 打开订单列表
	 */
	private void openOrderList() {
		Intent intent = new Intent(this, DistributionActivity.class);
		intent.putExtra("page", 0);
		intent.putExtra("tag", 2);
		startActivity(intent);
	}

}
