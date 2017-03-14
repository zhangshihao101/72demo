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

public class BrandAdapter extends BaseAdapter {

	private List<GuideInfo> brandList;
	private Context context;
	
	public BrandAdapter(List<GuideInfo> brandList, Context context) {
		super();
		this.brandList = brandList;
		this.context = context;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (brandList != null) {
			ret = brandList.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return brandList.get(position);
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
			ret = LayoutInflater.from(context).inflate(R.layout.item_guide_brands, null);
		}
		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.tv_brand_category = (TextView) ret.findViewById(R.id.tv_brand_category);
			viewHolder.img_brand_select = (ImageView) ret.findViewById(R.id.img_brand_select);
			ret.setTag(viewHolder);
		}
		
		viewHolder.tv_brand_category.setText(brandList.get(position).getBrandName());
		
		if (brandList.get(position).getFlag()) {
			viewHolder.img_brand_select.setVisibility(View.VISIBLE);
		}else{
			viewHolder.img_brand_select.setVisibility(View.GONE);
		}
		return ret;
	}

	private static class ViewHolder{
		TextView tv_brand_category;
		ImageView img_brand_select;
	}

}
