package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.ComBriefTagAdapter;
import com.spt.controler.FlowTagLayout;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ComBriefFragment extends Fragment {

	private Context mContext;
	private View view;
	private TextView tv_ft_brief_type, tv_ft_brief;
	private FlowTagLayout ftl_ft_brief_brand;
	private ComBriefTagAdapter mAdapter;
	private List<Object> tagList;
	private String partyId, accessToken, ownerPartyId;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_brief, null);

		initView();

		Bundle bundle = getArguments();
		partyId = bundle.getString("partyId");
		accessToken = bundle.getString("accessToken");
		ownerPartyId = bundle.getString("ownerPartyId");

		initData();

		return view;
	}

	private void initData() {
		if (ownerPartyId != null) {
			OkHttpManager.client
					.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getCompanyInformation)
							.post(new FormBody.Builder().add("accessToken", accessToken)
									.add("ownerPartyId", ownerPartyId).add("partyId", partyId).build())
							.build())
					.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									try {
										JSONObject object = new JSONObject(jsonStr);

										if (object.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
											tv_ft_brief_type.setText("供应商");
										} else if (object.optString("roleTypeId").equals("RETAILER")) {
											tv_ft_brief_type.setText("零售商");
										} else if (object.optString("roleTypeId").equals("S_R_ALL")) {
											tv_ft_brief_type.setText("供应商&零售商");
										}

										if (object.optString("brandNames") != null) {
											String[] strings = object.optString("brandNames").split(",");
											for (int i = 0; i < strings.length; i++) {
												if (!"".equals(strings[i])) {
													tagList.add(strings[i]);
												}
											}
											mAdapter.onlyAddAll(tagList);
										}
										if (object.optString("companyBrief") != null
												&& !object.optString("companyBrief").equals("")) {
											tv_ft_brief.setText(object.optString("companyBrief"));
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
		} else {
			OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getCompanyInformation)
					.post(new FormBody.Builder().add("accessToken", accessToken).add("partyId", partyId).build())
					.build()).enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response arg1) throws IOException {
							if (!arg1.isSuccessful()) {
								return;
							}
							final String jsonStr = arg1.body().string();
							new Handler(Looper.getMainLooper()).post(new Runnable() {

								@Override
								public void run() {
									try {
										JSONObject object = new JSONObject(jsonStr);

										if (object.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
											tv_ft_brief_type.setText("供应商");
										} else if (object.optString("roleTypeId").equals("RETAILER")) {
											tv_ft_brief_type.setText("零售商");
										} else if (object.optString("roleTypeId").equals("S_R_ALL")) {
											tv_ft_brief_type.setText("供应商&零售商");
										}

										if (object.optString("brandNames") != null) {
											String[] strings = object.optString("brandNames").split(",");
											for (int i = 0; i < strings.length; i++) {
												if (!"".equals(strings[i])) {
													tagList.add(strings[i]);
												}
											}
											mAdapter.onlyAddAll(tagList);
										}
										if (object.optString("companyBrief") != null
												&& !object.optString("companyBrief").equals("")) {
											tv_ft_brief.setText(object.optString("companyBrief"));
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});

		}

	}

	private void initView() {
		tv_ft_brief_type = (TextView) view.findViewById(R.id.tv_ft_brief_type);
		tv_ft_brief = (TextView) view.findViewById(R.id.tv_ft_brief);
		ftl_ft_brief_brand = (FlowTagLayout) view.findViewById(R.id.ftl_ft_brief_brand);
		mAdapter = new ComBriefTagAdapter(mContext);
		ftl_ft_brief_brand.setAdapter(mAdapter);
		tagList = new ArrayList<Object>();
	}

}
