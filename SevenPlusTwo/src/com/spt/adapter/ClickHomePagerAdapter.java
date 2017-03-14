package com.spt.adapter;

import java.util.List;

import com.spt.page.DisGoodsDetailsActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ClickHomePagerAdapter extends PagerAdapter {

	private Context mContext;
	private List<ImageView> imgList;
	private List<String> picList;

	public ClickHomePagerAdapter(Context mContext, List<ImageView> imgList, List<String> picList) {
		super();
		this.mContext = mContext;
		this.imgList = imgList;
		this.picList = picList;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = imgList.get(position % imgList.size());

		if (view.getParent() != null) {
			ViewGroup group = (ViewGroup) view.getParent();
			group.removeView(view);
		}

		container.removeView(view);
		container.addView(view);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("====" + position % imgList.size());
				if (picList.get(position % imgList.size()).contains("goods_id")) {
					Intent intent = new Intent(mContext, DisGoodsDetailsActivity.class);
					intent.putExtra("goodsId", picList.get(position % imgList.size()).substring(9));
					mContext.startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(picList.get(position % imgList.size()));
					intent.setData(content_url);
					mContext.startActivity(intent);
				}
			}
		});

		return imgList.get(position % imgList.size());
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// container.removeView(imgList.get(position % imgList.size()));
	}

}
