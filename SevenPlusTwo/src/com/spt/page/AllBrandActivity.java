package com.spt.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spt.adapter.AllGoodsAdapter;
import com.spt.bean.AllGoodsInfo;
import com.spt.sht.R;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 所有品牌
 * @author lihongxuan
 *
 */
public class AllBrandActivity extends BaseActivity {

	private ImageView iv_all_brand_back, iv_all_brand_order;
	private TextView tv_all_brand_name, tv_pop_all_order, tv_pop_stock_order, tv_pop_price_high, tv_pop_price_low;
	private ListView lv_all_brand;
	private List<AllGoodsInfo> allGoodsList;
	private AllGoodsAdapter allGoodsAdapter;

	private ProgressDialog progressdialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private String brandId;// 商品Id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_all_brand);
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	protected void init() {
		iv_all_brand_back = (ImageView) findViewById(R.id.iv_all_brand_back);
		iv_all_brand_order = (ImageView) findViewById(R.id.iv_all_brand_order);
		tv_all_brand_name = (TextView) findViewById(R.id.tv_all_brand_name);
		lv_all_brand = (ListView) findViewById(R.id.lv_all_brand);

		spHome = AllBrandActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");
	}

	private void initData() {

	}

	@Override
	protected void addClickEvent() {
		iv_all_brand_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AllBrandActivity.this.finish();
			}
		});

		// 排序
		iv_all_brand_order.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				View order_view = LayoutInflater.from(AllBrandActivity.this).inflate(R.layout.popwindow_order, null);
				tv_pop_all_order = (TextView) order_view.findViewById(R.id.tv_pop_all_order);
				tv_pop_stock_order = (TextView) order_view.findViewById(R.id.tv_pop_stock_order);
				tv_pop_price_high = (TextView) order_view.findViewById(R.id.tv_pop_price_high);
				tv_pop_price_low = (TextView) order_view.findViewById(R.id.tv_pop_price_low);
				PopupWindow order_pop = new PopupWindow(order_view, LayoutParams.MATCH_PARENT,
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

					}
				});

				/** 库存由高到低排序 */
				tv_pop_stock_order.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});

				/** 价格由高到低排序 */
				tv_pop_price_high.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});

				/** 价格由低到高 */
				tv_pop_price_low.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
			}
		});
	}

}
