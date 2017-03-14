package com.spt.adapter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.ComMemberInfo;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ComMemberAdapter extends BaseAdapter {

	private Context mContext;
	private List<ComMemberInfo> mList;
	private String userName, accessToken;

	public ComMemberAdapter(Context mContext, List<ComMemberInfo> mList, String userName, String accessToken) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.userName = userName;
		this.accessToken = accessToken;
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_com_member, null);
			holder = new ViewHolder();
			holder.tv_member_name = (TextView) convertView.findViewById(R.id.tv_member_name);
			holder.tv_member_position = (TextView) convertView.findViewById(R.id.tv_member_position);
			holder.tv_member_phone = (TextView) convertView.findViewById(R.id.tv_member_phone);
			holder.tv_member_isFriend = (TextView) convertView.findViewById(R.id.tv_member_isFriend);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String name = mList.get(position).getConnectionName();
		String job = mList.get(position).getPosition();
		if (name != null && !name.equals("null")) {
			holder.tv_member_name.setText(name);
		} else {
			holder.tv_member_name.setText(mList.get(position).getUserLoginId());
		}
		if (job != null && !job.equals("null")) {
			holder.tv_member_position.setText(job);
		} else {
			holder.tv_member_position.setText("暂无职位");
		}
		holder.tv_member_phone.setText(mList.get(position).getContactsTelephoneNumber());
		if (mList.get(position).getIsFirend().equals("Y")) {

		} else if (mList.get(position).getIsFirend().equals("N")) {
			holder.tv_member_isFriend.setBackgroundResource(R.drawable.shape_tag_company_brand);
			holder.tv_member_isFriend.setTextColor(Color.parseColor("#2492da"));
			holder.tv_member_isFriend.setText("加好友");
			holder.tv_member_isFriend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
								.post(new FormBody.Builder().add("masterId", userName)
										.add("slaveId", mList.get(position).getUserLoginId())
										.add("accessToken", accessToken).add("changeType", "ask").build())
								.build()).enqueue(new Callback() {

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								if (!response.isSuccessful()) {
									return;
								}
								final String jsonStr = response.body().string();
								System.out.println("=====加好友====" + jsonStr + "======");
								new Handler(Looper.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(jsonStr);
											String error = object.optString("_ERROR_MESSAGE_");
											String success = object.optString("isSuccess");
											if (success.equals("Y")) {
												mList.get(position).setIsFirend("Y");
												notifyDataSetChanged();
												Toast.makeText(mContext, "添加好友请求成功，等待对方确认", Toast.LENGTH_SHORT).show();
											} else if (error.equals("102")) {
												Toast.makeText(mContext, "登录秘钥失效，请重新登录", Toast.LENGTH_SHORT).show();
											} else if (error.equals("126")) {
												Toast.makeText(mContext, "已经请求，请等待对方确认", Toast.LENGTH_SHORT).show();
											}

										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});

							}

							@Override
							public void onFailure(Call arg0, IOException arg1) {
								new Handler(Looper.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(mContext, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
					}
				}
			});
		}

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_member_name, tv_member_position, tv_member_phone, tv_member_isFriend;
	}

}
