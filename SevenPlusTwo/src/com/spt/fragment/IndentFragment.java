package com.spt.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.DisOrderAdapter;
import com.spt.bean.DisOrderBottomInfo;
import com.spt.bean.DisOrderTopInfo;
import com.spt.controler.MyExpandableListView;
import com.spt.controler.MyScrollView;
import com.spt.controler.MyScrollView.OnGetBottomListener;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.page.DisOrderDetailActivity;
import com.spt.page.SearchIndentActivity;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 分销订单
 * 
 * @author lihongxuan
 *
 */
public class IndentFragment extends Fragment
		implements OnHeaderRefreshListener, OnFooterRefreshListener, OnGetBottomListener {

	private View view;
	private Context mContext;
	private ImageView iv_ft_ind_search, iv_ft_indent_back;
	private RelativeLayout rl_ft_ind_logo;
	private MyExpandableListView elv_ft_indent;
	private List<DisOrderTopInfo> groups;// 订单条目订单状态
	private Map<String, List<DisOrderBottomInfo>> children;// 订单条目商品
	private DisOrderAdapter mAdapter;
	private PullToRefreshView ptrv_indent;
	private MyScrollView msv_ft_indent;

	private String order_sn, status, add_time_from, add_time_to;

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private int offset = 0;// 订单列表起始位置

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_indent, null);
		mContext = getActivity();
		initView();

		Bundle bundle = getArguments();
		int tag = bundle.getInt("tag");
		if (tag == 1) {
			iv_ft_indent_back.setVisibility(View.VISIBLE);
			iv_ft_ind_search.setVisibility(View.GONE);
		} else if (tag == 2) {
			iv_ft_indent_back.setVisibility(View.GONE);
			iv_ft_ind_search.setVisibility(View.VISIBLE);
		} else {
			iv_ft_indent_back.setVisibility(View.GONE);
			iv_ft_ind_search.setVisibility(View.VISIBLE);
		}

		order_sn = bundle.getString("order_sn");
		status = bundle.getString("status");
		add_time_from = bundle.getString("add_time_from");
		add_time_to = bundle.getString("add_time_to");

		initListener();
		return view;
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		iv_ft_ind_search = (ImageView) view.findViewById(R.id.iv_ft_ind_search);
		rl_ft_ind_logo = (RelativeLayout) view.findViewById(R.id.rl_ft_ind_logo);
		elv_ft_indent = (MyExpandableListView) view.findViewById(R.id.elv_ft_indent);
		elv_ft_indent.setGroupIndicator(null);
		iv_ft_indent_back = (ImageView) view.findViewById(R.id.iv_ft_indent_back);

		ptrv_indent = (PullToRefreshView) view.findViewById(R.id.main_refreshview);
		ptrv_indent.setOnHeaderRefreshListener(this);
		ptrv_indent.setOnFooterRefreshListener(this);
		ptrv_indent.setLastUpdated(new Date().toLocaleString());
		msv_ft_indent = (MyScrollView) view.findViewById(R.id.msv_ft_indent);

		spHome = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");

		groups = new ArrayList<DisOrderTopInfo>();
		children = new HashMap<String, List<DisOrderBottomInfo>>();
		mAdapter = new DisOrderAdapter(mContext, groups, children);
		elv_ft_indent.setAdapter(mAdapter);
		// 设置标题部分不可点击收放
		elv_ft_indent.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

				Intent intent = new Intent(mContext, DisOrderDetailActivity.class);
				intent.putExtra("order_id", groups.get(groupPosition).getOrder_id());
				startActivity(intent);

				return true;
			}
		});

	}

	private void initListener() {
		iv_ft_ind_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SearchIndentActivity.class);
				startActivity(intent);
			}
		});

		iv_ft_indent_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});

		msv_ft_indent.setBottomListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (order_sn == null && status == null && add_time_from == null && add_time_to == null) {
			initData();
		} else {
			initData2();
		}
	}

	private void initData() {
		groups.clear();
		children.clear();
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("数据加载中,请稍等...");
		dialog.show();
		offset = 0;
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISORDER + "&offset=" + offset + "&size=" + 50, params,
				new OnCallBack() {

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
									DisOrderTopInfo disOrderInfo = new DisOrderTopInfo();
									disOrderInfo.setOrder_id(obj.optString("order_id"));
									disOrderInfo.setOrder_sn(obj.optString("order_sn"));
									disOrderInfo.setProfit(obj.optString("profit"));
									disOrderInfo.setAgent_id(obj.optString("agent_id"));
									if (obj.optString("status").equals("11")) {
										disOrderInfo.setStatus("待付款");
									} else if (obj.optString("status").equals("20")) {
										disOrderInfo.setStatus("待发货");
									} else if (obj.optString("status").equals("30")) {
										disOrderInfo.setStatus("已发货");
									} else if (obj.optString("status").equals("40")) {
										disOrderInfo.setStatus("交易成功");
									} else if (obj.optString("status").equals("50")) {
										disOrderInfo.setStatus("已退款");
									} else if (obj.optString("status").equals("0")) {
										disOrderInfo.setStatus("交易取消");
									}
									disOrderInfo.setPay_time(obj.optString("pay_time"));
									disOrderInfo.setFinal_amount(obj.optString("final_amount"));
									groups.add(disOrderInfo);

									List<DisOrderBottomInfo> list = new ArrayList<DisOrderBottomInfo>();
									JSONArray array2 = obj.optJSONArray("goods_list");
									for (int j = 0; j < array2.length(); j++) {
										JSONObject obj2 = array2.optJSONObject(j);
										DisOrderBottomInfo goodsList = new DisOrderBottomInfo();
										goodsList.setGoods_name(obj2.optString("goods_name"));
										goodsList.setGoods_image(obj2.optString("goods_image"));
										goodsList.setSpecification(obj2.optString("specification"));
										goodsList.setPrice(obj2.getString("price"));
										goodsList.setQuantity(obj2.optString("quantity"));
										list.add(goodsList);
									}
									children.put(groups.get(i).getOrder_id(), list);
									mAdapter.notifyDataSetChanged();
									for (int y = 0; y < mAdapter.getGroupCount(); y++) {
										elv_ft_indent.expandGroup(y);// 初始化时，将ExpandableListView以展开的方式呈现
									}
								}

								if (groups.size() > 0 && groups != null) {
									rl_ft_ind_logo.setVisibility(View.GONE);
									elv_ft_indent.setVisibility(View.VISIBLE);
								} else {
									rl_ft_ind_logo.setVisibility(View.VISIBLE);
									elv_ft_indent.setVisibility(View.GONE);
								}

							} else if (error.equals("1")) {
								Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						dialog.dismiss();
						groups.clear();
						children.clear();
						mAdapter.notifyDataSetChanged();
						Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});

	}

	private void initData2() {
		groups.clear();
		children.clear();
		dialog = new ProgressDialog(mContext);
		dialog.setMessage("数据加载中,请稍等...");
		dialog.show();
		offset = 0;
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISORDER + "&offset=" + offset + "&size=" + 50
				+ "&order_sn=" + order_sn + "&status=" + status + "&add_time_from=" + add_time_from + "&add_time_to="
				+ add_time_to, params, new OnCallBack() {

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
									DisOrderTopInfo disOrderInfo = new DisOrderTopInfo();
									disOrderInfo.setOrder_id(obj.optString("order_id"));
									disOrderInfo.setOrder_sn(obj.optString("order_sn"));
									disOrderInfo.setProfit(obj.optString("profit"));
									disOrderInfo.setAgent_id(obj.optString("agent_id"));
									if (obj.optString("status").equals("11")) {
										disOrderInfo.setStatus("待付款");
									} else if (obj.optString("status").equals("20")) {
										disOrderInfo.setStatus("待发货");
									} else if (obj.optString("status").equals("30")) {
										disOrderInfo.setStatus("已发货");
									} else if (obj.optString("status").equals("40")) {
										disOrderInfo.setStatus("交易成功");
									} else if (obj.optString("status").equals("50")) {
										disOrderInfo.setStatus("已退款");
									} else if (obj.optString("status").equals("0")) {
										disOrderInfo.setStatus("交易取消");
									}
									disOrderInfo.setPay_time(obj.optString("pay_time"));
									disOrderInfo.setFinal_amount(obj.optString("final_amount"));
									groups.add(disOrderInfo);

									List<DisOrderBottomInfo> list = new ArrayList<DisOrderBottomInfo>();
									JSONArray array2 = obj.optJSONArray("goods_list");
									for (int j = 0; j < array2.length(); j++) {
										JSONObject obj2 = array2.optJSONObject(j);
										DisOrderBottomInfo goodsList = new DisOrderBottomInfo();
										goodsList.setGoods_name(obj2.optString("goods_name"));
										goodsList.setGoods_image(obj2.optString("goods_image"));
										goodsList.setSpecification(obj2.optString("specification"));
										goodsList.setPrice(obj2.getString("price"));
										goodsList.setQuantity(obj2.optString("quantity"));
										list.add(goodsList);
									}
									children.put(groups.get(i).getOrder_id(), list);
									mAdapter.notifyDataSetChanged();
									for (int y = 0; y < mAdapter.getGroupCount(); y++) {
										elv_ft_indent.expandGroup(y);// 初始化时，将ExpandableListView以展开的方式呈现
									}
								}

								if (groups.size() > 0 && groups != null) {
									rl_ft_ind_logo.setVisibility(View.GONE);
									elv_ft_indent.setVisibility(View.VISIBLE);
								} else {
									rl_ft_ind_logo.setVisibility(View.VISIBLE);
									elv_ft_indent.setVisibility(View.GONE);
								}

							} else if (error.equals("1")) {
								Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						dialog.dismiss();
						groups.clear();
						children.clear();
						mAdapter.notifyDataSetChanged();
						Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		ptrv_indent.postDelayed(new Runnable() {

			@Override
			public void run() {
				offset += 50;
				dialog = new ProgressDialog(mContext);
				dialog.setMessage("数据加载中,请稍等...");
				dialog.show();
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISORDER + "&offset=" + offset + "&size=" + 50
						+ "&order_sn=" + order_sn + "&status=" + status + "&add_time_from=" + add_time_from
						+ "&add_time_to=" + add_time_to, params, new OnCallBack() {

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
									DisOrderTopInfo disOrderInfo = new DisOrderTopInfo();
									disOrderInfo.setOrder_id(obj.optString("order_id"));
									disOrderInfo.setOrder_sn(obj.optString("order_sn"));
									disOrderInfo.setProfit(obj.optString("profit"));
									disOrderInfo.setAgent_id(obj.optString("agent_id"));
									if (obj.optString("status").equals("11")) {
										disOrderInfo.setStatus("待付款");
									} else if (obj.optString("status").equals("20")) {
										disOrderInfo.setStatus("待发货");
									} else if (obj.optString("status").equals("30")) {
										disOrderInfo.setStatus("已发货");
									} else if (obj.optString("status").equals("40")) {
										disOrderInfo.setStatus("交易成功");
									} else if (obj.optString("status").equals("50")) {
										disOrderInfo.setStatus("已退款");
									} else if (obj.optString("status").equals("0")) {
										disOrderInfo.setStatus("交易取消");
									}
									disOrderInfo.setPay_time(obj.optString("pay_time"));
									disOrderInfo.setFinal_amount(obj.optString("final_amount"));
									groups.add(disOrderInfo);

									List<DisOrderBottomInfo> list = new ArrayList<DisOrderBottomInfo>();
									JSONArray array2 = obj.optJSONArray("goods_list");
									for (int j = 0; j < array2.length(); j++) {
										JSONObject obj2 = array2.optJSONObject(j);
										DisOrderBottomInfo goodsList = new DisOrderBottomInfo();
										goodsList.setGoods_name(obj2.optString("goods_name"));
										goodsList.setGoods_image(obj2.optString("goods_image"));
										goodsList.setSpecification(obj2.optString("specification"));
										goodsList.setPrice(obj2.getString("price"));
										goodsList.setQuantity(obj2.optString("quantity"));
										list.add(goodsList);
									}
									children.put(groups.get(i).getOrder_id(), list);
									mAdapter.notifyDataSetChanged();
									for (int y = 0; y < mAdapter.getGroupCount(); y++) {
										elv_ft_indent.expandGroup(y);// 初始化时，将ExpandableListView以展开的方式呈现
									}
								}

								if (groups.size() > 0 && groups != null) {
									rl_ft_ind_logo.setVisibility(View.GONE);
									elv_ft_indent.setVisibility(View.VISIBLE);
								} else {
									rl_ft_ind_logo.setVisibility(View.VISIBLE);
									elv_ft_indent.setVisibility(View.GONE);
								}

							} else if (error.equals("1")) {
								Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
				ptrv_indent.onFooterRefreshComplete();
			}
		}, 1000);

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		ptrv_indent.postDelayed(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				groups.clear();
				children.clear();
				mAdapter.notifyDataSetChanged();
				offset = 0;
				dialog = new ProgressDialog(mContext);
				dialog.setMessage("数据加载中,请稍等...");
				dialog.show();
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISORDER + "&offset=" + offset + "&size=" + 50,
						params, new OnCallBack() {

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
									DisOrderTopInfo disOrderInfo = new DisOrderTopInfo();
									disOrderInfo.setOrder_id(obj.optString("order_id"));
									disOrderInfo.setOrder_sn(obj.optString("order_sn"));
									disOrderInfo.setProfit(obj.optString("profit"));
									disOrderInfo.setAgent_id(obj.optString("agent_id"));
									if (obj.optString("status").equals("11")) {
										disOrderInfo.setStatus("待付款");
									} else if (obj.optString("status").equals("20")) {
										disOrderInfo.setStatus("待发货");
									} else if (obj.optString("status").equals("30")) {
										disOrderInfo.setStatus("已发货");
									} else if (obj.optString("status").equals("40")) {
										disOrderInfo.setStatus("交易成功");
									} else if (obj.optString("status").equals("50")) {
										disOrderInfo.setStatus("已退款");
									} else if (obj.optString("status").equals("0")) {
										disOrderInfo.setStatus("交易取消");
									}
									disOrderInfo.setPay_time(obj.optString("pay_time"));
									disOrderInfo.setFinal_amount(obj.optString("final_amount"));
									groups.add(disOrderInfo);

									List<DisOrderBottomInfo> list = new ArrayList<DisOrderBottomInfo>();
									JSONArray array2 = obj.optJSONArray("goods_list");
									for (int j = 0; j < array2.length(); j++) {
										JSONObject obj2 = array2.optJSONObject(j);
										DisOrderBottomInfo goodsList = new DisOrderBottomInfo();
										goodsList.setGoods_name(obj2.optString("goods_name"));
										goodsList.setGoods_image(obj2.optString("goods_image"));
										goodsList.setSpecification(obj2.optString("specification"));
										goodsList.setPrice(obj2.getString("price"));
										goodsList.setQuantity(obj2.optString("quantity"));
										list.add(goodsList);
									}
									children.put(groups.get(i).getOrder_id(), list);
									mAdapter.notifyDataSetChanged();
									for (int y = 0; y < mAdapter.getGroupCount(); y++) {
										elv_ft_indent.expandGroup(y);// 初始化时，将ExpandableListView以展开的方式呈现
									}
								}

								if (groups.size() > 0 && groups != null) {
									rl_ft_ind_logo.setVisibility(View.GONE);
									elv_ft_indent.setVisibility(View.VISIBLE);
								} else {
									rl_ft_ind_logo.setVisibility(View.VISIBLE);
									elv_ft_indent.setVisibility(View.GONE);
								}

							} else if (error.equals("1")) {
								Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
				ptrv_indent.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
			}
		}, 1000);

	}

	@Override
	public void onBottom() {
		// elv_ft_indent.setBottomFlag(true);
	}

}
