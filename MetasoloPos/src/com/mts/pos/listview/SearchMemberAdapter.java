package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchMemberAdapter extends BaseAdapter{

	private Context mContext;
	private List<SearchMemberInfo> mList;
	
	public SearchMemberAdapter(Context mContext, List<SearchMemberInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (mList!= null) {
			ret = mList.size();
		}
		return ret;
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
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_member, null);
			holder.tv_search_member_name = (TextView) convertView.findViewById(R.id.tv_search_member_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_search_member_name.setText(mList.get(position).getName());
		
		return convertView;
	}
	
	public final static class ViewHolder{
		TextView tv_search_member_name;
	}

}
