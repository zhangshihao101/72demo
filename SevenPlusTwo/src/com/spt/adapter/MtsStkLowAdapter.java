package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsStkLowInfo;
import com.spt.sht.R;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsStkLowAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsStkLowInfo> mList;

	public MtsStkLowAdapter(Context mContext, List<MtsStkLowInfo> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_low, null);
			holder = new ViewHolder();
			holder.tv_low_name = (TextView) convertView.findViewById(R.id.tv_low_name);
			holder.tv_low_brand = (TextView) convertView.findViewById(R.id.tv_low_brand);
			holder.tv_low_number = (TextView) convertView.findViewById(R.id.tv_low_number);
			holder.tv_low_code = (TextView) convertView.findViewById(R.id.tv_low_code);
			holder.tv_low_salecount = (TextView) convertView.findViewById(R.id.tv_low_salecount);
			holder.tv_low_nowcount = (TextView) convertView.findViewById(R.id.tv_low_nowcount);
			holder.tv_low_storage_name = (TextView) convertView.findViewById(R.id.tv_low_storage_name);
			holder.iv_low = (ImageView) convertView.findViewById(R.id.iv_low);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_low_name.setText(mList.get(position).getName());
		holder.tv_low_brand.setText(mList.get(position).getBrand());
		holder.tv_low_number.setText(mList.get(position).getNumber());
		holder.tv_low_code.setText(mList.get(position).getCode());
		holder.tv_low_salecount.setText(mList.get(position).getSaleCount());
		holder.tv_low_nowcount.setText(mList.get(position).getNowCount());
		holder.tv_low_storage_name.setText(mList.get(position).getStorageName() + ":可销售数量");
		Picasso.with(mContext).load(mList.get(position).getImgUrl()).placeholder(R.drawable.noheader)
				.error(R.drawable.noheader).resize(40, 40).into(holder.iv_low);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_low_name, tv_low_brand, tv_low_number, tv_low_code, tv_low_salecount, tv_low_nowcount,
				tv_low_storage_name;
		ImageView iv_low;
	}

}
