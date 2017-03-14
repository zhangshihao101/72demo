package com.spt.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.utils.MyTreeElement;

/**
 * <b>自定义树形控件适配器 <br>
 * 
 * <pre>
 * 修改履历
 * ===========================================================
 * 2015/05/14   李瑜峰（七迦二）    商户通    新版作成
 * </pre>
 */
public class MyTreeViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<MyTreeElement> mfilelist;
	private Bitmap mIconCollapse;
	private Bitmap mIconExpand;
	private EtOnClickListener mEtListener;
	private CbOnClickListener mCbListener;

	@SuppressLint("UseSparseArrays")
	public MyTreeViewAdapter(Context context, List<MyTreeElement> data, EtOnClickListener etlistener, CbOnClickListener cblistener) {
		this.mInflater = LayoutInflater.from(context);
		this.mfilelist = data;
		this.mIconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree_ec);
		this.mIconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree_ex);
		this.mEtListener = etlistener;
		this.mCbListener = cblistener;
	}

	@Override
	public int getCount() {
		return mfilelist.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取视图及其控件
		convertView = mInflater.inflate(R.layout.goodstypeitem, null);
		MyTreeElement element = mfilelist.get(position);
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_treeIv);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_treeTv);
		EditText etSort = (EditText) convertView.findViewById(R.id.et_treeEt);
		CheckBox cbChecked = (CheckBox) convertView.findViewById(R.id.cb_treeCb);

		// 添加数据
		int level = element.getLevel();
		ivIcon.setPadding(30 * (level + 1), ivIcon.getPaddingTop(), 0, ivIcon.getPaddingBottom());
		tvTitle.setText(element.getOutlineTitle());
		etSort.setText(element.getSort());
		etSort.setOnClickListener(mEtListener);
		etSort.setTag(element);
		cbChecked.setChecked(element.isChecked());
		cbChecked.setTag(element);
		cbChecked.setOnClickListener(mCbListener);
		if (element.isMhasChild() && (element.isExpanded() == false)) {
			ivIcon.setImageBitmap(mIconCollapse);
		} else if (element.isMhasChild() && (element.isExpanded() == true)) {
			ivIcon.setImageBitmap(mIconExpand);
		} else if (!element.isMhasChild()) {
			ivIcon.setImageBitmap(mIconCollapse);
			ivIcon.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}
	
	public static abstract class EtOnClickListener implements OnClickListener {

		public abstract void myEtOnClickListener(MyTreeElement element, View v);

		@Override
		public void onClick(View arg0) {
			myEtOnClickListener((MyTreeElement) arg0.getTag(), arg0);
		}

	}
	
	public static abstract class CbOnClickListener implements OnClickListener {

		public abstract void cbOnClickListener(MyTreeElement element, View v);

		@Override
		public void onClick(View arg0) {
			cbOnClickListener((MyTreeElement) arg0.getTag(), arg0);
		}

	}

}
