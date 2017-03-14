package com.spt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.spt.bean.Bean;
import com.spt.sht.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColorAndSizeAdapter extends BaseAdapter {

	private List<Bean> mList;
	private Context mContext;
	public ItemClickListener itemClickListener;

	public void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public ColorAndSizeAdapter( Context mContext) {
		super();
		mList = new ArrayList<Bean>();
		this.mContext = mContext;
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(mContext).inflate(R.layout.item_color_size_tag, null);
		}
		ViewHolder holder = (ViewHolder) ret.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.tv_item_tag = (TextView) ret.findViewById(R.id.tv_item_tag);
			ret.setTag(holder);
		}
		
		final Bean bean = mList.get(position);
		if (bean.getStates().equals("0")) {
			// 选中
			holder.tv_item_tag.setBackgroundResource(R.drawable.shape_1);
			holder.tv_item_tag.setTextColor(Color.parseColor("#FFFFFF"));
		} else if (bean.getStates().equals("1")) {
			// 未选中
			holder.tv_item_tag.setBackgroundResource(R.drawable.shape_2);
			holder.tv_item_tag.setTextColor(Color.parseColor("#333333"));
		} else if (bean.getStates().equals("2")) {
			// 不可选
			holder.tv_item_tag.setBackgroundResource(R.drawable.shape_3);
			holder.tv_item_tag.setTextColor(Color.parseColor("#EEEEEE"));
		}
		holder.tv_item_tag.setText(bean.getName());
		holder.tv_item_tag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (itemClickListener != null) {
					if (!bean.getStates().equals("2")) {
						itemClickListener.ItemClick(bean, position);
					}
				}
			}
		});

		return ret;
	}

	public void onlyAddAll(List<Bean> datas) {
		mList.addAll(datas);
		notifyDataSetChanged();
	}

	public void clearAndAddAll(List<Bean> datas) {
		mList.clear();
		onlyAddAll(datas);
	}

	private static class ViewHolder {
		TextView tv_item_tag;
	}

	public interface ItemClickListener {
		public void ItemClick(Bean bean, int position);
	}

}
