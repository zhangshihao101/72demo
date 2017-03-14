package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.FindPersonAdapter;
import com.spt.bean.SearchFriendsInfo;
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
import android.widget.ArrayAdapter;
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

public class FindPersonActivity extends FragmentActivity {

	private ImageView iv_find_com_back, iv_search_filter, iv_person_bg;
	private EditText et_find_person;
	private ListView lv_find_person, lv_filter;
	private FindPersonAdapter adapter;
	private SearchFriendsInfo info;
	private List<SearchFriendsInfo> personData;
	private PullToRefreshView ptrv_person;
	private LinearLayout ll_filter;

	private ProgressDialog dialog;
	private SharedPreferences sp;

	private String roleValue = "";
	private String viewSize = "10";
	private int viewIndex = 0;
	private int viewIndexS = 0;
	private List<String> filterData;
	private HashMap<String, String> filterMap;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_person);
		super.onCreate(arg0);

		initViews();

		findPerson();

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

		iv_search_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_filter.setVisibility(View.VISIBLE);
			}
		});

		iv_person_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_filter.setVisibility(View.GONE);
			}
		});

		et_find_person.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				personData.clear();
				adapter.notifyDataSetChanged();
				viewIndex = 0;
				findPerson();

				return true;
			}
		});

		lv_filter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				personData.clear();
				adapter.notifyDataSetChanged();
				roleValue = filterMap.get(filterData.get(position));
				ll_filter.setVisibility(View.GONE);
				filterPerson(roleValue);
			}
		});

		lv_find_person.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					Intent intent = new Intent();
					intent.setClass(FindPersonActivity.this, PersonalDataActivity.class);
					intent.putExtra("personId", personData.get(position).getUserLoginId());
					intent.putExtra("isF", personData.get(position).getmAdd());
					startActivity(intent);
				}
			}
		});

		ptrv_person.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_person.postDelayed(new Runnable() {

					@Override
					public void run() {
						if ("".equals(roleValue)) {
							viewIndex++;
							findPerson();
						} else {
							viewIndexS++;
							filterPerson(roleValue);
						}
						ptrv_person.onFooterRefreshComplete();
					}
				}, 1000);

			}
		});

		ptrv_person.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_person.postDelayed(new Runnable() {

					@Override
					public void run() {
						if ("".equals(roleValue)) {
							personData.clear();
							adapter.notifyDataSetChanged();
							viewIndex = 0;
							findPerson();
						} else {
							personData.clear();
							adapter.notifyDataSetChanged();
							viewIndexS = 0;
							filterPerson(roleValue);
						}
						ptrv_person.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);

			}
		});
	}

	private void initViews() {
		iv_find_com_back = (ImageView) findViewById(R.id.iv_find_com_back);
		iv_search_filter = (ImageView) findViewById(R.id.iv_search_filter);
		iv_person_bg = (ImageView) findViewById(R.id.iv_person_bg);
		et_find_person = (EditText) findViewById(R.id.et_find_person);
		lv_find_person = (ListView) findViewById(R.id.lv_find_person);
		lv_filter = (ListView) findViewById(R.id.lv_filter);
		sp = FindPersonActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		personData = new ArrayList<SearchFriendsInfo>();
		adapter = new FindPersonAdapter(FindPersonActivity.this, personData, sp.getString("username", ""),
				sp.getString("accessToken", ""));
		ll_filter = (LinearLayout) findViewById(R.id.ll_filter);
		ptrv_person = (PullToRefreshView) findViewById(R.id.ptrv_person);
		lv_find_person.setAdapter(adapter);

		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		filterData = new ArrayList<String>();
		filterData.add("全部");
		filterData.add("领队");
		filterData.add("俱乐部");
		filterData.add("社团");
		filterData.add("实体店主");
		filterData.add("网店店主");
		filterData.add("其他");
		lv_filter.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_item, filterData));
		filterMap = new HashMap<String, String>();
		filterMap.put("全部", "");
		filterMap.put("领队", "Leader");
		filterMap.put("俱乐部", "Club");
		filterMap.put("社团", "MassOrganizations");
		filterMap.put("实体店主", "StoreOwner");
		filterMap.put("网店店主", "WebShopOwner");
		filterMap.put("其他", "Other");
	}

	private void findPerson() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.find_person)
				.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
						.add("accessToken", sp.getString("accessToken", ""))
						.add("queryValue", et_find_person.getText().toString()).add("needPage", "0")
						.add("pageIndex", viewIndex + "").add("pageSize", "20").add("roleValue", roleValue).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("====找人====" + jsonStr + "====");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(jsonStr);
									String error = object.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = object.optJSONArray("queryReslut");
										if (array.length() == 0) {
											Toast.makeText(FindPersonActivity.this, "没有更多用户了", Toast.LENGTH_SHORT)
													.show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);
												info = new SearchFriendsInfo();
												info.setmHeader(obj.optString("logoPath"));
												info.setmName(obj.optString("connectionName").equals("null")
														? obj.optString("userLoginId")
														: obj.optString("connectionName"));
												info.setmPositon(obj.optString("cityName"));
												info.setmFlag(obj.optString("connectionRole"));
												info.setmAdd(obj.optString("isFriend"));
												info.setPartyId(obj.optString("partyId"));
												info.setUserLoginId(obj.optString("userLoginId"));

												personData.add(info);
											}
											adapter.notifyDataSetChanged();
										}
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
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
								Toast.makeText(FindPersonActivity.this, "网络错误请检查网络", Toast.LENGTH_SHORT).show();

							}
						});

					}
				});

	}

	private void filterPerson(String condition) {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.find_person)
				.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
						.add("accessToken", sp.getString("accessToken", ""))
						.add("queryValue", et_find_person.getText().toString()).add("needPage", "0")
						.add("pageIndex", viewIndexS + "").add("pageSize", "15").add("roleValue", condition).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("====条件====" + jsonStr + "====");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(jsonStr);
									String error = object.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = object.optJSONArray("queryReslut");
										if (array.length() == 0) {
											Toast.makeText(FindPersonActivity.this, "没有更多此类型用户", Toast.LENGTH_SHORT)
													.show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);
												info = new SearchFriendsInfo();
												info.setmHeader(obj.optString("logoPath"));
												info.setmName(obj.optString("connectionName").equals("null")
														? obj.optString("userLoginId")
														: obj.optString("connectionName"));
												info.setmPositon(obj.optString("provinceName"));
												info.setmFlag(obj.optString("connectionRole"));
												info.setmAdd(obj.optString("isFriend"));
												info.setPartyId(obj.optString("partyId"));
												info.setUserLoginId(obj.optString("userLoginId"));

												personData.add(info);
											}
											adapter.notifyDataSetChanged();
										}
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
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
								Toast.makeText(FindPersonActivity.this, "网络错误请检查网络", Toast.LENGTH_SHORT).show();

							}
						});

					}
				});
	}

}
