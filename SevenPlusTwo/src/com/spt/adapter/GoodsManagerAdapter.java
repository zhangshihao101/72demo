package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.GoodsInfo;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyUtil;

/**
 * 【商品管理】适配器
 * */
public class GoodsManagerAdapter extends BaseAdapter {
	private Context mContext;
	private List<GoodsInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public GoodsManagerAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<GoodsInfo>();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (mMap.containsKey(position)) {
			convertView = mMap.get(position);
		} else {
			convertView = mInflater.inflate(R.layout.goodsmanageritem, null);
			GoodsInfo goods = mData.get(position);
			TextView tvGoodName = (TextView) convertView.findViewById(R.id.tv_goodsmanager_goodName);
			TextView tvGoodBrand = (TextView) convertView.findViewById(R.id.tv_goodsmanager_goodBrand);
			TextView tvGoodType = (TextView) convertView.findViewById(R.id.tv_goodsmanager_goodType);
			TextView tvGoodBelong = (TextView) convertView.findViewById(R.id.tv_goodsmanager_goodBelong);
			TextView tvNoSale = (TextView) convertView.findViewById(R.id.tv_goodsmanager_noSale);
			TextView tvGoodStock = (TextView) convertView.findViewById(R.id.tv_goodsmanager_goodStock);
			TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_goodsmanager_price);
			ImageView ivImg = (ImageView) convertView.findViewById(R.id.iv_goodsmanager_img);

			int width = goods.getWidth();
			String url = MyUtil.getImageURL(goods.getDefault_image(), String.valueOf(width / 4), "300");

			// String url = MyUtil.getImageURL(goods.getDefault_image(), "180",
			// "250");

			AsynImageLoader asynImageLoader = new AsynImageLoader();
			asynImageLoader.showImageAsyn(ivImg, url, R.drawable.test180260);
			tvGoodName.setText(goods.getGoods_name());
			tvGoodBrand.setText(goods.getBrand());
			tvGoodType.setText(goods.getCate_name());
			tvGoodBelong.setText(goods.getExt_activity_type_name());
			String closed = goods.getClosed();
			if ("0".equals(closed)) {
				tvNoSale.setText("未禁售");
			} else if ("1".equals(closed)) {
				tvNoSale.setText("禁售");
			}
			tvGoodStock.setText(goods.getStock() + "件");
			tvPrice.setText("￥" + goods.getPrice());
			tvPrice.setTextColor(Color.rgb(255, 134, 23));

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addGoodsInfo(GoodsInfo goodsInfo) {

		mData.add(goodsInfo);

	}

	public GoodsInfo getGoodsInfo(int i) {
		if (i < 0 || i > mData.size() - 1) {
			return null;
		}
		return mData.get(i);
	}

	public void clear() {
		mData.clear();
		mMap.clear();
	}
}
