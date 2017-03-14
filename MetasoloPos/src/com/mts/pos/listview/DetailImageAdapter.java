package com.mts.pos.listview;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DetailImageAdapter extends PagerAdapter {
	
	private List<ImageView> imgList;

	public DetailImageAdapter(List<ImageView> imgList) {
		super();
		this.imgList = imgList;
	}

	@Override
	public int getCount() {
		return imgList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		View view = imgList.get(position % imgList.size());
		
		if (view.getParent() != null) {
			ViewGroup group = (ViewGroup) view.getParent();
			group.removeView(view);
		}
		
		container.removeView(view);
		container.addView(view);
		
		return imgList.get(position % imgList.size());
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imgList.get(position % imgList.size()));
	}

}
