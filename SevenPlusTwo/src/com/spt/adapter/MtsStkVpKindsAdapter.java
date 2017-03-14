package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsStkKindsInfo;
import com.spt.sht.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MtsStkVpKindsAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsStkKindsInfo> mList;

	public MtsStkVpKindsAdapter(Context mContext, List<MtsStkKindsInfo> mList) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_kinds, null);
			holder = new ViewHolder();
			holder.tv_mts_stk_kinds_name = (TextView) convertView.findViewById(R.id.tv_mts_stk_kinds_name);
			holder.tv_mts_stk_kinds_percent = (TextView) convertView.findViewById(R.id.tv_mts_stk_kinds_percent);
			holder.tv_mts_stk_kinds_sku = (TextView) convertView.findViewById(R.id.tv_mts_stk_kinds_sku);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_mts_stk_kinds_name.setText(mList.get(position).getName());
		holder.tv_mts_stk_kinds_percent.setText(mList.get(position).getPercent() + "%");
		holder.tv_mts_stk_kinds_sku.setText(mList.get(position).getStk());

		return convertView;
	}

	public static class ViewHolder {
		TextView tv_mts_stk_kinds_name, tv_mts_stk_kinds_percent, tv_mts_stk_kinds_sku;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
