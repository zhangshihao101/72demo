package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.EditPriceAdapter;
import com.spt.bean.EditPriceInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SetSalePriceActivity extends BaseActivity {

	private TextView tv_set_price_name;
	private ImageView iv_set_price_back;
	private Button btn_set_price_share;
	private ListView lv_edit_price;
	private String goodsName, goodsId, token, goodsImg, shareUrl;

	private List<String> editList;

	private List<EditPriceInfo> mList;
	private EditPriceAdapter mAdapter;

	private SharedPreferences spHome;
	private Editor editor;
	private static ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_set_sale_price);
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		goodsId = intent.getStringExtra("goodsId");
		goodsName = intent.getStringExtra("goodsName");
		goodsImg = intent.getStringExtra("goodsImg");
		tv_set_price_name.setText(goodsName);

		initData();
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder()
						.url(MyConstant.SERVICENAME + MyConstant.GETDISGOODS + "&goods_id=" + goodsId)
						.post(new FormBody.Builder().add("token", token).add("version", "2.1").build()).build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
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
									String error = object.optString("error");
									String msg = object.optString("msg");
									if (error.equals("0")) {
										JSONObject obj = object.optJSONObject("data");
										editor.putString("isEdit", obj.optString("ext_dis_settle"));
										editor.commit();
										JSONArray array = obj.optJSONArray("spec");
										for (int i = 0; i < array.length(); i++) {
											EditPriceInfo info = new EditPriceInfo();
											JSONObject obj1 = array.optJSONObject(i);
											info.setSpec_id(obj1.optString("spec_id"));
											info.setColor(obj1.optString("spec_1"));
											info.setSize(obj1.optString("spec_2"));
											info.setSalePrice(obj1.optString("seller_price"));
											info.setDisPrice(obj1.optDouble("dis_price"));
											mList.add(info);
											mAdapter.notifyDataSetChanged();
										}
									} else {
										Toast.makeText(SetSalePriceActivity.this, msg, Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

					}

					@Override
					public void onFailure(Call call, IOException arg1) {
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								btn_set_price_share.setEnabled(false);
								Toast.makeText(SetSalePriceActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

	@Override
	protected void init() {
		tv_set_price_name = (TextView) findViewById(R.id.tv_set_price_name);
		iv_set_price_back = (ImageView) findViewById(R.id.iv_set_price_back);
		btn_set_price_share = (Button) findViewById(R.id.btn_set_price_share);
		lv_edit_price = (ListView) findViewById(R.id.lv_edit_price);

		mList = new ArrayList<EditPriceInfo>();
		mAdapter = new EditPriceAdapter(this, mList);
		lv_edit_price.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		// 对EventBus进行注册
		EventBus.getDefault().register(this);
		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		editor = spHome.edit();
		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

	}

	public void onEvent(Map<Integer, String> map) {
		editList = new ArrayList<String>();
		if (map.size() == mList.size()) {
			for (int i = 0; i < mList.size(); i++) {
				editList.add(map.get(i));
			}
		}

	}

	@Override
	protected void addClickEvent() {
		iv_set_price_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_set_price_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}

				if (editList.size() != mList.size()) {
					Toast.makeText(SetSalePriceActivity.this, "销售价不可低于代销价", Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < mList.size(); i++) {
						btn_set_price_share.setEnabled(false);
						OkHttpManager.client
								.newCall(new Request.Builder().url(MyConstant.SERVICENAME + MyConstant.EDITDISPRICE)
										.post(new FormBody.Builder().add("token", token).add("version", "2.1")
												.add("goods_id", goodsId)
												.add("seller_price[" + mList.get(i).getSpec_id() + "]=",
														editList.get(i).toString())
												.build())
										.build())
								.enqueue(new Callback() {

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								if (!response.isSuccessful()) {
									return;
								}
								final String jsonStr = response.body().string();
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(jsonStr);
											String error = object.optString("error");
											String msg = object.optString("msg");
											if (error.equals("0")) {
												JSONObject obj = object.optJSONObject("data");
												shareUrl = obj.optString("url");
											} else {
												Toast.makeText(SetSalePriceActivity.this, msg, Toast.LENGTH_SHORT)
														.show();
											}
											btn_set_price_share.setEnabled(true);

										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});

							}

							@Override
							public void onFailure(Call call, IOException arg1) {
								new Handler(Looper.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(SetSalePriceActivity.this, "网络异常", Toast.LENGTH_LONG).show();
										btn_set_price_share.setEnabled(false);
									}
								});
							}
						});
					}
				}
				if (shareUrl != null) {
					new ShareAction(SetSalePriceActivity.this)
							.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
									SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
							.withText(goodsName).withTitle(goodsName)
							.withMedia(new UMImage(SetSalePriceActivity.this, MyConstant.BASEIMG + goodsImg))
							.withTargetUrl(shareUrl).setCallback(umShareListener).open();
				}
			}
		});

	}

	/**
	 * 分享之后的回调
	 */
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Toast.makeText(SetSalePriceActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(SetSalePriceActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
			if (t != null) {
				Log.d("throw", "throw:" + t.getMessage());
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(SetSalePriceActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};

	// 给友盟分享重写
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** attention to this below ,must add this **/
		UMShareAPI.get(SetSalePriceActivity.this).onActivityResult(requestCode, resultCode, data);
	};

}
