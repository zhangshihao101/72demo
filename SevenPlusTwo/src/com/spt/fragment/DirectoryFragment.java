package com.spt.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.AllBrandAdapter;
import com.spt.adapter.AllGoodsAdapter;
import com.spt.adapter.BrandHotOneAdapter;
import com.spt.adapter.BrandHotPagerAdapter;
import com.spt.adapter.BrandHotTwoAdapter;
import com.spt.adapter.HotGoodsAdapter;
import com.spt.bean.AllBrandInfo;
import com.spt.bean.AllGoodsInfo;
import com.spt.bean.BrandHotOneInfo;
import com.spt.bean.BrandHotTwoInfo;
import com.spt.bean.DirHotGoodsInfo;
import com.spt.controler.MyGridView;
import com.spt.controler.MyListView;
import com.spt.controler.MyScrollView;
import com.spt.controler.MyScrollView.OnGetBottomListener;
import com.spt.controler.MyViewPager;
import com.spt.page.AccountExplainActivity;
import com.spt.page.DisGoodsDetailsActivity;
import com.spt.page.DisSearchActivity;
import com.spt.page.SetSalePriceActivity;
import com.spt.sht.R;
import com.spt.sht.R.id;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class DirectoryFragment extends Fragment implements OnClickListener, OnGetBottomListener {

	private ImageView iv_ft_dir_back, iv_ft_dir_search, iv_ft_dir_order, iv_dt_dir_help, iv_ft_dir_top;
	private TextView tv_pop_all_order, tv_pop_stock_order, tv_pop_price_high, tv_pop_price_low, tv_ft_dir_brand_one,
			tv_ft_dir_brand_two;
	private CheckBox cb_not_begin, cb_begin;
	private MyGridView gv_ft_dir_hot;
	private MyListView lv_ft_dir_all_goods;
	private MyScrollView sv_ft_dir;
	private LinearLayout ll_ft_dir_listview, ll_ft_dir_point1, ll_ft_dir_point2;
	private MyViewPager vp_ft_dir_brand1, vp_ft_dir_brand2;
	private View view;
	private Context mContext;
	private BrandHotPagerAdapter brandHotPagerAdapter;// 品牌热销1ViewPager适配器
	private BrandHotOneAdapter brandHotOneAdapter;// 品牌热销1适配器
	private BrandHotTwoAdapter brandHotTwoAdapter;// 品牌热销2适配器
	private List<DirHotGoodsInfo> hotGoodsList;// 热门商品集合
	private List<BrandHotOneInfo> brandHotOneInfos;// 品牌热销1集合
	private List<BrandHotTwoInfo> brandHotTwoInfos;// 品牌热销2集合
	private List<AllBrandInfo> allBrandList;// 所有品牌集合
	private List<AllGoodsInfo> allGoodsList;// 所有商品集合
	private HotGoodsAdapter hotGoodsAdapter;// 热门商品适配器
	private AllBrandAdapter allBrandAdapter;// 所有品牌适配器
	private AllGoodsAdapter allGoodsAdapter;// 所有商品适配器

	private int prePosition;// 轮播两个点前一个位置的标记
	private int pageOne, pageTwo = 0;
	private ProgressDialog progressdialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private int offset = 0;// 所有商品起始页
	private boolean isBottom;// 判断是否到底部

	private String time_select = "";

	private static final int GRIDVIEW_COUNT = 3;
	private List<View> brandHotOneGv;
	private List<View> brandHotTwoGv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_directory, null);
		mContext = getActivity();

		initView();

		// 获取屏幕高度，给自定义listview设置参数
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				height - 200);
		ll_ft_dir_listview.setLayoutParams(lp);

		initData();

		initListener();

		return view;
	}

	private void initData() {

		params.put("token", token);
		params.put("version", "2.1");

		progressdialog = new ProgressDialog(mContext);
		progressdialog.setMessage("数据正在加载中，请稍等...");
		progressdialog.show();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISHOMEHOTGOODS, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				progressdialog.dismiss();
				try {
					JSONObject obj = new JSONObject(data);
					String error = obj.optString("error");
					if (error.equals("0")) {
						JSONObject obj1 = obj.optJSONObject("data");
						tv_ft_dir_brand_one.setText("品牌热销(" + obj1.optString("floor_one_name") + ")");
						tv_ft_dir_brand_two.setText("品牌热销(" + obj1.optString("floor_two_name") + ")");
						JSONArray array = obj1.optJSONArray("hot_goods");
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.optJSONObject(i);
							DirHotGoodsInfo hotGoodsInfo = new DirHotGoodsInfo();
							hotGoodsInfo.setHot_goods_img_url(object.optString("hot_goods_image_url"));
							hotGoodsInfo.setHot_goods_url(object.optString("hot_goods_url"));
							hotGoodsInfo.setHot_goods_name(object.optString("hot_goods_name"));
							hotGoodsInfo.setHot_goods_price(object.optString("hot_goods_price"));
							hotGoodsList.add(hotGoodsInfo);
							hotGoodsAdapter.notifyDataSetChanged();
						}
						JSONArray array2 = obj1.optJSONArray("floor_set_one");
						for (int i = 0; i < array2.length(); i++) {
							JSONObject object = array2.optJSONObject(i);
							BrandHotOneInfo brandHotOneInfo = new BrandHotOneInfo();
							brandHotOneInfo.setBrand_sell_one_img_url(object.optString("floor_set_one_image_url"));
							brandHotOneInfo.setBrand_sell_one_url(object.optString("floor_set_one_url"));
							brandHotOneInfo.setBrand_sell_one_name(object.optString("floor_set_one_name"));
							brandHotOneInfo.setBrand_sell_one_price(object.optString("floor_set_one_price"));
							brandHotOneInfos.add(brandHotOneInfo);
						}
						loadBrandHotOne();
						JSONArray array3 = obj1.optJSONArray("floor_set_two");
						for (int i = 0; i < array3.length(); i++) {
							JSONObject object = array3.optJSONObject(i);
							BrandHotTwoInfo brandHotTwoInfo = new BrandHotTwoInfo();
							brandHotTwoInfo.setBrand_sell_two_img_url(object.optString("floor_set_two_image_url"));
							brandHotTwoInfo.setBrand_sell_two_url(object.optString("floor_set_two_url"));
							brandHotTwoInfo.setBrand_sell_two_name(object.optString("floor_set_two_name"));
							brandHotTwoInfo.setBrand_sell_two_price(object.optString("floor_set_two_price"));
							brandHotTwoInfos.add(brandHotTwoInfo);
						}
						loadBrandHotTwo();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void OnError(VolleyError volleyError) {
				progressdialog.dismiss();
				Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

		hotGoodsAdapter = new HotGoodsAdapter(hotGoodsList, mContext);
		gv_ft_dir_hot.setAdapter(hotGoodsAdapter);
		gv_ft_dir_hot.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str = hotGoodsList.get(position).getHot_goods_url();
				String goodsId = str.substring(27, 32);
				Intent intent = new Intent(mContext, DisGoodsDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", goodsId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		/* 所有品牌 */
		// allBrandList = new ArrayList<AllBrandInfo>();
		// VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.ALLBRAN,
		// params, new OnCallBack() {
		//
		// @Override
		// public void OnSuccess(String data) {
		// try {
		// JSONObject obj = new JSONObject(data);
		// String error = obj.optString("error");
		// String msg = obj.optString("msg");
		// if (error.equals("0")) {
		// JSONArray array = obj.optJSONArray("data");
		// for (int i = 0; i < array.length(); i++) {
		// JSONObject object = array.optJSONObject(i);
		// AllBrandInfo brandInfo = new AllBrandInfo();
		// brandInfo.setBrandId(object.optString("brand_id"));
		// brandInfo.setBrandName(object.optString("brand_name"));
		// brandInfo.setBrandLogo(object.optString("brand_logo"));
		// allBrandList.add(brandInfo);
		// allBrandAdapter.notifyDataSetChanged();
		// }
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void OnError(VolleyError volleyError) {
		// Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
		// }
		// });
		// brandRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
		// StaggeredGridLayoutManager.HORIZONTAL));
		// allBrandAdapter = new AllBrandAdapter(allBrandList, mContext);
		// brandRecyclerView.setAdapter(allBrandAdapter);

		/* 所有商品 */
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				progressdialog.dismiss();
				try {
					JSONObject obj = new JSONObject(data);
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
							allGoodsList.add(allGoodsInfo);
							allGoodsAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void OnError(VolleyError volleyError) {
				// 取消刷新条
				progressdialog.dismiss();
				Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@SuppressLint("NewApi")
	private void loadBrandHotOne() {
		final int pageCount = (brandHotOneInfos.size() + GRIDVIEW_COUNT - 1) / GRIDVIEW_COUNT;
		brandHotOneGv.clear();
		for (int i = 0; i < pageCount; i++) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.gridview_brand_hot_one, null);
			GridView mGridView = (GridView) view.findViewById(R.id.gv_brand_hot_one);
			brandHotOneAdapter = new BrandHotOneAdapter(mContext, brandHotOneInfos, i);
			mGridView.setAdapter(brandHotOneAdapter);
			mGridView.setOnItemClickListener(gvOneListener);
			brandHotOneGv.add(view);
			brandHotOneAdapter.notifyDataSetChanged();
			if (i == 1) {
				break;
			}
		}

		brandHotPagerAdapter = new BrandHotPagerAdapter(brandHotOneGv);
		vp_ft_dir_brand1.setAdapter(brandHotPagerAdapter);
		brandHotPagerAdapter.notifyDataSetChanged();

		if (pageCount <= 1) {
			ll_ft_dir_point1.getChildAt(0).setVisibility(View.GONE);
			ll_ft_dir_point1.getChildAt(1).setVisibility(View.GONE);
		} else {
			// 设置第一个点为默认点
			ll_ft_dir_point1.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);
			vp_ft_dir_brand1.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					pageOne = position;
					// 当滑动到下一张图片，修改之前图片的点
					ll_ft_dir_point1.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
					// 滑动到当前图片，修改当前图片的点
					ll_ft_dir_point1.getChildAt(position % pageCount).setBackgroundResource(R.drawable.dot_focus);
					// 这一次的当前位置为下一次当前位置的前一个选中条目
					prePosition = position % pageCount;
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
		}
	}

	OnItemClickListener gvOneListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String str = brandHotOneInfos.get(pageOne * GRIDVIEW_COUNT + position).getBrand_sell_one_url();
			String goodsId = str.substring(27, 32);
			Intent intent = new Intent(mContext, DisGoodsDetailsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("goodsId", goodsId);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	@SuppressLint("NewApi")
	private void loadBrandHotTwo() {
		final int pageCount = (brandHotTwoInfos.size() + GRIDVIEW_COUNT - 1) / GRIDVIEW_COUNT;
		brandHotTwoGv.clear();
		for (int i = 0; i < pageCount; i++) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.gridview_brand_hot_two, null);
			GridView mGridView = (GridView) view.findViewById(R.id.gv_brand_hot_two);
			brandHotTwoAdapter = new BrandHotTwoAdapter(mContext, brandHotTwoInfos, i);
			mGridView.setAdapter(brandHotTwoAdapter);
			mGridView.setOnItemClickListener(gvTwoListener);
			brandHotTwoGv.add(view);
			brandHotTwoAdapter.notifyDataSetChanged();
			if (i == 1) {
				break;
			}
		}

		brandHotPagerAdapter = new BrandHotPagerAdapter(brandHotTwoGv);
		vp_ft_dir_brand2.setAdapter(brandHotPagerAdapter);
		brandHotPagerAdapter.notifyDataSetChanged();

		if (pageCount <= 1) {
			ll_ft_dir_point2.getChildAt(0).setVisibility(View.GONE);
			ll_ft_dir_point2.getChildAt(1).setVisibility(View.GONE);
		} else {
			// 设置第一个点为默认点
			ll_ft_dir_point2.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);
			vp_ft_dir_brand2.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					pageTwo = position;
					// 当滑动到下一张图片，修改之前图片的点
					ll_ft_dir_point2.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
					// 滑动到当前图片，修改当前图片的点
					ll_ft_dir_point2.getChildAt(position % pageCount).setBackgroundResource(R.drawable.dot_focus);
					// 这一次的当前位置为下一次当前位置的前一个选中条目
					prePosition = position % pageCount;
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
		}

	}

	OnItemClickListener gvTwoListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String str = brandHotTwoInfos.get(pageTwo * GRIDVIEW_COUNT + position).getBrand_sell_two_url();
			String goodsId = str.substring(27, 32);
			Intent intent = new Intent(mContext, DisGoodsDetailsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("goodsId", goodsId);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	private void initListener() {
		iv_ft_dir_back.setOnClickListener(this);
		iv_ft_dir_search.setOnClickListener(this);
		iv_ft_dir_order.setOnClickListener(this);
		iv_dt_dir_help.setOnClickListener(this);
		iv_ft_dir_top.setOnClickListener(this);
		sv_ft_dir.setBottomListener(this);

		cb_not_begin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					time_select = "2";
					cb_not_begin.setTextColor(Color.WHITE);
					cb_begin.setTextColor(Color.BLACK);
					cb_begin.setChecked(false);
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&time_select=2", params,
							new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
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
				} else {
					cb_not_begin.setTextColor(Color.BLACK);
					if (cb_begin.isChecked() == false) {
						time_select = "";
						cb_begin.setTextColor(Color.BLACK);
						allGoodsList.clear();
						allGoodsAdapter.notifyDataSetChanged();
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject obj = new JSONObject(data);
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
											allGoodsList.add(allGoodsInfo);
											allGoodsAdapter.notifyDataSetChanged();
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
					}
				}
			}
		});

		cb_begin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					time_select = "1";
					cb_begin.setTextColor(Color.WHITE);
					cb_not_begin.setTextColor(Color.BLACK);
					cb_not_begin.setChecked(false);
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&time_select=1", params,
							new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
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
				} else {
					cb_begin.setTextColor(Color.BLACK);
					if (cb_not_begin.isChecked() == false) {
						time_select = "";
						cb_not_begin.setTextColor(Color.BLACK);
						allGoodsList.clear();
						allGoodsAdapter.notifyDataSetChanged();
						VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS, params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								try {
									JSONObject obj = new JSONObject(data);
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
											allGoodsList.add(allGoodsInfo);
											allGoodsAdapter.notifyDataSetChanged();
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
					}
				}
			}
		});

		lv_ft_dir_all_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String goodsId = allGoodsList.get(position).getGoodsId();
				Intent intent = new Intent(mContext, DisGoodsDetailsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", goodsId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// 给adapter中的分享按钮设置点击事件
		allGoodsAdapter.setShareClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				Intent intent = new Intent(mContext, SetSalePriceActivity.class);
				intent.putExtra("goodsId", allGoodsList.get(position).getGoodsId());
				intent.putExtra("goodsName", allGoodsList.get(position).getGoodsName());
				intent.putExtra("goodsImg", allGoodsList.get(position).getGoodsImg());
				startActivity(intent);
			}
		});

		// 判断listview是否到底部，分页加载
		lv_ft_dir_all_goods.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE && isBottom) {
					offset += 50;
					progressdialog = new ProgressDialog(mContext);
					progressdialog.setMessage("数据正在加载中，请稍等...");
					progressdialog.show();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&offset=" + offset + "&size=50"
							+ "&time_select=" + time_select, params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
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

	private void initView() {
		iv_ft_dir_back = (ImageView) view.findViewById(R.id.iv_ft_dir_back);
		iv_ft_dir_search = (ImageView) view.findViewById(R.id.iv_ft_dir_search);
		iv_ft_dir_order = (ImageView) view.findViewById(R.id.iv_ft_dir_order);
		iv_dt_dir_help = (ImageView) view.findViewById(R.id.iv_dt_dir_help);
		iv_ft_dir_top = (ImageView) view.findViewById(R.id.iv_ft_dir_top);
		gv_ft_dir_hot = (MyGridView) view.findViewById(R.id.gv_ft_dir_hot);
		lv_ft_dir_all_goods = (MyListView) view.findViewById(R.id.lv_ft_dir_all_goods);
		ll_ft_dir_listview = (LinearLayout) view.findViewById(R.id.ll_ft_dir_listview);
		ll_ft_dir_point1 = (LinearLayout) view.findViewById(R.id.ll_ft_dir_point1);
		ll_ft_dir_point2 = (LinearLayout) view.findViewById(R.id.ll_ft_dir_point2);
		sv_ft_dir = (MyScrollView) view.findViewById(R.id.sv_ft_dir);
		vp_ft_dir_brand1 = (MyViewPager) view.findViewById(R.id.vp_ft_dir_brand1);
		vp_ft_dir_brand2 = (MyViewPager) view.findViewById(R.id.vp_ft_dir_brand2);
		tv_ft_dir_brand_one = (TextView) view.findViewById(R.id.tv_ft_dir_brand_one);
		tv_ft_dir_brand_two = (TextView) view.findViewById(R.id.tv_ft_dir_brand_two);
		cb_not_begin = (CheckBox) view.findViewById(R.id.cb_not_begin);
		cb_begin = (CheckBox) view.findViewById(R.id.cb_begin);

		/* 热门商品和两个品牌热销 */
		hotGoodsList = new ArrayList<DirHotGoodsInfo>();
		// 第一个品牌热销数据集合
		brandHotOneInfos = new ArrayList<BrandHotOneInfo>();
		// 第二个品牌热销数据集合
		brandHotTwoInfos = new ArrayList<BrandHotTwoInfo>();
		brandHotOneGv = new ArrayList<View>();
		brandHotTwoGv = new ArrayList<View>();

		allGoodsList = new ArrayList<AllGoodsInfo>();
		allGoodsAdapter = new AllGoodsAdapter(allGoodsList, mContext);
		lv_ft_dir_all_goods.setAdapter(allGoodsAdapter);
		allGoodsAdapter.notifyDataSetChanged();

		spHome = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数

	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_ft_dir_back:
			((Activity) mContext).finish();
			break;
		// 跳转搜索界面
		case R.id.iv_ft_dir_search:
			Intent intent = new Intent(mContext, DisSearchActivity.class);
			startActivity(intent);
			break;
		// 结算说明界面
		case R.id.iv_dt_dir_help:
			Intent intent2 = new Intent(mContext, AccountExplainActivity.class);
			startActivity(intent2);
			break;
		case R.id.iv_ft_dir_top:
			sv_ft_dir.smoothScrollTo(0, 0);
			sv_ft_dir.setFocusable(true);
			break;
		// 排序按钮
		case R.id.iv_ft_dir_order:
			View order_view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_order, null);
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
					progressdialog = new ProgressDialog(mContext);
					progressdialog.setMessage("数据正在加载中，请稍等...");
					progressdialog.show();
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS, params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});
					order_pop.dismiss();
				}
			});

			/** 库存由高到低排序 */
			tv_pop_stock_order.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressdialog = new ProgressDialog(mContext);
					progressdialog.setMessage("数据正在加载中，请稍等...");
					progressdialog.show();
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&order=stock&sort=desc",
							params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});
					order_pop.dismiss();
				}
			});

			/** 价格由高到低排序 */
			tv_pop_price_high.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressdialog = new ProgressDialog(mContext);
					progressdialog.setMessage("数据正在加载中，请稍等...");
					progressdialog.show();
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&order=dis_price&sort=desc",
							params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});
					order_pop.dismiss();
				}
			});

			/** 价格由低到高 */
			tv_pop_price_low.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressdialog = new ProgressDialog(mContext);
					progressdialog.setMessage("数据正在加载中，请稍等...");
					progressdialog.show();
					allGoodsList.clear();
					allGoodsAdapter.notifyDataSetChanged();
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISALLGOODS + "&order=dis_price&sort=asc",
							params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
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
										allGoodsList.add(allGoodsInfo);
										allGoodsAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
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

	@Override
	public void onBottom() {
		lv_ft_dir_all_goods.setBottomFlag(true);
	}

}
