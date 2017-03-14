package com.spt.adapter;

import java.util.List;
import com.spt.bean.BrandHotTwoInfo;
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
public class BrandHotTwoAdapter extends BaseAdapter {

	private Context mContext;
	private List<BrandHotTwoInfo> mList;
	private int pagePosition;

	public BrandHotTwoAdapter(Context context, List<BrandHotTwoInfo> mList, int pagePosition) {
		this.mContext = context;
		this.mList = mList;
		this.pagePosition = pagePosition;
	}

	@Override
	public int getCount() {

		int size = (mList == null ? 0 : mList.size() - 3 * pagePosition);

		if (size > 3) {
			return 3;
		} else {
			return size > 0 ? size : 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int nowPosition = 3 * pagePosition + position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bran_hot_one, null);
			holder = new ViewHolder();
			holder.iv_brand_hot_two = (ImageView) convertView.findViewById(R.id.iv_brand_hot_one);
			holder.tv_bran_hot_two = (TextView) convertView.findViewById(R.id.tv_brand_hot_one);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Picasso.with(mContext).load(MyConstant.BASEIMG + mList.get(nowPosition).getBrand_sell_two_img_url())
				.error(R.drawable.test180180).into(holder.iv_brand_hot_two);
		holder.tv_bran_hot_two.setText("￥" + mList.get(nowPosition).getBrand_sell_two_price());

		return convertView;
	}

	private static class ViewHolder {
		ImageView iv_brand_hot_two;
		TextView tv_bran_hot_two;
	}

}
