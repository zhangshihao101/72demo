package com.spt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.spt.sht.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ComBriefTagAdapter extends BaseAdapter {

	private Context mContext;
	private List<Object> mDataList;

	public ComBriefTagAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		mDataList = new ArrayList<Object>();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
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
		} else {
			ret = LayoutInflater.from(mContext).inflate(R.layout.item_brief_tag, null);
		}
		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.tv_tag = (TextView) ret.findViewById(R.id.tv_company_tag);
			ret.setTag(viewHolder);
		}

		viewHolder.tv_tag.setText(mDataList.get(position).toString());

		return ret;
	}

	public void onlyAddAll(List<Object> datas) {
		mDataList.addAll(datas);
		notifyDataSetChanged();
	}

	public void clearAndAddAll(List<Object> datas) {
		mDataList.clear();
		onlyAddAll(datas);
	}

	private static class ViewHolder {
		TextView tv_tag;
	}
}
