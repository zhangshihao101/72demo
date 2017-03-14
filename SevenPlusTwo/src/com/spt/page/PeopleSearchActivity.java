package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.ConSynSearchAdapter;
import com.spt.bean.ConSynInfo;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class PeopleSearchActivity extends FragmentActivity {

	private ImageView iv_people_search_back;
	private EditText et_people_search;
	private PullToRefreshView ptrv_people_search;
	private ListView lv_people_search;
	private ConSynSearchAdapter mAdapter;
	private List<ConSynInfo> mList;

	private String partyId, accessToken, userLoginId, queryValue;
	private String pageSize = "20";
	private int pageIndex = 0;

	private ProgressDialog dialog;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_people_search);
		super.onCreate(arg0);

		initView();

		if (getIntent().getStringExtra("scan") != null) {
			et_people_search.setText(getIntent().getStringExtra("scan"));
		}

		initListener();

	}

	private void initView() {
		iv_people_search_back = (ImageView) findViewById(R.id.iv_people_search_back);
		et_people_search = (EditText) findViewById(R.id.et_people_search);
		ptrv_people_search = (PullToRefreshView) findViewById(R.id.ptrv_people_search);
		lv_people_search = (ListView) findViewById(R.id.lv_people_search);
		mList = new ArrayList<ConSynInfo>();
		mAdapter = new ConSynSearchAdapter(this, mList);
		lv_people_search.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
		sp = PeopleSearchActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		partyId = sp.getString("partyId", "");
		accessToken = sp.getString("accessToken", "");
		userLoginId = sp.getString("username", "");
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_connections)
				.post(new FormBody.Builder().add("userLoginId", userLoginId).add("accessToken", accessToken)
						.add("partyId", partyId).add("pageIndex", pageIndex + "").add("pageSize", pageSize)
						.add("queryValue", queryValue).build())
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
									JSONArray array = object.optJSONArray("result");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.optJSONObject(i);
										ConSynInfo info = new ConSynInfo();
										info.setBrandNames(obj.optString("brandNames"));
										info.setCityName(obj.optString("cityName"));
										info.setCompanyBrief(obj.optString("companyBrief"));
										info.setCompanyName(obj.optString("companyName"));
										info.setConnectionName(obj.optString("connectionName").equals("null")
												? obj.optString("userLoginId") : obj.optString("connectionName"));
										info.setConnectionRole(obj.optString("connectionRole"));
										info.setIsFriend(obj.optString("isFriend"));
										info.setLogoPath(obj.optString("logoPath"));
										info.setLogoUrl(obj.optString("logoUrl"));
										info.setResultType(obj.optString("resultType"));
										info.setPartyId(obj.optString("partyId"));
										if (obj.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
											info.setRoleTypeId("供应商");
										} else if (obj.optString("roleTypeId").equals("RETAILER")) {
											info.setRoleTypeId("零售商");
										} else if (obj.optString("roleTypeId").equals("S_R_ALL")) {
											info.setRoleTypeId("供应商&零售商");
										}
										info.setUserLoginId(obj.optString("userLoginId"));
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
								Toast.makeText(PeopleSearchActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

	private void initListener() {
		iv_people_search_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		et_people_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (!et_people_search.getText().toString().equals("")) {
					queryValue = et_people_search.getText().toString();
					dialog.show();
					OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_connections)
							.post(new FormBody.Builder().add("userLoginId", userLoginId).add("accessToken", accessToken)
									.add("partyId", partyId).add("pageIndex", pageIndex + "").add("pageSize", pageSize)
									.add("queryValue", queryValue).build())
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
										String error = object.optString("_ERROR_MESSAGE_");
										System.out.println("错误信息==" + error);
										JSONArray array = object.optJSONArray("result");
										for (int i = 0; i < array.length(); i++) {
											JSONObject obj = array.optJSONObject(i);
											ConSynInfo info = new ConSynInfo();
											info.setBrandNames(obj.optString("brandNames"));
											info.setCityName(obj.optString("cityName"));
											info.setCompanyBrief(obj.optString("companyBrief"));
											info.setCompanyName(obj.optString("companyName"));
											info.setConnectionName(obj.optString("connectionName").equals("null")
													? obj.optString("userLoginId") : obj.optString("connectionName"));
											info.setConnectionRole(obj.optString("connectionRole"));
											info.setIsFriend(obj.optString("isFriend"));
											info.setLogoPath(obj.optString("logoPath"));
											info.setLogoUrl(obj.optString("logoUrl"));
											info.setResultType(obj.optString("resultType"));
											info.setPartyId(obj.optString("partyId"));
											if (obj.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
												info.setRoleTypeId("供应商");
											} else if (obj.optString("roleTypeId").equals("RETAILER")) {
												info.setRoleTypeId("零售商");
											} else if (obj.optString("roleTypeId").equals("S_R_ALL")) {
												info.setRoleTypeId("供应商&零售商");
											}
											info.setUserLoginId(obj.optString("userLoginId"));
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
									Toast.makeText(PeopleSearchActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
				} else {
					Toast.makeText(PeopleSearchActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		});

		ptrv_people_search.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_people_search.postDelayed(new Runnable() {

					@Override
					public void run() {
						pageIndex++;
						initData();
						ptrv_people_search.onFooterRefreshComplete();
					}
				}, 1000);

			}
		});

		ptrv_people_search.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_people_search.postDelayed(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						mList.clear();
						pageIndex = 0;
						initData();
						ptrv_people_search.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});

		lv_people_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					if (mList.get(position).getResultType().equals("group")) {
						Intent intent = new Intent(PeopleSearchActivity.this, CompanyDetailActivity.class);
						intent.putExtra("partyId", mList.get(position).getPartyId());
						startActivity(intent);
					} else if (mList.get(position).getResultType().equals("people")) {
						Intent intent = new Intent(PeopleSearchActivity.this, PersonalDataActivity.class);
						intent.putExtra("personId", mList.get(position).getUserLoginId());
						intent.putExtra("isF", mList.get(position).getIsFriend());
						startActivity(intent);
					}
				}
			}
		});

	}

}
