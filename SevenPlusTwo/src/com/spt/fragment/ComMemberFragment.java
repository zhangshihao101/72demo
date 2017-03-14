package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.ComMemberAdapter;
import com.spt.bean.ComMemberInfo;
import com.spt.controler.App;
import com.spt.controler.PullToRefreshView;
import com.spt.controler.PullToRefreshView.OnFooterRefreshListener;
import com.spt.controler.PullToRefreshView.OnHeaderRefreshListener;
import com.spt.page.CompanyDetailActivity;
import com.spt.page.FindCompanyActivity;
import com.spt.page.FindPersonActivity;
import com.spt.sht.R;
import com.spt.utils.FastBlurUtil;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ComMemberFragment extends Fragment {

	private Context mContext;
	private View view;
	private PullToRefreshView ptrv_ft_member;
	private ListView lv_ft_member;
	private List<ComMemberInfo> mList;
	private ComMemberAdapter mAdapter;
	private String partyId, userLoginId, accessToken;
	private int pageIndex = 0;

	private SharedPreferences sp;
	private ProgressDialog dialog;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_member, null);

		initView();

		Bundle bundle = getArguments();
		partyId = bundle.getString("partyId");
		userLoginId = bundle.getString("userName");
		accessToken = bundle.getString("accessToken");

		initData();

		initListener();

		return view;
	}

	private void initView() {
		sp = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

		ptrv_ft_member = (PullToRefreshView) view.findViewById(R.id.ptrv_ft_member);
		lv_ft_member = (ListView) view.findViewById(R.id.lv_ft_member);
		mList = new ArrayList<ComMemberInfo>();
		mAdapter = new ComMemberAdapter(mContext, mList, sp.getString("username", ""), sp.getString("accessToken", ""));
		lv_ft_member.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_contacts)
						.post(new FormBody.Builder().add("accessToken", accessToken).add("userLoginId", userLoginId)
								.add("partyId", partyId).add("requestType", "0").add("needPage", "0")
								.add("pageIndex", pageIndex + "").add("pageSize", "20").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						if (!arg1.isSuccessful()) {
							return;
						}
						final String jsonStr = arg1.body().string();
						System.out.println("AAA===" + jsonStr);
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("contacts");
									if (array != null && array.length() != 0) {
										for (int i = 0; i < array.length(); i++) {
											JSONObject obj = array.optJSONObject(i);
											ComMemberInfo memberInfo = new ComMemberInfo();
											memberInfo.setConnectionName(obj.optString("connectionName"));
											memberInfo.setPosition(obj.optString("position"));
											memberInfo.setContactsTelephoneNumber(
													obj.optString("contactsTelephoneNumber"));
											memberInfo.setIsFirend(obj.optString("isFriend"));
											memberInfo.setUserLoginId(obj.optString("userLoginId"));
											mList.add(memberInfo);
											mAdapter.notifyDataSetChanged();
										}
									} else {
										Toast.makeText(mContext, "没有公司成员", Toast.LENGTH_SHORT).show();
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
								Toast.makeText(mContext, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

	private void initListener() {
		ptrv_ft_member.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				ptrv_ft_member.postDelayed(new Runnable() {

					@Override
					public void run() {
						pageIndex++;
						initData();
						ptrv_ft_member.onFooterRefreshComplete();
					}
				}, 1000);

			}
		});

		ptrv_ft_member.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				ptrv_ft_member.postDelayed(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						mList.clear();
						pageIndex = 0;
						initData();
						ptrv_ft_member.onHeaderRefreshComplete("更新于:" + new Date().toLocaleString());
					}
				}, 1000);
			}
		});

	}

}
