package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsStkHighAdapter;
import com.spt.bean.MtsStkHighInfo;
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
import android.support.v4.app.Fragment;
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

public class MtsStkResultHighFragment extends Fragment {

	private View view;
	private Context mContext;
	private PullToRefreshView ptrv_mts_stk_high;
	private ListView lv_mts_stk_high;
	private List<MtsStkHighInfo> highList;
	private MtsStkHighAdapter highAdapter;
	private int page = 0;

	private String facilityName = "", productName = "", brandName = "", unicode = "", modelId = "", searchParam;

	private ProgressDialog dialog;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_mts_stk_high, null);
		mContext = getActivity();

		initView();

		searchParam = getArguments().getString("search");

		initListener();
		initData();

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

				highList.clear();
				initData3();
			} else {

			}

		}
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
										.add("orderBy", "-totalAvailableQuantity").add("facilityName", facilityName)
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
						System.out.println("=======" + "由高到低3" + "========" + jsonStr + "=============");
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("inventoryList");
									for (int i = 0; i < array.length(); i++) {
										MtsStkHighInfo highInfo = new MtsStkHighInfo();
										JSONObject obj = array.optJSONObject(i);
										JSONObject obj2 = obj.optJSONObject("product");
										highInfo.setProductId(obj2.optString("productId"));
										highInfo.setName(obj2.optString("productName"));
										highInfo.setBrand(obj2.optString("brandName"));
										highInfo.setImgUrl(obj2.optString("smallImageUrl"));
										highInfo.setNumber(obj2.optString("modelId"));
										highInfo.setCode(
												obj2.optString("dimensionDesc") + " " + obj2.optString("colorDesc"));
										JSONArray array2 = obj.optJSONArray("facility");
										for (int j = 0; j < array2.length(); j++) {
											JSONObject obj3 = array2.optJSONObject(j);
											String str1 = obj3.optString("totalAvailableQuantity");
											String str2 = obj3.optString("totalQuantity");
											highInfo.setSaleCount(str1.substring(0, str1.indexOf(".")));
											highInfo.setNowCount(str2.substring(0, str2.indexOf(".")));
											highInfo.setStorageName(obj3.optString("facilityName"));
										}
										highList.add(highInfo);
										highAdapter.notifyDataSetChanged();
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

	private void initData() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getInventoryBySearchParams)
				.post(new FormBody.Builder().add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
						.add("orderBy", "-totalAvailableQuantity").add("searchParam", searchParam)
						.add("viewIndex", page + "").build())
				.build()).enqueue(new Callback() {

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
										MtsStkHighInfo highInfo = new MtsStkHighInfo();
										JSONObject obj = array.optJSONObject(i);
										JSONObject obj2 = obj.optJSONObject("product");
										highInfo.setProductId(obj2.optString("productId"));
										highInfo.setName(obj2.optString("productName"));
										highInfo.setBrand(obj2.optString("brandName"));
										highInfo.setImgUrl(obj2.optString("smallImageUrl"));
										highInfo.setNumber(obj2.optString("modelId"));
										highInfo.setCode(
												obj2.optString("dimensionDesc") + " " + obj2.optString("colorDesc"));
										JSONArray array2 = obj.optJSONArray("facility");
										for (int j = 0; j < array2.length(); j++) {
											JSONObject obj3 = array2.optJSONObject(j);
											String str1 = obj3.optString("totalAvailableQuantity");
											String str2 = obj3.optString("totalQuantity");
											highInfo.setSaleCount(str1.substring(0, str1.indexOf(".")));
											highInfo.setNowCount(str2.substring(0, str2.indexOf(".")));
											highInfo.setStorageName(obj3.optString("facilityName"));
										}
										highList.add(highInfo);
										highAdapter.notifyDataSetChanged();
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
		ptrv_mts_stk_high = (PullToRefreshView) view.findViewById(R.id.ptrv_mts_stk_high);
		lv_mts_stk_high = (ListView) view.findViewById(R.id.lv_mts_stk_high);
		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		highList = new ArrayList<MtsStkHighInfo>();
		highAdapter = new MtsStkHighAdapter(mContext, highList);
		lv_mts_stk_high.setAdapter(highAdapter);

		ptrv_mts_stk_high.setLastUpdated(new Date().toLocaleString());
	}

	private void initListener() {
		ptrv_mts_stk_high.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_mts_stk_high.postDelayed(new Runnable() {

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
						ptrv_mts_stk_high.onFooterRefreshComplete();
					}
				}, 1000);

			}
		});

		ptrv_mts_stk_high.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_mts_stk_high.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (facilityName == null && productName == null && brandName == null && unicode == null
								&& modelId == null) {
							highList.clear();
							page = 0;
							initData();
						} else {
							highList.clear();
							page = 0;
							initData3();
						}
						ptrv_mts_stk_high.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});
	}

}
