package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsStkGoodsInfo;
import com.spt.sht.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MtsStkVpGoodsAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsStkGoodsInfo> mList;

	public MtsStkVpGoodsAdapter(Context mContext, List<MtsStkGoodsInfo> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_goods, null);
			holder = new ViewHolder();
			holder.tv_mts_stk_goods_name = (TextView) convertView.findViewById(R.id.tv_mts_stk_goods_name);
			holder.tv_mts_stk_goods_percent = (TextView) convertView.findViewById(R.id.tv_mts_stk_goods_percent);
			holder.tv_mts_stk_goods_sku = (TextView) convertView.findViewById(R.id.tv_mts_stk_goods_sku);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_mts_stk_goods_name.setText(mList.get(position).getName());
		holder.tv_mts_stk_goods_percent.setText(mList.get(position).getPercent() + "%");
		holder.tv_mts_stk_goods_sku.setText(mList.get(position).getStk());

		return convertView;
	}

	public static class ViewHolder {
		TextView tv_mts_stk_goods_name, tv_mts_stk_goods_percent, tv_mts_stk_goods_sku;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
