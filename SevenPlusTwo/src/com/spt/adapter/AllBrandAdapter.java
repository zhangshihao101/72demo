package com.spt.adapter;

import java.util.List;

import com.spt.bean.AllBrandInfo;
import com.spt.sht.R;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AllBrandAdapter extends RecyclerView.Adapter<AllBrandAdapter.ViewHolder>{

	private List<AllBrandInfo> mList;
	private Context mContext;

	public AllBrandAdapter(List<AllBrandInfo> mList, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_brand, viewGroup, false);
		ViewHolder holder = new ViewHolder(view);
		holder.iv_dir_all_brand = (ImageView) view.findViewById(R.id.iv_dir_all_brand);

		return holder;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);

		}

		ImageView iv_dir_all_brand;

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Picasso.with(mContext).load(mList.get(position).getBrandLogo()).error(R.drawable.test)
				.into(holder.iv_dir_all_brand);
	}
	
}
