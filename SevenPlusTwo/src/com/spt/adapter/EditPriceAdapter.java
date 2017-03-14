package com.spt.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spt.bean.EditPriceInfo;
import com.spt.sht.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class EditPriceAdapter extends BaseAdapter {

	private Context mContext;
	private List<EditPriceInfo> mList;
	private Map<Integer, String> map = new HashMap<>();

	public EditPriceAdapter(Context mContext, List<EditPriceInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edit_price, null);
			holder = new ViewHolder();
			holder.tv_edit_price_color = (TextView) convertView.findViewById(R.id.tv_edit_price_color);
			holder.tv_edit_price_size = (TextView) convertView.findViewById(R.id.tv_edit_price_size);
			holder.tv_edit_dis_price = (TextView) convertView.findViewById(R.id.tv_edit_dis_price);
			holder.tv_edit_price_predict = (TextView) convertView.findViewById(R.id.tv_edit_price_predict);
			holder.et_edit_price = (EditText) convertView.findViewById(R.id.et_edit_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_edit_price_color.setText(mList.get(position).getColor());
		holder.tv_edit_price_size.setText(mList.get(position).getSize());
		holder.tv_edit_dis_price.setText("￥" + mList.get(position).getDisPrice());

		SharedPreferences spHome = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		String isEdit = spHome.getString("isEdit", "");
		if (isEdit.equals("1")) {
			holder.et_edit_price.setVisibility(View.VISIBLE);
			// if (mList.get(position).getSalePrice().equals("null") ||
			// "".equals(mList.get(position).getSalePrice())) {
			// holder.et_edit_price.setText("" + 0.0);
			// } else {
			// holder.et_edit_price.setText(mList.get(position).getSalePrice());
			// }
		} else if (isEdit.equals("2")) {
			holder.et_edit_price.setVisibility(View.VISIBLE);
			holder.et_edit_price.setBackground(null);
			holder.et_edit_price.setText(mList.get(position).getSalePrice());
			holder.et_edit_price.setFocusable(false);
			holder.tv_edit_price_predict.setText("￥" + String.format("%.2f",
					Double.parseDouble((mList.get(position).getSalePrice())) - mList.get(position).getDisPrice()));
			for (int i = 0; i < mList.size(); i++) {
				map.put(i, mList.get(i).getSalePrice());
			}
		}
		holder.et_edit_price.setTag(position);

		class MyTextWatcher implements TextWatcher {

			private ViewHolder mHolder;

			public MyTextWatcher(ViewHolder mHolder) {
				super();
				this.mHolder = mHolder;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int position1 = (int) mHolder.et_edit_price.getTag();
				if (s != null && !"".equals(s.toString())) {
					double myPrice = Double.parseDouble(s.toString());
					if ((myPrice - mList.get(position1).getDisPrice()) < 0) {
						map.remove(position1);
						holder.tv_edit_price_predict
								.setText("￥" + String.format("%.2f", myPrice - mList.get(position1).getDisPrice()));
					} else {
						holder.tv_edit_price_predict
								.setText("￥" + String.format("%.2f", myPrice - mList.get(position1).getDisPrice()));
						map.put(position1, s.toString());
					}
				} else {
					holder.tv_edit_price_predict.setText("￥0.0");
					map.remove(position1);
				}
			}

		}

		holder.et_edit_price.addTextChangedListener(new MyTextWatcher(holder));
		EventBus.getDefault().post(map);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_edit_price_color, tv_edit_price_size, tv_edit_dis_price, tv_edit_price_predict;
		EditText et_edit_price;
	}

}
