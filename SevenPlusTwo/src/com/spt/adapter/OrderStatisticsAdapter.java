package com.spt.adapter;

import java.util.List;

import com.spt.bean.OrderStatisticsInfo;
import com.spt.sht.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderStatisticsAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderStatisticsInfo> mList;
	private final int VIEW_TYPE = 2;
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;

	public OrderStatisticsAdapter(Context mContext, List<OrderStatisticsInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_1;
		} else {
			return TYPE_2;
		}
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return VIEW_TYPE;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 5;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ViewHolder0 holder0 = null;
		int type = getItemViewType(position);
		if (convertView == null) {

			switch (type) {
			case TYPE_1:
				holder0 = new ViewHolder0();

				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_order_statistics_today, null);
				holder0.tv_order_sum = (TextView) convertView.findViewById(R.id.tv_order_sum);
				holder0.tv_order_count = (TextView) convertView.findViewById(R.id.tv_order_count);

				convertView.setTag(holder0);
				break;
			case TYPE_2:
				holder = new ViewHolder();

				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_order_statistics, null);
				holder.rl_all = (RelativeLayout) convertView.findViewById(R.id.rl_all);
				holder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
				holder.tv_orderSum = (TextView) convertView.findViewById(R.id.tv_orderSum);
				holder.tv_orderCount = (TextView) convertView.findViewById(R.id.tv_orderCount);
				holder.iv_mark = (ImageView) convertView.findViewById(R.id.iv_mark);
				convertView.setTag(holder);
				break;
			}

		} else {
			switch (type) {
			case TYPE_1:
				holder0 = (ViewHolder0) convertView.getTag();
				break;
			case TYPE_2:
				holder = (ViewHolder) convertView.getTag();
				break;
			}
		}

		switch (type) {
		case TYPE_1:

			holder0.tv_order_sum.setText("￥ " + mList.get(0).getOrderSum());
			holder0.tv_order_count.setText("" + mList.get(0).getOrderCount()+" 笔");
			break;
		case TYPE_2:
			holder.tv_orderSum.setText("￥ " + mList.get(position).getOrderSum());
			holder.tv_orderCount.setText(mList.get(position).getOrderCount() + "笔");

			if (position == 1) {
				holder.tv_day.setText("昨日");
				holder.iv_mark.setImageResource(R.drawable.mark1);
			} else if (position == 2) {
				holder.tv_day.setText("七日");
				holder.iv_mark.setImageResource(R.drawable.mark2);
			} else if (position == 3) {
				holder.tv_day.setText("本月");
				holder.iv_mark.setImageResource(R.drawable.mark3);
			} else if (position == 4) {
				holder.tv_day.setText("本年");
				holder.iv_mark.setImageResource(R.drawable.mark4);
			}

			break;
		}

		// holder.tv_orderSum.setText("￥ " + mList.get(position).getOrderSum());
		// holder.tv_orderCount.setText(mList.get(position).getOrderCount() +
		// "笔");

		// if (position == 0) {
		// holder.rl_all.setBackgroundColor(0xffecf7f5);
		// holder.tv_day.setText("今天");
		// holder.tv_day.setBackgroundColor(0xff72ccbd);
		// holder.tv_orderSum.setTextColor(0xff72ccbd);
		// holder.tv_orderCount.setTextColor(0xff72ccbd);
		// } else if (position == 1) {
		// holder.rl_all.setBackgroundColor(0xfff2f8e4);
		// holder.tv_day.setText("昨天");
		// holder.tv_day.setBackgroundColor(0xffa5ce51);
		// holder.tv_orderSum.setTextColor(0xffa5ce51);
		// holder.tv_orderCount.setTextColor(0xffa5ce51);
		// } else if (position == 2) {
		// holder.rl_all.setBackgroundColor(0xffe8f3fb);
		// holder.tv_day.setText("本月");
		// holder.tv_day.setBackgroundColor(0xff52aee2);
		// holder.tv_orderSum.setTextColor(0xff52aee2);
		// holder.tv_orderCount.setTextColor(0xff52aee2);
		// } else if (position == 3) {
		// holder.rl_all.setBackgroundColor(0xfffceeee);
		// holder.tv_day.setText("本年");
		// holder.tv_day.setBackgroundColor(0xfff38d8d);
		// holder.tv_orderSum.setTextColor(0xfff38d8d);
		// holder.tv_orderCount.setTextColor(0xfff38d8d);
		// }

		return convertView;
	}

	public final static class ViewHolder {
		TextView tv_day, tv_orderSum, tv_orderCount;
		ImageView iv_mark;
		RelativeLayout rl_all;
	}

	public final static class ViewHolder0 {
		TextView tv_order_sum, tv_order_count;
	}

}
