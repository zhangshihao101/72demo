package com.spt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CompanyTagAdapter extends BaseAdapter {

	private Context mContext;
	private List<Object> mList;

	public CompanyTagAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		mList = new ArrayList<Object>();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tag_company, null);
			holder = new ViewHolder();
			holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_company_tag);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_tag.setText(mList.get(position).toString());
		if (position == 0) {
			holder.tv_tag.setBackgroundResource(R.drawable.shape_tag_company_role);
			holder.tv_tag.setTextColor(Color.parseColor("#ffa800"));
		}

		return convertView;
	}

	public void onlyAddAll(List<Object> datas) {
		mList.addAll(datas);
		notifyDataSetChanged();
	}

	public void clearAndAddAll(List<Object> datas) {
		mList.clear();
		onlyAddAll(datas);
	}

	private static class ViewHolder {
		TextView tv_tag;
	}

}
