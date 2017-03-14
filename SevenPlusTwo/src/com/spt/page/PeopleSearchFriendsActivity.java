package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.PeopleSearchAdapter;
import com.spt.bean.SearchFriendsInfo;
import com.spt.controler.MipcaActivityCapture;
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
import android.view.Window;
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

public class PeopleSearchFriendsActivity extends FragmentActivity {

	private ImageView iv_mts_back, iv_scan_friends;
	private TextView tv_text;
	private EditText et_friends_search;
	private ListView lv_friends;
	private PeopleSearchAdapter adapter = null;
	private List<SearchFriendsInfo> mData;
	private SearchFriendsInfo info;
	private List<SearchFriendsInfo> mDtaNewFriends;
	private SearchFriendsInfo newFriendsInfo;
	private PeopleSearchAdapter adapterNewFriends = null;
	private SearchFriendsInfo myFriendsInfo;
	private PeopleSearchAdapter adapterMyFriends = null;
	private List<SearchFriendsInfo> mDtaMyFriends;
	private SearchFriendsInfo joinInfo;
	private PeopleSearchAdapter adapterJoin = null;
	private List<SearchFriendsInfo> mDtaJoin;

	private final static int SCANNIN_GREQUEST_CODE = 1;

	private static ProgressDialog dialog;
	private SharedPreferences sp;
	private String type = "";

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_people_newfriends);
		super.onCreate(arg0);

		Intent intent = getIntent();
		type = intent.getStringExtra("type");

		initViews();

		if (type.equals("yes")) {
			iv_scan_friends.setVisibility(View.VISIBLE);
			et_friends_search.setVisibility(View.VISIBLE);
			tv_text.setVisibility(View.GONE);
		} else if (type.equals("no")) {
			iv_scan_friends.setVisibility(View.GONE);
			et_friends_search.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
			tv_text.setText("我的公司成员");
			getCompanyMem();
			lv_friends.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						Intent i = new Intent(PeopleSearchFriendsActivity.this, PersonalDataActivity.class);
						i.putExtra("personId", mData.get(position).getUserLoginId());
						i.putExtra("isF", mData.get(position).getmAdd());
						startActivity(i);
					}
				}
			});
		} else if (type.equals("newfri")) {
			iv_scan_friends.setVisibility(View.GONE);
			et_friends_search.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
			tv_text.setText("新的朋友");
			getNewFriends();
			lv_friends.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						Intent i = new Intent(PeopleSearchFriendsActivity.this, PersonalDataActivity.class);
						i.putExtra("personId", mDtaNewFriends.get(position).getUserLoginId());
						i.putExtra("isF", "1");
						startActivity(i);
					}
				}
			});
		} else if (type.equals("myfri")) {
			iv_scan_friends.setVisibility(View.GONE);
			et_friends_search.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
			tv_text.setText("我的朋友");
			getMyFriends();
			lv_friends.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						Intent i = new Intent(PeopleSearchFriendsActivity.this, PersonalDataActivity.class);
						i.putExtra("personId", mDtaMyFriends.get(position).getUserLoginId());
						i.putExtra("isF", "Y");
						startActivity(i);
					}
				}
			});
		} else if (type.equals("join")) {
			iv_scan_friends.setVisibility(View.GONE);
			et_friends_search.setVisibility(View.GONE);
			tv_text.setVisibility(View.VISIBLE);
			getJoin();

		}

		iv_scan_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PeopleSearchFriendsActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		et_friends_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				// 进行好友搜索

				return true;
			}
		});

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getJoin() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_join)
						.post(new FormBody.Builder().add("accessToken", sp.getString("accessToken", ""))
								.add("partyId", sp.getString("partyId", "")).add("cooperationType", "received")
								.add("viewIndex", "0").add("viewSize", "10").add("isPage", "N").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "合作列表" + "========" + jsonStr + "=============");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject jsonobject = new JSONObject(jsonStr);

									String error = jsonobject.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = jsonobject.optJSONArray("list");
										if (array.length() == 0) {
											Toast.makeText(PeopleSearchFriendsActivity.this, "您还没有被邀请合作",
													Toast.LENGTH_LONG).show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);

												joinInfo = new SearchFriendsInfo();
												joinInfo.setmHeader(obj.getString("logoUrl"));
												joinInfo.setmName(obj.optString("groupName"));
												joinInfo.setmFlag(obj.optString("id"));
												joinInfo.setmAdd(obj.optString("state"));

												mDtaJoin.add(joinInfo);
											}
											adapterJoin.notifyDataSetChanged();
										}
									} else {
										Toast.makeText(PeopleSearchFriendsActivity.this, "数据获取失败", Toast.LENGTH_LONG)
												.show();
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
								Toast.makeText(PeopleSearchFriendsActivity.this, "网络错误，请检查网络", Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				});

	}

	private void getMyFriends() {
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_friends)
				.post(new FormBody.Builder().add("userLoginId", sp.getString("username", "")).add("needPage", "1")
						.add("accessToken", sp.getString("accessToken", "")).add("relationStatus", "agreed").build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "获取我的朋友" + "========" + jsonStr + "=============");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {

								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									String error = jsonobject.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = jsonobject.optJSONArray("firends");
										if (array.length() == 0) {
											Toast.makeText(PeopleSearchFriendsActivity.this, "您还没有朋友",
													Toast.LENGTH_LONG).show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);

												myFriendsInfo = new SearchFriendsInfo();
												myFriendsInfo.setmHeader(obj.optString("logoPath"));
												myFriendsInfo.setmName(obj.optString("connectionName").equals("null")
														? obj.optString("userLoginId")
														: obj.optString("connectionName"));
												myFriendsInfo.setUserLoginId(obj.optString("userLoginId"));
												myFriendsInfo.setmAdd(obj.optString("isFriend"));
												mDtaMyFriends.add(myFriendsInfo);
											}
											adapterMyFriends.notifyDataSetChanged();
										}

									} else if (error.equals("102")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "accessToken失效，请重新登录",
												Toast.LENGTH_LONG).show();
									} else if (error.equals("200")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "程序报错，请重新登录",
												Toast.LENGTH_LONG).show();
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
								Toast.makeText(PeopleSearchFriendsActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT)
										.show();
							}
						});

					}
				});

	}

	private void getNewFriends() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_friends)
						.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
								.add("accessToken", sp.getString("accessToken", ""))
								.add("relationStatus", "waiting_acceptance").add("needPage", "1").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "获取新的朋友" + "========" + jsonStr + "=============");
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									String error = jsonobject.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = jsonobject.optJSONArray("firends");
										if (array.length() == 0) {
											Toast.makeText(PeopleSearchFriendsActivity.this, "还没有新的好友",
													Toast.LENGTH_LONG).show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);

												newFriendsInfo = new SearchFriendsInfo();
												newFriendsInfo.setmHeader(obj.optString("logoPath"));
												newFriendsInfo.setmName(obj.optString("connectionName").equals("null")
														? obj.optString("userLoginId")
														: obj.optString("connectionName"));
												newFriendsInfo.setUserLoginId(obj.optString("userLoginId"));
												newFriendsInfo.setmAdd(obj.optString("isFriend"));
												mDtaNewFriends.add(newFriendsInfo);
											}
											adapterNewFriends.notifyDataSetChanged();
										}

									} else if (error.equals("102")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "accessToken失效，请重新登录",
												Toast.LENGTH_LONG).show();
									} else if (error.equals("200")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "程序报错，请重新登录",
												Toast.LENGTH_LONG).show();
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
								Toast.makeText(PeopleSearchFriendsActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT)
										.show();
							}
						});

					}
				});

	}

	private void getCompanyMem() {
		mData.clear();
		adapter.notifyDataSetChanged();
		dialog.show();
		OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_contacts)
				.post(new FormBody.Builder().add("partyId", sp.getString("partyId", ""))
						.add("userLoginId", sp.getString("username", "")).add("requestType", "1").add("needPage", "1")
						.add("accessToken", sp.getString("accessToken", "")).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======获取公司成员====" + jsonStr);
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									String error = jsonobject.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										JSONArray array = jsonobject.optJSONArray("contacts");
										if (array == null || array.length() == 0) {
											Toast.makeText(PeopleSearchFriendsActivity.this, "您的公司还没有成员",
													Toast.LENGTH_LONG).show();
										} else {
											for (int i = 0; i < array.length(); i++) {
												JSONObject obj = (JSONObject) array.get(i);

												info = new SearchFriendsInfo();
												info.setmHeader(obj.optString("logoPath"));
												info.setmName(obj.optString("connectionName").equals("null")
														? obj.optString("userLoginId")
														: obj.optString("connectionName"));
												info.setUserLoginId(obj.optString("userLoginId"));
												info.setmAdd(obj.optString("isFriend"));
												info.setState(obj.optString("subordinateStatus"));
												info.setFlag(false);
												info.setFlag1(false);
												info.setFlag2(false);
												// info.setmAdd(mAdd);
												mData.add(info);
											}
											adapter.notifyDataSetChanged();
										}
									} else if (error.equals("102")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "accessToken失效，请重新登录",
												Toast.LENGTH_LONG).show();
									} else if (error.equals("200")) {
										Toast.makeText(PeopleSearchFriendsActivity.this, "程序报错，请重新登录",
												Toast.LENGTH_LONG).show();
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
								Toast.makeText(PeopleSearchFriendsActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							}
						});

					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == -1) {
				Bundle bundle = data.getExtras();

				et_friends_search.setText(bundle.getString("result"));

			}
			break;
		default:
			break;
		}
	}

	private void initViews() {
		iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
		iv_scan_friends = (ImageView) findViewById(R.id.iv_scan_friends);
		tv_text = (TextView) findViewById(R.id.tv_text);
		et_friends_search = (EditText) findViewById(R.id.et_friends_search);
		lv_friends = (ListView) findViewById(R.id.lv_friends);

		mData = new ArrayList<SearchFriendsInfo>();
		adapter = new PeopleSearchAdapter(PeopleSearchFriendsActivity.this, mData, "company");
		mDtaNewFriends = new ArrayList<SearchFriendsInfo>();
		adapterNewFriends = new PeopleSearchAdapter(PeopleSearchFriendsActivity.this, mDtaNewFriends, "newFri");
		mDtaMyFriends = new ArrayList<SearchFriendsInfo>();
		adapterMyFriends = new PeopleSearchAdapter(PeopleSearchFriendsActivity.this, mDtaMyFriends, "myFri");
		mDtaJoin = new ArrayList<SearchFriendsInfo>();
		adapterJoin = new PeopleSearchAdapter(PeopleSearchFriendsActivity.this, mDtaJoin, "join");

		if (type.equals("no")) {
			lv_friends.setAdapter(adapter);
		} else if (type.equals("newfri")) {
			lv_friends.setAdapter(adapterNewFriends);
		} else if (type.equals("myfri")) {
			lv_friends.setAdapter(adapterMyFriends);
		} else if (type.equals("join")) {
			lv_friends.setAdapter(adapterJoin);
		}

		dialog = ProgressDialog.show(PeopleSearchFriendsActivity.this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
		sp = PeopleSearchFriendsActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
	}

}
