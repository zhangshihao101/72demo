package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Activity.ReturnGoodsActivity;
import com.mts.pos.Activity.SaledorderDetailActivity;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SaledOrderProductAdapter extends BaseAdapter {

	private Context mContext;
	private List<SaledOrderProductInfo> mList;
	private String orderId;
	private int count;

	public SaledOrderProductAdapter(Context mContext, List<SaledOrderProductInfo> mList, String orderId) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.orderId = orderId;
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

		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_saled_orderform_detail, parent, false);
			holder = new ViewHolder();
			holder.btn_return = (Button) convertView.findViewById(R.id.btn_return);
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_style = (TextView) convertView.findViewById(R.id.tv_style);
			holder.tv_color = (TextView) convertView.findViewById(R.id.tv_color);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.tv_return = (TextView) convertView.findViewById(R.id.tv_return);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(mList.get(position).getName());
		holder.tv_style.setText("款号：" + mList.get(position).getStyle());
		holder.tv_color.setText("颜色：" + mList.get(position).getColor());
		holder.tv_size.setText("尺码：" + mList.get(position).getSize());
		holder.tv_price.setText("￥ " + mList.get(position).getPrice());
		holder.tv_count.setText("X" + mList.get(position).getCount());
		count = mList.get(position).getCount() - mList.get(position).getIsReturn();
		holder.tv_return.setText("" + count);

		if (!mList.get(position).getUrl().equals("") && mList.get(position).getUrl() != null) {
			Picasso.with(mContext).load(mList.get(position).getUrl()).into(holder.iv_pic);
		} else {
			holder.iv_pic.setImageResource(R.drawable.product_no_img);
		}
		if (count == 0) {
			holder.btn_return.setBackgroundResource(R.drawable.return_not);
			holder.btn_return.setEnabled(false);
			holder.btn_return.setText("退货");
			holder.btn_return.setTextColor(0x7f070017);
		}

		holder.btn_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ReturnGoodsActivity.class);
				Bundle b = new Bundle();
				b.putString("productId", mList.get(position).getId());
				b.putString("orderId", orderId);
				b.putInt("canReturn", count);
				b.putDouble("returnPrice", mList.get(position).getPrice());
				intent.putExtras(b);
				((SaledorderDetailActivity) mContext).startActivityForResult(intent, 0);
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_name, tv_style, tv_color, tv_size, tv_price, tv_count, tv_return;
		Button btn_return;
		ImageView iv_pic;
	}

}
