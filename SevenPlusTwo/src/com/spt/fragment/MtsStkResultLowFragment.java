package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsStkLowAdapter;
import com.spt.bean.MtsStkLowInfo;
import com.spt.common.BaseMtsFragment;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsStkResultLowFragment extends BaseMtsFragment {
	private View view;
	private Context mContext;
	private PullToRefreshView ptrv_mts_stk_low;
	private ListView lv_mts_stk_low;
	private List<MtsStkLowInfo> lowList;
	private MtsStkLowAdapter lowAdapter;
	private int page = 0;

	private String facilityName = "", productName = "", brandName = "", unicode = "", modelId = "", searchParam;

	private ProgressDialog dialog;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_mts_stk_low, null);
		mContext = getActivity();

		initView();
		
		searchParam = getArguments().getString("search");

		initData();

		initListener();

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == 0) {
				Bundle bundle = data.getExtras();
				facilityName = bundle.getString("facilityName");
				productName = bundle.getString("productName");
				brandName = bundle.getString("brandName");
				unicode = bundle.getString("unicode");
				modelId = bundle.getString("modelId");

				lowList.clear();
				initData3();
			} else {

			}

		}
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getInventoryBySearchParams)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
								.add("orderBy", "totalAvailableQuantity").add("searchParam", searchParam).add("viewIndex", page + "").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("inventoryList");
									for (int i = 0; i < array.length(); i++) {
										MtsStkLowInfo lowInfo = new MtsStkLowInfo();
										JSONObject obj = array.optJSONObject(i);
										JSONObject obj2 = obj.optJSONObject("product");
										lowInfo.setProductId(obj2.optString("productId"));
										lowInfo.setName(obj2.optString("productName"));
										lowInfo.setBrand(obj2.optString("brandName"));
										lowInfo.setImgUrl(obj2.optString("smallImageUrl"));
										lowInfo.setNumber(obj2.optString("modelId"));
										lowInfo.setCode(
												obj2.optString("dimensionDesc") + " " + obj2.optString("colorDesc"));
										JSONArray array2 = obj.optJSONArray("facility");
										for (int j = 0; j < array2.length(); j++) {
											JSONObject obj3 = array2.optJSONObject(j);
											String str1 = obj3.optString("totalAvailableQuantity");
											String str2 = obj3.optString("totalQuantity");
											lowInfo.setSaleCount(str1.substring(0, str1.indexOf(".")));
											lowInfo.setNowCount(str2.substring(0, str2.indexOf(".")));
											lowInfo.setStorageName(obj3.optString("facilityName"));
										}
										lowList.add(lowInfo);
										lowAdapter.notifyDataSetChanged();
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
								Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});

	}

	private void initData3() {
		dialog.show();
		OkHttpManager.client
				.newCall(
						new Request.Builder()
								.url(MtsUrls.base
										+ MtsUrls.getInventoryBySearchParams)
								.post(new FormBody.Builder()
										.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
										.add("orderBy", "totalAvailableQuantity").add("facilityName", facilityName)
										.add("productName", productName).add("brandName", brandName)
										.add("unicode", unicode).add("modelId", modelId).add("viewIndex", page + "")
										.build())
								.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("inventoryList");
									for (int i = 0; i < array.length(); i++) {
										MtsStkLowInfo lowInfo = new MtsStkLowInfo();
										JSONObject obj = array.optJSONObject(i);
										JSONObject obj2 = obj.optJSONObject("product");
										lowInfo.setProductId(obj2.optString("productId"));
										lowInfo.setName(obj2.optString("productName"));
										lowInfo.setBrand(obj2.optString("brandName"));
										lowInfo.setImgUrl(obj2.optString("smallImageUrl"));
										lowInfo.setNumber(obj2.optString("modelId"));
										lowInfo.setCode(
												obj2.optString("dimensionDesc") + " " + obj2.optString("colorDesc"));
										JSONArray array2 = obj.optJSONArray("facility");
										for (int j = 0; j < array2.length(); j++) {
											JSONObject obj3 = array2.optJSONObject(j);
											String str1 = obj3.optString("totalAvailableQuantity");
											String str2 = obj3.optString("totalQuantity");
											lowInfo.setSaleCount(str1.substring(0, str1.indexOf(".")));
											lowInfo.setNowCount(str2.substring(0, str2.indexOf(".")));
											lowInfo.setStorageName(obj3.optString("facilityName"));
										}
										lowList.add(lowInfo);
										lowAdapter.notifyDataSetChanged();
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
								Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

	private void initView() {
		ptrv_mts_stk_low = (PullToRefreshView) view.findViewById(R.id.ptrv_mts_stk_low);
		lv_mts_stk_low = (ListView) view.findViewById(R.id.lv_mts_stk_low);
		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		lowList = new ArrayList<MtsStkLowInfo>();

		lowAdapter = new MtsStkLowAdapter(mContext, lowList);
		lv_mts_stk_low.setAdapter(lowAdapter);

		ptrv_mts_stk_low.setLastUpdated(new Date().toLocaleString());
	}

	private void initListener() {
		ptrv_mts_stk_low.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_mts_stk_low.postDelayed(new Runnable() {

					@Override
					public void run() {

						if (facilityName == null && productName == null && brandName == null && unicode == null
								&& modelId == null) {
							page++;
							initData();
						} else {
							page++;
							initData3();
						}
						ptrv_mts_stk_low.onFooterRefreshComplete();
					}
				}, 1000);
			}
		});

		ptrv_mts_stk_low.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_mts_stk_low.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (facilityName == null && productName == null && brandName == null && unicode == null
								&& modelId == null) {
							lowList.clear();
							page = 0;
							initData();
						} else {
							lowList.clear();
							page = 0;
							initData3();
						}
						ptrv_mts_stk_low.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});
	}
}
