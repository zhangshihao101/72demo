package com.spt.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MyHomeViewPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public MyHomeViewPagerAdapter(List<View> listViews) {
		super();
		this.mListViews = listViews;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mListViews.get(position % mListViews.size()).getParent() != null) {
			((ViewPager) mListViews.get(position % mListViews.size()).getParent()).removeView(mListViews.get(position
					% mListViews.size()));
		}
		((ViewPager) container).addView(mListViews.get(position % mListViews.size()), 0);
		return mListViews.get(position % mListViews.size());

	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
