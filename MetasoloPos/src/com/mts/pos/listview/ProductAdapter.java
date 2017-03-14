package com.mts.pos.listview;

import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Activity.ChangeNumberActivity;
import com.mts.pos.Activity.ChangeNumberPromoActivity;
import com.mts.pos.Activity.DeleteProductItemActivity;
import com.mts.pos.Activity.PayActivity;
import com.mts.pos.Common.SomeMethod;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductAdapter extends BaseAdapter {

	Context mContext;
	List<ProductInfo> mlist;

	public ProductAdapter(Context mContext, List<ProductInfo> mlist) {
		super();
		this.mContext = mContext;
		this.mlist = mlist;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return (ProductInfo) mlist.get(position);
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
			convertView = ln.inflate(R.layout.item_product, parent, false);
			holder = new ViewHolder();
			holder.btn_del = (Button) convertView.findViewById(R.id.btn_del);
			holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
			holder.iv_cut = (ImageView) convertView.findViewById(R.id.iv_cut);
			holder.iv_product = (ImageView) convertView.findViewById(R.id.iv_product);
			holder.tv_productname = (TextView) convertView.findViewById(R.id.tv_productname);
			holder.tv_color = (TextView) convertView.findViewById(R.id.tv_color);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
			holder.tv_present = (TextView) convertView.findViewById(R.id.tv_present);
			holder.tv_original = (TextView) convertView.findViewById(R.id.tv_original);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 减少数量
		holder.iv_cut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((mlist.get(position).getProduct_count() - 1) < 1) {
					Toast.makeText(mContext, "商品个数不能少于1", Toast.LENGTH_SHORT).show();
				} else {
					mlist.get(position).setProduct_count((mlist.get(position).getProduct_count() - 1));
					mlist.get(position).setTotal(Double.valueOf((Double.valueOf(mlist.get(position).getPresent_cost())
							* Double.valueOf(mlist.get(position).getProduct_count()))));
					PayActivity.adapter.notifyDataSetChanged();
					PayActivity.total = 0;
					PayActivity.total_coast = 0.00;
					for (int i = 0; i < PayActivity.productData.size(); i++) {
						PayActivity.total += PayActivity.productData.get(i).getProduct_count();
						PayActivity.total_coast += PayActivity.productData.get(i).getPresent_cost()
								* PayActivity.productData.get(i).getProduct_count();
					}
					PayActivity.tv_total.setText("总计：" + PayActivity.total_coast);
					PayActivity.tv_account.setText("结    算（" + PayActivity.total + "）");
					// PayActivity.total_money
					// .setText("总计：￥ " +
					// String.valueOf(SomeMethod.getCommaDouble(PayActivity.total)));
				}
				// MobclickAgent.onEvent(mContext,
				// "ProductTallyFragment_item_cut");
			}
		});
		// 增加数量
		holder.iv_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mlist.get(position).getProduct_count() == 999) {
					Toast.makeText(mContext, "商品个数不能大于999", Toast.LENGTH_SHORT).show();
				} else if (mlist.get(position).getProduct_count() > mlist.get(position).getProduct_salecount() - 1) {
					Log.e("SEE", "最大库存在加减数量==" + mlist.get(position).getProduct_salecount());
					Toast.makeText(mContext, "商品个数不能大于库存", Toast.LENGTH_SHORT).show();
				} else {
					mlist.get(position).setProduct_count((mlist.get(position).getProduct_count() + 1));
					mlist.get(position).setTotal(Double.valueOf((Double.valueOf(mlist.get(position).getPresent_cost())
							* Double.valueOf(mlist.get(position).getProduct_count()))));
					PayActivity.adapter.notifyDataSetChanged();
					PayActivity.total = 0;
					PayActivity.total_coast = 0.00;
					for (int i = 0; i < PayActivity.productData.size(); i++) {
						PayActivity.total += PayActivity.productData.get(i).getProduct_count();
						PayActivity.total_coast += PayActivity.productData.get(i).getPresent_cost()
								* PayActivity.productData.get(i).getProduct_count();
					}
					PayActivity.tv_total.setText("总计：" + PayActivity.total_coast);
					PayActivity.tv_account.setText("结    算（" + PayActivity.total + "）");
					// PayActivity.total_money
					// .setText("总计：￥ " +
					// String.valueOf(SomeMethod.getCommaDouble(PayActivity.total)));
					// MobclickAgent.onEvent(mContext,
					// "ProductTallyFragment_item_add");
				}
			}
		});

		// LruCache lruCache = new LruCache(mContext);
		// lruCache.clear();
		// Picasso picasso = new
		// Picasso.Builder(mContext).memoryCache(lruCache).build();
		//
		if (!mlist.get(position).getProduct_img().equals("") && mlist.get(position).getProduct_img() != null) {

			// picasso.load(mlist.get(position).getProduct_img()).resize(140,
			// 140).centerCrop()
			// .error(R.drawable.product_no_img).into(holder.iv_product);
			Picasso.with(mContext).load(mlist.get(position).getProduct_img()).into(holder.iv_product);
		} else {
			holder.iv_product.setImageResource(R.drawable.product_no_img);
		}

		holder.tv_number.setText(String.valueOf(mlist.get(position).getProduct_count()));
		holder.tv_present.setText(String.valueOf(SomeMethod.getCommaDouble(mlist.get(position).getPresent_cost())));
		holder.tv_productname.setText(String.valueOf(mlist.get(position).getProduct_name2()));

		holder.tv_original
				.setText("￥ " + String.valueOf(SomeMethod.getCommaDouble(mlist.get(position).getOriginal_cost())));
		// holder.total.setText(String.valueOf(SomeMethod.getCommaDouble(mlist.get(position).getTotal())));
		holder.tv_color.setText("颜色：" + String.valueOf(mlist.get(position).getProductcolor()));
		holder.tv_size.setText("尺码：" + String.valueOf(mlist.get(position).getProductsize()));
		holder.btn_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DeleteProductItemActivity.class);
				intent.putExtra("position", String.valueOf(position));
				intent.putExtra("which", "1");
				((PayActivity) mContext).startActivityForResult(intent, 3);
				// MobclickAgent.onEvent(mContext,
				// "ProductTallyFragment_item_delete");
			}
		});
		holder.tv_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChangeNumberActivity.class);
				intent.putExtra("type", "number");
				intent.putExtra("position", String.valueOf(position));
				((PayActivity) mContext).startActivity(intent);
				// MobclickAgent.onEvent(mContext,
				// "ProductTallyFragment_item_number");
			}
		});
		holder.tv_present.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChangeNumberPromoActivity.class);
				// intent.putExtra("type", "cost");
				intent.putExtra("position", String.valueOf(position));
				((PayActivity) mContext).startActivity(intent);
				// MobclickAgent.onEvent(mContext,
				// "ProductTallyFragment_item_present_cost");
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_productname, tv_color, tv_size, tv_number, tv_present, tv_original;
		Button btn_del;
		ImageView iv_product, iv_cut, iv_add;
	}

}
