package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsShopInfo;
import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MtsShopAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsShopInfo> mList;

	public MtsShopAdapter(Context mContext, List<MtsShopInfo> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_shop, null);
			holder = new ViewHolder();
			holder.tv_mts_shop_name = (TextView) convertView.findViewById(R.id.tv_mts_shop_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_mts_shop_name.setText(mList.get(position).getName());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_mts_shop_name;
	}

}
