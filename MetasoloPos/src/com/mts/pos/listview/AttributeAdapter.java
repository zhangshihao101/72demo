package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttributeAdapter extends BaseAdapter {

	private Context context;
	private List<String> attList;

	public AttributeAdapter(Context context, List<String> attList) {
		super();
		this.context = context;
		this.attList = attList;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (attList != null) {
			ret = attList.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return attList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(context).inflate(R.layout.item_detail_attribute, null);
		}

		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.tv_detail_attrName = (TextView) ret.findViewById(R.id.tv_detail_attrName);
			ret.setTag(viewHolder);
		}
		viewHolder.tv_detail_attrName.setText(attList.get(position));
		return ret;
	}

	private static class ViewHolder {
		TextView tv_detail_attrName;
	}

}
