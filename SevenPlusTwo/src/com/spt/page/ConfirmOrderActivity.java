package com.spt.page;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.DisConfirmOrderStoreAdapter;
import com.spt.alipay.AliPayHelper;
import com.spt.alipay.AliPayHelper.AliPayCallback;
import com.spt.bean.OrderGoodsInfo;
import com.spt.bean.StoreInfo;
import com.spt.controler.MyExpandableListView;
import com.spt.dialog.ChangeAddressDialog;
import com.spt.dialog.ChangeAddressDialog.OnAddressCListener;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.NetUtils;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;
import com.spt.wechat.WXHelper;
import com.spt.wechat.WXPayModel;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 确认订单
 * 
 * @author lihongxuan
 *
 */
public class ConfirmOrderActivity extends BaseActivity implements OnClickListener {

	private TextView tv_confrim_commit, tv_confirm_myaddress, tv_confirm_freight, tv_confirm_total, tv_confirm_address,
			tv_confirm_paymethod;
	private ImageView iv_confirm_back, iv_confirm_paymethod;
	private EditText et_cofirm_consignee, et_cofirm_phone, et_cofirm_postal, et_cofirm_address;
	private CheckBox cb_confirm_saveaddress;
	private LinearLayout ll_confirm_myaddress;
	private RelativeLayout rl_confirm_pay;
	private MyExpandableListView elv_confirm;
	private List<StoreInfo> groups;// 店铺名称集合
	private Map<String, List<OrderGoodsInfo>> children;// 商品集合
	private DisConfirmOrderStoreAdapter mAdapter;

	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private ProgressDialog dialog;
	private DecimalFormat df;

	private String goodsId;// 商品Id字符串
	private String user_id;// 用户id
	private String consignee;// 收货人
	private String address;// 详细地址
	private String zipcode;// 邮编
	private String phone_mob;// 手机
	private String ext_region_id_1;// 省id
	private String ext_region_id_2;// 市id
	private String ext_region_id_3;// 区/县id
	private String is_save = "0";// 是否保存地址
	private String address_options;// 收货地址id
	private String payment = "1";// 支付方式id
	private String ip;

	/**
	 * 电话号码正则匹配表达式（11位手机号码）^0\d{2,3}(\-)?\d{7,8}$
	 * (13[0-9]|15[0-9]|18[0-9])\\d{8}$
	 */
	public static final String TEL_CHECK = "^(13[0-9]|15[0-9]|18[0-9])\\d{8}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_confirmorder);
		super.onCreate(savedInstanceState);
		// 给我的地址设置下划线
		tv_confirm_myaddress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_confirm_myaddress.setTextColor(Color.parseColor("#FF6634"));

		Bundle bundle = getIntent().getExtras();
		goodsId = bundle.getString("goodsId");
		user_id = bundle.getString("user_id");
		String totalPrice = bundle.getString("totalPrice");
		tv_confirm_total.setText(totalPrice);

		initData();

	}

	private void initData() {

		groups = new ArrayList<StoreInfo>();
		children = new HashMap<String, List<OrderGoodsInfo>>();
		params.put("token", token);
		params.put("version", "2.1");
		params.put("ids", goodsId);
		dialog = new ProgressDialog(this);
		dialog.setMessage("数据正在加载中，请稍等...");
		dialog.show();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISCONFIRMORDER, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(data);
					String error = object.optString("error");
					String msg = object.optString("msg");
					if (error.equals("0")) {
						JSONArray array = object.optJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.optJSONObject(i);
							StoreInfo storeInfo = new StoreInfo();
							storeInfo.setSroreId(obj.optString("store_id"));
							storeInfo.setSotreName(obj.optString("store_name"));
							groups.add(storeInfo);

							List<OrderGoodsInfo> goodsList = new ArrayList<OrderGoodsInfo>();
							JSONArray array2 = obj.optJSONArray("goods");
							for (int j = 0; j < array2.length(); j++) {
								JSONObject obj2 = array2.optJSONObject(j);
								OrderGoodsInfo goodsInfo = new OrderGoodsInfo();
								goodsInfo.setId(obj2.optString("id"));
								goodsInfo.setGoods_id(obj2.optString("goods_id"));
								goodsInfo.setGoods_name(obj2.optString("goods_name"));
								goodsInfo.setSpec_id(obj2.optString("spec_id"));
								goodsInfo.setSpecification(obj2.optString("specification"));
								goodsInfo.setGoods_image(obj2.optString("goods_image"));
								goodsInfo.setQuantity(obj2.optString("quantity"));
								goodsInfo.setPrice(obj2.optString("price"));
								goodsInfo.setFreight_id(obj2.optInt("freight_id"));
								goodsList.add(goodsInfo);
							}

							children.put(groups.get(i).getSroreId(), goodsList);
							mAdapter.notifyDataSetChanged();
							for (int y = 0; y < mAdapter.getGroupCount(); y++) {
								elv_confirm.expandGroup(y);// 初始化时，将ExpandableListView以展开的方式呈现
							}
						}

					} else if (error.equals("1")) {
						Toast.makeText(ConfirmOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void OnError(VolleyError volleyError) {
				dialog.dismiss();
				Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

		mAdapter = new DisConfirmOrderStoreAdapter(this, groups, children);
		elv_confirm.setAdapter(mAdapter);
		// 设置标题部分不可点击
		elv_confirm.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
	}

	@Override
	protected void init() {
		tv_confirm_address = (TextView) findViewById(R.id.tv_confirm_address);
		tv_confirm_freight = (TextView) findViewById(R.id.tv_confirm_freight);
		tv_confirm_total = (TextView) findViewById(R.id.tv_confirm_total);
		tv_confrim_commit = (TextView) findViewById(R.id.tv_confrim_commit);
		tv_confirm_myaddress = (TextView) findViewById(R.id.tv_confirm_myaddress);
		tv_confirm_paymethod = (TextView) findViewById(R.id.tv_confirm_paymethod);
		iv_confirm_paymethod = (ImageView) findViewById(R.id.iv_confirm_paymethod);
		iv_confirm_back = (ImageView) findViewById(R.id.iv_confirm_back);
		et_cofirm_consignee = (EditText) findViewById(R.id.et_cofirm_consignee);
		et_cofirm_phone = (EditText) findViewById(R.id.et_cofirm_phone);
		et_cofirm_postal = (EditText) findViewById(R.id.et_cofirm_postal);
		et_cofirm_address = (EditText) findViewById(R.id.et_cofirm_address);
		cb_confirm_saveaddress = (CheckBox) findViewById(R.id.cb_confirm_saveaddress);
		ll_confirm_myaddress = (LinearLayout) findViewById(R.id.ll_confirm_myaddress);
		rl_confirm_pay = (RelativeLayout) findViewById(R.id.rl_confirm_pay);
		elv_confirm = (MyExpandableListView) findViewById(R.id.elv_confirm);
		elv_confirm.setGroupIndicator(null);

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		df = new DecimalFormat("#.00");

	}

	@Override
	protected void addClickEvent() {
		// 返回键
		iv_confirm_back.setOnClickListener(this);

		// 选择区域
		tv_confirm_address.setOnClickListener(this);

		// 点击付款，判断哪些方式没有填写
		tv_confrim_commit.setOnClickListener(this);

		// 选择地址
		ll_confirm_myaddress.setOnClickListener(this);

		// 选择支付方式
		rl_confirm_pay.setOnClickListener(this);

		// 是否保存地址
		cb_confirm_saveaddress.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == true) {
					is_save = "1";
				} else {
					is_save = "0";
				}
			}
		});

	}

	/*
	 * 检查收货人详细信息方法
	 */
	private boolean checkEdit() {

		if (et_cofirm_consignee.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写收货人信息", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (tv_confirm_address.getText().toString().equals("")) {
			Toast toast = Toast.makeText(this, "请选择区域信息", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_cofirm_phone.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (!checkStringStyle(et_cofirm_phone.getText().toString().replace(" ", ""), TEL_CHECK)) {
			Toast toast = Toast.makeText(this, "请填写正确手机号", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_cofirm_postal.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写邮政编码", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_cofirm_address.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}

		return true;
	}

	// 检查手机号格式、邮政编码格式
	public static boolean checkStringStyle(String str, String style) {
		Pattern REGEX = Pattern.compile(style);
		// 非空判断
		if (null == str) {
			return false;
		}
		Matcher matcher = REGEX.matcher(str);
		return matcher.matches();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_confirm_back:
			ConfirmOrderActivity.this.finish();
			break;
		case R.id.tv_confirm_address:

			ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(ConfirmOrderActivity.this);
			mChangeAddressDialog.setAddress("天津", "天津市", "南开区");
			mChangeAddressDialog.show();
			mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

				@Override
				public void onClick(String province, String city, String country) {
					String provinces[] = province.split(" ");
					String citys[] = city.split(" ");
					String countrys[] = country.split(" ");
					ext_region_id_1 = provinces[1];
					ext_region_id_2 = citys[1];
					ext_region_id_3 = countrys[1];
					tv_confirm_address.setText(provinces[0] + " " + citys[0] + " " + countrys[0]);
					params.clear();
					params.put("token", token);
					params.put("version", "2.1");
					params.put("ids", goodsId);
					params.put("ext_region_id_1", ext_region_id_1);
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISCONFIRMORDER, params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							try {
								JSONObject object = new JSONObject(data);
								String error = object.optString("error");
								String msg = object.optString("msg");
								if (error.equals("0")) {
									JSONArray array = object.optJSONArray("data");
									double fee = 0;
									double total = 0;
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.optJSONObject(i);
										fee += obj.optDouble("total_fee");
										total += obj.optDouble("total_amount");
									}
									if (fee == 0) {
										tv_confirm_freight.setText("0.00");
									} else {
										tv_confirm_freight.setText(fee + "");
									}
									tv_confirm_total.setText(total + "");

								} else if (error.equals("1")) {
									Toast.makeText(ConfirmOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void OnError(VolleyError volleyError) {
							Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});

			break;
		case R.id.tv_confrim_commit:
			if (checkEdit()) {
				consignee = et_cofirm_consignee.getText().toString();
				address = et_cofirm_address.getText().toString();
				zipcode = et_cofirm_postal.getText().toString();
				phone_mob = et_cofirm_phone.getText().toString();
				params.clear();
				params.put("token", token);
				params.put("version", "2.1");
				params.put("consignee", consignee);
				params.put("address", address);
				params.put("zipcode", zipcode);
				params.put("phone_mob", phone_mob);
				params.put("ext_region_id_1", ext_region_id_1);
				params.put("ext_region_id_2", ext_region_id_2);
				params.put("ext_region_id_3", ext_region_id_3);
				params.put("is_save", is_save);
				tv_confrim_commit.setEnabled(false);
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.ADDADDRESS, params, new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							String msg = object.optString("msg");
							if (error.equals("0")) {
								JSONObject obj = object.optJSONObject("data");
								address_options = obj.optString("addr_id");
								if (tv_confirm_paymethod.getText().toString().equals("支付宝")) {
									payment = "1";
									params.clear();
									params.put("token", token);
									params.put("version", "2.1");
									params.put("ids", goodsId);
									params.put("address_options", address_options);
									params.put("payment", payment);
									dialog = new ProgressDialog(ConfirmOrderActivity.this);
									dialog.setMessage("获取订单中...");
									dialog.show();
									VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.COMMITORDER, params,
											new OnCallBack() {

										@Override
										public void OnSuccess(String data) {
											try {
												dialog.dismiss();
												JSONObject object = new JSONObject(data);
												String error = object.optString("error");
												if (error.equals("0")) {
													JSONObject obj = object.optJSONObject("data");
													String order_ids = obj.optString("order_id");
													final String order_sn = obj.optString("order_sn");
													params.clear();
													params.put("token", token);
													params.put("version", "2.1");
													final ProgressDialog dialog = new ProgressDialog(
															ConfirmOrderActivity.this);
													dialog.setMessage("跳转支付界面...");
													dialog.show();
													VolleyHelper.post(
															MyConstant.SERVICENAME + MyConstant.GOTOPAY + "&order_ids="
																	+ order_ids + "&payment=" + payment,
															params, new OnCallBack() {

														@Override
														public void OnSuccess(String data) {
															dialog.dismiss();
															try {
																JSONObject object = new JSONObject(data);
																String error = object.optString("error");
																if (error.equals("0")) {
																	JSONObject object2 = object.optJSONObject("data");
																	AliPayHelper.getInstance(ConfirmOrderActivity.this)
																			.pay(object2.optString("out_trade_sn"),
																					object2.optString("final_amount"),
																					order_sn, new AliPayCallback() {

																		@Override
																		public void onPay(int status) {
																			switch (status) {
																			case 9000:// 支付成功
																				Toast.makeText(
																						ConfirmOrderActivity.this,
																						"支付成功", Toast.LENGTH_SHORT)
																						.show();
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
															dialog.dismiss();
															Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络",
																	Toast.LENGTH_SHORT).show();
														}
													});
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void OnError(VolleyError volleyError) {
											dialog.dismiss();
											Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT)
													.show();
										}
									});
								} else if (tv_confirm_paymethod.getText().toString().equals("微信")) {
									payment = "23";
									ip = "";
									if (NetUtils.isWifiConnected(ConfirmOrderActivity.this)) {
										ip = "192.168.1.1";
									} else if (NetUtils.isGPRSConnected(ConfirmOrderActivity.this)) {
										ip = NetUtils.getLocalIpAddress();
									}
									params.clear();
									params.put("token", token);
									params.put("version", "2.1");
									params.put("ids", goodsId);
									params.put("address_options", address_options);
									params.put("payment", payment);
									params.put("ip", ip);
									dialog = new ProgressDialog(ConfirmOrderActivity.this);
									dialog.setMessage("获取订单中...");
									dialog.show();
									VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.COMMITORDER, params,
											new OnCallBack() {

										@Override
										public void OnSuccess(String data) {
											dialog.dismiss();
											try {
												JSONObject object = new JSONObject(data);
												String error = object.optString("error");
												if (error.equals("0")) {
													JSONObject obj = object.optJSONObject("data");
													String order_ids = obj.optString("order_id");
													params.clear();
													params.put("token", token);
													params.put("version", "2.1");
													final ProgressDialog dialog = new ProgressDialog(
															ConfirmOrderActivity.this);
													dialog.setMessage("跳转支付界面");
													dialog.show();
													VolleyHelper.post(
															MyConstant.SERVICENAME + MyConstant.GOTOPAY + "&order_ids="
																	+ order_ids + "&payment=" + payment + "&ip=" + ip,
															params, new OnCallBack() {

														@Override
														public void OnSuccess(String data) {
															dialog.dismiss();
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
															dialog.dismiss();
															Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络",
																	Toast.LENGTH_SHORT).show();
														}
													});
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void OnError(VolleyError volleyError) {
											dialog.dismiss();
											Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT)
													.show();
										}
									});
								} else if (tv_confirm_paymethod.getText().toString().equals("线下支付")) {
									payment = "18";
									params.clear();
									params.put("token", token);
									params.put("version", "2.1");
									params.put("ids", goodsId);
									params.put("address_options", address_options);
									params.put("payment", payment);
									dialog = new ProgressDialog(ConfirmOrderActivity.this);
									dialog.setMessage("正在跳转支付...");
									dialog.show();
									VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.COMMITORDER, params,
											new OnCallBack() {

										@Override
										public void OnSuccess(String data) {
											dialog.dismiss();
											try {
												JSONObject object = new JSONObject(data);
												String error = object.optString("error");
												String msg = object.optString("msg");
												if (error.equals("0")) {
													Toast toast = Toast.makeText(ConfirmOrderActivity.this,
															"请尽快完成支付...", Toast.LENGTH_SHORT);
													toast.setGravity(Gravity.CENTER, 0, 0);
													toast.show();
													openOrderList();
													ConfirmOrderActivity.this.finish();
												} else {
													Toast.makeText(ConfirmOrderActivity.this, msg, Toast.LENGTH_SHORT)
															.show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void OnError(VolleyError volleyError) {
											dialog.dismiss();
											Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT)
													.show();
										}
									});
								}

							} else {
								Toast.makeText(ConfirmOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
			}
			tv_confrim_commit.setEnabled(true);
			break;
		case R.id.ll_confirm_myaddress:
			Intent intent = new Intent(ConfirmOrderActivity.this, MyAddressActivity.class);
			intent.putExtra("user_id", user_id);
			startActivityForResult(intent, 1);
			break;
		case R.id.rl_confirm_pay:
			Intent intent2 = new Intent(ConfirmOrderActivity.this, PayMethodActivity.class);
			intent2.putExtra("tag", 0);
			startActivityForResult(intent2, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * 微信支付
	 * 
	 * @param model
	 */
	private void payWX(WXPayModel model) {
		IWXAPI mIwxapi = WXHelper.registeAppId(ConfirmOrderActivity.this);
		PayReq req = WXHelper.payReq(model);
		if (mIwxapi.sendReq(req)) {
		} else {
			Toast.makeText(this, "请安装登录微信", Toast.LENGTH_SHORT).show();
			openOrderList();
			finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == 0) {
				iv_confirm_paymethod.setBackgroundResource(R.drawable.alipay);
				tv_confirm_paymethod.setText("支付宝");
			} else if (resultCode == 1) {
				iv_confirm_paymethod.setBackgroundResource(R.drawable.wechat);
				tv_confirm_paymethod.setText("微信");
			} else if (resultCode == 2) {
				iv_confirm_paymethod.setBackgroundResource(R.drawable.linedown);
				tv_confirm_paymethod.setText("线下支付");
			}
		} else if (requestCode == 1) {
			if (resultCode == 0) {
				et_cofirm_consignee.setText(data.getStringExtra("consignee"));
				tv_confirm_address.setText(data.getStringExtra("region_name"));
				et_cofirm_phone.setText(data.getStringExtra("phone_mob"));
				et_cofirm_postal.setText(data.getStringExtra("zipcode"));
				et_cofirm_address.setText(data.getStringExtra("address"));
				ext_region_id_1 = data.getStringExtra("ext_region_id_1");
				ext_region_id_2 = data.getStringExtra("ext_region_id_2");
				ext_region_id_3 = data.getStringExtra("ext_region_id_3");

				params.clear();
				params.put("token", token);
				params.put("version", "2.1");
				params.put("ids", goodsId);
				params.put("ext_region_id_1", ext_region_id_1);
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISCONFIRMORDER, params, new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							if (error.equals("0")) {
								JSONArray array = object.optJSONArray("data");
								double fee = 0;
								double total = 0;
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.optJSONObject(i);
									fee += obj.optDouble("total_fee");
									total += obj.optDouble("total_amount");
								}
								if (fee == 0) {
									tv_confirm_freight.setText("0.00");
								} else {
									tv_confirm_freight.setText(df.format(fee));
								}
								tv_confirm_total.setText(total + "");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						Toast.makeText(ConfirmOrderActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});

			} else if (resultCode == 1) {

			}
		}
	}

}
