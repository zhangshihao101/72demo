package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.OrderListInfo;
import com.spt.utils.MyUtil;

/**
 * 【订单列表】适配器
 * */
public class OrderListAdapter extends BaseAdapter {
	private Context mContext;
	private List<OrderListInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public OrderListAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<OrderListInfo>();
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
			convertView = mInflater.inflate(R.layout.ordersitem, null);
			OrderListInfo info = mData.get(position);
			TextView tvOrdersName = (TextView) convertView.findViewById(R.id.tv_ordersItem_ordersName);
			TextView tvOrdersPrice = (TextView) convertView.findViewById(R.id.tv_ordersItem_ordersPrice);
			TextView tvOrdersDate = (TextView) convertView.findViewById(R.id.tv_ordersItem_ordersDate);
			TextView tvOrdersState = (TextView) convertView.findViewById(R.id.tv_ordersItem_ordersState);
			TextView tvChanged = (TextView) convertView.findViewById(R.id.tv_ordersItem_ordersPriceChanged);

			String order_sn = info.getOrder_sn();
			String extension = info.getExtension();
			String status = info.getStatus();
			String add_time = info.getAdd_time();
			String final_amount = info.getFinal_amount();
			String is_change = info.getIs_change();
			
			if ("0".equals(is_change)) {
				tvChanged.setVisibility(View.GONE);
			} else if ("1".equals(is_change)) {
				tvChanged.setVisibility(View.VISIBLE);
			}
			if ("normal".equals(extension)) {
				tvOrdersName.setText("订单号：" + order_sn + "(" + "商城" + ")");
			} else {
				tvOrdersName.setText("订单号：" + order_sn + "(" + "团购" + ")");
			}
			tvOrdersPrice.setText("￥" + final_amount);
			tvOrdersDate.setText("下单时间：" + MyUtil.millisecondsToStr(add_time));
			if ("11".equals(status)) {
				tvOrdersState.setText("待付款");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "待付款"));
			} else if ("20".equals(status)) {
				tvOrdersState.setText("待发货");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "待发货"));
			} else if ("30".equals(status)) {
				tvOrdersState.setText("已发货");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "已发货"));
			} else if ("40".equals(status)) {
				tvOrdersState.setText("已完成");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "已完成"));
			} else if ("50".equals(status)) {
				tvOrdersState.setText("已退款");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "已退款"));
			} else if ("0".equals(status)) {
				tvOrdersState.setText("已取消");
				tvOrdersState.setTextColor(MyUtil.stateColor(mContext, "已取消"));
			}

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addOrderListInfo(OrderListInfo orderListInfo) {

		mData.add(orderListInfo);

	}
	
	public void delOrderListInfo(int location) {

		mData.remove(location);
		notifyDataSetChanged();

	}

	public OrderListInfo getOrderListInfo(int i) {
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
