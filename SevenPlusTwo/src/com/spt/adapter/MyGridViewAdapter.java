package com.spt.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.sht.R;

public class MyGridViewAdapter extends BaseAdapter {
	private List<HashMap<String, Object>> contentList;
	private LayoutInflater inflater;

	public MyGridViewAdapter(Context context, List<HashMap<String, Object>> contentList) {
		super();
		this.contentList = contentList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return contentList.size();
	}

	@Override
	public Object getItem(int position) {
		return contentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.homepage_item, null);
		ImageView iv = (ImageView) convertView.findViewById(R.id.iv_itemPic);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_itemCount);
		TextView title = (TextView) convertView.findViewById(R.id.tv_itemTitle);

		HashMap<String, Object> map = contentList.get(position);
		int picId = (Integer) map.get("iv_itemPic");
		String itemCount = (String) map.get("tv_itemNo");
		boolean isShow = (Boolean) map.get("isShow");
		iv.setImageResource(picId);
		tv.setText(itemCount);
		if (isShow) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.INVISIBLE);
		}
		title.setText(map.get("title").toString());

		return convertView;
	}

}
