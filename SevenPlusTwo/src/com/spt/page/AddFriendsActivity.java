package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.FindPersonAdapter;
import com.spt.bean.SearchFriendsInfo;
import com.spt.controler.MipcaActivityCapture;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class AddFriendsActivity extends FragmentActivity {

	private ImageView iv_mts_back, iv_scan_friends;
	private EditText et_friends_search;
	private PullToRefreshView ptrv_person;
	private ListView lv_find_person;
	private FindPersonAdapter adapter;
	private SearchFriendsInfo info;
	private List<SearchFriendsInfo> personData;

	private ProgressDialog dialog;
	private SharedPreferences sp;

	private int viewIndex = 0;

	private final static int SCANNIN_GREQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_friends);
		super.onCreate(arg0);

		initViews();

		// findPerson(et_friends_search.getText().toString());

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				finish();
			}
		});

		iv_scan_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AddFriendsActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		et_friends_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				personData.clear();
				adapter.notifyDataSetChanged();
				viewIndex = 0;
				findPerson(et_friends_search.getText().toString());
				return true;
			}
		});

		// et_friends_search.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// personData.clear();
		// adapter.notifyDataSetChanged();
		// viewIndex = 0;
		// findPerson();
		// }
		// });

		lv_find_person.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(AddFriendsActivity.this, PersonalDataActivity.class);
				intent.putExtra("personId", personData.get(position).getUserLoginId());
				intent.putExtra("isF", personData.get(position).getmAdd());
				startActivity(intent);
			}
		});

		ptrv_person.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_person.postDelayed(new Runnable() {

					@Override
					public void run() {
						viewIndex++;
						findPerson(et_friends_search.getText().toString());
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
						personData.clear();
						viewIndex = 0;
						findPerson(et_friends_search.getText().toString());
						ptrv_person.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});

	}

	private void findPerson(String search) {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_users)
						.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
								.add("accessToken", sp.getString("accessToken", "")).add("queryValue", search)
								.add("pageIndex", viewIndex + "").add("pageSize", "10").build())
						.build())
				.enqueue(new Callback() {

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
										JSONArray array = object.optJSONArray("result");
										if (array == null || array.length() == 0) {
											Toast.makeText(AddFriendsActivity.this, "目前无用户", Toast.LENGTH_SHORT).show();
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
								Toast.makeText(AddFriendsActivity.this, "网络错误请检查网络", Toast.LENGTH_SHORT).show();
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
				personData.clear();
				adapter.notifyDataSetChanged();
				findPerson(bundle.getString("result"));
			}
			break;
		default:
			break;
		}
	}

	private void initViews() {
		iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
		iv_scan_friends = (ImageView) findViewById(R.id.iv_scan_friends);
		et_friends_search = (EditText) findViewById(R.id.et_friends_search);
		ptrv_person = (PullToRefreshView) findViewById(R.id.ptrv_person);
		lv_find_person = (ListView) findViewById(R.id.lv_find_person);
		personData = new ArrayList<SearchFriendsInfo>();
		sp = AddFriendsActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		adapter = new FindPersonAdapter(AddFriendsActivity.this, personData, sp.getString("username", ""),
				sp.getString("accessToken", ""));
		lv_find_person.setAdapter(adapter);
		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
	}

}
