package com.mts.pos.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Activity.GoodsDetailActivity;
import com.mts.pos.Common.BaseFragment;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.DetailImageAdapter;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

@SuppressLint("HandlerLeak")
public class BitmapFragment extends BaseFragment {

	private View view;
	private ViewPager vp_bitmap;
	private LinearLayout ll_Point;
	private List<ImageView> imgList;
	private List<String> imgUrl;
	private ImageView image, iv_bitmap;
	// 前一个位置的标记
	private int prePosition;
	private Context mContext;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_bitmap, null);
		initView();
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey", Localxml.search(mContext, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productId", GoodsDetailActivity.productId));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(mContext, "storeid")));
		getTask(mContext, Urls.base + Urls.detail_guide, nameValuePair, "0");
		return view;
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			try {
				imgUrl = new ArrayList<String>();
				JSONObject jsonObject = new JSONObject(result).optJSONObject("product");
				imgUrl.add(jsonObject.optString("smallImageUrl"));
				JSONArray array = new JSONObject(result).optJSONArray("productExtendImages");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.optJSONObject(i);
					imgUrl.add(obj.optString("ossImageKey"));
				}
				JSONArray array2 = new JSONObject(result).optJSONArray("variantExtendImages");
				for (int i = 0; i < array2.length(); i++) {
					JSONObject object = array2.optJSONObject(i);
					imgUrl.add(object.optString("ossImageKey"));
				}
				initData();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.updateUI(whichtask, result);
	}

	private void initView() {
		vp_bitmap = (ViewPager) view.findViewById(R.id.vp_bitmap);
		ll_Point = (LinearLayout) view.findViewById(R.id.ll_Point);
		iv_bitmap = (ImageView) view.findViewById(R.id.iv_bitmap);
	}

	private void initData() {
		// 存图片的集合
		imgList = new ArrayList<ImageView>();
		if (imgUrl.size() == 0) {
			vp_bitmap.setVisibility(View.GONE);
			iv_bitmap.setVisibility(View.VISIBLE);
		} else {
			for (int i = 0; i < imgUrl.size(); i++) {
				image = new ImageView(mContext);
				Picasso.with(mContext).load(imgUrl.get(i)).error(R.drawable.product_no_img).into(image);
				imgList.add(image);
			}
			for (int i = 0; i < imgList.size(); i++) {
				// 五个点
				View viewPoint = new View(mContext);
				viewPoint.setBackgroundResource(R.drawable.dot_not_focus);
				LayoutParams params = new LayoutParams(15, 15);
				params.leftMargin = 10;
				viewPoint.setLayoutParams(params);
				ll_Point.addView(viewPoint);
			}
			// 设置第一个点为默认点
			ll_Point.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);

			DetailImageAdapter adapter = new DetailImageAdapter(imgList);

			vp_bitmap.setAdapter(adapter);

			vp_bitmap.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {

					// 当滑动到下一张图片，修改之前图片的点
					ll_Point.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
					// 滑动到当前图片，修改当前图片的点
					ll_Point.getChildAt(position % imgList.size()).setBackgroundResource(R.drawable.dot_focus);
					// 这一次的当前位置为下一次当前位置的前一个选中条目
					prePosition = position % imgList.size();
				}

				@Override
				public void onPageScrolled(int position, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int position) {

				}
			});
		}

	}

}
