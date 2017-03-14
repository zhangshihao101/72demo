package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.MessageDeleteInfo;
import com.spt.utils.AsynImageLoader;

/**
 * 【待处理消息】适配器
 * */
public class MessageDeleteAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessageDeleteInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public MessageDeleteAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<MessageDeleteInfo>();
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
			MessageDeleteInfo info = mData.get(position);
			TextView tvCheck = (TextView) convertView.findViewById(R.id.tv_MessageDeleteItem_checked);
			boolean bl = info.isChecked();
			if (bl) {
				tvCheck.setBackgroundResource(R.drawable.cbcheck);
			} else {
				tvCheck.setBackgroundResource(R.drawable.cbuncheck);
			}
		} else {
			convertView = mInflater.inflate(R.layout.messagedeleteitem, null);
			MessageDeleteInfo info = mData.get(position);
			TextView tvName = (TextView) convertView.findViewById(R.id.iv_MessageDeleteItem_name);
			TextView tvDate = (TextView) convertView.findViewById(R.id.iv_MessageDeleteItem_date);
			TextView tvContent = (TextView) convertView.findViewById(R.id.iv_MessageDeleteItem_content);
			ImageView ivImg = (ImageView) convertView.findViewById(R.id.iv_MessageDeleteItem_icon);
			TextView tvCheck = (TextView) convertView.findViewById(R.id.tv_MessageDeleteItem_checked);

			String url = info.getPortrait();
			AsynImageLoader asynImageLoader = new AsynImageLoader();
			tvName.setText(info.getUser_name());
			if ("系统消息".equals(info.getUser_name())) {
				tvName.setTextColor(Color.rgb(51, 120, 57));
				ivImg.setImageResource(R.drawable.icon100);
			} else {
				asynImageLoader.showImageAsyn(ivImg, url, R.drawable.test);
			}
			tvDate.setText(info.getAdd_time());
			tvContent.setText(info.getContent());
			boolean bl = info.isChecked();
			if (bl) {
				tvCheck.setBackgroundResource(R.drawable.cbcheck);
			} else {
				tvCheck.setBackgroundResource(R.drawable.cbuncheck);
			}

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addMessageDeleteInfo(MessageDeleteInfo messageInfo) {
		mData.add(messageInfo);
	}

	public MessageDeleteInfo getMessageDeleteInfo(int i) {
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
