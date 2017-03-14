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

public class RecommendAdapter extends BaseAdapter {

	private Context context;
	private List<RecommendInfo> recommendList;

	public RecommendAdapter(Context context, List<RecommendInfo> recommendList) {
		super();
		this.context = context;
		this.recommendList = recommendList;
	}

	@Override
	public int getCount() {
		return recommendList.size();
	}

	@Override
	public Object getItem(int position) {
		return recommendList.get(position);
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
			ret = LayoutInflater.from(context).inflate(R.layout.item_detail_recommend, null);
		}

		ViewHolder viewHolder = (ViewHolder) ret.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.iv_detail_recommend = (ImageView) ret.findViewById(R.id.iv_detail_recommend);
			viewHolder.tv_detail_recommendNmae = (TextView) ret.findViewById(R.id.tv_detail_recommendNmae);
			viewHolder.tv_detail_recommendPrice = (TextView) ret.findViewById(R.id.tv_detail_recommendPrice);
			ret.setTag(viewHolder);
		}

		viewHolder.tv_detail_recommendNmae.setText(recommendList.get(position).getRecommendName());
		Picasso.with(context).load(recommendList.get(position).getImgUrl()).into(viewHolder.iv_detail_recommend);
		viewHolder.tv_detail_recommendPrice.setText("￥1,208.00");

		return ret;
	}

	private static class ViewHolder {
		ImageView iv_detail_recommend;
		TextView tv_detail_recommendNmae, tv_detail_recommendPrice;
	}

}
