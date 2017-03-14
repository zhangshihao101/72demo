package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideAdapter extends BaseAdapter {

	private List<GuideInfo> guideList;
	private Context context;

	public GuideAdapter(List<GuideInfo> guideList, Context context) {
		super();
		this.guideList = guideList;
		this.context = context;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (guideList != null) {
			ret = guideList.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return guideList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View ret = null;
		if (convertView != null) {
			ret = convertView;
		} else {
			ret = LayoutInflater.from(context).inflate(R.layout.item_guide, null);
		}

		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.img_guide = (ImageView) ret.findViewById(R.id.img_guide);
			viewHolder.tv_guide_content = (TextView) ret.findViewById(R.id.tv_guide_content);
			viewHolder.tv_guide_count = (TextView) ret.findViewById(R.id.tv_guide_count);
			viewHolder.tv_guide_price = (TextView) ret.findViewById(R.id.tv_guide_price);
			ret.setTag(viewHolder);
		}

		viewHolder.tv_guide_content.setText(guideList.get(position).getProductName());
		String price = guideList.get(position).getProductListPrice();
		if (price.equals("") || price == null) {
			viewHolder.tv_guide_price.setText("暂无价格");
		}
		viewHolder.tv_guide_price.setText("￥" + price + ".00");
		viewHolder.tv_guide_count.setText("数量：" + guideList.get(position).getTotalAvailableQuantity());
		String imgUrl = guideList.get(position).getSmallImageUrl();
		Picasso.with(context).load(imgUrl).error(R.drawable.product_no_img).into(viewHolder.img_guide);
		return ret;
	}

	private static class ViewHolder {
		ImageView img_guide;
		TextView tv_guide_content, tv_guide_price, tv_guide_count;
	}

}
