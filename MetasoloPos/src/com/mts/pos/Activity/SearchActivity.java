package com.mts.pos.Activity;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.listview.SearchAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_search_back;
	private ListView lv_search_product;
	private SearchAdapter adapter;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		super.onCreate(inState);

		// 初始化控件
		initView();

		// 初始化数据
		initData();

		// 初始化点击事件监听
		initListener();

	}

	private void initListener() {
		iv_search_back.setOnClickListener(this);
		// et_search_search.setOnEditorActionListener(new
		// OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event) {
		// if (et_search_search.getText().toString().replace(" ",
		// "").equals("")) {
		// Toast.makeText(SearchActivity.this, "搜索内容不能为空",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// searchProduct();
		// }
		// return true;
		// }
		// });

		lv_search_product.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// PayActivity.searchData.get(position).setFlag(true);
				// adapter.notifyDataSetChanged();
				if (PayActivity.searchData.get(position).getProduct_salecount() == 0) {
					Toast.makeText(SearchActivity.this, "商品的库存为零，不能选择", Toast.LENGTH_SHORT).show();
				} else {
					PayActivity.productId = PayActivity.searchData.get(position).getProductid();
					PayActivity.productName = PayActivity.searchData.get(position).getProductname();
					PayActivity.productName2 = PayActivity.searchData.get(position).getProductname2();
					PayActivity.productUrl = PayActivity.searchData.get(position).getProduct_img();
					PayActivity.count = PayActivity.searchData.get(position).getProduct_count();
					PayActivity.salecount = PayActivity.searchData.get(position).getProduct_salecount();
					PayActivity.productColor = PayActivity.searchData.get(position).getProductcolor();
					PayActivity.productSize = PayActivity.searchData.get(position).getProductsize();
					PayActivity.productPrice = PayActivity.searchData.get(position).getProductPrice();
					backIntent(1);
				}

			}
		});

	}

	private void backIntent(int result) {
		Intent intent = new Intent();
		setResult(result, intent);
		finish();
	}

	private void initData() {
		adapter = new SearchAdapter(SearchActivity.this, PayActivity.searchData);
		lv_search_product.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void initView() {
		iv_search_back = (ImageView) findViewById(R.id.iv_search_back);
		lv_search_product = (ListView) findViewById(R.id.lv_search_product);
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_search_back:
			finish();
			break;
		default:
			break;
		}
	}

	// @Override
	// protected void updateUI(String whichtask, String result) {
	// if (whichtask.equals("0")) {
	// Log.e("6666666", result);
	// try {
	// JSONArray array = new JSONObject(result).optJSONArray("productsList");
	// if (array.length() == 0) {
	// Toast.makeText(SearchActivity.this, "没有搜索的商品",
	// Toast.LENGTH_SHORT).show();
	// } else {
	// for (int i = 0; i < array.length(); i++) {
	// JSONObject obj = array.optJSONObject(i);
	// SearchProductInfo info = new SearchProductInfo();
	// info.setProduct_img(obj.optString("originalImageUrl"));
	// info.setBrandName(obj.optString("brandName"));
	// info.setModeId(obj.optString("modelId"));
	// info.setProductname2(obj.optString("productName"));
	// info.setProductcolor(obj.optString("colorDesc"));
	// info.setProductsize(obj.optString("dimensionDesc"));
	// info.setProduct_salecount(obj.optInt("totalAvailableQuantity"));
	// searchData.add(info);
	// adapter.notifyDataSetChanged();
	// }
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// protected void searchProduct() {
	//
	// List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	// nameValuePair.add(new BasicNameValuePair("searchByProductStr",
	// et_search_search.getText().toString()));
	// nameValuePair.add(
	// new BasicNameValuePair("externalLoginKey",
	// Localxml.search(SearchActivity.this, "externalloginkey")));
	// getTask(SearchActivity.this, Urls.base + Urls.search_product,
	// nameValuePair, "0");
	// }

}
