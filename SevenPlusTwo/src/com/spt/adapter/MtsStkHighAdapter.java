package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsStkHighInfo;
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

@SuppressLint("InflateParams")
public class MtsStkHighAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsStkHighInfo> mList;

	public MtsStkHighAdapter(Context mContext, List<MtsStkHighInfo> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_high, null);
			holder = new ViewHolder();
			holder.tv_high_name = (TextView) convertView.findViewById(R.id.tv_high_name);
			holder.tv_high_brand = (TextView) convertView.findViewById(R.id.tv_high_brand);
			holder.tv_high_number = (TextView) convertView.findViewById(R.id.tv_high_number);
			holder.tv_high_code = (TextView) convertView.findViewById(R.id.tv_high_code);
			holder.tv_high_salecount = (TextView) convertView.findViewById(R.id.tv_high_salecount);
			holder.tv_high_nowcount = (TextView) convertView.findViewById(R.id.tv_high_nowcount);
			holder.tv_high_storage_name = (TextView) convertView.findViewById(R.id.tv_high_storage_name);
			holder.iv_high = (ImageView) convertView.findViewById(R.id.iv_high);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_high_name.setText(mList.get(position).getName());
		holder.tv_high_brand.setText(mList.get(position).getBrand());
		holder.tv_high_number.setText(mList.get(position).getNumber());
		holder.tv_high_code.setText(mList.get(position).getCode());
		holder.tv_high_salecount.setText(mList.get(position).getSaleCount());
		holder.tv_high_nowcount.setText(mList.get(position).getNowCount());
		holder.tv_high_storage_name.setText(mList.get(position).getStorageName() + ":可销售数量");
		Picasso.with(mContext).load(mList.get(position).getImgUrl()).placeholder(R.drawable.noheader)
				.error(R.drawable.noheader).into(holder.iv_high);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_high_name, tv_high_brand, tv_high_number, tv_high_code, tv_high_salecount, tv_high_nowcount,
				tv_high_storage_name;
		ImageView iv_high;
	}

}
