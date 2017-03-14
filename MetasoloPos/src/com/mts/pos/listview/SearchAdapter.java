package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import com.mts.pos.listview.ProductAdapter.ViewHolder;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private Context context;
	private List<SearchProductInfo> mList;

	public SearchAdapter(Context context, List<SearchProductInfo> mList) {
		super();
		this.context = context;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (mList.size() != 0) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_search, parent, false);
			holder = new ViewHolder();
			holder.iv_search_product_img = (ImageView) convertView.findViewById(R.id.iv_search_product_img);
			holder.iv_search_product_checked = (ImageView) convertView.findViewById(R.id.iv_search_product_checked);
			holder.tv_search_product_brand = (TextView) convertView.findViewById(R.id.tv_search_product_brand);
			holder.tv_search_product_stylenum = (TextView) convertView.findViewById(R.id.tv_search_product_stylenum);
			holder.tv_search_product_name = (TextView) convertView.findViewById(R.id.tv_search_product_name);
			holder.tv_search_product_color = (TextView) convertView.findViewById(R.id.tv_search_product_color);
			holder.tv_search_product_size = (TextView) convertView.findViewById(R.id.tv_search_product_size);
			holder.tv_search_product_count = (TextView) convertView.findViewById(R.id.tv_search_product_count);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		LruCache lruCache = new LruCache(context);
//		lruCache.clear();
//		Picasso picasso = new Picasso.Builder(context).memoryCache(lruCache).build();

		if (!mList.get(position).getProduct_img().equals("") || mList.get(position).getProduct_img() == null) {
//			picasso.load(mList.get(position).getProduct_img()).resize(140, 140).centerCrop()
//					.into(holder.iv_search_product_img);
			 Picasso.with(context).load(mList.get(position).getProduct_img()).into(holder.iv_search_product_img);
		} else {
			holder.iv_search_product_img.setImageResource(R.drawable.product_no_img);
		}

		holder.tv_search_product_brand.setText(mList.get(position).getBrandName());
		holder.tv_search_product_stylenum.setText(mList.get(position).getModeId());
		holder.tv_search_product_name.setText(mList.get(position).getProductname2());
		holder.tv_search_product_color.setText(mList.get(position).getProductcolor());
		holder.tv_search_product_size.setText(mList.get(position).getProductsize());
		holder.tv_search_product_count.setText(mList.get(position).getProduct_salecount() + "");

		// if (mList.get(position).getFlag()) {
		// viewHolder.iv_search_product_checked.setVisibility(View.VISIBLE);
		// } else {
		// viewHolder.iv_search_product_checked.setVisibility(View.GONE);
		// }

		return convertView;
	}

	class ViewHolder {
		ImageView iv_search_product_img, iv_search_product_checked;
		TextView tv_search_product_brand, tv_search_product_stylenum, tv_search_product_name, tv_search_product_color,
				tv_search_product_size, tv_search_product_count;
	}

}
