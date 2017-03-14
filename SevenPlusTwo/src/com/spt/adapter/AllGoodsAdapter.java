package com.spt.adapter;

import java.util.List;

import com.spt.bean.AllGoodsInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllGoodsAdapter extends BaseAdapter {

	private List<AllGoodsInfo> mList;
	private Context mContext;

	private OnClickListener shareClickListener;

	public void setShareClickListener(OnClickListener shareClickListener) {
		this.shareClickListener = shareClickListener;
	}

	public AllGoodsAdapter(List<AllGoodsInfo> mList, Context mContext) {
		super();
		this.mList = mList;
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(mContext).inflate(R.layout.item_all_goods, null);
		}

		ViewHolder holder = (ViewHolder) ret.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.iv_all_goods = (ImageView) ret.findViewById(R.id.iv_all_goods);
			holder.iv_all_goods_share = (ImageView) ret.findViewById(R.id.iv_all_goods_share);
			holder.tv_all_goods_name = (TextView) ret.findViewById(R.id.tv_all_goods_name);
			holder.tv_all_goods_stock = (TextView) ret.findViewById(R.id.tv_all_goods_stock);
			holder.tv_all_goods_disprice = (TextView) ret.findViewById(R.id.tv_all_goods_disprice);
			holder.tv_all_goods_market_price = (TextView) ret.findViewById(R.id.tv_all_goods_market_price);
			holder.tv_all_goods_shop_price = (TextView) ret.findViewById(R.id.tv_all_goods_shop_price);
			holder.tv_begin_time = (TextView) ret.findViewById(R.id.tv_begin_time);
			holder.tv_end_time = (TextView) ret.findViewById(R.id.tv_end_time);
			holder.tv_is_start = (TextView) ret.findViewById(R.id.tv_is_start);
			ret.setTag(holder);
		} else {
			holder = (ViewHolder) ret.getTag();
		}

		holder.tv_all_goods_name.setText(mList.get(position).getGoodsName());
		holder.tv_all_goods_stock.setText(mList.get(position).getGoodsStock());

		String min_dis_price = mList.get(position).getGoodsMinDisPrice();
		String max_dis_price = mList.get(position).getGoodsMaxDisPrice();
		if (min_dis_price.equals(max_dis_price) || min_dis_price == max_dis_price) {
			holder.tv_all_goods_disprice.setText("￥" + min_dis_price);
		} else {
			holder.tv_all_goods_disprice.setText("￥" + min_dis_price + "-" + "￥" + max_dis_price);
		}

		String min_mar_price = mList.get(position).getGoodsMinMarPrice();
		String max_mar_price = mList.get(position).getGoodsMaxMarPrice();
		if (min_mar_price.equals(max_mar_price) || min_mar_price == max_mar_price) {
			holder.tv_all_goods_market_price.setText("￥" + min_mar_price);
		} else {
			holder.tv_all_goods_market_price.setText("￥" + min_mar_price + "-" + "￥" + max_mar_price);
		}
		holder.tv_all_goods_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

		String min_shop_price = mList.get(position).getGoodsMinShopPrice();
		String max_shop_price = mList.get(position).getGoodsMaxShopPrice();
		if (min_shop_price.equals(max_shop_price) || max_shop_price == min_shop_price) {
			holder.tv_all_goods_shop_price.setText("￥" + min_shop_price);
		} else {
			holder.tv_all_goods_shop_price.setText("￥" + min_shop_price + "-" + "￥" + max_shop_price);
		}
		holder.tv_all_goods_shop_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		holder.tv_begin_time.setText(MyUtil.millisecondsToDate(mList.get(position).getDisStartTime()));
		holder.tv_end_time.setText(MyUtil.millisecondsToDate(mList.get(position).getDisEndTime()));
		String type = mList.get(position).getDisTimeType();
		if (type.equals("1")) {
			holder.tv_is_start.setText("未开始");
			holder.tv_is_start.setTextColor(Color.parseColor("#8CB92A"));
		} else if (type.equals("2")) {
			holder.tv_is_start.setText("已开始");
			holder.tv_is_start.setTextColor(Color.parseColor("#FF6634"));
		}

		Picasso.with(mContext).load(MyConstant.BASEIMG + mList.get(position).getGoodsImg()).error(R.drawable.test180180)
				.into(holder.iv_all_goods);
		holder.iv_all_goods_share.setTag(position);
		holder.iv_all_goods_share.setOnClickListener(shareClickListener);

		return ret;
	}

	private static class ViewHolder {
		ImageView iv_all_goods, iv_all_goods_share;
		TextView tv_all_goods_name, tv_all_goods_stock, tv_all_goods_disprice, tv_all_goods_market_price,
				tv_all_goods_shop_price, tv_begin_time, tv_end_time, tv_is_start;
	}

}
