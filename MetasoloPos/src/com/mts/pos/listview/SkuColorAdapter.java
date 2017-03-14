package com.mts.pos.listview;

import java.util.ArrayList;
import java.util.List;

import com.mts.pos.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SkuColorAdapter extends BaseAdapter {

	private List<Bean> list;
	private Context context;

	public ItemClickListener itemClickListener;

	public void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public SkuColorAdapter(Context context) {
		super();
		list = new ArrayList<Bean>();
		this.context = context;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (list != null) {
			ret = list.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(context).inflate(R.layout.item_detail_color, null);
		}
		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.tv_detail_color = (TextView) ret.findViewById(R.id.tv_detail_color);
			ret.setTag(viewHolder);
		}

		final Bean bean = list.get(position);
		if (bean.getStates().equals("0")) {
			// 选中
			viewHolder.tv_detail_color.setBackgroundResource(R.drawable.shape_1);
			viewHolder.tv_detail_color.setTextColor(Color.parseColor("#66CCFF"));
		} else if (bean.getStates().equals("1")) {
			// 未选中
			viewHolder.tv_detail_color.setBackgroundResource(R.drawable.shape_2);
			viewHolder.tv_detail_color.setTextColor(Color.parseColor("#717171"));
		} else if (bean.getStates().equals("2")) {

			// 不可选
			viewHolder.tv_detail_color.setBackgroundResource(R.drawable.shape_2);
			viewHolder.tv_detail_color.setTextColor(Color.parseColor("#EEEEEE"));
		}

		viewHolder.tv_detail_color.setText(bean.getName());
		viewHolder.tv_detail_color.setOnClickListener(new OnClickListener() {

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
		list.addAll(datas);
		notifyDataSetChanged();
	}

	public void clearAndAddAll(List<Bean> datas) {
		list.clear();
		onlyAddAll(datas);
	}

	public final static class ViewHolder {
		TextView tv_detail_color;
	}

	public interface ItemClickListener {
		public void ItemClick(Bean bean, int position);
	}

}
