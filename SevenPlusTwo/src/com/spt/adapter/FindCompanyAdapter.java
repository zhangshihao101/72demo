package com.spt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.spt.bean.CompanyInfo;
import com.spt.controler.CircleImageView;
import com.spt.controler.FlowTagLayout;
import com.spt.sht.R;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindCompanyAdapter extends BaseAdapter {

	private Context mContext;
	private List<CompanyInfo> mList;

	public FindCompanyAdapter(Context mContext, List<CompanyInfo> mList) {
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

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_find_company, null);
			holder = new ViewHolder();
			holder.iv_find_company_logo = (CircleImageView) convertView.findViewById(R.id.iv_find_company_logo);
			holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
			holder.tv_company_site = (TextView) convertView.findViewById(R.id.tv_company_site);
			holder.tv_company_sketch = (TextView) convertView.findViewById(R.id.tv_company_sketch);
			holder.ftl_company_lable = (FlowTagLayout) convertView.findViewById(R.id.ftl_company_lable);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_company_name.setText(mList.get(position).getCompanyName());
		holder.tv_company_site.setText(mList.get(position).getCityName());
		String CompanyBrief = mList.get(position).getCompanyBrief();
		if (CompanyBrief != null && !CompanyBrief.equals("null")) {
			holder.tv_company_sketch.setText("企业描述：" + CompanyBrief);
		} else {
			holder.tv_company_sketch.setText("企业描述：");
		}
		Picasso.with(mContext).load(mList.get(position).getLogoUrl()).placeholder(R.drawable.noheader)
				.error(R.drawable.noheader).resize(100, 100).into(holder.iv_find_company_logo);

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int width2 = holder.iv_find_company_logo.getWidth();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_company_sketch.getLayoutParams();
		int margin1 = params.rightMargin;
		int margin2 = params.bottomMargin;

		int width3 = holder.tv_company_site.getWidth();
		LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.tv_company_site.getLayoutParams();
		int margin3 = params2.rightMargin;
		int margin4 = params2.leftMargin;
		int padding = holder.tv_company_name.getPaddingLeft();
		int width4 = width - width2 - width3 - margin1 - margin2 - margin3 - margin4 - padding - 20;
		holder.tv_company_name.setMaxWidth(width4);

		TextPaint tp = holder.tv_company_name.getPaint();
		tp.setFakeBoldText(true);

		CompanyTagAdapter mAdapter = new CompanyTagAdapter(mContext);
		holder.ftl_company_lable.setAdapter(mAdapter);
		List<Object> tagList = new ArrayList<Object>();
		tagList.add(mList.get(position).getRoleTypeId());
		String[] strings = mList.get(position).getBrandNames().split(",");
		for (int i = 0; i < strings.length; i++) {
			if (!"".equals(strings[i])) {
				tagList.add(strings[i]);
			}
		}
		mAdapter.onlyAddAll(tagList);

		return convertView;
	}

	private static class ViewHolder {
		ImageView iv_find_company_logo;
		TextView tv_company_name, tv_company_site, tv_company_sketch;
		FlowTagLayout ftl_company_lable;
	}

}
