package com.spt.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.DisShopCartAdapter;
import com.spt.bean.ShopCartInfo;
import com.spt.page.ConfirmOrderActivity;
import com.spt.sht.R;
import com.spt.utils.ListUtils;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 购物车
 * 
 * @author lihongxuan
 *
 */
@SuppressLint("InflateParams")
public class ShopCartFragment extends BaseFragment implements OnCheckedChangeListener, OnRefreshListener {

	private View view;
	private Context mContext;
	private ImageView iv_ft_shop_del, iv_ft_cart_back;
	private TextView tv_ft_cart_total, tv_ft_cart_account;
	private CheckBox cb_ft_cart_allcheck;
	private ListView lv_ft_cart;
	private SwipeRefreshLayout srl_shopcart;
	private RelativeLayout rl_ft_cart_bg;
	private List<ShopCartInfo> mList;
	private DisShopCartAdapter mAdapter;
	private double totalPrice; // 商品总价格s
	private boolean select_all = true;// 全选标记
	private int position;
	private List<String> goodsIdList;// 购物车商品id集合
	private String user_id;

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shopcart, null);
		mContext = getActivity();

		initView();

		Bundle bundle = getArguments();
		boolean flag = bundle.getBoolean("flag");
		if (flag == true) {
			iv_ft_cart_back.setVisibility(View.VISIBLE);
		} else {
			iv_ft_cart_back.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		goodsIdList.clear();
		cb_ft_cart_allcheck.setChecked(false);
		tv_ft_cart_total.setText("0.00");
		initData();

		initListener();
	}

	@SuppressLint("InlinedApi")
	private void initView() {
		tv_ft_cart_total = (TextView) view.findViewById(R.id.tv_ft_cart_total);
		tv_ft_cart_account = (TextView) view.findViewById(R.id.tv_ft_cart_account);
		iv_ft_shop_del = (ImageView) view.findViewById(R.id.iv_ft_shop_del);
		iv_ft_cart_back = (ImageView) view.findViewById(R.id.iv_ft_cart_back);
		cb_ft_cart_allcheck = (CheckBox) view.findViewById(R.id.cb_ft_cart_allcheck);
		lv_ft_cart = (ListView) view.findViewById(R.id.lv_ft_cart);
		rl_ft_cart_bg = (RelativeLayout) view.findViewById(R.id.rl_ft_cart_bg);
		srl_shopcart = (SwipeRefreshLayout) view.findViewById(R.id.srl_shopcart);
		srl_shopcart.setOnRefreshListener(this);
		srl_shopcart.setColorSchemeResources(android.R.color.holo_orange_light);
		srl_shopcart.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		srl_shopcart.setSize(SwipeRefreshLayout.DEFAULT);

		goodsIdList = new ArrayList<String>();
		spHome = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");
		mList = new ArrayList<ShopCartInfo>();
		mAdapter = new DisShopCartAdapter(mContext, mList, params, handler);
		lv_ft_cart.setAdapter(mAdapter);
	}

	private void initData() {

		mList.clear();
		mAdapter.notifyDataSetChanged();

		dialog = new ProgressDialog(mContext);
		dialog.setMessage("数据正在加载,请稍等...");
		dialog.show();

		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISSHOPCART, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(data);
					String error = object.optString("error");
					if (error.equals("0")) {
						JSONArray array = object.optJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.optJSONObject(i);
							ShopCartInfo shopCartInfo = new ShopCartInfo();
							user_id = obj.optString("user_id");
							shopCartInfo.setGoodsName(obj.optString("item_name"));
							shopCartInfo.setGoodsImg(obj.optString("goods_image"));
							shopCartInfo.setGoodsSpec(obj.optString("specification"));
							shopCartInfo.setGoodsCount(obj.optInt("quantity"));
							shopCartInfo.setGoodsPrice(obj.optString("price"));
							shopCartInfo.setGoodsRec_id(obj.optString("rec_id"));
							shopCartInfo.setGoodsId(obj.optString("item_id"));
							shopCartInfo.setGoodsSpecId(obj.optString("spec_id"));
							mList.add(shopCartInfo);
							mAdapter.notifyDataSetChanged();
						}
						if (mList.size() > 0 && mList != null) {
							rl_ft_cart_bg.setVisibility(View.GONE);
							lv_ft_cart.setVisibility(View.VISIBLE);
						} else {
							rl_ft_cart_bg.setVisibility(View.VISIBLE);
							lv_ft_cart.setVisibility(View.GONE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void OnError(VolleyError volleyError) {
				dialog.dismiss();
				Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				mAdapter.notifyDataSetChanged();
			} else if (msg.what == 2) {
				mAdapter.notifyDataSetChanged();
			}

		}
	};

	private void initListener() {
		// 结算按钮
		tv_ft_cart_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (goodsIdList.size() == 0) {
					Toast toast = Toast.makeText(mContext, "您的购物车选择商品为空，无法结算", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					Intent intent = new Intent(mContext, ConfirmOrderActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("user_id", user_id);
					bundle.putString("goodsId", ListUtils.listToString(goodsIdList));
					bundle.putString("totalPrice", tv_ft_cart_total.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		// 返回按钮
		iv_ft_cart_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});
		// 删除购物车商品
		iv_ft_shop_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (goodsIdList.size() == 0 || goodsIdList == null) {
					Toast toast = Toast.makeText(mContext, "请选择要删除的商品", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setMessage("确定要删除这些商品吗？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String goodsId = ListUtils.listToString(goodsIdList);
							VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DELGOODS + goodsId, params,
									new OnCallBack() {

								@Override
								public void OnSuccess(String data) {
									try {
										JSONObject object = new JSONObject(data);
										String error = object.optString("error");
										String msg = object.optString("msg");
										if (error.equals("0")) {
											goodsIdList.clear();
											mAdapter.notifyDataSetChanged();
											Toast toast = Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0, 0);
											toast.show();
											mList.clear();
											VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISSHOPCART, params,
													new OnCallBack() {

												@Override
												public void OnSuccess(String data) {
													try {
														JSONObject object = new JSONObject(data);
														String error = object.optString("error");
														if (error.equals("0")) {
															JSONArray array = object.optJSONArray("data");
															for (int i = 0; i < array.length(); i++) {
																JSONObject obj = array.optJSONObject(i);
																ShopCartInfo shopCartInfo = new ShopCartInfo();
																shopCartInfo.setGoodsName(obj.optString("item_name"));
																shopCartInfo.setGoodsImg(obj.optString("goods_image"));
																shopCartInfo
																		.setGoodsSpec(obj.optString("specification"));
																shopCartInfo.setGoodsCount(obj.optInt("quantity"));
																shopCartInfo.setGoodsPrice(obj.optString("price"));
																shopCartInfo.setGoodsRec_id(obj.optString("rec_id"));
																shopCartInfo.setGoodsId(obj.optString("item_id"));
																shopCartInfo.setGoodsSpecId(obj.optString("spec_id"));
																mList.add(shopCartInfo);
																mAdapter.notifyDataSetChanged();
															}

															if (mList.size() > 0 && mList != null) {
																rl_ft_cart_bg.setVisibility(View.GONE);
																lv_ft_cart.setVisibility(View.VISIBLE);
															} else {
																rl_ft_cart_bg.setVisibility(View.VISIBLE);
																lv_ft_cart.setVisibility(View.GONE);
																tv_ft_cart_account
																		.setText("去结算(" + goodsIdList.size() + ")");
																tv_ft_cart_total.setText("0.00");
															}
														}
													} catch (JSONException e) {
														e.printStackTrace();
													}
												}

												@Override
												public void OnError(VolleyError volleyError) {
													Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
												}
											});
										} else if (error.equals("1")) {
											Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void OnError(VolleyError volleyError) {
									Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					}).setNegativeButton("取消", null).create().show();

				}

			}
		});
		mAdapter.setIsCheckedChangeListener(this);// 给checkbox设置点击事件
		mAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				super.onChanged();

				// 商品总价重新计算
				totalPrice = 0;
				for (int i = 0; i < mList.size(); i++) {
					ShopCartInfo shopCartInfo = mList.get(i);
					if (shopCartInfo.isChecked()) {
						totalPrice += shopCartInfo.getGoodsCount() * Double.valueOf(shopCartInfo.getGoodsPrice());
					}
				}
				if (totalPrice == 0) {
					tv_ft_cart_total.setText("0.00");
				} else {
					tv_ft_cart_total.setText(totalPrice + "");
				}

			}

		});

		// 全选
		cb_ft_cart_allcheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (select_all) {
					goodsIdList.clear();
					for (int i = 0; i < mList.size(); i++) {
						mList.get(i).setChecked(true);
						goodsIdList.add(mList.get(i).getGoodsRec_id());
					}
				} else {
					goodsIdList.clear();
					for (int i = 0; i < mList.size(); i++) {
						mList.get(i).setChecked(false);
					}
				}

				tv_ft_cart_account.setText("去结算(" + goodsIdList.size() + ")");

				mAdapter.notifyDataSetChanged();
				select_all = !select_all;
			}
		});

		// msv_ft_shopcart.setBottomListener(this);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Object tag = buttonView.getTag();

		if (tag instanceof Integer) {
			position = (Integer) tag;
		}

		String goodsId = mList.get(position).getGoodsRec_id();
		if (isChecked) {
			if (!goodsIdList.contains(goodsId)) {
				goodsIdList.add(goodsId);
				mList.get(position).setChecked(isChecked);
			}
		} else {
			goodsIdList.remove(goodsId);
			mList.get(position).setChecked(isChecked);
		}

		tv_ft_cart_account.setText("去结算(" + goodsIdList.size() + ")");

		// 商品总价重新计算
		totalPrice = 0;
		for (int i = 0; i < mList.size(); i++) {
			ShopCartInfo shopCartInfo = mList.get(i);
			if (shopCartInfo.isChecked()) {
				totalPrice += shopCartInfo.getGoodsCount() * Double.valueOf(shopCartInfo.getGoodsPrice());
			}
		}

		if (totalPrice == 0) {
			tv_ft_cart_total.setText("0.00");
		} else {
			tv_ft_cart_total.setText(totalPrice + "");
		}

	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				initData();
				srl_shopcart.setRefreshing(false);
			}
		}, 1000);
	}

}
