package com.mts.pos.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.laiqian.print.model.IPrinterDiscoverySession;
import com.laiqian.print.model.PrintContent;
import com.laiqian.print.model.PrintManager;
import com.laiqian.print.model.PrinterInfo;
import com.mts.pos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SaledAdapter extends BaseAdapter {

	Context mContext;
	List<SaledorderInfo> mlist;

	OnClickListener listener;

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

	public SaledAdapter(Context mContext, List<SaledorderInfo> mlist) {
		super();
		this.mContext = mContext;
		this.mlist = mlist;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return (SaledorderInfo) mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_saled_orderform, parent, false);
			holder = new ViewHolder();
			holder.tv_orderId = (TextView) convertView.findViewById(R.id.tv_order_no);
			holder.tv_clientName = (TextView) convertView.findViewById(R.id.tv_client_name);
			holder.tv_orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
			holder.tv_orderSum = (TextView) convertView.findViewById(R.id.tv_order_sum);
			holder.btn_rePrint = (Button) convertView.findViewById(R.id.btn_reprint);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_orderId.setText("订单编号： " + mlist.get(position).getOrderId());
		holder.tv_clientName.setText("客户： " + mlist.get(position).getClientName());
		holder.tv_orderTime.setText("订单时间： " + mlist.get(position).getOrderTime());
		holder.tv_orderSum.setText("收款金额：￥" + mlist.get(position).getOrderSum());
		
		holder.btn_rePrint.setOnClickListener(listener);
		holder.btn_rePrint.setTag(position);

		return convertView;
	}

	class ViewHolder {
		TextView tv_orderId, tv_clientName, tv_orderTime, tv_orderSum;
		Button btn_rePrint;
	}

}
