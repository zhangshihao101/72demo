package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsGoodsAdapter;
import com.spt.bean.MtsGoodsListInfo;
import com.spt.common.BaseMtsFragment;
import com.spt.common.Constants;
import com.spt.common.MyPostTask;
import com.spt.common.NetworkUtil;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.page.MtsChoseShopActivity;
import com.spt.page.MtsGoodsDetailActivity;
import com.spt.page.MtsGoodsEditActivity;
import com.spt.page.MtsGoodsFilterActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.utils.Log;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsGoodsFragment extends BaseMtsFragment implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private View view;
	private Context mContext;

	private ImageView iv_back, iv_clear;
	private TextView tv_filter;
	private EditText et_search;
	private ListView lv_goods;

	private PullToRefreshView mPullToRefreshView;

	private ProgressDialog progressDialog;

	private MtsGoodsListInfo goodsInfo;
	private List<MtsGoodsListInfo> goodsData;
	private MtsGoodsAdapter adapter;

	private int showFlag = 0;// 搜索类型
	private int page = 0;// 页数

	private String storageId = "", goodsName = "", brandId = "", barcodeId = "", styleId = "", classifyId = "",
			stateId = "", titleId = "";

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getAllGoods("0");
				Toast.makeText(mContext, "商品删除成功", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 2) {
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getAllGoods("0");
				Toast.makeText(mContext, "商品下架成功", Toast.LENGTH_SHORT).show();
			}

		}
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContext = getActivity();
		view = LayoutInflater.from(mContext).inflate(R.layout.fragment_mts_goods, null);
		initView();
		getAllGoods("0");

		goodsData = new ArrayList<MtsGoodsListInfo>();
		adapter = new MtsGoodsAdapter(mContext, goodsData, handler);
		lv_goods.setAdapter(adapter);

		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				getActivity().finish();
			}
		});

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getSearchGoods("0");

				showFlag = 1;
				page = 0;

				return true;
			}
		});

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!"".equals(et_search.getText().toString())) {
					iv_clear.setVisibility(View.VISIBLE);
					showFlag = 1;
				} else {
					iv_clear.setVisibility(View.INVISIBLE);
					showFlag = 0;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		iv_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_search.setText("");
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getAllGoods("0");
			}
		});

		tv_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MtsGoodsFilterActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mContext, MtsGoodsDetailActivity.class);
				Bundle b = new Bundle();
				b.putString("productId", goodsData.get(position).getProductId());
				b.putDouble("productPrice", goodsData.get(position).getProductPrice());
				b.putString("imgUrl", goodsData.get(position).getProductPic());
				b.putString("name", goodsData.get(position).getProductName());
				b.putString("partyId", goodsData.get(position).getPartyId());
				intent.putExtras(b);
				startActivityForResult(intent, 1);
			}
		});

		adapter.setOnShareClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (int) v.getTag();
				Intent intent = new Intent(mContext, MtsChoseShopActivity.class);
				intent.putExtra("productId", goodsData.get(position).getProductId());
				intent.putExtra("imgUrl", goodsData.get(position).getProductPic());
				intent.putExtra("name", goodsData.get(position).getProductName());
				intent.putExtra("partyId", goodsData.get(position).getPartyId());
				startActivity(intent);
			}
		});

		adapter.setOnEditClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (int) v.getTag();
				Intent intent = new Intent(mContext, MtsGoodsEditActivity.class);

				intent.putExtra("productId", goodsData.get(position).getProductId());
				startActivityForResult(intent, 400);
			}
		});

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == 0) {

				storageId = data.getExtras().getString("storageId");
				goodsName = data.getExtras().getString("goodsName");
				brandId = data.getExtras().getString("brandId");
				barcodeId = data.getExtras().getString("barcodeId");
				styleId = data.getExtras().getString("styleId");
				classifyId = data.getExtras().getString("classifyId");
				stateId = data.getExtras().getString("stateId");
				titleId = data.getExtras().getString("titleId");

				showFlag = 2;

				goodsData.clear();
				adapter.notifyDataSetChanged();
				getFilterGoods("0");
				adapter.notifyDataSetChanged();

			} else {

			}
		} else if (requestCode == 1) {
			if (resultCode == 0) {
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getAllGoods("0");
				adapter.notifyDataSetChanged();
			} else if (resultCode == 1) {
				goodsData.clear();
				adapter.notifyDataSetChanged();
				getAllGoods("0");
				adapter.notifyDataSetChanged();
			}
		} else if (requestCode == 400) {
			goodsData.clear();
			adapter.notifyDataSetChanged();
			getAllGoods("0");
			adapter.notifyDataSetChanged();
		}
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_clear = (ImageView) view.findViewById(R.id.iv_clear);
		tv_filter = (TextView) view.findViewById(R.id.tv_filter);
		et_search = (EditText) view.findViewById(R.id.et_search);
		lv_goods = (ListView) view.findViewById(R.id.lv_goods);
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_refreshview);

		progressDialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		progressDialog.dismiss();

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page++;
				if (showFlag == 0) {
					getAllGoods(page + "");
				} else if (showFlag == 1) {
					getSearchGoods(page + "");
				} else if (showFlag == 2) {
					getFilterGoods(page + "");
				}

				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 800);

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				page = 0;
				if (showFlag == 0) {
					goodsData.clear();
					adapter.notifyDataSetChanged();
					getAllGoods("0");
				} else if (showFlag == 1) {
					goodsData.clear();
					adapter.notifyDataSetChanged();
					getSearchGoods("0");
				} else if (showFlag == 2) {
					goodsData.clear();
					adapter.notifyDataSetChanged();
					getFilterGoods("0");
				}

				mPullToRefreshView.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
			}
		}, 800);

	}

	private void getAllGoods(String ss) {
		progressDialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.search_product)
				.post(new FormBody.Builder().add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
						.add("productIsActive", "Y").add("viewIndex", ss).add("viewSize", "20").build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "搜索商品1" + "========" + jsonStr + "=============");
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();

								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("products");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = (JSONObject) array.get(i);
										goodsInfo = new MtsGoodsListInfo();
										goodsInfo.setProductId(obj.optString("productId"));
										goodsInfo.setProductName(obj.optString("productName"));
										goodsInfo.setProductPic(obj.optString("smallImageUrl"));
										goodsInfo.setProductPrice(obj.optDouble("productListPrice"));
										goodsInfo.setStyleId(obj.optString("modelId"));
										goodsInfo.setBrandId(obj.optString("brandEnName"));
										goodsInfo.setPartyId(obj.optString("ownerPartyId"));
										goodsData.add(goodsInfo);
									}

									adapter.notifyDataSetChanged();

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						progressDialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});

	}

	private void getSearchGoods(String ss) {
		progressDialog.show();
		OkHttpManager.client
				.newCall(
						new Request.Builder()
								.url(MtsUrls.base
										+ MtsUrls.search_product)
								.post(new FormBody.Builder()
										.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
										.add("productIsActive", "Y").add("viewIndex", ss).add("viewSize", "20")
										.add("searchKey", et_search.getText().toString()).build())
								.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "搜索商品2" + "========" + jsonStr + "=============");
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								progressDialog.dismiss();

								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("products");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = (JSONObject) array.get(i);
										goodsInfo = new MtsGoodsListInfo();
										goodsInfo.setProductId(obj.optString("productId"));
										goodsInfo.setProductName(obj.optString("productName"));
										goodsInfo.setProductPic(obj.optString("smallImageUrl"));
										goodsInfo.setProductPrice(obj.optDouble("productListPrice"));
										goodsInfo.setStyleId(obj.optString("modelId"));
										goodsInfo.setBrandId(obj.optString("brandEnName"));

										goodsData.add(goodsInfo);
									}
									adapter.notifyDataSetChanged();

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						progressDialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});

	}

	private void getFilterGoods(String ss) {
		progressDialog.show();
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey", Localxml.search(mContext, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("viewIndex", ss));
		nameValuePair.add(new BasicNameValuePair("viewSize", "20"));

		if (et_search.getText().toString() != null && !"".equals(et_search.getText().toString())) {
			nameValuePair.add(new BasicNameValuePair("searchKey", et_search.getText().toString()));
		} else {

		}

		if (goodsName != null && !"".equals(goodsName)) {
			nameValuePair.add(new BasicNameValuePair("productName", goodsName));
		} else {

		}

		if (brandId != null && !"".equals(brandId)) {
			nameValuePair.add(new BasicNameValuePair("brandName", brandId));
		} else {

		}

		if (barcodeId != null && !"".equals(barcodeId)) {
			nameValuePair.add(new BasicNameValuePair("barcode", barcodeId));
		} else {

		}

		if (styleId != null && !"".equals(styleId)) {
			nameValuePair.add(new BasicNameValuePair("modelId", styleId));
		} else {

		}

		if (classifyId != null && !"".equals(classifyId)) {
			nameValuePair.add(new BasicNameValuePair("privateCategoryId", classifyId));
		} else {

		}

		// if (stateId != null && !"active_all".equals(stateId) &&
		// !"".equals(stateId)) {
		// nameValuePair.add(new BasicNameValuePair("productIsActive",
		// stateId));
		// } else {
		//
		// }

		if (stateId.equals("N")) {
			nameValuePair.add(new BasicNameValuePair("productIsActive", "N"));
		} else {
			nameValuePair.add(new BasicNameValuePair("productIsActive", "Y"));
		}

		if (titleId != null && !"assoctypeid_all".equals(titleId) && !"".equals(titleId)) {
			nameValuePair.add(new BasicNameValuePair("productAssocTypeId", titleId));
		} else {

		}

		if (NetworkUtil.isConnected(mContext)) {
			PromoTask promotask = new PromoTask(mContext, MtsUrls.base + MtsUrls.search_product, nameValuePair, "2");
			promotask.execute("");
		} else {
			progressDialog.dismiss();
			Toast.makeText(mContext, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		progressDialog.dismiss();
		if (whichtask.equals("2")) {
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.optJSONArray("products");
				if (array.length() == 0) {
					Toast.makeText(mContext, "没有符合条件的商品！", Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = (JSONObject) array.get(i);
						goodsInfo = new MtsGoodsListInfo();
						goodsInfo.setProductId(obj.optString("productId"));
						goodsInfo.setProductName(obj.optString("productName"));
						goodsInfo.setProductPic(obj.optString("smallImageUrl"));
						goodsInfo.setProductPrice(obj.optDouble("productListPrice"));
						goodsInfo.setStyleId(obj.optString("modelId"));
						goodsInfo.setBrandId(obj.optString("brandEnName"));

						goodsData.add(goodsInfo);
					}
					adapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class PromoTask extends MyPostTask {
		String which;

		public PromoTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				Toast.makeText(mContext, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}
}
