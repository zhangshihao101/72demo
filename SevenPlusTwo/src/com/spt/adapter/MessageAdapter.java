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
import com.spt.bean.MessageInfo;
import com.spt.utils.AsynImageLoader;

/**
 * 【待处理消息】适配器
 * */
public class MessageAdapter extends BaseAdapter {
	private Context mContext;
	private List<MessageInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public MessageAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<MessageInfo>();
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
			convertView = mInflater.inflate(R.layout.messageitem, null);
			MessageInfo info = mData.get(position);
			TextView tvName = (TextView) convertView.findViewById(R.id.iv_MessageItem_name);
			TextView tvDate = (TextView) convertView.findViewById(R.id.iv_MessageItem_date);
			TextView tvContent = (TextView) convertView.findViewById(R.id.iv_MessageItem_content);
			ImageView ivImg = (ImageView) convertView.findViewById(R.id.iv_MessageItem_icon);

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

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addMessageInfo(MessageInfo messageInfo) {
		mData.add(messageInfo);
	}
	
	public void deleteMessageInfo(int position) {
		mData.remove(position);
		notifyDataSetChanged();
	}

	public MessageInfo getMessageInfo(int i) {
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
