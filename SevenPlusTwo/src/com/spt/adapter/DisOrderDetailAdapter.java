package com.spt.adapter;

import java.util.List;

import com.spt.bean.OrderGoodsInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DisOrderDetailAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderGoodsInfo> mList;

	public DisOrderDetailAdapter(Context mContext, List<OrderGoodsInfo> mList) {
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
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_store_product, null);
			holder = new ViewHolder();
			holder.iv_store_goods_img = (ImageView) convertView.findViewById(R.id.iv_store_goods_img);
			holder.tv_store_goods_name = (TextView) convertView.findViewById(R.id.tv_store_goods_name);
			holder.tv_store_goods_spec = (TextView) convertView.findViewById(R.id.tv_store_goods_spec);
			holder.tv_store_goods_price = (TextView) convertView.findViewById(R.id.tv_store_goods_price);
			holder.tv_store_goods_count = (TextView) convertView.findViewById(R.id.tv_store_goods_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_store_goods_name.setText(mList.get(position).getGoods_name());
		holder.tv_store_goods_spec.setText(mList.get(position).getSpecification());
		holder.tv_store_goods_price.setText("￥" + mList.get(position).getPrice());
		holder.tv_store_goods_count.setText("X" + mList.get(position).getQuantity());
		Picasso.with(mContext).load(MyConstant.BASEIMG + mList.get(position).getGoods_image())
				.error(R.drawable.test180180).into(holder.iv_store_goods_img);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_store_goods_name, tv_store_goods_spec,

				tv_store_goods_price, tv_store_goods_count;
		ImageView iv_store_goods_img;
	}

}
