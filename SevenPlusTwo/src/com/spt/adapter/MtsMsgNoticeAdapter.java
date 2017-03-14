package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsMsgNoticeInfo;
import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MtsMsgNoticeAdapter extends BaseAdapter {

	private Context mContext;
	private List<MtsMsgNoticeInfo> mList;

	public MtsMsgNoticeAdapter(Context mContext, List<MtsMsgNoticeInfo> mList) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_msg_notice, null);
			holder = new ViewHolder();
			holder.tv_notice_title = (TextView) convertView.findViewById(R.id.tv_notice_title);
			holder.tv_notice_time = (TextView) convertView.findViewById(R.id.tv_notice_time);
			holder.tv_notice_content = (TextView) convertView.findViewById(R.id.tv_notice_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_notice_content.setText(mList.get(position).getContent());
		holder.tv_notice_time.setText(mList.get(position).getTime());
		holder.tv_notice_title.setText(mList.get(position).getTitle());
		TextPaint tp = holder.tv_notice_title.getPaint();
		tp.setFakeBoldText(true);
		
		return convertView;
	}
	
	private static class ViewHolder{
		TextView tv_notice_title, tv_notice_time, tv_notice_content;
	}

}
