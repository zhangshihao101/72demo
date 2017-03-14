package com.spt.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.spt.adapter.OrderAdapter.ViewHolder;
import com.spt.bean.OrderDetailInfo;
import com.spt.sht.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderDetailAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderDetailInfo> mList;

	DecimalFormat df;

	public OrderDetailAdapter(Context mContext, List<OrderDetailInfo> mList) {
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
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_order_detail, parent, false);
			holder = new ViewHolder();

			holder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
			holder.tv_style = (TextView) convertView.findViewById(R.id.tv_style);
			holder.tv_color = (TextView) convertView.findViewById(R.id.tv_color);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.tv_brand = (TextView) convertView.findViewById(R.id.tv_brand);
			holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.tv_sended = (TextView) convertView.findViewById(R.id.tv_sended);
			holder.tv_nosend = (TextView) convertView.findViewById(R.id.tv_nosend);
			holder.tv_return = (TextView) convertView.findViewById(R.id.tv_return);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		df = new DecimalFormat("0.00");

		holder.tv_product_name.setText("品名："
				+ (mList.get(position).getProductName().equals("null") ? "未填写" : mList.get(position).getProductName()));
		holder.tv_style.setText("款号：" + mList.get(position).getStyleNo());
		holder.tv_color.setText("颜色：" + mList.get(position).getProductColor());
		holder.tv_size.setText("尺码：" + mList.get(position).getProductSize());
		holder.tv_brand.setText("品牌：" + mList.get(position).getProductBrand());
		holder.tv_price.setText("单价：￥" + df.format(mList.get(position).getProductPrice()));
		holder.tv_count.setText("订购数量：" + mList.get(position).getProductCount() + "件");
		holder.tv_sended.setText("已发货：" + mList.get(position).getSendedCount());
		holder.tv_nosend.setText("未发货：" + mList.get(position).getSendingCount());
		holder.tv_return.setText("已退货：" + mList.get(position).getReturnedCount());

		return convertView;
	}

	class ViewHolder {
		TextView tv_product_name, tv_style, tv_color, tv_size, tv_brand, tv_price, tv_count, tv_sended, tv_nosend,
				tv_return;
	}
}
