package com.spt.adapter;

import java.util.List;

import com.spt.bean.OrderInfo;
import com.spt.sht.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderInfo> mList;

	public OrderAdapter(Context mContext, List<OrderInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList.size();
		// return 6;
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
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_order, parent, false);
			holder = new ViewHolder();
			holder.tv_order_no = (TextView) convertView.findViewById(R.id.tv_order_no);
			holder.tv_order_date = (TextView) convertView.findViewById(R.id.tv_order_date);
			holder.tv_shop = (TextView) convertView.findViewById(R.id.tv_shop);
			holder.tv_channels = (TextView) convertView.findViewById(R.id.tv_channels);
			holder.tv_sum = (TextView) convertView.findViewById(R.id.tv_sum);
			holder.tv_isfinish = (TextView) convertView.findViewById(R.id.tv_isfinish);
			holder.tv_issucces = (TextView) convertView.findViewById(R.id.tv_issucces);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_order_no.setText("订单：" + mList.get(position).getOrderId());
		holder.tv_order_date.setText("订单日期：" + mList.get(position).getOrderDate());
		holder.tv_shop.setText("店铺：" + mList.get(position).getShopName());
		if (mList.get(position).getChannel().equals("POS_SALES_CHANNEL")) {
			holder.tv_channels.setText("销售渠道：POS零售");
		} else if (mList.get(position).getChannel().equals("WHOLES_CHANNEL")) {
			holder.tv_channels.setText("销售渠道：批发");
		} else if (mList.get(position).getChannel().equals("DISTRI_CHANNEL")) {
			holder.tv_channels.setText("销售渠道：分销");
		} else if (mList.get(position).getChannel().equals("72_SALES_CHANNEL")) {
			holder.tv_channels.setText("销售渠道：七加二商城");
		}

		holder.tv_sum.setText("￥" + mList.get(position).getTotle());

		if (mList.get(position).getIsFinish().equals("ORDER_CREATED")) {
			holder.tv_isfinish.setText("已创建");
		} else if (mList.get(position).getIsFinish().equals("ORDER_APPROVED")) {
			holder.tv_isfinish.setText("已批准");
		} else if (mList.get(position).getIsFinish().equals("ORDER_HOLD")) {
			holder.tv_isfinish.setText("已保留");
		} else if (mList.get(position).getIsFinish().equals("ORDER_COMPLETED")) {
			holder.tv_isfinish.setText("已完成");
		} else if (mList.get(position).getIsFinish().equals("ORDER_CANCELLED")) {
			holder.tv_isfinish.setText("已取消");
		}

		if (mList.get(position).getIsPayment().equals("PMNT_NOPAY_RECV")) {
			holder.tv_issucces.setText("未收款");
			holder.tv_issucces.setBackgroundColor(0xfffc6d27);
		} else if (mList.get(position).getIsPayment().equals("PMNT_PARTIAL_RECV")) {
			holder.tv_issucces.setText("部分收款");
			holder.tv_issucces.setBackgroundColor(0xfffc6d27);
		} else if (mList.get(position).getIsPayment().equals("PMNT_RETURN_CUSTOMER")) {
			holder.tv_issucces.setText("已退款");
			holder.tv_issucces.setBackgroundColor(0xffa0a0a0);
		} else if (mList.get(position).getIsPayment().equals("PMNT_TOTAL_RECV")) {
			holder.tv_issucces.setText("已结清");
			holder.tv_issucces.setBackgroundColor(0xff80d900);
		}

		return convertView;
	}

	class ViewHolder {
		TextView tv_order_no, tv_order_date, tv_shop, tv_channels, tv_sum, tv_isfinish, tv_issucces;
	}

}
