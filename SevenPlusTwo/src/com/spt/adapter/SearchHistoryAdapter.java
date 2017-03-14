package com.spt.adapter;

import java.util.List;

import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchHistoryAdapter extends BaseAdapter{

	private Context mContext;
	private List<String> mList;
	
	public SearchHistoryAdapter(Context mContext, List<String> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_history, null);
			holder = new ViewHolder();
			holder.tv_search_history = (TextView) convertView.findViewById(R.id.tv_search_history);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_search_history.setText(mList.get(position).toString());
		
		return convertView;
	}
	
	private static class ViewHolder{
		TextView tv_search_history;
	}

}
