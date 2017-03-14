package com.spt.adapter;

import java.util.List;
import com.spt.bean.AddressInfo;
import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAddressAdapter extends BaseAdapter {

	private Context mContext;
	private List<AddressInfo> mList;

	public MyAddressAdapter(Context mContext, List<AddressInfo> mList) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_myaddress, null);
			holder = new ViewHolder();
			holder.tv_myaddress_consignee = (TextView) convertView.findViewById(R.id.tv_myaddress_consignee);
			holder.tv_myaddress_detail = (TextView) convertView.findViewById(R.id.tv_myaddress_detail);
			holder.tv_myaddress_phone = (TextView) convertView.findViewById(R.id.tv_myaddress_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_myaddress_consignee.setText(mList.get(position).getConsignee());
		holder.tv_myaddress_detail.setText(mList.get(position).getAddress());
		holder.tv_myaddress_phone.setText(mList.get(position).getPhone_mob());

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_myaddress_consignee, tv_myaddress_phone, tv_myaddress_detail;
	}

}
