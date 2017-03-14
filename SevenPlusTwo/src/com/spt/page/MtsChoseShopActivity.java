package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsShopAdapter;
import com.spt.bean.MtsShopInfo;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsChoseShopActivity extends FragmentActivity {

	private ImageView iv_mts_shop_back;
	private ListView lv_mts_shop;

	private ProgressDialog dialog;
	private String productId, name, imgUrl, partyId, userName;
	private List<MtsShopInfo> mList;
	private MtsShopAdapter mAdapter;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_chose_shop);
		super.onCreate(arg0);

		Intent intent = getIntent();

		productId = intent.getStringExtra("productId");
		name = intent.getStringExtra("name");
		imgUrl = intent.getStringExtra("imgUrl");
		partyId = intent.getStringExtra("partyId");

		initView();

		initData();

		initListener();

	}

	private void initListener() {
		iv_mts_shop_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv_mts_shop.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("============账号========" + userName);
				new ShareAction(MtsChoseShopActivity.this)
						.setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN,
								SHARE_MEDIA.WEIXIN_CIRCLE)
						.withText(name).withTitle(name).withMedia(new UMImage(MtsChoseShopActivity.this, imgUrl))
						.withTargetUrl(
								"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3c3e51ba57b4632e&redirect_uri=https%3a%2f%2fwww.metasolo.cn%2fnewHomepage%2fweChat%2fpindex.html%3fpartyId%3d"
										+ partyId + "%26userLoginId%3d" + userName + "%26productId%3d" + productId
										+ "%26productStoreId%3d" + mList.get(position).getId()
										+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect")
						.setCallback(umShareListener).open();
			}
		});
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(
						new Request.Builder().url(MtsUrls.base + MtsUrls.getProductSalingStores)
								.post(new FormBody.Builder()
										.add("externalLoginKey", Localxml.search(this, "externalloginkey"))
										.add("productId", productId).build())
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
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("stores");
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = array.optJSONObject(i);
										MtsShopInfo shopInfo = new MtsShopInfo();
										shopInfo.setName(obj.optString("storeName"));
										shopInfo.setId(obj.optString("productStoreId"));
										mList.add(shopInfo);
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
								Toast.makeText(MtsChoseShopActivity.this, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
	}

	private void initView() {
		iv_mts_shop_back = (ImageView) findViewById(R.id.iv_mts_shop_back);
		lv_mts_shop = (ListView) findViewById(R.id.lv_mts_shop);

		sp = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		userName = sp.getString("mtsUserName", "");

		mList = new ArrayList<MtsShopInfo>();
		mAdapter = new MtsShopAdapter(this, mList);
		lv_mts_shop.setAdapter(mAdapter);

		dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

	}

	/**
	 * 分享之后的回调
	 */
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			// Log.d("plat", "platform" + platform);
			// if (platform.name().equals("WEIXIN_FAVORITE")) {
			// Toast.makeText(mContext, platform + " 收藏成功啦",
			// Toast.LENGTH_SHORT).show();
			// } else {
			Toast.makeText(MtsChoseShopActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();
			// }
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(MtsChoseShopActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(MtsChoseShopActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
		}
	};

	// 给友盟分享重写
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** attention to this below ,must add this **/
		UMShareAPI.get(MtsChoseShopActivity.this).onActivityResult(requestCode, resultCode, data);
	};

}
