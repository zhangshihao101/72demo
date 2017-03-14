package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseLeftMenuActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.BrandAdapter;
import com.mts.pos.listview.CategoryAdapter;
import com.mts.pos.listview.GuideAdapter;
import com.mts.pos.listview.GuideInfo;
import com.mts.pos.listview.LeftMenuInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class GuideActivity extends BaseLeftMenuActivity implements OnClickListener {

	private LinearLayout ll_guide_sort, ll_guide_brand;
	private Button btn_guide_menu, btn_guide_scan;
	private EditText et_guide_search;
	private TextView tv_guide_sort, tv_guide_brand, tv_guide_amount, tv_guide_rank, tv_price_ascend, tv_price_decline,
			tv_guide_sort_cancal, tv_guide_sort_confirm, tv_guide_brand_cancal, tv_guide_brand_confirm;
	private GridView gv_guide_product;
	private ListView lv_guide_sort, lv_guide_brand;
	private List<GuideInfo> guideList;
	private List<String> brandItemList, categroyItemList;
	private Intent intent;
	private GuideAdapter adapter;
	private CategoryAdapter categoryAdapter;
	private List<GuideInfo> categoryList;
	private Set<Integer> categroySet;
	private BrandAdapter brandAdapter;
	private List<GuideInfo> brandList;
	private Set<Integer> brandSet;
	private String brandId;
	private String productCateId;
	private int page = 1;// 页数
	private int totalPage;// 总页数
	private boolean isBottom;// 判断是否到达底部
	private int a = 0;
	private final static int SCANNIN_GREQUEST_CODE = 10;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_guide);
		super.onCreate(inState);
		// 调用初始化控件方法
		initView();

		// 调用获取导购页面全部数据接口方法
		getGuideTotalProduct();

		// 用来记录条目内容
		categroyItemList = new ArrayList<String>();
		// 用来记录图片显示
		categroySet = new HashSet<Integer>();

		// 分类筛选条件数据
		categoryList = new ArrayList<GuideInfo>();
		categoryAdapter = new CategoryAdapter(categoryList, GuideActivity.this);
		lv_guide_sort.setAdapter(categoryAdapter);

		lv_guide_sort.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean show = categroySet.contains(position);
				if (show) {
					for (Integer integer : categroySet) {
						productCateId = categoryList.get(integer).getProductCategoryId();
						if (categroyItemList.contains(productCateId)) {
							categroyItemList.remove(productCateId);
						} else {
							continue;
						}
					}
					categroySet.remove(position);
					categoryList.get(position).setFlag(false);
					categoryAdapter.notifyDataSetChanged();
				} else {
					categroySet.add(position);
					categoryList.get(position).setFlag(true);
					categoryAdapter.notifyDataSetChanged();
					for (Integer integer : categroySet) {
						productCateId = categoryList.get(integer).getProductCategoryId();
						if (!categroyItemList.contains(productCateId)) {
							categroyItemList.add(productCateId);
						} else {
							continue;
						}
					}
				}
			}
		});

		// 用来记录条目内容
		brandItemList = new ArrayList<String>();
		// 用来记录图片显示
		brandList = new ArrayList<GuideInfo>();
		brandSet = new HashSet<Integer>();
		brandAdapter = new BrandAdapter(brandList, GuideActivity.this);
		lv_guide_brand.setAdapter(brandAdapter);
		lv_guide_brand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean show = brandSet.contains(position);
				if (show) {
					for (Integer integer : brandSet) {
						brandId = brandList.get(integer).getBrandId();
						if (brandItemList.contains(brandId)) {
							brandItemList.remove(brandId);
						} else {
							continue;
						}
					}
					brandSet.remove(position);
					brandList.get(position).setFlag(false);
					brandAdapter.notifyDataSetChanged();
				} else {
					brandSet.add(position);
					brandList.get(position).setFlag(true);
					brandAdapter.notifyDataSetChanged();
					for (int integer : brandSet) {
						brandId = brandList.get(integer).getBrandId();
						if (!brandItemList.contains(brandId)) {
							brandItemList.add(brandId);
						} else {
							continue;
						}
					}
				}

			}
		});

		// 导购所有商品数据
		guideList = new ArrayList<GuideInfo>();
		adapter = new GuideAdapter(guideList, GuideActivity.this);
		gv_guide_product.setAdapter(adapter);

		gv_guide_product.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(GuideActivity.this, GoodsDetailActivity.class);
				intent.putExtra("productId", guideList.get(position).getProductId());
				intent.putExtra("total", guideList.get(position).getTotalAvailableQuantity());
				startActivity(intent);
			}
		});

		// 查找数据监听
		et_guide_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				// 调用获取搜索数据接口方法
				searchGuideProduct();

				a = 1;
				page = 1;

				return true;

			}
		});

		gv_guide_product.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE && isBottom && page <= totalPage) {
					page++;
					if (a == 0) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 1) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 2) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < categroyItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
						}
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 3) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < brandItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
						}
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 4) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 5) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < categroyItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
						}
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 6) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < brandItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
						}
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 7) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 8) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 9) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < categroyItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
						}
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 10) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						for (int i = 0; i < brandItemList.size(); i++) {
							nameValuePairs.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
						}
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					} else if (a == 11) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(GuideActivity.this, "externalloginkey")));
						nameValuePairs.add(new BasicNameValuePair("page", page + ""));
						nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
						getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
					isBottom = true;
				} else {
					isBottom = false;
				}
			}
		});

		// 调用点击事件监听
		initListener();

	}

	// 解析数据,更新UI界面
	@Override
	protected void updateUI(String whichtask, String result) {

		if (whichtask.equals("0")) {
			try {
				JSONArray array = new JSONObject(result).optJSONArray("products");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.opt(i);
					GuideInfo guideInfo = new GuideInfo();
					guideInfo.setProductName(obj.optString("productName"));
					guideInfo.setProductListPrice(obj.optString("productListPrice"));
					guideInfo.setTotalAvailableQuantity(obj.optString("totalAvailableQuantity"));
					guideInfo.setSmallImageUrl(obj.optString("smallImageUrl"));
					guideInfo.setProductId(obj.optString("productParentId"));
					guideList.add(guideInfo);
					adapter.notifyDataSetChanged();
				}
				totalPage = new JSONObject(result).optInt("totalPage");
				tv_guide_amount.setText(new JSONObject(result).optString("total"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			try {
				JSONArray array = new JSONObject(result).optJSONArray("categories");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.opt(i);
					GuideInfo guideInfo = new GuideInfo();
					guideInfo.setCategoryName(obj.optString("categoryName"));
					guideInfo.setProductCategoryId(obj.optString("productCategoryId"));
					guideInfo.setFlag(false);
					categoryList.add(guideInfo);
					categoryAdapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("2")) {
			try {
				JSONArray array = new JSONObject(result).getJSONArray("brands");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.opt(i);
					GuideInfo guideInfo = new GuideInfo();
					guideInfo.setBrandName(obj.optString("brandName"));
					guideInfo.setBrandId(obj.optString("brandId"));
					guideInfo.setFlag(false);
					brandList.add(guideInfo);
					brandAdapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.updateUI(whichtask, result);
	}

	// 初始化控件方法
	private void initView() {
		btn_guide_menu = (Button) findViewById(R.id.btn_guide_menu);
		btn_guide_scan = (Button) findViewById(R.id.btn_guide_scan);
		et_guide_search = (EditText) findViewById(R.id.et_guide_search);
		tv_guide_sort = (TextView) findViewById(R.id.tv_guide_sort);
		tv_guide_brand = (TextView) findViewById(R.id.tv_guide_brand);
		tv_guide_amount = (TextView) findViewById(R.id.tv_guide_amount);
		tv_guide_rank = (TextView) findViewById(R.id.tv_guide_rank);
		gv_guide_product = (GridView) findViewById(R.id.gv_guide_product);
		lv_guide_sort = (ListView) findViewById(R.id.lv_guide_sort);
		lv_guide_brand = (ListView) findViewById(R.id.lv_guide_brand);
		ll_guide_brand = (LinearLayout) findViewById(R.id.ll_guide_brand);
		ll_guide_sort = (LinearLayout) findViewById(R.id.ll_guide_sort);
		tv_guide_brand_cancal = (TextView) findViewById(R.id.tv_guide_brand_cancal);
		tv_guide_brand_confirm = (TextView) findViewById(R.id.tv_guide_brand_confirm);
		tv_guide_sort_cancal = (TextView) findViewById(R.id.tv_guide_sort_cancal);
		tv_guide_sort_confirm = (TextView) findViewById(R.id.tv_guide_sort_confirm);
	}

	// 点击事件回调接口方法
	private void initListener() {
		btn_guide_menu.setOnClickListener(this);
		btn_guide_scan.setOnClickListener(this);
		tv_guide_sort.setOnClickListener(this);
		tv_guide_rank.setOnClickListener(this);
		tv_guide_brand.setOnClickListener(this);
		tv_guide_sort_cancal.setOnClickListener(this);
		tv_guide_sort_confirm.setOnClickListener(this);
		tv_guide_brand_cancal.setOnClickListener(this);
		tv_guide_brand_confirm.setOnClickListener(this);
	}

	@Override
	protected void onMenuItemClicked(int position, LeftMenuInfo item) {

	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	// 点击事件
	@SuppressWarnings("deprecation")
	@SuppressLint({ "ResourceAsColor", "InflateParams" })
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击展开侧滑栏
		case R.id.btn_guide_menu:
			mMenuDrawer.openMenu();
			break;
		// 点击跳转二维码
		case R.id.btn_guide_scan:
			intent = new Intent(GuideActivity.this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			break;
		// 点击展开分类
		case R.id.tv_guide_sort:
			tv_guide_sort.setTextColor(getResources().getColor(R.color.guide_sort_cancal));
			ll_guide_sort.setVisibility(View.VISIBLE);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("externalLoginKey",
					Localxml.search(GuideActivity.this, "externalloginkey")));
			getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "1");
			break;
		// 取消分类按钮
		case R.id.tv_guide_sort_cancal:
			ll_guide_sort.setVisibility(View.GONE);
			tv_guide_sort.setTextColor(Color.BLACK);
			break;
		// 确定分类按钮
		case R.id.tv_guide_sort_confirm:
			a = 2;
			page = 1;
			tv_guide_sort.setTextColor(Color.BLACK);
			ll_guide_sort.setVisibility(View.GONE);
			guideList.clear();
			List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
			nameValuePairs1.add(new BasicNameValuePair("externalLoginKey",
					Localxml.search(GuideActivity.this, "externalloginkey")));
			for (int i = 0; i < categroyItemList.size(); i++) {
				nameValuePairs1.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
			}
			getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs1, "0");
			break;
		// 点击展开品牌
		case R.id.tv_guide_brand:
			tv_guide_brand.setTextColor(getResources().getColor(R.color.guide_sort_cancal));
			ll_guide_brand.setVisibility(View.VISIBLE);
			List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>();
			nameValuePairs2.add(new BasicNameValuePair("externalLoginKey",
					Localxml.search(GuideActivity.this, "externalloginkey")));
			getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs2, "2");
			break;
		// 取消品牌按钮
		case R.id.tv_guide_brand_cancal:
			ll_guide_brand.setVisibility(View.GONE);
			tv_guide_brand.setTextColor(Color.BLACK);
			break;
		// 确认品牌按钮
		case R.id.tv_guide_brand_confirm:
			a = 3;
			page = 1;
			tv_guide_brand.setTextColor(Color.BLACK);
			ll_guide_brand.setVisibility(View.GONE);
			guideList.clear();
			List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>();
			nameValuePairs3.add(new BasicNameValuePair("externalLoginKey",
					Localxml.search(GuideActivity.this, "externalloginkey")));
			for (int i = 0; i < brandItemList.size(); i++) {
				nameValuePairs3.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
			}
			getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs3, "0");
			break;
		// 点击展开排序
		case R.id.tv_guide_rank:
			View rank_view = LayoutInflater.from(GuideActivity.this).inflate(R.layout.item_guide_rank, null);
			tv_price_ascend = (TextView) rank_view.findViewById(R.id.tv_price_ascend);
			tv_price_decline = (TextView) rank_view.findViewById(R.id.tv_price_decline);

			final PopupWindow rank_pop = new PopupWindow(rank_view, v.getWidth(), 180);
			// 需要设置一下此参数，点击外边可消失
			rank_pop.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			rank_pop.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			rank_pop.setFocusable(true);
			// rank_pop.setAnimationStyle(R.style.rank_anima);
			// 设置弹出位置
			// rank_pop.showAtLocation(v, Gravity.NO_GRAVITY, v.getWidth() +
			// 951, 155);
			rank_pop.showAsDropDown(v);
			// 按价格升序
			tv_price_ascend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					tv_guide_rank.setText("-" + "按价格升序" + "-");
					if (et_guide_search.getText().toString() != null
							&& !et_guide_search.getText().toString().equals("")) {
						searchOrderAscend();
						a = 4;
						page = 1;
					} else if (categroyItemList.size() != 0 && categroyItemList != null) {
						sortOrderAscend();
						a = 5;
						page = 1;
					} else if (brandItemList != null && brandItemList.size() != 0) {
						brandOrderAscend();
						a = 6;
						page = 1;
					} else {
						orderAscend();
						a = 7;
						page = 1;
					}
					if (rank_pop != null) {
						rank_pop.dismiss();
					}
				}
			});
			// 按价格降序
			tv_price_decline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					tv_guide_rank.setText("-" + "按价格降序" + "-");
					if (et_guide_search.getText().toString() != null
							&& !et_guide_search.getText().toString().equals("")) {
						searchOrderDecline();
						a = 8;
						page = 1;
					} else if (categroyItemList.size() != 0 && categroyItemList != null) {
						sortOrderDecline();
						a = 9;
						page = 1;
					} else if (brandItemList != null && brandItemList.size() != 0) {
						brandOrderDecline();
						a = 10;
						page = 1;
					} else {
						orderDecline();
						a = 11;
						page = 1;
					}
					if (rank_pop != null) {
						rank_pop.dismiss();
					}
				}
			});
			break;
		default:
			break;
		}
	}

	// 获取导购页面全部数据接口
	private void getGuideTotalProduct() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePair, "0");
	}

	// 获取搜索导购页面商品接口
	protected void searchGuideProduct() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面价格升序接口
	private void orderAscend() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面价格降序接口
	private void orderDecline() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面搜索价格升序接口
	private void searchOrderAscend() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面搜索价格降序接口
	private void searchOrderDecline() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		nameValuePairs.add(new BasicNameValuePair("keyWord", et_guide_search.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面分类价格升序接口
	private void sortOrderAscend() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		for (int i = 0; i < categroyItemList.size(); i++) {
			nameValuePairs.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
		}
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面分类价格降序接口
	private void sortOrderDecline() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		for (int i = 0; i < categroyItemList.size(); i++) {
			nameValuePairs.add(new BasicNameValuePair("cateId", categroyItemList.get(i)));
		}
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面品牌价格升序接口
	private void brandOrderAscend() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		for (int i = 0; i < brandItemList.size(); i++) {
			nameValuePairs.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
		}
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceUp"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	// 获取导购页面品牌价格降序接口
	private void brandOrderDecline() {
		guideList.clear();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(GuideActivity.this, "externalloginkey")));
		for (int i = 0; i < brandItemList.size(); i++) {
			nameValuePairs.add(new BasicNameValuePair("brandId", brandItemList.get(i)));
		}
		nameValuePairs.add(new BasicNameValuePair("orderBy", "priceDown"));
		getTask(GuideActivity.this, Urls.base + Urls.search_guide, nameValuePairs, "0");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 通过二维码扫描获取的结果
		if (requestCode == SCANNIN_GREQUEST_CODE && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			et_guide_search.setText(bundle.getString("result"));
		}
		// 换班退出
		else if (requestCode == 2) {
			if (resultCode == 112) {
				Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		}
	}

	// 禁止回退
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
