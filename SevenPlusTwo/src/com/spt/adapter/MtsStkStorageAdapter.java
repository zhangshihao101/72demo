package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsStkStorageInfo;
import com.spt.sht.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsStkStorageAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsStkStorageInfo> mList;

	public MtsStkStorageAdapter(Context mContext, List<MtsStkStorageInfo> mList) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_storage, null);
			holder = new ViewHolder();
			holder.tv_mts_stk_name = (TextView) convertView.findViewById(R.id.tv_mts_stk_name);
			holder.iv_mts_stk_storage = (ImageView) convertView.findViewById(R.id.iv_mts_stk_storage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_mts_stk_name.setText(mList.get(position).getName());

		if (mList.get(position).isFlag()) {
			holder.iv_mts_stk_storage.setBackgroundResource(R.drawable.stock_filter_choice_hl);
		} else {
			holder.iv_mts_stk_storage.setBackgroundResource(R.drawable.stock_filter_choice);
		}

		return convertView;
	}

	public class ViewHolder {
		TextView tv_mts_stk_name;
		ImageView iv_mts_stk_storage;
	}

}
