package com.spt.adapter;

import java.util.List;
import java.util.Map;

import com.spt.bean.OrderGoodsInfo;
import com.spt.bean.StoreInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DisConfirmOrderStoreAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<StoreInfo> groups;
	private Map<String, List<OrderGoodsInfo>> children;

	public DisConfirmOrderStoreAdapter(Context mContext, List<StoreInfo> groups,
			Map<String, List<OrderGoodsInfo>> children) {
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
		String groupId = groups.get(groupPosition).getSroreId();
		return children.get(groupId).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<OrderGoodsInfo> childs = children.get(groups.get(groupPosition).getSroreId());
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
		GroupViewHolder groupViewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_store_group, null);
			groupViewHolder = new GroupViewHolder();
			groupViewHolder.tv_store_name = (TextView) convertView.findViewById(R.id.tv_store_name);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}

		groupViewHolder.tv_store_name.setText(groups.get(groupPosition).getSotreName());
		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildViewHolder childViewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_store_product, null);
			childViewHolder = new ChildViewHolder();
			childViewHolder.iv_store_goods_img = (ImageView) convertView.findViewById(R.id.iv_store_goods_img);
			childViewHolder.tv_store_goods_name = (TextView) convertView.findViewById(R.id.tv_store_goods_name);
			childViewHolder.tv_store_goods_spec = (TextView) convertView.findViewById(R.id.tv_store_goods_spec);
			childViewHolder.tv_store_goods_price = (TextView) convertView.findViewById(R.id.tv_store_goods_price);
			childViewHolder.tv_store_goods_count = (TextView) convertView.findViewById(R.id.tv_store_goods_count);
			convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}

		childViewHolder.tv_store_goods_name
				.setText(children.get(groups.get(groupPosition).getSroreId()).get(childPosition).getGoods_name());
		childViewHolder.tv_store_goods_spec
				.setText(children.get(groups.get(groupPosition).getSroreId()).get(childPosition).getSpecification());
		childViewHolder.tv_store_goods_price
				.setText(children.get(groups.get(groupPosition).getSroreId()).get(childPosition).getPrice());
		childViewHolder.tv_store_goods_count
				.setText("X" + children.get(groups.get(groupPosition).getSroreId()).get(childPosition).getQuantity());
		Picasso.with(mContext)
				.load(MyConstant.BASEIMG
						+ children.get(groups.get(groupPosition).getSroreId()).get(childPosition).getGoods_image())
				.into(childViewHolder.iv_store_goods_img);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private static class GroupViewHolder {
		TextView tv_store_name;
	}

	private static class ChildViewHolder {
		TextView tv_store_goods_name, tv_store_goods_spec,

				tv_store_goods_price, tv_store_goods_count;
		ImageView iv_store_goods_img;
	}

}
