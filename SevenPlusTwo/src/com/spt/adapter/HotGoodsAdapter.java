package com.spt.adapter;

import java.util.List;

import com.spt.bean.DirHotGoodsInfo;
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

@SuppressLint("InflateParams")
public class HotGoodsAdapter extends BaseAdapter {

	private List<DirHotGoodsInfo> mList;
	private Context mContext;

	public HotGoodsAdapter(List<DirHotGoodsInfo> mList, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (mList != null) {
			ret = mList.size();
		}
		return ret;
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
		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(mContext).inflate(R.layout.item_hot_goods, null);
		}

		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.iv_dir_hot_goods = (ImageView) ret.findViewById(R.id.iv_dir_hot_goods);
			viewHolder.tv_dir_hot_goods = (TextView) ret.findViewById(R.id.tv_dir_hot_goods);
			ret.setTag(viewHolder);
		}

		viewHolder.tv_dir_hot_goods.setText("￥" + mList.get(position).getHot_goods_price());
		Picasso.with(mContext).load(MyConstant.BASEIMG + mList.get(position).getHot_goods_img_url()).error(R.drawable.test)
				.into(viewHolder.iv_dir_hot_goods);
		return ret;
	}

	private static class ViewHolder {
		ImageView iv_dir_hot_goods;
		TextView tv_dir_hot_goods;
	}

}
