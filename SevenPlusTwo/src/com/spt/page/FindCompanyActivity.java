package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.FindCompanyAdapter;
import com.spt.bean.CompanyInfo;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class FindCompanyActivity extends FragmentActivity {

	private TextView tv_company_all, tv_company_provider, tv_company_dealer;
	private ImageView iv_find_com_back, iv_search_filter, iv_company_bg;
	private EditText et_find_company;
	private ListView lv_find_company;
	private LinearLayout ll_company_filter;
	private List<CompanyInfo> mList;
	private FindCompanyAdapter mAdapter;
	private PullToRefreshView ptrv_company;

	private String partyId, accessToken;
	private String roleType = "";
	private String viewSize = "20";
	private int viewIndex = 0;

	private ProgressDialog dialog;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_company);
		super.onCreate(arg0);

		initView();

		initData();

		initLIstener();

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		tv_company_dealer = (TextView) findViewById(R.id.tv_company_dealer);
		tv_company_provider = (TextView) findViewById(R.id.tv_company_provider);
		tv_company_all = (TextView) findViewById(R.id.tv_company_all);
		iv_find_com_back = (ImageView) findViewById(R.id.iv_find_com_back);
		iv_search_filter = (ImageView) findViewById(R.id.iv_search_filter);
		iv_company_bg = (ImageView) findViewById(R.id.iv_company_bg);
		et_find_company = (EditText) findViewById(R.id.et_find_company);
		lv_find_company = (ListView) findViewById(R.id.lv_find_company);
		ptrv_company = (PullToRefreshView) findViewById(R.id.ptrv_company);
		ptrv_company.setLastUpdated(new Date().toLocaleString());
		ll_company_filter = (LinearLayout) findViewById(R.id.ll_company_filter);

		mList = new ArrayList<CompanyInfo>();
		mAdapter = new FindCompanyAdapter(this, mList);
		lv_find_company.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
		sp = FindCompanyActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		partyId = sp.getString("partyId", "");
		accessToken = sp.getString("accessToken", "");
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_company)
						.post(new FormBody.Builder().add("accessToken", accessToken).add("partyId", partyId)
								.add("viewIndex", viewIndex + "").add("viewSize", viewSize).add("roleType", roleType)
								.add("keyWords", et_find_company.getText().toString()).build())
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
									JSONArray array = object.optJSONArray("list");
									for (int i = 0; i < array.length(); i++) {
										CompanyInfo info = new CompanyInfo();
										JSONObject obj = array.optJSONObject(i);
										info.setCompanyName(obj.optString("companyName"));
										info.setLogoUrl(obj.optString("logoUrl"));
										info.setBrandNames(obj.optString("brandNames"));
										info.setCityName(obj.optString("cityName"));
										info.setCompanyBrief(obj.optString("companyBrief"));
										info.setPartyId(obj.optString("partyId"));
										if (obj.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
											info.setRoleTypeId("供应商");
										} else if (obj.optString("roleTypeId").equals("RETAILER")) {
											info.setRoleTypeId("零售商");
										} else if (obj.optString("roleTypeId").equals("S_R_ALL")) {
											info.setRoleTypeId("供应商&零售商");
										}
										mList.add(info);
										mAdapter.notifyDataSetChanged();
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
								Toast.makeText(FindCompanyActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

	private void initLIstener() {
		iv_find_com_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		ptrv_company.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_company.postDelayed(new Runnable() {

					@Override
					public void run() {
						viewIndex++;
						initData();
						ptrv_company.onFooterRefreshComplete();
					}
				}, 1000);

			}
		});

		ptrv_company.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_company.postDelayed(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						mList.clear();
						viewIndex = 0;
						initData();
						ptrv_company.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});

		et_find_company.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				mList.clear();
				mAdapter.notifyDataSetChanged();
				viewIndex = 0;
				initData();
				return true;
			}
		});

		iv_search_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_company_filter.setVisibility(View.VISIBLE);
			}
		});

		iv_company_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_company_filter.setVisibility(View.GONE);
			}
		});

		tv_company_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mList.clear();
				mAdapter.notifyDataSetChanged();
				roleType = "";
				ll_company_filter.setVisibility(View.GONE);
				getCompanyRoleType();
			}
		});

		tv_company_provider.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mList.clear();
				mAdapter.notifyDataSetChanged();
				roleType = "CERTIFICATE_SUPPLIER";
				ll_company_filter.setVisibility(View.GONE);
				getCompanyRoleType();
			}
		});

		tv_company_dealer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mList.clear();
				mAdapter.notifyDataSetChanged();
				roleType = "RETAILER";
				ll_company_filter.setVisibility(View.GONE);
				getCompanyRoleType();
			}
		});

		lv_find_company.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					Intent intent = new Intent(FindCompanyActivity.this, CompanyDetailActivity.class);
					intent.putExtra("partyId", mList.get(position).getPartyId());
					startActivity(intent);
				}
			}
		});

	}

	private void getCompanyRoleType() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_company)
				.post(new FormBody.Builder().add("accessToken", accessToken).add("partyId", partyId)
						.add("viewIndex", viewIndex + "").add("viewSize", viewSize).add("roleType", roleType).build())
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
									JSONArray array = object.optJSONArray("list");
									for (int i = 0; i < array.length(); i++) {
										CompanyInfo info = new CompanyInfo();
										JSONObject obj = array.optJSONObject(i);
										info.setCompanyName(obj.optString("companyName"));
										info.setLogoUrl(obj.optString("logoUrl"));
										info.setBrandNames(obj.optString("brandNames"));
										info.setCityName(obj.optString("cityName"));
										info.setCompanyBrief(obj.optString("companyBrief"));
										info.setPartyId(obj.optString("partyId"));
										if (obj.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
											info.setRoleTypeId("供应商");
										} else if (obj.optString("roleTypeId").equals("RETAILER")) {
											info.setRoleTypeId("零售商");
										} else if (obj.optString("roleTypeId").equals("S_R_ALL")) {
											info.setRoleTypeId("供应商&零售商");
										}
										mList.add(info);
										mAdapter.notifyDataSetChanged();
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
								dialog.dismiss();
								Toast.makeText(FindCompanyActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

}
