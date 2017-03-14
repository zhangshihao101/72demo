package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.DataUtil;
import com.mts.pos.Common.FlowTagLayout;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.Fragment.AttributeFragment;
import com.mts.pos.Fragment.BitmapFragment;
import com.mts.pos.listview.Bean;
import com.mts.pos.listview.ProductInfo;
import com.mts.pos.listview.SkuAdapter;
import com.mts.pos.listview.SkuAdapter.OnItemClickListener;
import com.mts.pos.listview.SkuColorAdapter;
import com.mts.pos.listview.SkuColorAdapter.ItemClickListener;
import com.mts.pos.listview.SkuItme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

public class GoodsDetailActivity extends BaseActivity implements OnClickListener {

	private TextView tv_detail_name, tv_detail_dropPrice, tv_detail_total, tv_detail_count, tv_detail_join,
			tv_detail_buy;
	private ImageView img_goods_back, img_detail_reduce, img_detail_add;
	private RadioGroup rg_detail;
	private FlowTagLayout ftl_color, ftl_size;
	private SkuColorAdapter skuColorAdapter;// 颜色适配器
	private SkuAdapter skuSizeAdapter;// 尺码适配器
	private String color;
	private String size;
	private String imgUrl;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private ProductInfo productInfo;
	private SkuItme item;
	private List<SkuItme> mList;// 商品数据
	private List<Bean> mColorList;// 颜色列表
	private List<String> colorList, sizeList;
	private List<Bean> mSizeList;// 尺码列表
	private int stock = 0;// 库存
	private int count;
	public static double total_coast;// 商品总价格
	private double productPrice;
	public static String productId;
	private String total;
	private BitmapFragment bitmapFragment;
	private AttributeFragment attributeFragment;
	// private RecommendFragment recommendFragment;
	private final static int REQUEST_CODE = 1;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_goodsdetail);
		super.onCreate(inState);

		// 初始化控件
		initView();
		// 初始化监听
		initListener();
		initFragment();

		productInfo = new ProductInfo();

		mList = new ArrayList<SkuItme>();
		mColorList = new ArrayList<Bean>();
		mSizeList = new ArrayList<Bean>();

		Intent intent = getIntent();
		productId = intent.getStringExtra("productId");
		total = intent.getStringExtra("total");
		if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
			tv_detail_total.setText(total);
		}

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(GoodsDetailActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productId", productId));
		nameValuePair
				.add(new BasicNameValuePair("productStoreId", Localxml.search(GoodsDetailActivity.this, "storeid")));
		getTask(GoodsDetailActivity.this, Urls.base + Urls.detail_guide, nameValuePair, "0");

		count = Integer.valueOf(tv_detail_count.getText().toString());

		skuColorAdapter = new SkuColorAdapter(this);
		ftl_color.setAdapter(skuColorAdapter);
		skuColorAdapter.setItemClickListener(new ItemClickListener() {

			@Override
			public void ItemClick(Bean bean, int position) {
				color = bean.getName();
				if (bean.getStates().equals("0")) {
					// 清空尺码
					mSizeList = DataUtil.clearAdapterStates(mSizeList);
					skuSizeAdapter.notifyDataSetChanged();
					// 清空颜色
					mColorList = DataUtil.clearAdapterStates(mColorList);
					skuColorAdapter.notifyDataSetChanged();
					color = "";
					// 判断使用选中了尺码
					if (!TextUtils.isEmpty(size)) {
						// 选中尺码，计算库存
						stock = DataUtil.getSizeAllStock(mList, size);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						// 获取该尺码对应的颜色列表
						List<String> list = DataUtil.getColorListBySize(mList, size);
						if (list != null && list.size() > 0) {
							// 更新颜色列表
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
							skuColorAdapter.notifyDataSetChanged();
						}
						mSizeList = DataUtil.setAdapterStates(mSizeList, size);
						skuSizeAdapter.notifyDataSetChanged();
					} else {
						// 所有库存
						tv_detail_total.setText(total);
					}
				} else if (bean.getStates().equals("1")) {

					// 选中颜色
					mColorList = DataUtil.updateAdapterStates(mColorList, "0", position);
					skuColorAdapter.notifyDataSetChanged();
					// 计算该颜色对应的尺码列表
					List<String> list = DataUtil.getSizeListByColor(mList, color);
					if (!TextUtils.isEmpty(size)) {
						// 计算该颜色与尺码对应的库存
						stock = DataUtil.getStockByColorAndSize(mList, color, size);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
							skuSizeAdapter.notifyDataSetChanged();
						}
					} else {
						// 根据颜色计算库存
						stock = DataUtil.getSizeAllStock(mList, color);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, "");
							skuSizeAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});
		skuSizeAdapter = new SkuAdapter(this);
		ftl_size.setAdapter(skuSizeAdapter);
		skuSizeAdapter.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Bean bean, int position) {
				size = bean.getName();
				if (bean.getStates().equals("0")) {
					// 清空尺码
					mSizeList = DataUtil.clearAdapterStates(mSizeList);
					skuSizeAdapter.notifyDataSetChanged();
					// 清空颜色
					mColorList = DataUtil.clearAdapterStates(mColorList);
					skuColorAdapter.notifyDataSetChanged();
					size = "";
					if (!TextUtils.isEmpty(color)) {
						// 计算改颜色对应的所有库存
						stock = DataUtil.getColorAllStock(mList, color);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						// 计算改颜色对应的尺码列表
						List<String> list = DataUtil.getSizeListByColor(mList, color);
						if (list != null && list.size() > 0) {
							// 更新尺码列表
							mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
							skuSizeAdapter.notifyDataSetChanged();
						}
						mColorList = DataUtil.setAdapterStates(mColorList, color);
						skuColorAdapter.notifyDataSetChanged();
					} else {
						// 获取所有库存
						tv_detail_total.setText(total);
					}
				} else if (bean.getStates().equals("1")) {

					// 选中尺码
					mSizeList = DataUtil.updateAdapterStates(mSizeList, "0", position);
					skuSizeAdapter.notifyDataSetChanged();
					// 获取该尺码对应的颜色列表
					List<String> list = DataUtil.getColorListBySize(mList, size);
					if (!TextUtils.isEmpty(color)) {
						// 计算改颜色与尺码对应的库存
						stock = DataUtil.getStockByColorAndSize(mList, color, size);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						if (list != null && list.size() > 0) {
							// 更新颜色列表
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
							skuColorAdapter.notifyDataSetChanged();
						}
					} else {
						// 计算改尺码的所有库存
						stock = DataUtil.getSizeAllStock(mList, size);
						if (stock > 0) {
							tv_detail_total.setText(stock + "");
						} else {
							tv_detail_total.setText("0");
						}
						if (list != null && list.size() > 0) {
							mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, "");
							skuColorAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});

	}

	// 解析接口数据
	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			try {
				JSONArray array = new JSONObject(result).optJSONArray("productVariants");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = (JSONObject) array.opt(i);
					item = new SkuItme();
					item.setColorId(obj.optString("colorId"));
					item.setSizeId(obj.optString("dimensionId"));
					item.setProductId(obj.optString("productId"));
					tv_detail_name.setText(obj.optString("productName"));
					item.setSkuColor(obj.optString("colorDesc"));
					tv_detail_dropPrice.setText("￥" + obj.optString("listPrice") + ".00");
					productPrice = Double.valueOf(obj.optString("listPrice"));
					item.setSkuSize(obj.optString("dimensionDesc"));
					item.setSkuStock(obj.optInt("totalAvailableQuantity"));
					imgUrl = obj.optString("skuImageUrl");
					item.setImgUrl(imgUrl);
					mList.add(item);
				}
				colorList = new ArrayList<String>();
				for (SkuItme skuItme : mList) {
					String colorDesc = skuItme.getSkuColor();
					if (!colorList.contains(colorDesc)) {
						colorList.add(colorDesc);
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

				skuColorAdapter.onlyAddAll(mColorList);

				sizeList = new ArrayList<String>();
				for (SkuItme skuItme : mList) {
					String dimensionDesc = skuItme.getSkuSize();
					if (!sizeList.contains(dimensionDesc)) {
						sizeList.add(dimensionDesc);
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

				skuSizeAdapter.onlyAddAll(mSizeList);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.updateUI(whichtask, result);
	}

	private void initFragment() {
		manager = getSupportFragmentManager();
		rg_detail.check(R.id.rbt_detail_bitmap);
		showFragment(1);
	}

	private void showFragment(int index) {
		transaction = manager.beginTransaction();
		// 想要显示一个fragment先隐藏其它fragment，防止重叠
		hideFragment(transaction);
		switch (index) {
		case 1:
			// 如果fragment已经存在，则显示出来
			if (bitmapFragment != null) {
				transaction.show(bitmapFragment);
				// 否则第一次切换则添加fragment，注意添加后是会显示出来的，replace方法也是
			} else {
				bitmapFragment = new BitmapFragment();
				transaction.add(R.id.fragment, bitmapFragment);
			}
			break;
		case 2:
			if (attributeFragment != null) {
				transaction.show(attributeFragment);
			} else {
				attributeFragment = new AttributeFragment();
				rg_detail.check(R.id.rbt_detail_attribute);
				transaction.add(R.id.fragment, attributeFragment);
				Bundle bundle = new Bundle();
				bundle.putString("productId", productId);
				attributeFragment.setArguments(bundle);
			}
			break;
		// case 3:
		// if (recommendFragment != null) {
		// transaction.show(recommendFragment);
		// } else {
		// recommendFragment = new RecommendFragment();
		// rg_detail.check(R.id.rbt_detail_recommend);
		// transaction.add(R.id.fragment, recommendFragment);
		// }
		// break;
		default:
			break;
		}
		transaction.commit();
	}

	// 当fragment已被实例化就隐藏起来
	private void hideFragment(FragmentTransaction transaction) {
		if (bitmapFragment != null) {
			transaction.hide(bitmapFragment);
		}
		if (attributeFragment != null) {
			transaction.hide(attributeFragment);
		}
		// if (recommendFragment != null) {
		// transaction.hide(recommendFragment);
		// }
	}

	private void initListener() {
		tv_detail_count.setOnClickListener(this);
		tv_detail_join.setOnClickListener(this);
		tv_detail_buy.setOnClickListener(this);
		img_goods_back.setOnClickListener(this);
		img_detail_reduce.setOnClickListener(this);
		img_detail_add.setOnClickListener(this);
		// 切换商品详情三个属性的监听
		rg_detail.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbt_detail_bitmap:
					showFragment(1);
					break;
				case R.id.rbt_detail_attribute:
					showFragment(2);
					break;
				// case R.id.rbt_detail_recommend:
				// showFragment(3);
				// break;
				default:
					break;
				}
			}
		});

	}

	private void initView() {
		tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
		tv_detail_dropPrice = (TextView) findViewById(R.id.tv_detail_dropPrice);
		tv_detail_total = (TextView) findViewById(R.id.tv_detail_total);
		tv_detail_count = (TextView) findViewById(R.id.tv_detail_count);
		tv_detail_join = (TextView) findViewById(R.id.tv_detail_join);
		tv_detail_buy = (TextView) findViewById(R.id.tv_detail_buy);
		img_goods_back = (ImageView) findViewById(R.id.img_goods_back);
		img_detail_reduce = (ImageView) findViewById(R.id.img_detail_reduce);
		img_detail_add = (ImageView) findViewById(R.id.img_detail_add);
		rg_detail = (RadioGroup) findViewById(R.id.rg_detail);
		ftl_color = (FlowTagLayout) findViewById(R.id.ftl_color);
		ftl_size = (FlowTagLayout) findViewById(R.id.ftl_size);
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_detail_count:
			Intent intent = new Intent(this, ChangeDetailNumberActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("stock", stock);
			bundle.putString("color", color);
			bundle.putString("size", size);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE);
			break;
		case R.id.img_goods_back:
			GoodsDetailActivity.this.finish();
			break;
		case R.id.img_detail_reduce:
			if (count - 1 < 1) {
				Toast.makeText(this, "商品数量不能小于1", Toast.LENGTH_SHORT).show();
			} else {
				count--;
				tv_detail_count.setText(String.valueOf(count));
			}
			break;
		case R.id.img_detail_add:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码和颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (count + 1 > stock) {
				Toast.makeText(this, "商品数量不能大于库存", Toast.LENGTH_SHORT).show();
			} else {
				count++;
				tv_detail_count.setText(String.valueOf(count));
			}
			break;
		case R.id.tv_detail_join:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码和颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (stock == 0) {
				Toast.makeText(this, "库存数量不足", Toast.LENGTH_SHORT).show();
			} else if (count > stock) {
				Toast.makeText(this, "数量不能大于库存", Toast.LENGTH_SHORT).show();
			} else {
				stock = DataUtil.getStockByColorAndSize(mList, color, size);
				productInfo.setProduct_count(count);
				productInfo.setProduct_name2(tv_detail_name.getText().toString());
				productInfo.setProduct_name(tv_detail_name.getText().toString());
				productInfo.setProductcolor(color);
				productInfo.setProductsize(size);
				productInfo.setPresent_cost(productPrice);
				productInfo.setOriginal_cost(productPrice);
				productInfo.setProduct_salecount(stock);
				productInfo.setProduct_img(DataUtil.getImgUrlByColorAndSize(mList, color, size));
				productInfo.setProduct_id(DataUtil.getProductIdByColorAndSize(mList, color, size));
				// EventBus，可以点对点之间传值
				EventBus.getDefault().post(productInfo);
				Toast toast = Toast.makeText(this, "加入购物车成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			break;
		case R.id.tv_detail_buy:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码和颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if (stock == 0) {
				Toast.makeText(this, "库存数量不足", Toast.LENGTH_SHORT).show();
			} else if (count > stock) {
				Toast.makeText(this, "数量不能大于库存", Toast.LENGTH_SHORT).show();
			} else {
				total_coast = productPrice * count;
				Intent intent2 = new Intent(this, AccountingActivity.class);
				Bundle bundle2 = new Bundle();
				bundle2.putString("productName", tv_detail_name.getText().toString());
				bundle2.putDouble("totalCost", total_coast);
				bundle2.putString("comeFlag", "Goods");
				bundle2.putString("productId", DataUtil.getProductIdByColorAndSize(mList, color, size));
				bundle2.putInt("count", count);
				intent2.putExtras(bundle2);
				startActivity(intent2);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == ChangeDetailNumberActivity.RESULT_CODE) {
				Bundle bundle = data.getExtras();
				count = bundle.getInt("number");
				tv_detail_count.setText(String.valueOf(count));
			}
		}
	}

}
