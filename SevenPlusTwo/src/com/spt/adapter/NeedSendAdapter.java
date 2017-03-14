package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.NeedSendOrderInfo;

/**
 * 【待发货订单】适配器
 * */
public class NeedSendAdapter extends BaseAdapter {
	private Context mContext;
	private List<NeedSendOrderInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public NeedSendAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<NeedSendOrderInfo>();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (mMap.containsKey(position)) {
			convertView = mMap.get(position);
		} else {
			convertView = mInflater.inflate(R.layout.needsendordersitem, null);
			NeedSendOrderInfo info = mData.get(position);
			TextView tvOrdersName = (TextView) convertView.findViewById(R.id.tv_needSendOrdersItem_ordersName);
			TextView tvOrdersDate = (TextView) convertView.findViewById(R.id.tv_needSendOrdersItem_ordersDate);
			TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_needSendOrdersItem_price);
			TextView tvChanged = (TextView) convertView.findViewById(R.id.tv_needSendOrdersItem_priceChanged);

			String changed = info.getIs_change();
			
			if ("0".equals(changed)) {
				tvChanged.setVisibility(View.GONE);
			} else if ("1".equals(changed)) {
				tvChanged.setVisibility(View.VISIBLE);
			}
			tvOrdersName.setText("订单号：" + info.getOrder_sn());
			tvOrdersDate.setText("下单时间：" + info.getAdd_time());
			tvPrice.setText("￥" + info.getFinal_amount());

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addNeedSendOrderInfo(NeedSendOrderInfo needSendOrderInfo) {

		mData.add(needSendOrderInfo);

	}

	public NeedSendOrderInfo getNeedSendOrderInfo(int i) {
		if (i < 0 || i > mData.size() - 1) {
			return null;
		}
		return mData.get(i);
	}

	public void clear() {
		mData.clear();
		mMap.clear();
		notifyDataSetChanged();
	}

	public void deleteData(String orderID) {
		int count = mData.size();
		for (int i = 0; i < count; i++) {
			NeedSendOrderInfo info = mData.get(i);
			String order_id = info.getOrder_id();
			if (orderID.equals(order_id)) {
				mData.remove(i);
				notifyDataSetChanged();
			}
		}
	}
}
