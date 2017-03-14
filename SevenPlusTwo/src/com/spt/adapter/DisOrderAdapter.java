package com.spt.adapter;

import java.util.List;
import java.util.Map;

import com.spt.bean.DisOrderBottomInfo;
import com.spt.bean.DisOrderTopInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DisOrderAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<DisOrderTopInfo> groups;
	private Map<String, List<DisOrderBottomInfo>> children;

	public DisOrderAdapter(Context mContext, List<DisOrderTopInfo> groups,
			Map<String, List<DisOrderBottomInfo>> children) {
		super();
		this.mContext = mContext;
		this.groups = groups;
		this.children = children;
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String groupId = groups.get(groupPosition).getOrder_id();
		return children.get(groupId).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<DisOrderBottomInfo> childs = children.get(groups.get(groupPosition).getOrder_id());
		return childs.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		GroupViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_disorder_top, null);
			holder = new GroupViewHolder();
			holder.tv_disorder_number = (TextView) convertView.findViewById(R.id.tv_disorder_number);
			holder.tv_disorder_status = (TextView) convertView.findViewById(R.id.tv_disorder_status);
			holder.tv_disorder_time = (TextView) convertView.findViewById(R.id.tv_disorder_time);
			holder.tv_disorder_total = (TextView) convertView.findViewById(R.id.tv_disorder_total);
			holder.tv_disorder_predict = (TextView) convertView.findViewById(R.id.tv_disorder_predict);
			holder.TextView6 = (TextView) convertView.findViewById(R.id.TextView6);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		holder.tv_disorder_number.setText(groups.get(groupPosition).getOrder_sn());
		holder.tv_disorder_status.setText(groups.get(groupPosition).getStatus());
		if (groups.get(groupPosition).getPay_time().equals("0")) {
			holder.tv_disorder_time.setText("未支付");
		} else {
			holder.tv_disorder_time.setText(MyUtil.millisecondsToStr(groups.get(groupPosition).getPay_time()));
		}
		holder.tv_disorder_total.setText(groups.get(groupPosition).getFinal_amount());
		if (groups.get(groupPosition).getAgent_id().equals("0")
				|| groups.get(groupPosition).getStatus().equals("已退款")) {
			holder.TextView6.setVisibility(View.GONE);
			holder.tv_disorder_predict.setVisibility(View.GONE);
		} else {
			holder.TextView6.setVisibility(View.VISIBLE);
			holder.tv_disorder_predict.setVisibility(View.VISIBLE);
			holder.tv_disorder_predict.setText("￥" + groups.get(groupPosition).getProfit());
		}

		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		ChildViewHolder childViewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_disorder_botoom, null);
			childViewHolder = new ChildViewHolder();
			childViewHolder.iv_disorder_img = (ImageView) convertView.findViewById(R.id.iv_disorder_img);
			childViewHolder.tv_disorder_name = (TextView) convertView.findViewById(R.id.tv_disorder_name);
			childViewHolder.tv_disorder_spec = (TextView) convertView.findViewById(R.id.tv_disorder_spec);
			childViewHolder.tv_disorder_disprice = (TextView) convertView.findViewById(R.id.tv_disorder_disprice);
			childViewHolder.tv_disorder_count = (TextView) convertView.findViewById(R.id.tv_disorder_count);
			convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}

		childViewHolder.tv_disorder_name
				.setText(children.get(groups.get(groupPosition).getOrder_id()).get(childPosition).getGoods_name());
		childViewHolder.tv_disorder_spec
				.setText(children.get(groups.get(groupPosition).getOrder_id()).get(childPosition).getSpecification());
		childViewHolder.tv_disorder_disprice
				.setText("￥" + children.get(groups.get(groupPosition).getOrder_id()).get(childPosition).getPrice());
		childViewHolder.tv_disorder_count
				.setText("X" + children.get(groups.get(groupPosition).getOrder_id()).get(childPosition).getQuantity());
		Picasso.with(mContext)
				.load(MyConstant.BASEIMG
						+ children.get(groups.get(groupPosition).getOrder_id()).get(childPosition).getGoods_image())
				.error(R.drawable.test180180).into(childViewHolder.iv_disorder_img);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private static class GroupViewHolder {
		TextView tv_disorder_number, tv_disorder_status, tv_disorder_time, tv_disorder_total, tv_disorder_predict,
				TextView6;
	}

	private static class ChildViewHolder {
		TextView tv_disorder_name, tv_disorder_spec, tv_disorder_disprice, tv_disorder_count;
		ImageView iv_disorder_img;
	}

}
