package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter{

	private List<GuideInfo> categoryList;
	private Context context;
	
	public CategoryAdapter(List<GuideInfo> categoryList, Context context) {
		super();
		this.categoryList = categoryList;
		this.context = context;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (categoryList != null) {
			ret = categoryList.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return categoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View ret = null;
		if (convertView != null) {
			ret = convertView;
		}else {
			ret = LayoutInflater.from(context).inflate(R.layout.item_guide_category, null);
		}
		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.tv_sort_category = (TextView) ret.findViewById(R.id.tv_sort_category);
			viewHolder.img_sort_select = (ImageView) ret.findViewById(R.id.img_sort_select);
			ret.setTag(viewHolder);
		}
		
		viewHolder.tv_sort_category.setText(categoryList.get(position).getCategoryName());
		
		if (categoryList.get(position).getFlag()) {
			viewHolder.img_sort_select.setVisibility(View.VISIBLE);
		}else{
			viewHolder.img_sort_select.setVisibility(View.GONE);
		}
		
		return ret;
	}

	private static class ViewHolder{
		TextView tv_sort_category;
		ImageView img_sort_select;
	}
	
}
