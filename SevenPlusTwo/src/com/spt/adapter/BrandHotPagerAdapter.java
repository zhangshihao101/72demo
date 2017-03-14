package com.spt.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class BrandHotPagerAdapter extends PagerAdapter {

	private List<View> mList;

	public BrandHotPagerAdapter( List<View> mList) {
		super();
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mList.get(position % mList.size());

		if (view.getParent() != null) {
			ViewGroup group = (ViewGroup) view.getParent();
			group.removeView(view);
		}

		container.removeView(view);
		container.addView(view);

		return mList.get(position % mList.size());
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mList.get(position % mList.size()));
	}

}
