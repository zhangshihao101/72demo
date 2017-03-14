package com.spt.page;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.controler.App;
import com.spt.fragment.ComBriefFragment;
import com.spt.fragment.ComLicenseFragment;
import com.spt.fragment.ComMemberFragment;
import com.spt.sht.R;
import com.spt.utils.FastBlurUtil;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class CompanyDetailActivity extends FragmentActivity {

	private TextView tv_com_detail_collaborate, tv_company_detail_name, tv_company_detail_city, tv_company_detail_apply;
	private ImageView iv_company_detail_back, iv_com_detail_more, iv_company_detail_top, iv_company_detail_logo;
	private RadioGroup rg_company_detail;
	private RadioButton rb_com_detail_brief, rb_com_detail_member, rb_com_detail_license;

	private ComBriefFragment briefFragment;
	private ComMemberFragment memberFragment;
	private ComLicenseFragment licenseFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private ProgressDialog dialog;
	private SharedPreferences sp;
	private String partyId, accessToken, ownerPartyId, userName;
	private String isSuccess = "";

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_company_detail);
		super.onCreate(arg0);

		initView();

		partyId = getIntent().getStringExtra("partyId");

		if (ownerPartyId != null) {
			tv_com_detail_collaborate.setEnabled(true);
			tv_com_detail_collaborate.setBackgroundResource(R.drawable.selector_com_detail_click_bg);
		} else {
			tv_com_detail_collaborate.setEnabled(false);
			tv_com_detail_collaborate.setBackgroundResource(R.drawable.shape_com_unclick);
		}

		initData();

		fragmentManager = getSupportFragmentManager();
		setTabSelection(0);

		initListener();

	}

	private void initData() {
		if (ownerPartyId != null && !ownerPartyId.equals("")) {
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

								@SuppressWarnings("unused")
								@Override
								public void run() {
									dialog.dismiss();
									try {
										JSONObject object = new JSONObject(jsonStr);
										tv_company_detail_name.setText(object.optString("companyName"));
										tv_company_detail_city.setText(object.optString("cityName"));
										String cooperationState = object.optString("cooperationState");
										if (cooperationState.equals("1")) {
											tv_com_detail_collaborate.setEnabled(false);
											tv_com_detail_collaborate.setText("已合作");
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.shape_com_unclick);
										} else if (cooperationState.equals("3")) {
											tv_com_detail_collaborate.setEnabled(false);
											tv_com_detail_collaborate.setText("待确认");
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.shape_com_unclick);
										} else if (cooperationState.equals("5")) {
											tv_com_detail_collaborate.setEnabled(false);
											tv_com_detail_collaborate.setText("已拒绝");
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.selector_com_detail_click_bg);
										} else if (cooperationState == null) {
											tv_com_detail_collaborate.setEnabled(true);
											tv_com_detail_collaborate.setText("我想找您合作");
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.selector_com_detail_click_bg);
										}
										final String url = object.optString("logoUrl");
										if (url != null && !url.equals("")) {
											Picasso.with(CompanyDetailActivity.this).load(url)
													.placeholder(R.drawable.noheader).error(R.drawable.noheader)
													.resize(100, 100).into(iv_company_detail_logo);
											new Thread(new Runnable() {
												@Override
												public void run() {

													// 下面的这个方法必须在子线程中执行
													final Bitmap blurBitmap2 = FastBlurUtil.GetUrlBitmap(url, 0);

													// 刷新ui必须在主线程中执行
													App.runOnUIThread(new Runnable() {
														@Override
														public void run() {
															iv_company_detail_top
																	.setScaleType(ImageView.ScaleType.CENTER_CROP);
															iv_company_detail_top.setImageBitmap(blurBitmap2);
														}
													});
												}
											}).start();
										} else {
											// 获取需要被模糊的原图bitmap
											Resources res = getResources();
											Bitmap scaledBitmap = BitmapFactory.decodeResource(res,
													R.drawable.me_head_bg);

											// scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
											Bitmap blurBitmap = FastBlurUtil.toBlur(scaledBitmap, 0);
											iv_company_detail_top.setScaleType(ImageView.ScaleType.CENTER_CROP);
											iv_company_detail_top.setImageBitmap(blurBitmap);
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
							Toast.makeText(CompanyDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
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
										tv_company_detail_name.setText(object.optString("companyName"));
										tv_company_detail_city.setText(object.optString("cityName"));
										final String url = object.optString("logoUrl");
										if (url != null && !url.equals("")) {
											Picasso.with(CompanyDetailActivity.this).load(url)
													.placeholder(R.drawable.noheader).error(R.drawable.noheader)
													.resize(100, 100).into(iv_company_detail_logo);
											new Thread(new Runnable() {
												@Override
												public void run() {

													// 下面的这个方法必须在子线程中执行
													final Bitmap blurBitmap2 = FastBlurUtil.GetUrlBitmap(url, 0);

													// 刷新ui必须在主线程中执行
													App.runOnUIThread(new Runnable() {
														@Override
														public void run() {
															iv_company_detail_top
																	.setScaleType(ImageView.ScaleType.CENTER_CROP);
															iv_company_detail_top.setImageBitmap(blurBitmap2);
														}
													});
												}
											}).start();
										} else {
											// 获取需要被模糊的原图bitmap
											Resources res = getResources();
											Bitmap scaledBitmap = BitmapFactory.decodeResource(res,
													R.drawable.me_head_bg);

											// scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
											Bitmap blurBitmap = FastBlurUtil.toBlur(scaledBitmap, 0);
											iv_company_detail_top.setScaleType(ImageView.ScaleType.CENTER_CROP);
											iv_company_detail_top.setImageBitmap(blurBitmap);
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
									Toast.makeText(CompanyDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
		}
	}

	private void initView() {
		tv_company_detail_apply = (TextView) findViewById(R.id.tv_company_detail_apply);
		tv_com_detail_collaborate = (TextView) findViewById(R.id.tv_com_detail_collaborate);
		tv_company_detail_name = (TextView) findViewById(R.id.tv_company_detail_name);
		tv_company_detail_city = (TextView) findViewById(R.id.tv_company_detail_city);
		iv_company_detail_back = (ImageView) findViewById(R.id.iv_company_detail_back);
		iv_com_detail_more = (ImageView) findViewById(R.id.iv_com_detail_more);
		iv_company_detail_top = (ImageView) findViewById(R.id.iv_company_detail_top);
		iv_company_detail_logo = (ImageView) findViewById(R.id.iv_company_detail_logo);
		rg_company_detail = (RadioGroup) findViewById(R.id.rg_company_detail);
		rb_com_detail_brief = (RadioButton) findViewById(R.id.rb_com_detail_brief);
		rb_com_detail_member = (RadioButton) findViewById(R.id.rb_com_detail_member);
		rb_com_detail_license = (RadioButton) findViewById(R.id.rb_com_detail_license);

		TextPaint tPaint = tv_company_detail_name.getPaint();
		tPaint.setFakeBoldText(true);
		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
		sp = CompanyDetailActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		// ownerPartyId = null;
		ownerPartyId = sp.getString("partyId", "");
		userName = sp.getString("username", "");

		if (ownerPartyId != null && !ownerPartyId.equals("")) {
			iv_com_detail_more.setVisibility(View.GONE);
		}
	}

	private void initListener() {
		iv_company_detail_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		iv_com_detail_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tv_company_detail_apply.getVisibility() == View.GONE) {
					tv_company_detail_apply.setVisibility(View.VISIBLE);
				} else if (tv_company_detail_apply.getVisibility() == View.VISIBLE) {
					tv_company_detail_apply.setVisibility(View.GONE);
				}
			}
		});

		tv_company_detail_apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSuccess.equals("Y")) {
					Toast.makeText(CompanyDetailActivity.this, "申请中，请耐心等待", Toast.LENGTH_SHORT).show();
				} else {
					dialog.show();
					OkHttpManager.client
							.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.applyForGroup)
									.post(new FormBody.Builder().add("userLoginId", userName)
											.add("accessToken", accessToken).add("position", "职员")
											.add("partyId", partyId).add("requestType", "ask").build())
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
										isSuccess = object.optString("isSuccess");
										if (isSuccess.equals("Y")) {
											Toast.makeText(CompanyDetailActivity.this, "操作成功", Toast.LENGTH_SHORT)
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
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									dialog.dismiss();
									Toast.makeText(CompanyDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
				}
				tv_company_detail_apply.setVisibility(View.GONE);
			}
		});

		tv_com_detail_collaborate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					dialog.show();
					OkHttpManager.client
							.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.applyCooperation)
									.post(new FormBody.Builder().add("accessToken", accessToken)
											.add("partyId", ownerPartyId).add("toPartyId", partyId).build())
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
										String flag = object.optString("flag");
										if (flag.equals("Y")) {
											Toast.makeText(CompanyDetailActivity.this, "操作成功", Toast.LENGTH_SHORT)
													.show();
											tv_com_detail_collaborate.setEnabled(false);
											tv_com_detail_collaborate.setText("等待对方同意");
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.shape_com_unclick);
										} else if (flag.equals("N")) {
											tv_com_detail_collaborate.setEnabled(true);
											tv_com_detail_collaborate
													.setBackgroundResource(R.drawable.selector_com_detail_click_bg);
											Toast.makeText(CompanyDetailActivity.this, "操作失败", Toast.LENGTH_SHORT)
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
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									dialog.dismiss();
									Toast.makeText(CompanyDetailActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
				}
			}
		});

		rg_company_detail.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rb_com_detail_brief.getId()) {
					setTabSelection(0);
					Drawable drawable1 = getResources().getDrawable(R.drawable.co_detail_title_hl);
					drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
					rb_com_detail_brief.setCompoundDrawables(null, null, null, drawable1);
					TextPaint tp = rb_com_detail_brief.getPaint();
					tp.setFakeBoldText(true);

					Drawable drawable2 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
					rb_com_detail_member.setCompoundDrawables(null, null, null, drawable2);
					TextPaint tp2 = rb_com_detail_member.getPaint();
					tp2.setFakeBoldText(false);

					Drawable drawable3 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
					rb_com_detail_license.setCompoundDrawables(null, null, null, drawable3);
					TextPaint tp3 = rb_com_detail_license.getPaint();
					tp3.setFakeBoldText(false);
				} else if (checkedId == rb_com_detail_member.getId()) {
					setTabSelection(1);
					Drawable drawable1 = getResources().getDrawable(R.drawable.co_detail_title_hl);
					drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
					rb_com_detail_member.setCompoundDrawables(null, null, null, drawable1);
					TextPaint tp = rb_com_detail_member.getPaint();
					tp.setFakeBoldText(true);

					Drawable drawable2 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
					rb_com_detail_brief.setCompoundDrawables(null, null, null, drawable2);
					TextPaint tp2 = rb_com_detail_brief.getPaint();
					tp2.setFakeBoldText(false);

					Drawable drawable3 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
					rb_com_detail_license.setCompoundDrawables(null, null, null, drawable3);
					TextPaint tp3 = rb_com_detail_license.getPaint();
					tp3.setFakeBoldText(false);
				} else if (checkedId == rb_com_detail_license.getId()) {
					setTabSelection(2);
					Drawable drawable1 = getResources().getDrawable(R.drawable.co_detail_title_hl);
					drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
					rb_com_detail_license.setCompoundDrawables(null, null, null, drawable1);
					TextPaint tp = rb_com_detail_license.getPaint();
					tp.setFakeBoldText(true);

					Drawable drawable2 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
					rb_com_detail_brief.setCompoundDrawables(null, null, null, drawable2);
					TextPaint tp2 = rb_com_detail_brief.getPaint();
					tp2.setFakeBoldText(false);

					Drawable drawable3 = getResources().getDrawable(R.drawable.co_detail_title_hl2);
					drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
					rb_com_detail_member.setCompoundDrawables(null, null, null, drawable3);
					TextPaint tp3 = rb_com_detail_member.getPaint();
					tp3.setFakeBoldText(false);
				}
			}
		});
	}

	/**
	 * 切换两个fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			if (briefFragment == null) {
				briefFragment = new ComBriefFragment();
				fragmentTransaction.add(R.id.fl_company_detail, briefFragment, "briefF");
				Bundle bundle = new Bundle();
				bundle.putString("partyId", partyId);
				bundle.putString("accessToken", accessToken);
				bundle.putString("ownerPartyId", ownerPartyId);
				briefFragment.setArguments(bundle);
			} else {
				fragmentTransaction.show(briefFragment);
			}
			break;

		case 1:
			if (memberFragment == null) {
				memberFragment = new ComMemberFragment();
				fragmentTransaction.add(R.id.fl_company_detail, memberFragment, "memberF");
				Bundle bundle = new Bundle();
				bundle.putString("partyId", partyId);
				bundle.putString("accessToken", accessToken);
				bundle.putString("userName", userName);
				memberFragment.setArguments(bundle);
			} else {
				fragmentTransaction.show(memberFragment);
			}
			break;

		case 2:
			if (licenseFragment == null) {
				licenseFragment = new ComLicenseFragment();
				fragmentTransaction.add(R.id.fl_company_detail, licenseFragment, "licenseF");
				Bundle bundle = new Bundle();
				bundle.putString("partyId", partyId);
				bundle.putString("accessToken", accessToken);
				bundle.putString("ownerPartyId", ownerPartyId);
				licenseFragment.setArguments(bundle);
			} else {
				fragmentTransaction.show(licenseFragment);
			}
			break;

		}
		fragmentTransaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (briefFragment != null) {
			transaction.hide(briefFragment);
		}
		if (memberFragment != null) {
			transaction.hide(memberFragment);
		}
		if (licenseFragment != null) {
			transaction.hide(licenseFragment);
		}
	}

}
