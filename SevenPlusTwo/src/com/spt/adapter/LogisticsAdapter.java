package com.spt.adapter;

import java.util.List;

import com.spt.bean.LogisticsInfo;
import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogisticsAdapter extends BaseAdapter {

	private Context mContext;
	private List<LogisticsInfo> mList;

	public LogisticsAdapter(Context mContext, List<LogisticsInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_logistics_children, null);
			holder = new ViewHolder();
			holder.tv_logistics_msg = (TextView) convertView.findViewById(R.id.tv_logistics_msg);
			holder.tv_logistics_time = (TextView) convertView.findViewById(R.id.tv_logistics_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_logistics_msg.setText(mList.get(position).getMsg());
		holder.tv_logistics_time.setText(mList.get(position).getTime());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_logistics_time, tv_logistics_msg;
	}

}
