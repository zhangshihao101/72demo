package com.spt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.spt.bean.ConSynInfo;
import com.spt.controler.FlowTagLayout;
import com.spt.sht.R;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConSynSearchAdapter extends BaseAdapter {

	private static final int TYPE_COM = 0;
	private static final int TYPE_PER = 1;
	private Context mContext;
	private List<ConSynInfo> mList;

	public ConSynSearchAdapter(Context mContext, List<ConSynInfo> mList) {
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

	@Override
	public int getItemViewType(int position) {
		if (mList.get(position).getResultType().equals("group")) {
			return TYPE_COM;
		} else if (mList.get(position).getResultType().equals("people")) {
			return TYPE_PER;
		} else {
			return super.getItemViewType(position);
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder1 holder1 = null;
		ViewHolder2 holder2 = null;
		if (convertView == null) {
			switch (type) {
			case TYPE_COM:
				holder1 = new ViewHolder1();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_con_syn_company, null);
				holder1.iv_con_syn_com_logo = (ImageView) convertView.findViewById(R.id.iv_con_syn_com_logo);
				holder1.tv_con_syn_com_name = (TextView) convertView.findViewById(R.id.tv_con_syn_com_name);
				holder1.tv_con_syn_com_site = (TextView) convertView.findViewById(R.id.tv_con_syn_com_site);
				holder1.tv_con_syn_com_sketch = (TextView) convertView.findViewById(R.id.tv_con_syn_com_sketch);
				holder1.ftl_con_syn_com_lable = (FlowTagLayout) convertView.findViewById(R.id.ftl_con_syn_com_lable);
				convertView.setTag(holder1);
				break;
			case TYPE_PER:
				holder2 = new ViewHolder2();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_con_syn_person, null);
				holder2.iv_con_syn_per_logo = (ImageView) convertView.findViewById(R.id.iv_con_syn_per_logo);
				holder2.iv_con_syn_per_type = (ImageView) convertView.findViewById(R.id.iv_con_syn_per_type);
				holder2.tv_con_syn_per_name = (TextView) convertView.findViewById(R.id.tv_con_syn_per_name);
				holder2.tv_con_syn_per_city = (TextView) convertView.findViewById(R.id.tv_con_syn_per_city);
				convertView.setTag(holder2);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case TYPE_COM:
				holder1 = (ViewHolder1) convertView.getTag();
				break;
			case TYPE_PER:
				holder2 = (ViewHolder2) convertView.getTag();
				break;
			default:
				break;
			}
		}

		switch (type) {
		case TYPE_COM:
			holder1.tv_con_syn_com_name.setText(mList.get(position).getCompanyName());
			holder1.tv_con_syn_com_site.setText(mList.get(position).getCityName());
			String CompanyBrief = mList.get(position).getCompanyBrief();
			if (CompanyBrief != null && !CompanyBrief.equals("null")) {
				holder1.tv_con_syn_com_sketch.setText("企业描述：" + CompanyBrief);
			} else {
				holder1.tv_con_syn_com_sketch.setText("企业描述：");
			}
			Picasso.with(mContext).load(mList.get(position).getLogoUrl()).placeholder(R.drawable.noheader)
					.error(R.drawable.noheader).resize(100, 100).into(holder1.iv_con_syn_com_logo);
			TextPaint tp = holder1.tv_con_syn_com_name.getPaint();
			tp.setFakeBoldText(true);

			CompanyTagAdapter mAdapter = new CompanyTagAdapter(mContext);
			holder1.ftl_con_syn_com_lable.setAdapter(mAdapter);
			List<Object> tagList = new ArrayList<Object>();
			tagList.add(mList.get(position).getRoleTypeId());
			String[] strings = mList.get(position).getBrandNames().split(",");
			for (int i = 0; i < strings.length; i++) {
				if (!"".equals(strings[i])) {
					tagList.add(strings[i]);
				}
			}
			mAdapter.onlyAddAll(tagList);
			break;
		case TYPE_PER:
			Picasso.with(mContext).load(mList.get(position).getLogoPath()).placeholder(R.drawable.noheader)
					.error(R.drawable.noheader).resize(100, 100).into(holder2.iv_con_syn_per_logo);
			holder2.tv_con_syn_per_name.setText(mList.get(position).getConnectionName());
			holder2.tv_con_syn_per_city.setText(mList.get(position).getCityName());
			if (mList.get(position).getConnectionRole().equals("Leader")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag1);
			} else if (mList.get(position).getConnectionRole().equals("Club")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag5);
			} else if (mList.get(position).getConnectionRole().equals("MassOrganizations")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag4);
			} else if (mList.get(position).getConnectionRole().equals("WebShopOwner")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag3);
			} else if (mList.get(position).getConnectionRole().equals("StoreOwner")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag2);
			} else if (mList.get(position).getConnectionRole().equals("Other")) {
				holder2.iv_con_syn_per_type.setImageResource(R.drawable.new_peer_tag6);
			} else {
				holder2.iv_con_syn_per_type.setVisibility(View.INVISIBLE);
			}
			break;
		default:
			break;
		}

		return convertView;
	}

	private static class ViewHolder1 {
		TextView tv_con_syn_com_name, tv_con_syn_com_site, tv_con_syn_com_sketch;
		ImageView iv_con_syn_com_logo;
		FlowTagLayout ftl_con_syn_com_lable;
	}

	private static class ViewHolder2 {
		TextView tv_con_syn_per_name, tv_con_syn_per_city;
		ImageView iv_con_syn_per_logo, iv_con_syn_per_type;
	}

}
