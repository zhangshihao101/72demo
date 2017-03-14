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
import com.spt.bean.ScanTransInfo;

/**
 * 【查看物流】适配器
 * */
public class ScanTransInfoAdapter extends BaseAdapter {
	private Context mContext;
	private List<ScanTransInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public ScanTransInfoAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<ScanTransInfo>();
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
			convertView = mInflater.inflate(R.layout.scantransinfoitem, null);
			ScanTransInfo info = mData.get(position);
			TextView tvTime = (TextView) convertView.findViewById(R.id.tv_scanTransItem_time);
			TextView tvContext = (TextView) convertView.findViewById(R.id.tv_scanTransItem_context);

			tvTime.setText(info.getTime());
			tvContext.setText(info.getContext());

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addScanTransInfo(ScanTransInfo scanTransInfo) {

		mData.add(scanTransInfo);

	}

	public ScanTransInfo getScanTransInfo(int i) {
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
