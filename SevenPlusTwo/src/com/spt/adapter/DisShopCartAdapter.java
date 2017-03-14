package com.spt.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.bean.ShopCartInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisShopCartAdapter extends BaseAdapter {

	private Context mContext;
	private List<ShopCartInfo> mList;
	private Handler mHandler;
	private Map<String, String> params;

	private CompoundButton.OnCheckedChangeListener isCheckedChangeListener;// 选中监听事件

	public void setIsCheckedChangeListener(CompoundButton.OnCheckedChangeListener isCheckedChangeListener) {
		this.isCheckedChangeListener = isCheckedChangeListener;
	}

	public DisShopCartAdapter(Context mContext, List<ShopCartInfo> mList, Map<String, String> params,
			Handler mHandler) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.params = params;
		this.mHandler = mHandler;
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
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dis_shop_cart, null);
			holder = new ViewHolder();
			holder.tv_dis_shop_name = (TextView) convertView.findViewById(R.id.tv_dis_shop_name);
			holder.tv_dis_shop_spec = (TextView) convertView.findViewById(R.id.tv_dis_shop_spec);
			holder.tv_dis_shop_price = (TextView) convertView.findViewById(R.id.tv_dis_shop_price);
			holder.tv_dis_shop_count = (TextView) convertView.findViewById(R.id.tv_dis_shop_count);
			holder.iv_dis_shop = (ImageView) convertView.findViewById(R.id.iv_dis_shop);
			holder.iv_dis_shop_reduce = (ImageView) convertView.findViewById(R.id.iv_dis_shop_reduce);
			holder.iv_dis_shop_plus = (ImageView) convertView.findViewById(R.id.iv_dis_shop_plus);
			holder.cb_dis_shop = (CheckBox) convertView.findViewById(R.id.cb_dis_shop);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.cb_dis_shop.setTag(position);
		// 设置checkbox监听
		holder.cb_dis_shop.setOnCheckedChangeListener(isCheckedChangeListener);
		// 初始化checkbox的状态
		boolean checked = mList.get(position).isChecked();

		if (checked) {
			holder.cb_dis_shop.setChecked(checked);
		} else {
			holder.cb_dis_shop.setChecked(checked);
		}

		holder.tv_dis_shop_name.setText(mList.get(position).getGoodsName());
		holder.tv_dis_shop_spec.setText(mList.get(position).getGoodsSpec());
		holder.tv_dis_shop_price.setText("￥" + mList.get(position).getGoodsPrice());
		holder.tv_dis_shop_count.setText(mList.get(position).getGoodsCount() + "");
		Picasso.with(mContext).load(MyConstant.BASEIMG + mList.get(position).getGoodsImg()).error(R.drawable.test180180)
				.into(holder.iv_dis_shop);

		/**
		 * 对加减设置监听
		 */
		holder.iv_dis_shop_reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = mList.get(position).getGoodsCount();
				String specId = mList.get(position).getGoodsSpecId();
				if (count - 1 == 0) {
					Toast toast = Toast.makeText(mContext, "数量已达到最低，不能再减啦！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					count--;
					VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.UPDATECOUNT + specId + "&quantity=" + count,
							params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							try {
								JSONObject object = new JSONObject(data);
								String error = object.optString("error");
								if (error.equals("0")) {
									mList.get(position).setGoodsCount(mList.get(position).getGoodsCount() - 1);
									mHandler.sendEmptyMessage(1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void OnError(VolleyError volleyError) {
							Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});

		holder.iv_dis_shop_plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = mList.get(position).getGoodsCount();
				count++;
				String specId = mList.get(position).getGoodsSpecId();
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.UPDATECOUNT + specId + "&quantity=" + count,
						params, new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							String msg = object.optString("msg");
							if (error.equals("0")) {
								mList.get(position).setGoodsCount(mList.get(position).getGoodsCount() + 1);
								mHandler.sendEmptyMessage(2);
							} else if (error.equals("1")) {
								Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						Toast.makeText(mContext, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_dis_shop_name, tv_dis_shop_spec, tv_dis_shop_price, tv_dis_shop_count;
		ImageView iv_dis_shop, iv_dis_shop_reduce, iv_dis_shop_plus;
		CheckBox cb_dis_shop;
	}

}
