package com.spt.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.bean.LogInfo;
import com.spt.utils.MyUtil;

/**
 * 【操作信息】适配器
 * */
public class LogAdapter extends BaseAdapter {
	private Context mContext;
	private List<LogInfo> mData;
	private LayoutInflater mInflater;
	private HashMap<Integer, View> mMap;

	@SuppressLint("UseSparseArrays")
	public LogAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mMap = new HashMap<Integer, View>();
		this.mData = new ArrayList<LogInfo>();
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
			convertView = mInflater.inflate(R.layout.operationlogitem, null);
			LogInfo info = mData.get(position);
			TextView tvLogContent = (TextView) convertView.findViewById(R.id.tv_logItem_content);
			TextView tvLogTime = (TextView) convertView.findViewById(R.id.tv_logItem_time);
			TextView tvLogOperator = (TextView) convertView.findViewById(R.id.tv_logItem_operator);

			String log_time = info.getLog_time();
			String operator = info.getOperator();
			String remark = info.getRemark();
			String order_status = info.getOrder_status();
			String changed_status = info.getChanged_status();
			
			CharSequence csOrder_status = Html.fromHtml(order_status);
			CharSequence csChanged_status = Html.fromHtml(changed_status);
			
			StringBuilder sb = new StringBuilder();
			sb.append("将订单状态从");
			if (MyUtil.isContainHTML(order_status)) {
				sb.append(Html.fromHtml(order_status));
			} else {
				sb.append(order_status);
			}
			sb.append("改变为");
			if (MyUtil.isContainHTML(changed_status)) {
				sb.append(Html.fromHtml(changed_status));
			} else {
				sb.append(changed_status);
			}
			if (!"".equals(remark)) {
				sb.append(" 原因：").append(remark);
			}
			String contentStr = sb.toString();
			SpannableString style = new SpannableString(contentStr);
			style = MyUtil.changePartOfStringColor(mContext, style, String.valueOf(csOrder_status));
			style = MyUtil.changePartOfStringColor(mContext, style, String.valueOf(csChanged_status));

			tvLogContent.setText(style);
			tvLogTime.setText(MyUtil.millisecondsToStr(log_time));
			tvLogOperator.setText(operator);

			mMap.put(position, convertView);

		}
		return convertView;
	}

	public void addLogInfo(LogInfo logInfo) {

		mData.add(logInfo);

	}

	public LogInfo getLogInfo(int i) {
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
