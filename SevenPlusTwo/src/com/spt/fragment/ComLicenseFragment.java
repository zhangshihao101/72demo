package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.page.BusinessLicenseActivity;
import com.spt.page.CredentialActivity;
import com.spt.page.LegalCardActicvity;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ComLicenseFragment extends Fragment {

	private Context mContext;
	private View view;
	private TextView tv_license_name, tv_license_yn1, tv_license_yn2, tv_license_yn3;
	private RelativeLayout rl_ft_license_card, rl_ft_license, rl_ft_license_credential;

	private String partyId, accessToken, ownerPartyId, licenseImgUrl, brandImgUrl, cardImgUrlOne, cardImgUrlTwo;
	private ProgressDialog dialog;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_license, null);

		initView();

		Bundle bundle = getArguments();
		partyId = bundle.getString("partyId");
		accessToken = bundle.getString("accessToken");
		ownerPartyId = bundle.getString("ownerPartyId");

		initData();

		initListener();

		return view;
	}

	private void initView() {
		tv_license_name = (TextView) view.findViewById(R.id.tv_license_name);
		tv_license_yn1 = (TextView) view.findViewById(R.id.tv_license_yn1);
		tv_license_yn2 = (TextView) view.findViewById(R.id.tv_license_yn2);
		tv_license_yn3 = (TextView) view.findViewById(R.id.tv_license_yn3);
		rl_ft_license_card = (RelativeLayout) view.findViewById(R.id.rl_ft_license_card);
		rl_ft_license = (RelativeLayout) view.findViewById(R.id.rl_ft_license);
		rl_ft_license_credential = (RelativeLayout) view.findViewById(R.id.rl_ft_license_credential);

		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
	}

	private void initData() {
		if (ownerPartyId != null) {
			dialog.show();
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
									dialog.dismiss();
									try {
										JSONObject object = new JSONObject(jsonStr);
										String roleTypeId = object.optString("roleTypeId");
										if (roleTypeId.equals("RETAILER")) {
											rl_ft_license_credential.setVisibility(View.GONE);
										} else {
											rl_ft_license_credential.setVisibility(View.VISIBLE);
										}
										tv_license_name.setText(object.optString("legalName"));
										JSONArray array = object.optJSONArray("cerImages");
										if (array != null && array.length() != 0) {
											rl_ft_license_card.setEnabled(true);
											tv_license_yn1.setText("已上传");
											tv_license_yn1.setTextColor(Color.parseColor("#000000"));
											cardImgUrlOne = array.getString(0);
											cardImgUrlTwo = array.getString(1);
										} else {
											rl_ft_license_card.setEnabled(false);
											tv_license_yn1.setText("未上传");
											tv_license_yn1.setTextColor(Color.parseColor("#d2d2d2"));
										}

										JSONArray array2 = object.optJSONArray("licenseImages");
										if (array2 != null && array2.length() != 0) {
											rl_ft_license.setEnabled(true);
											tv_license_yn2.setText("已上传");
											tv_license_yn2.setTextColor(Color.parseColor("#000000"));
											for (int i = 0; i < array.length(); i++) {
												licenseImgUrl = array.getString(i);
											}
										} else {
											rl_ft_license.setEnabled(false);
											tv_license_yn2.setText("未上传");
											tv_license_yn2.setTextColor(Color.parseColor("#d2d2d2"));
										}

										JSONArray array3 = object.optJSONArray("brandImages");
										if (array3 != null && array3.length() != 0) {
											rl_ft_license_credential.setEnabled(true);
											tv_license_yn3.setText("已上传");
											tv_license_yn3.setTextColor(Color.parseColor("#000000"));
											for (int i = 0; i < array.length(); i++) {
												brandImgUrl = array.getString(i);
											}
										} else {
											rl_ft_license_credential.setEnabled(false);
											tv_license_yn3.setText("未上传");
											tv_license_yn3.setTextColor(Color.parseColor("#d2d2d2"));
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
									dialog.dismiss();
									Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
		} else {
			dialog.show();
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
									dialog.dismiss();
									try {
										JSONObject object = new JSONObject(jsonStr);
										tv_license_name.setText(object.optString("legalName"));
										String roleTypeId = object.optString("roleTypeId");
										if (roleTypeId.equals("RETAILER")) {
											rl_ft_license_credential.setVisibility(View.GONE);
										} else {
											rl_ft_license_credential.setVisibility(View.VISIBLE);
										}
										JSONArray array = object.optJSONArray("cerImages");
										if (array != null && array.length() != 0) {
											rl_ft_license_card.setEnabled(true);
											tv_license_yn1.setText("已上传");
											tv_license_yn1.setTextColor(Color.parseColor("#000000"));
											cardImgUrlOne = array.getString(0);
											cardImgUrlTwo = array.getString(1);
										} else {
											rl_ft_license_card.setEnabled(false);
											tv_license_yn1.setText("未上传");
											tv_license_yn1.setTextColor(Color.parseColor("#d2d2d2"));
										}

										JSONArray array2 = object.optJSONArray("licenseImages");
										if (array2 != null && array2.length() != 0) {
											rl_ft_license.setEnabled(true);
											tv_license_yn2.setText("已上传");
											tv_license_yn2.setTextColor(Color.parseColor("#000000"));
											for (int i = 0; i < array.length(); i++) {
												licenseImgUrl = array.getString(i);
											}
										} else {
											rl_ft_license.setEnabled(false);
											tv_license_yn2.setText("未上传");
											tv_license_yn2.setTextColor(Color.parseColor("#d2d2d2"));
										}

										JSONArray array3 = object.optJSONArray("brandImages");
										if (array3 != null && array3.length() != 0) {
											rl_ft_license_credential.setEnabled(true);
											tv_license_yn3.setText("已上传");
											tv_license_yn3.setTextColor(Color.parseColor("#000000"));
											for (int i = 0; i < array.length(); i++) {
												brandImgUrl = array.getString(i);
											}
										} else {
											rl_ft_license_credential.setEnabled(false);
											tv_license_yn3.setText("未上传");
											tv_license_yn3.setTextColor(Color.parseColor("#d2d2d2"));
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
									dialog.dismiss();
									Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});

		}
	}

	private void initListener() {
		rl_ft_license_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LegalCardActicvity.class);
				intent.putExtra("cardImgUrlOne", cardImgUrlOne);
				intent.putExtra("cardImgUrlTwo", cardImgUrlTwo);
				startActivity(intent);
			}
		});

		rl_ft_license.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BusinessLicenseActivity.class);
				intent.putExtra("licenseImgUrl", licenseImgUrl);
				startActivity(intent);
			}
		});

		rl_ft_license_credential.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CredentialActivity.class);
				intent.putExtra("brandImgUrl", brandImgUrl);
				startActivity(intent);
			}
		});
	}
}
