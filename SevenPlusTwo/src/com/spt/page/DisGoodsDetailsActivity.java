package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.ColorAndSizeAdapter;
import com.spt.adapter.ColorAndSizeAdapter.ItemClickListener;
import com.spt.bean.Bean;
import com.spt.bean.SkuItme;
import com.spt.controler.FlowTagLayout;
import com.spt.controler.PullUpLayout;
import com.spt.controler.PullUpLayout.OnPullListener;
import com.spt.controler.PullUpLayout.PullEdge;
import com.spt.controler.PullUpLayout.ShowMode;
import com.spt.sht.R;
import com.spt.utils.DataUtil;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 分销商品详情
 * 
 * @author lihongxuan
 *
 */
public class DisGoodsDetailsActivity extends BaseActivity implements OnClickListener {

	private TextView tv_goods_details_name, tv_goods_details_disPrice, tv_goods_details_marPrice,
			tv_goods_details_brand, tv_goods_details_stock, tv_goods_details_count, tv_goods_details_serve,
			tv_goods_details_specification, tv_pop_goods_details_spec, tv_pop_goods_details_disprice,
			tv_pop_goods_details_name, tv_pop_goods_details_stock, tv_pop_goods_details_count, tv_goods_details_predict,
			tv_goods_details_start, tv_goods_details_is_start, tv_goods_details_end;
	private ImageView iv_goods_details_back, iv_goods_details, iv_goods_details_share, iv_goods_details_reduce,
			iv_goods_details_plus, iv_pop_goods_details, iv_pop_goods_details_reduce, iv_pop_goods_details_plus,
			iv_pop_goods_bg;
	private Button btn_goods_details_join, btn_pop_goods_details_join;
	private RelativeLayout rl_goods_details_chose, rl_goods_details_pop;
	private WebView wv_goods_details;
	private PullUpLayout pul_goods_details;
	private String goodsId;
	private FlowTagLayout ftl_pop_color, ftl_pop_size;
	private ColorAndSizeAdapter mColorAdapter, mSizeAdapter;
	private String color, size, specId;// 尺码和颜色和Id
	private List<SkuItme> mItemList;// 商品数据
	private List<Bean> mColorList, mSizeList;// 颜色与尺码列表与选中状态
	private List<String> colorList, sizeList;// 颜色尺码集合
	private int stock = 0;// 库存
	private int count;// 数量
	private String minDisPrice, maxDisPrice;// 最大分销价和最小分销价
	private String imgUrl;// 图片地址
	private boolean isBottom;

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_goods_details);
		super.onCreate(savedInstanceState);

		goodsId = getIntent().getExtras().getString("goodsId");

		initData();

		count = Integer.valueOf(tv_pop_goods_details_count.getText().toString());

	}

	private void initData() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("数据正在加载中,请稍等...");
		dialog.show();
		mItemList = new ArrayList<SkuItme>();
		mColorList = new ArrayList<Bean>();
		mSizeList = new ArrayList<Bean>();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.GOODSDETAILS + goodsId, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				Log.e("QQQQQ", data);
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(data);
					String error = object.optString("error");
					if (error.equals("0")) {
						JSONObject obj = object.optJSONObject("data");
						tv_goods_details_name.setText(obj.optString("goods_name"));
						tv_pop_goods_details_name.setText(obj.optString("goods_name"));
						imgUrl = obj.optString("default_image");
						Picasso.with(DisGoodsDetailsActivity.this).load(MyConstant.BASEIMG + imgUrl)
								.error(R.drawable.test180180).into(iv_goods_details);
						Picasso.with(DisGoodsDetailsActivity.this).load(MyConstant.BASEIMG + imgUrl)
								.error(R.drawable.test180180).into(iv_pop_goods_details);
						if (obj.optString("ext_dis_settle").equals("1")) {
							tv_goods_details_predict.setText("预计收益会根据您的销售价来计算");
						} else {
							if (obj.optString("min_profit").equals(obj.optString("max_profit"))) {
								tv_goods_details_predict.setText("￥" + obj.optString("max_profit"));
							} else {
								tv_goods_details_predict.setText(
										"￥" + obj.optString("min_profit") + "-" + "￥" + obj.optString("max_profit"));
							}
						}
						minDisPrice = obj.optString("min_price");
						maxDisPrice = obj.optString("max_price");
						if (minDisPrice.equals(maxDisPrice)) {
							tv_goods_details_disPrice.setText("￥" + minDisPrice);
							tv_pop_goods_details_disprice.setText("￥" + minDisPrice);
						} else {
							tv_goods_details_disPrice.setText("￥" + minDisPrice + "-" + "￥" + maxDisPrice);
							tv_pop_goods_details_disprice.setText("￥" + minDisPrice + "-" + "￥" + maxDisPrice);
						}
						tv_goods_details_start.setText(MyUtil.millisecondsToDate(obj.optString("ext_dis_stime")));
						tv_goods_details_end.setText(MyUtil.millisecondsToDate(obj.optString("ext_dis_etime")));
						String type = obj.optString("dis_time_type");
						if (type.equals("1")) {
							tv_goods_details_is_start.setText("未开始");
							tv_goods_details_is_start.setTextColor(Color.parseColor("#8CB92A"));
						} else if (type.equals("2")) {
							tv_goods_details_is_start.setText("已开始");
							tv_goods_details_is_start.setTextColor(Color.parseColor("#FF6634"));
						}
						tv_goods_details_marPrice.setText(obj.optString("market_price"));
						tv_goods_details_marPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
						tv_goods_details_brand.setText(obj.optString("brand"));
						tv_goods_details_stock.setText(obj.optString("stock"));
						tv_goods_details_serve.setText(obj.optString("store_name"));

						JSONArray array = obj.optJSONArray("_specs");
						for (int i = 0; i < array.length(); i++) {
							JSONObject object2 = array.optJSONObject(i);
							SkuItme item = new SkuItme();
							item.setColorId(object2.optString("ext_spec_1_id"));
							item.setSizeId(object2.optString("spec_2_id"));
							item.setDisPrice(object2.optString("dis_price"));
							item.setSkuColor(object2.optString("spec_1"));
							item.setSkuSize(object2.optString("spec_2"));
							item.setSkuStock(object2.optInt("stock"));
							item.setProductId(object2.optString("spec_id"));
							mItemList.add(item);
						}
						colorList = new ArrayList<String>();
						for (SkuItme item : mItemList) {
							String color = item.getSkuColor();
							if (!colorList.contains(color)) {
								colorList.add(color);
							} else {
								continue;
							}
						}
						for (int i = 0; i < colorList.size(); i++) {
							Bean bean = new Bean();
							bean.setName(colorList.get(i));
							bean.setStates("1");
							mColorList.add(bean);
						}

						mColorAdapter.onlyAddAll(mColorList);

						sizeList = new ArrayList<String>();
						for (SkuItme item : mItemList) {
							String size = item.getSkuSize();
							if (!sizeList.contains(size)) {
								sizeList.add(size);
							} else {
								continue;
							}
						}
						for (int i = 0; i < sizeList.size(); i++) {
							Bean bean = new Bean();
							bean.setName(sizeList.get(i));
							bean.setStates("1");
							mSizeList.add(bean);
						}
						mSizeAdapter.onlyAddAll(mSizeList);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void OnError(VolleyError volleyError) {
				dialog.dismiss();
				Toast.makeText(DisGoodsDetailsActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

		// 颜色适配器
		mColorAdapter = new ColorAndSizeAdapter(DisGoodsDetailsActivity.this);
		ftl_pop_color.setAdapter(mColorAdapter);
		mColorAdapter.setItemClickListener(new ItemClickListener() {

			@Override
			public void ItemClick(Bean bean, int position) {
				color = bean.getName();
				if (bean.getStates().equals("0")) {
					// 清空尺码
					mSizeList = DataUtil.clearAdapterStates(mSizeList);
					mSizeAdapter.notifyDataSetChanged();
					// 清空颜色
					mColorList = DataUtil.clearAdapterStates(mColorList);
					mColorAdapter.notifyDataSetChanged();
					color = "";
					// 判断使用选中了尺码
					if (!TextUtils.isEmpty(size)) {
						// 选中尺码，计算库存
						stock = DataUtil.getSizeAllStock(mItemList, size);
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						tv_pop_goods_details_spec.setText("请选择颜色");
						tv_goods_details_specification.setText("请选择颜色");
						// 获取该尺码对应的颜色列表
						List<String> list = DataUtil.getColorListBySize(mItemList, size);
						if (list != null && list.size() > 0) {
							// 更新颜色列表
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
							mColorAdapter.notifyDataSetChanged();
						}
						mSizeList = DataUtil.setAdapterStates(mSizeList, size);
						mSizeAdapter.notifyDataSetChanged();
					} else {
						tv_pop_goods_details_stock.setText(tv_goods_details_stock.getText().toString());
						tv_pop_goods_details_spec.setText("请选择产品规格");
						tv_goods_details_specification.setText("请选择产品规格");
					}
				} else if (bean.getStates().equals("1")) {

					// 选中颜色
					mColorList = DataUtil.updateAdapterStates(mColorList, "0", position);
					mColorAdapter.notifyDataSetChanged();
					// 计算该颜色对应的尺码列表
					List<String> list = DataUtil.getSizeListByColor(mItemList, color);
					if (!TextUtils.isEmpty(size)) {
						// 计算该颜色与尺码对应的库存
						stock = DataUtil.getStockByColorAndSize(mItemList, color, size);
						// 获取单品ID
						specId = DataUtil.getProductIdByColorAndSize(mItemList, color, size);
						// 设置已选规格
						tv_pop_goods_details_spec.setText(color + "-" + size);
						tv_goods_details_specification.setText(color + "-" + size);
						tv_pop_goods_details_disprice
								.setText("￥" + DataUtil.getPriceByColorAndSize(mItemList, color, size));
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
							mSizeAdapter.notifyDataSetChanged();
						}
					} else {
						// 根据颜色计算库存
						stock = DataUtil.getSizeAllStock(mItemList, color);
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						tv_pop_goods_details_spec.setText("请选择尺码");
						tv_goods_details_specification.setText("请选择尺码");
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, "");
							mSizeAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});

		// 尺码适配器
		mSizeAdapter = new ColorAndSizeAdapter(DisGoodsDetailsActivity.this);
		ftl_pop_size.setAdapter(mSizeAdapter);
		mSizeAdapter.setItemClickListener(new ItemClickListener() {

			@Override
			public void ItemClick(Bean bean, int position) {
				size = bean.getName();
				if (bean.getStates().equals("0")) {
					// 清空尺码
					mSizeList = DataUtil.clearAdapterStates(mSizeList);
					mSizeAdapter.notifyDataSetChanged();
					// 清空颜色
					mColorList = DataUtil.clearAdapterStates(mColorList);
					mColorAdapter.notifyDataSetChanged();
					size = "";
					if (!TextUtils.isEmpty(color)) {
						// 计算该颜色对应的所有库存
						stock = DataUtil.getColorAllStock(mItemList, color);
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						tv_pop_goods_details_spec.setText("请选择尺码");
						tv_goods_details_specification.setText("请选择尺码");
						// 计算该颜色对应的尺码列表
						List<String> list = DataUtil.getSizeListByColor(mItemList, color);
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
							mSizeAdapter.notifyDataSetChanged();
						}
						mColorList = DataUtil.setAdapterStates(mColorList, color);
						mColorAdapter.notifyDataSetChanged();
					} else {
						tv_pop_goods_details_spec.setText("请选择产品规格");
						tv_goods_details_specification.setText("请选择产品规格");
						tv_pop_goods_details_stock.setText(tv_goods_details_stock.getText().toString());
					}
				} else if (bean.getStates().equals("1")) {

					// 选中尺码
					mSizeList = DataUtil.updateAdapterStates(mSizeList, "0", position);
					mSizeAdapter.notifyDataSetChanged();
					// 获取该尺码对应的颜色列表
					List<String> list = DataUtil.getColorListBySize(mItemList, size);
					if (!TextUtils.isEmpty(color)) {
						// 计算该颜色与尺码对应的库存
						stock = DataUtil.getStockByColorAndSize(mItemList, color, size);
						// 获取单品Id
						specId = DataUtil.getProductIdByColorAndSize(mItemList, color, size);
						// 设置已选规格
						tv_goods_details_specification.setText(color + "-" + size);
						tv_pop_goods_details_spec.setText(color + "-" + size);
						tv_pop_goods_details_disprice
								.setText("￥" + DataUtil.getPriceByColorAndSize(mItemList, color, size));
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						if (list != null && list.size() > 0) {
							// 更新颜色列表
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
							mColorAdapter.notifyDataSetChanged();
						}
					} else {
						// 计算该尺码的所有库存
						stock = DataUtil.getSizeAllStock(mItemList, size);
						tv_pop_goods_details_spec.setText("请选择颜色");
						tv_goods_details_specification.setText("请选择颜色");
						if (stock > 0) {
							tv_pop_goods_details_stock.setText(stock + "");
						} else {
							tv_pop_goods_details_stock.setText("0");
						}
						if (list != null && list.size() > 0) {
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, "");
							mColorAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});

	}

	@Override
	protected void init() {
		tv_goods_details_name = (TextView) findViewById(R.id.tv_goods_details_name);
		tv_goods_details_disPrice = (TextView) findViewById(R.id.tv_goods_details_disPrice);
		tv_goods_details_marPrice = (TextView) findViewById(R.id.tv_goods_details_marPrice);
		tv_goods_details_brand = (TextView) findViewById(R.id.tv_goods_details_brand);
		tv_goods_details_stock = (TextView) findViewById(R.id.tv_goods_details_stock);
		tv_goods_details_count = (TextView) findViewById(R.id.tv_goods_details_count);
		tv_goods_details_serve = (TextView) findViewById(R.id.tv_goods_details_serve);
		tv_goods_details_specification = (TextView) findViewById(R.id.tv_goods_details_specification);
		tv_goods_details_predict = (TextView) findViewById(R.id.tv_goods_details_predict);
		tv_goods_details_start = (TextView) findViewById(R.id.tv_goods_details_start);
		tv_goods_details_is_start = (TextView) findViewById(R.id.tv_goods_details_is_start);
		tv_goods_details_end = (TextView) findViewById(R.id.tv_goods_details_end);
		iv_goods_details_back = (ImageView) findViewById(R.id.iv_goods_details_back);
		iv_goods_details = (ImageView) findViewById(R.id.iv_goods_details);
		iv_goods_details_share = (ImageView) findViewById(R.id.iv_goods_details_share);
		iv_goods_details_reduce = (ImageView) findViewById(R.id.iv_goods_details_reduce);
		iv_goods_details_plus = (ImageView) findViewById(R.id.iv_goods_details_plus);
		iv_pop_goods_bg = (ImageView) findViewById(R.id.iv_pop_goods_bg);
		btn_goods_details_join = (Button) findViewById(R.id.btn_goods_details_join);
		rl_goods_details_chose = (RelativeLayout) findViewById(R.id.rl_goods_details_chose);
		rl_goods_details_pop = (RelativeLayout) findViewById(R.id.rl_goods_details_pop);
		tv_pop_goods_details_spec = (TextView) findViewById(R.id.tv_pop_goods_details_spec);
		tv_pop_goods_details_disprice = (TextView) findViewById(R.id.tv_pop_goods_details_disprice);
		tv_pop_goods_details_name = (TextView) findViewById(R.id.tv_pop_goods_details_name);
		tv_pop_goods_details_stock = (TextView) findViewById(R.id.tv_pop_goods_details_stock);
		tv_pop_goods_details_count = (TextView) findViewById(R.id.tv_pop_goods_details_count);
		iv_pop_goods_details = (ImageView) findViewById(R.id.iv_pop_goods_details);
		iv_pop_goods_details_reduce = (ImageView) findViewById(R.id.iv_pop_goods_details_reduce);
		iv_pop_goods_details_plus = (ImageView) findViewById(R.id.iv_pop_goods_details_plus);
		iv_pop_goods_bg = (ImageView) findViewById(R.id.iv_pop_goods_bg);
		ftl_pop_color = (FlowTagLayout) findViewById(R.id.ftl_pop_color);
		ftl_pop_size = (FlowTagLayout) findViewById(R.id.ftl_pop_size);
		btn_pop_goods_details_join = (Button) findViewById(R.id.btn_pop_goods_details_join);
		wv_goods_details = (WebView) findViewById(R.id.wv_goods_details);
		pul_goods_details = (PullUpLayout) findViewById(R.id.pul_goods_details);
		pul_goods_details.setShowMode(ShowMode.PullOut);
		pul_goods_details.setPullEdge(PullEdge.Bottom);
		pul_goods_details.setPullDistance(96);
		pul_goods_details.addOnPullListener(new OnPullListener() {

			@Override
			public void onUpdate(PullUpLayout layout, int offsetX, int offsetY) {

			}

			@Override
			public void onRelease(PullUpLayout layout, float offsetX, float offsetY) {

			}

			@Override
			public void onOpenStart(PullUpLayout layout) {

			}

			@Override
			public void onOpenFinish(PullUpLayout layout) {
				if (isBottom) {
					return;
				}
				isBottom = true;
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.DISGOODSDESC + goodsId, params, new OnCallBack() {

					@SuppressWarnings("deprecation")
					@SuppressLint("SetJavaScriptEnabled")
					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							wv_goods_details.loadDataWithBaseURL(null, object.optString("data"), "text/html", "utf-8",
									null);
							wv_goods_details.setHapticFeedbackEnabled(false);
							wv_goods_details.setInitialScale(0); // 改变这个值可以设定初始大小
							WebSettings settings = wv_goods_details.getSettings();
							settings.setDefaultTextEncodingName("UTF-8");
							settings.setJavaScriptEnabled(true);
							settings.setBuiltInZoomControls(true);
							settings.setLightTouchEnabled(true);
							settings.setSupportZoom(true);
							settings.setUseWideViewPort(true);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						Toast.makeText(DisGoodsDetailsActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onCloseStart(PullUpLayout layout) {

			}

			@Override
			public void onCloseFinish(PullUpLayout layout) {

			}
		});

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");

	}

	@Override
	protected void addClickEvent() {
		iv_goods_details_back.setOnClickListener(this);
		iv_goods_details_share.setOnClickListener(this);
		iv_goods_details_reduce.setOnClickListener(this);
		iv_goods_details_plus.setOnClickListener(this);
		iv_pop_goods_details_reduce.setOnClickListener(this);
		iv_pop_goods_details_plus.setOnClickListener(this);
		iv_pop_goods_bg.setOnClickListener(this);
		rl_goods_details_chose.setOnClickListener(this);
		btn_goods_details_join.setOnClickListener(this);
		btn_pop_goods_details_join.setOnClickListener(this);
		wv_goods_details.setHapticFeedbackEnabled(false);
		wv_goods_details.setInitialScale(0); // 改变这个值可以设定初始大小

		wv_goods_details.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("enlarge:")) {
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.iv_goods_details_back:
			DisGoodsDetailsActivity.this.finish();
			break;
		// 分享按钮
		case R.id.iv_goods_details_share:
			Intent intent = new Intent(DisGoodsDetailsActivity.this, SetSalePriceActivity.class);
			intent.putExtra("goodsId", goodsId);
			intent.putExtra("goodsName", tv_goods_details_name.getText().toString());
			intent.putExtra("goodsImg", imgUrl);
			startActivity(intent);
			break;
		// 减少商品数量按钮
		case R.id.iv_goods_details_reduce:
			if (!TextUtils.isEmpty(color) && !TextUtils.isEmpty(size)) {
				if (count - 1 < 1) {
					Toast.makeText(DisGoodsDetailsActivity.this, "商品数量不能小于1", Toast.LENGTH_SHORT).show();
				} else {
					count--;
					tv_goods_details_count.setText(String.valueOf(count));
					tv_pop_goods_details_count.setText(String.valueOf(count));
				}
			} else {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择商品规格", Toast.LENGTH_SHORT).show();
			}
			break;
		// 增加商品数量按钮
		case R.id.iv_goods_details_plus:
			if (!TextUtils.isEmpty(color) && !TextUtils.isEmpty(size)) {
				if (count + 1 > stock) {
					Toast.makeText(DisGoodsDetailsActivity.this, "商品数量不能大于库存", Toast.LENGTH_SHORT).show();
				} else {
					count++;
					tv_goods_details_count.setText(String.valueOf(count));
					tv_pop_goods_details_count.setText(String.valueOf(count));
				}
			} else {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择商品规格", Toast.LENGTH_SHORT).show();
			}
			break;
		// 弹出框增加商品按钮
		case R.id.iv_pop_goods_details_plus:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择尺码和颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (count + 1 > stock) {
				Toast.makeText(DisGoodsDetailsActivity.this, "商品数量不能大于库存", Toast.LENGTH_SHORT).show();
			} else {
				count++;
				tv_pop_goods_details_count.setText(String.valueOf(count));
				tv_goods_details_count.setText(String.valueOf(count));
			}
			break;
		// 弹出框减少商品按钮
		case R.id.iv_pop_goods_details_reduce:
			if (count - 1 < 1) {
				Toast.makeText(DisGoodsDetailsActivity.this, "商品数量不能小于1", Toast.LENGTH_SHORT).show();
			} else {
				count--;
				tv_pop_goods_details_count.setText(String.valueOf(count));
				tv_goods_details_count.setText(String.valueOf(count));
			}
			break;
		// 弹出框加入购物车
		case R.id.btn_pop_goods_details_join:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择产品规格", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (stock == 0) {
				Toast.makeText(DisGoodsDetailsActivity.this, "库存数量不足", Toast.LENGTH_SHORT).show();
			} else {
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.COMMITSHOPCART + specId + "&quantity=" + count,
						params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								Toast.makeText(DisGoodsDetailsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisGoodsDetailsActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
				rl_goods_details_pop.setVisibility(View.GONE);
			}
			break;
		// 显示选择商品规格
		case R.id.rl_goods_details_chose:
			rl_goods_details_pop.setVisibility(View.VISIBLE);
			break;
		// 加入购物车
		case R.id.btn_goods_details_join:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				rl_goods_details_pop.setVisibility(View.VISIBLE);
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择产品规格", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				rl_goods_details_pop.setVisibility(View.VISIBLE);
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				rl_goods_details_pop.setVisibility(View.VISIBLE);
				Toast.makeText(DisGoodsDetailsActivity.this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (stock == 0) {
				Toast.makeText(DisGoodsDetailsActivity.this, "库存数量不足", Toast.LENGTH_SHORT).show();
			} else {
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.COMMITSHOPCART + specId + "&quantity=" + count,
						params, new OnCallBack() {

							@Override
							public void OnSuccess(String data) {
								Toast.makeText(DisGoodsDetailsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void OnError(VolleyError volleyError) {
								Toast.makeText(DisGoodsDetailsActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
			}
			break;
		// 隐藏选择产品规格
		case R.id.iv_pop_goods_bg:
			rl_goods_details_pop.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

}
