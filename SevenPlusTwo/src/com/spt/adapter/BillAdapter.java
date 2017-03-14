package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spt.bean.BillInfo;
import com.spt.sht.R;
import com.spt.utils.MyUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 【对账单】适配器
 * */
public class BillAdapter extends BaseAdapter {
	private Context mContext;
	private List<BillInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public BillAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<BillInfo>();
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
			convertView = mInflater.inflate(R.layout.billitem, null);
			BillInfo info = mData.get(position);
			TextView tvBillNo = (TextView) convertView.findViewById(R.id.tv_billitem_billNo);
			TextView tvBillState = (TextView) convertView.findViewById(R.id.tv_billitem_billState);
			TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_billitem_price);
			TextView tvDate = (TextView) convertView.findViewById(R.id.tv_billitem_date);

			String sta_sn = info.getSta_sn();
			String sta_plat = info.getSta_plat();
			if ("1".equals(sta_plat)) {
				sta_plat = "商城";
			} else if ("2".equals(sta_plat)) {
				sta_plat = "团购";
			}
			tvBillNo.setText(sta_sn + " (" + sta_plat + ")");

			String sta_status = info.getSta_status();
			if ("0".equals(sta_status)) {
				sta_status = "未确认";
				tvBillState.setTextColor(Color.rgb(139, 186, 41));
				tvBillState.setText(sta_status);
				tvDate.setText(MyUtil.millisecondsToStr(info.getAdd_time()));
			} else if ("1".equals(sta_status)) {
				sta_status = "已确认";
				tvBillState.setTextColor(Color.rgb(240, 132, 39));
				tvBillState.setText(sta_status);
				tvDate.setText(MyUtil.millisecondsToStr(info.getConfirm_time()));
			} else if ("2".equals(sta_status)) {
				sta_status = "已结账";
				tvBillState.setTextColor(Color.rgb(240, 132, 39));
				tvBillState.setText(sta_status);
				tvDate.setText(MyUtil.millisecondsToStr(info.getConfirm_time()));
			}
			tvPrice.setText("￥" + info.getTotal_order_pay());

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addBillInfo(BillInfo billInfo) {

		mData.add(billInfo);

	}

	public BillInfo getBillInfo(int i) {
		if (i < 0 || i > mData.size() - 1) {
			return null;
		}
		return mData.get(i);
	}

	public void clear() {
		mData.clear();
		mMap.clear();
	}
}
