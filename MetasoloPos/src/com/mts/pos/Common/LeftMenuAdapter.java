package com.mts.pos.Common;

import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Activity.PayActivity;
import com.mts.pos.listview.LeftMenuInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LeftMenuAdapter extends BaseAdapter {

	Context mContext;
	List<LeftMenuInfo> mlist;

	public interface MenuListener {

		void onActiveViewChanged(View v);
	}

	private MenuListener mListener;

	private int mActivePosition = -1;

	public void setListener(MenuListener listener) {
		mListener = listener;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof LeftMenuInfo ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position) instanceof LeftMenuInfo;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public void setActivePosition(int activePosition) {
		mActivePosition = activePosition;
	}

	public LeftMenuAdapter(Context context, List<LeftMenuInfo> list) {
		this.mContext = context;
		this.mlist = list;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public LeftMenuInfo getItem(int position) {
		return mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_left_menu, parent, false);
			holder = new ViewHolder();
			holder.rl1 = (RelativeLayout) convertView.findViewById(R.id.rl1);
			holder.rl2 = (RelativeLayout) convertView.findViewById(R.id.rl2);
			holder.category_img = (ImageView) convertView.findViewById(R.id.category_img);
			holder.category = (TextView) convertView.findViewById(R.id.category);
			holder.username = (TextView) convertView.findViewById(R.id.tv_username);
			holder.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == 0) {
			holder.rl1.setVisibility(View.VISIBLE);
			holder.rl2.setVisibility(View.GONE);
			// holder.username.setText(text);
		} else {
			holder.rl1.setVisibility(View.GONE);
			holder.rl2.setVisibility(View.VISIBLE);
		}
		holder.category_img.setImageResource(mlist.get(position).getImg());
		holder.category.setText(mlist.get(position).getCategory());
		holder.tv_user.setText(Localxml.search(mContext, "username"));
		holder.username.setText(Localxml.search(mContext, "pname"));
		
		return convertView;
	}

	class ViewHolder {
		ImageView category_img;
		TextView category;
		RelativeLayout rl1;
		RelativeLayout rl2;
		TextView username;
		TextView tv_user;
	}

}
