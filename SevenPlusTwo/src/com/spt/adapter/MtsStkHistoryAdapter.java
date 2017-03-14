package com.spt.adapter;

import java.util.List;

import com.spt.sht.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsStkHistoryAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> mList;

	private OnClickListener deleteOnClickListener;

	public void setDeleteOnClickListener(OnClickListener deleteOnClickListener) {
		this.deleteOnClickListener = deleteOnClickListener;
	}

	public MtsStkHistoryAdapter(Context mContext, List<String> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	// 刷新适配器,更新数据
	public void refresh(List<String> data) {
		this.mList = data;
		notifyDataSetChanged();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mts_stk_history, null);
			holder = new ViewHolder();
			holder.tv_history = (TextView) convertView.findViewById(R.id.tv_history);
			holder.iv_history_del = (ImageView) convertView.findViewById(R.id.iv_history_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_history.setText(mList.get(position).toString());
		holder.iv_history_del.setTag(position);
		holder.iv_history_del.setOnClickListener(deleteOnClickListener);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_history;
		ImageView iv_history_del;
	}

}
