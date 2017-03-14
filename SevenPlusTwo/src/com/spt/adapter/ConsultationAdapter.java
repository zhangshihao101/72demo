package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.ChatInfo;

/**
 * 【产品咨询】适配器
 * */
public class ConsultationAdapter extends BaseAdapter {
	private Context mContext;
	private List<ChatInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;
	private TvOnClickListener mListener;

	@SuppressLint("UseSparseArrays")
	public ConsultationAdapter(Context context, TvOnClickListener listener) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<ChatInfo>();
		this.mListener = listener;
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
			convertView = mInflater.inflate(R.layout.consutationmanageritem, null);
			ChatInfo chat = mData.get(position);
			TextView tvProductName = (TextView) convertView.findViewById(R.id.tv_product_productName);
			TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_product_userName);
			TextView tvCallDate = (TextView) convertView.findViewById(R.id.tv_product_callDate);
			TextView tvCallContent = (TextView) convertView.findViewById(R.id.tv_product_callContent);
			TextView tvMyName = (TextView) convertView.findViewById(R.id.tv_product_myName);
			TextView tvRecallDate = (TextView) convertView.findViewById(R.id.tv_product_recallDate);
			TextView tvRecallContent = (TextView) convertView.findViewById(R.id.tv_product_recallContent);

			tvProductName.setText(chat.getItem_name()); // 商品名称
			String user_id = chat.getUser_id();
			if ("0".equals(user_id)) {
				tvUserName.setText("游客"); // 提问用户名
			} else {
				tvUserName.setText(chat.getUser_name()); // 提问用户名
			}

			tvCallDate.setText(chat.getTime_post()); // 提问时间
			tvCallContent.setText(chat.getQuestion_content()); // 问题内容
			String recall = chat.getReply_content();// 回复内容
			if (!"".equals(recall)) {
				tvMyName.setText("我的回复");
				tvMyName.setTextColor(Color.rgb(194, 194, 194));
				tvRecallDate.setText(chat.getTime_reply()); // 回复时间
				tvRecallContent.setText(recall);// 回复内容
//				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				tvRecallContent.setLayoutParams(params); // 如果已经回复则包裹内容
			} else {
				tvMyName.setText("请回复");
				tvMyName.setTextColor(Color.rgb(236, 142, 80));
				tvRecallDate.setText(""); // 回复时间
				tvRecallContent.setText("");// 回复内容
			}

			tvRecallContent.setTag(chat);
			tvRecallContent.setOnClickListener(mListener);

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addChatInfo(ChatInfo chatInfo) {

		mData.add(chatInfo);

	}

	public ChatInfo getChatInfo(int i) {
		if (i < 0 || i > mData.size() - 1) {
			return null;
		}
		return mData.get(i);
	}

	public void clear() {
		mData.clear();
		mMap.clear();
	}

	public static abstract class TvOnClickListener implements OnClickListener {

		public abstract void myTvOnClickListener(ChatInfo info, View v);

		@Override
		public void onClick(View arg0) {
			myTvOnClickListener((ChatInfo) arg0.getTag(), arg0);
		}

	}
}
