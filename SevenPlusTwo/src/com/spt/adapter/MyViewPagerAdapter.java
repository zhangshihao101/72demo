package com.spt.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyViewPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public MyViewPagerAdapter(List<View> listViews) {
		super();
		this.mListViews = listViews;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mListViews.get(position), 0);
		System.out.println("vp已执行");
		return mListViews.get(position);
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}