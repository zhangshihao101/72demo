package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.AllGoodsAdapter;
import com.spt.bean.AllGoodsInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

/**
 * 分销查询结果
 * 
 * @author lihongxuan
 *
 */
public class DisSearchResultActivity extends BaseActivity implements OnClickListener {

	private TextView tv_pop_all_order, tv_pop_stock_order, tv_pop_price_high, tv_pop_price_low;
	private ImageView iv_search_result_back, iv_result_search, iv_search_result_order;
	private ListView lv_search_result;
	private List<AllGoodsInfo> mList;
	private AllGoodsAdapter mAdapter;

	private ProgressDialog dialog;

	private String search;// 传过来的搜索关键字
	private SharedPreferences spHome;
	private String token;// 必须传的参数
	private int offset = 0;// 所有商品起始页
	private boolean isBottom;// 判断是否到底部

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_search_result);
		super.onCreate(savedInstanceState);

		search = getIntent().getStringExtra("search");

		initData();

	}

	private void initData() {

		dialog = new ProgressDialog(this);
		dialog.setMessage("数据正在加载中,请稍等...");
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder()
						.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&keywords=" + search)
						.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						if (!arg1.isSuccessful()) {
							return;
						}
						final String jsonStr = arg1.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject obj = new JSONObject(jsonStr);
									String error = obj.optString("error");
									if (error.equals("0")) {
										JSONArray array = obj.optJSONArray("data");
										for (int i = 0; i < array.length(); i++) {
											JSONObject object = array.optJSONObject(i);
											AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
											allGoodsInfo.setGoodsId(object.optString("goods_id"));
											allGoodsInfo.setGoodsName(object.optString("goods_name"));
											allGoodsInfo.setGoodsImg(object.optString("default_image"));
											allGoodsInfo.setGoodsStock(object.optString("total_stock"));
											allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
											allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
											allGoodsInfo.setGoodsMinMarPrice(object.optString("all_min_market_price"));
											allGoodsInfo.setGoodsMaxMarPrice(object.optString("all_max_market_price"));
											allGoodsInfo.setGoodsMinShopPrice(object.optString("all_min_price"));
											allGoodsInfo.setGoodsMaxShopPrice(object.optString("all_max_price"));
											allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
											allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
											allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
											mList.add(allGoodsInfo);
											mAdapter.notifyDataSetChanged();
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

		lv_search_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String goodsId = mList.get(position).getGoodsId();
				Intent intent = new Intent(DisSearchResultActivity.this, DisGoodsDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", goodsId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 分享
		mAdapter.setShareClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				Intent intent = new Intent(DisSearchResultActivity.this, SetSalePriceActivity.class);
				intent.putExtra("goodsId", mList.get(position).getGoodsId());
				intent.putExtra("goodsName", mList.get(position).getGoodsName());
				startActivity(intent);
			}
		});

		// 判断listview是否到底部，分页加载
		lv_search_result.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE && isBottom) {
					offset += 50;
					dialog = new ProgressDialog(DisSearchResultActivity.this);
					dialog.setMessage("数据正在加载中，请稍等...");
					dialog.show();
					OkHttpManager.client.newCall(new Request.Builder()
							.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&offset=" + offset
									+ "&size=50&keywords=" + search)
							.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
							.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject obj = new JSONObject(jsonStr);
										String error = obj.optString("error");
										if (error.equals("0")) {
											JSONArray array = obj.optJSONArray("data");
											if (array.length() == 0 || array.length() < 50) {
												isBottom = false;
											} else {
												for (int i = 0; i < array.length(); i++) {
													JSONObject object = array.optJSONObject(i);
													AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
													allGoodsInfo.setGoodsId(object.optString("goods_id"));
													allGoodsInfo.setGoodsName(object.optString("goods_name"));
													allGoodsInfo.setGoodsImg(object.optString("default_image"));
													allGoodsInfo.setGoodsStock(object.optString("total_stock"));
													allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
													allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
													allGoodsInfo.setGoodsMinMarPrice(
															object.optString("all_min_market_price"));
													allGoodsInfo.setGoodsMaxMarPrice(
															object.optString("all_max_market_price"));
													allGoodsInfo
															.setGoodsMinShopPrice(object.optString("all_min_price"));
													allGoodsInfo
															.setGoodsMaxShopPrice(object.optString("all_max_price"));
													allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
													allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
													allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
													mList.add(allGoodsInfo);
													mAdapter.notifyDataSetChanged();
												}
											}

										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							dialog.dismiss();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					});

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					isBottom = true;
				} else {
					isBottom = false;
				}
			}
		});

	}

	@Override
	protected void init() {
		iv_search_result_back = (ImageView) findViewById(R.id.iv_search_result_back);
		iv_result_search = (ImageView) findViewById(R.id.iv_result_search);
		iv_search_result_order = (ImageView) findViewById(R.id.iv_search_result_order);
		lv_search_result = (ListView) findViewById(R.id.lv_search_result);

		mList = new ArrayList<AllGoodsInfo>();
		mAdapter = new AllGoodsAdapter(mList, this);
		lv_search_result.setAdapter(mAdapter);
		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
	}

	@Override
	protected void addClickEvent() {
		iv_search_result_back.setOnClickListener(this);
		iv_result_search.setOnClickListener(this);
		iv_search_result_order.setOnClickListener(this);
	}

	// 所有控件点击事件
	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "InlinedApi" })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.iv_search_result_back:
			DisSearchResultActivity.this.finish();
			break;
		// 返回搜索界面
		case R.id.iv_result_search:
			DisSearchResultActivity.this.finish();
			break;
		// 排序按钮
		case R.id.iv_search_result_order:
			View order_view = LayoutInflater.from(this).inflate(R.layout.popwindow_order, null);
			tv_pop_all_order = (TextView) order_view.findViewById(R.id.tv_pop_all_order);
			tv_pop_stock_order = (TextView) order_view.findViewById(R.id.tv_pop_stock_order);
			tv_pop_price_high = (TextView) order_view.findViewById(R.id.tv_pop_price_high);
			tv_pop_price_low = (TextView) order_view.findViewById(R.id.tv_pop_price_low);
			final PopupWindow order_pop = new PopupWindow(order_view, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			// 需要设置一下此参数，点击外边可消失
			order_pop.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			order_pop.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			order_pop.setFocusable(true);
			// 弹出位置
			order_pop.showAsDropDown(v);

			/** 综合排序 */
			tv_pop_all_order.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = new ProgressDialog(DisSearchResultActivity.this);
					dialog.setMessage("数据正在加载中,请稍等...");
					dialog.show();
					mList.clear();
					OkHttpManager.client.newCall(new Request.Builder()
							.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS)
							.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
							.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject obj = new JSONObject(jsonStr);
										String error = obj.optString("error");
										if (error.equals("0")) {
											JSONArray array = obj.optJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject object = array.optJSONObject(i);
												AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
												allGoodsInfo.setGoodsId(object.optString("goods_id"));
												allGoodsInfo.setGoodsName(object.optString("goods_name"));
												allGoodsInfo.setGoodsImg(object.optString("default_image"));
												allGoodsInfo.setGoodsStock(object.optString("total_stock"));
												allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
												allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
												allGoodsInfo
														.setGoodsMinMarPrice(object.optString("all_min_market_price"));
												allGoodsInfo
														.setGoodsMaxMarPrice(object.optString("all_max_market_price"));
												allGoodsInfo.setGoodsMinShopPrice(object.optString("all_min_price"));
												allGoodsInfo.setGoodsMaxShopPrice(object.optString("all_max_price"));
												allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
												allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
												allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
												mList.add(allGoodsInfo);
												mAdapter.notifyDataSetChanged();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							dialog.dismiss();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					});
					order_pop.dismiss();
				}
			});

			/** 库存由高到低排序 */
			tv_pop_stock_order.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = new ProgressDialog(DisSearchResultActivity.this);
					dialog.setMessage("数据正在加载中,请稍等...");
					dialog.show();
					mList.clear();
					OkHttpManager.client.newCall(new Request.Builder()
							.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&order=stock&sort=desc&keywords="
									+ search)
							.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
							.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject obj = new JSONObject(jsonStr);
										String error = obj.optString("error");
										if (error.equals("0")) {
											JSONArray array = obj.optJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject object = array.optJSONObject(i);
												AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
												allGoodsInfo.setGoodsId(object.optString("goods_id"));
												allGoodsInfo.setGoodsName(object.optString("goods_name"));
												allGoodsInfo.setGoodsImg(object.optString("default_image"));
												allGoodsInfo.setGoodsStock(object.optString("total_stock"));
												allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
												allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
												allGoodsInfo
														.setGoodsMinMarPrice(object.optString("all_min_market_price"));
												allGoodsInfo
														.setGoodsMaxMarPrice(object.optString("all_max_market_price"));
												allGoodsInfo.setGoodsMinShopPrice(object.optString("all_min_price"));
												allGoodsInfo.setGoodsMaxShopPrice(object.optString("all_max_price"));
												allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
												allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
												allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
												mList.add(allGoodsInfo);
												mAdapter.notifyDataSetChanged();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							dialog.dismiss();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					});
					order_pop.dismiss();
				}
			});

			/** 价格由高到低排序 */
			tv_pop_price_high.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = new ProgressDialog(DisSearchResultActivity.this);
					dialog.setMessage("数据正在加载中,请稍等...");
					dialog.show();
					mList.clear();
					OkHttpManager.client.newCall(new Request.Builder()
							.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS
									+ "&order=dis_price&sort=desc&keywords=" + search)
							.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
							.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject obj = new JSONObject(jsonStr);
										String error = obj.optString("error");
										if (error.equals("0")) {
											JSONArray array = obj.optJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject object = array.optJSONObject(i);
												AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
												allGoodsInfo.setGoodsId(object.optString("goods_id"));
												allGoodsInfo.setGoodsName(object.optString("goods_name"));
												allGoodsInfo.setGoodsImg(object.optString("default_image"));
												allGoodsInfo.setGoodsStock(object.optString("total_stock"));
												allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
												allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
												allGoodsInfo
														.setGoodsMinMarPrice(object.optString("all_min_market_price"));
												allGoodsInfo
														.setGoodsMaxMarPrice(object.optString("all_max_market_price"));
												allGoodsInfo.setGoodsMinShopPrice(object.optString("all_min_price"));
												allGoodsInfo.setGoodsMaxShopPrice(object.optString("all_max_price"));
												allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
												allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
												allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
												mList.add(allGoodsInfo);
												mAdapter.notifyDataSetChanged();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							dialog.dismiss();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					});
					order_pop.dismiss();
				}
			});

			/** 价格由低到高 */
			tv_pop_price_low.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog = new ProgressDialog(DisSearchResultActivity.this);
					dialog.setMessage("数据正在加载中,请稍等...");
					dialog.show();
					mList.clear();
					OkHttpManager.client.newCall(new Request.Builder()
							.url(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&order=dis_price&sort=asc&keywords="
									+ search)
							.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
							.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject obj = new JSONObject(jsonStr);
										String error = obj.optString("error");
										if (error.equals("0")) {
											JSONArray array = obj.optJSONArray("data");
											for (int i = 0; i < array.length(); i++) {
												JSONObject object = array.optJSONObject(i);
												AllGoodsInfo allGoodsInfo = new AllGoodsInfo();
												allGoodsInfo.setGoodsId(object.optString("goods_id"));
												allGoodsInfo.setGoodsName(object.optString("goods_name"));
												allGoodsInfo.setGoodsImg(object.optString("default_image"));
												allGoodsInfo.setGoodsStock(object.optString("total_stock"));
												allGoodsInfo.setGoodsMinDisPrice(object.optString("min_price"));
												allGoodsInfo.setGoodsMaxDisPrice(object.optString("max_price"));
												allGoodsInfo
														.setGoodsMinMarPrice(object.optString("all_min_market_price"));
												allGoodsInfo
														.setGoodsMaxMarPrice(object.optString("all_max_market_price"));
												allGoodsInfo.setGoodsMinShopPrice(object.optString("all_min_price"));
												allGoodsInfo.setGoodsMaxShopPrice(object.optString("all_max_price"));
												allGoodsInfo.setDisStartTime(object.optString("ext_dis_stime"));
												allGoodsInfo.setDisEndTime(object.optString("ext_dis_etime"));
												allGoodsInfo.setDisTimeType(object.optString("dis_time_type"));
												mList.add(allGoodsInfo);
												mAdapter.notifyDataSetChanged();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							dialog.dismiss();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(DisSearchResultActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT)
											.show();
								}
							});
						}
					});
					order_pop.dismiss();
				}
			});
			break;
		default:
			break;
		}
	}

}
