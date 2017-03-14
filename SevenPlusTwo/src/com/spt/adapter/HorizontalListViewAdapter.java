package com.spt.adapter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.ConPeopleInfo;
import com.spt.controler.CircleImageView;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class HorizontalListViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<ConPeopleInfo> mList;

	private SharedPreferences sp;

	public HorizontalListViewAdapter(Context mContext, List<ConPeopleInfo> mList) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_people_add, parent, false);
			holder = new ViewHolder();
			holder.iv_header = (CircleImageView) convertView.findViewById(R.id.iv_header);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_job = (TextView) convertView.findViewById(R.id.tv_job);
			holder.tv_add = (TextView) convertView.findViewById(R.id.tv_add);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		sp = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

		Picasso.with(mContext).load(mList.get(position).getmHeader()).placeholder(R.drawable.noheader)
				.error(R.drawable.noheader).resize(80, 80).into(holder.iv_header);
		if (mList.get(position).getmName() != null && !mList.get(position).getmName().equals("null")) {
			holder.tv_name.setText(mList.get(position).getmName());
		} else {
			holder.tv_name.setText("");
		}
		String job = mList.get(position).getConnectionRole();
		if (job.equals("Leader")) {
			holder.tv_job.setText("领队");
		} else if (job.equals("Club")) {
			holder.tv_job.setText("俱乐部");
		} else if (job.equals("MassOrganizations")) {
			holder.tv_job.setText("社团");
		} else if (job.equals("WebShopOwner")) {
			holder.tv_job.setText("网店店主");
		} else if (job.equals("StoreOwner")) {
			holder.tv_job.setText("实体店店主");
		} else if (job.equals("Other")) {
			holder.tv_job.setText("其它");
		}

		if (sp.getString("username", "").equals(mList.get(position).getUserLoginId())) {
			holder.tv_add.setBackgroundResource(R.drawable.main_approve_card_add_btn_off);
			holder.tv_add.setTextColor(Color.parseColor("#d2d2d2"));
			holder.tv_add.setText("本用户");
			holder.tv_add.setEnabled(false);
		} else if (mList.get(position).getIsFriend().equals("Y")) {
			holder.tv_add.setBackgroundResource(R.drawable.main_approve_card_add_btn_off);
			holder.tv_add.setText("已是好友");
			holder.tv_add.setTextColor(Color.parseColor("#d2d2d2"));
			holder.tv_add.setEnabled(false);
		} else if (mList.get(position).getIsFriend().equals("W")) {
			holder.tv_add.setBackgroundResource(R.drawable.main_approve_card_add_btn_off);
			holder.tv_add.setText("已请求");
			holder.tv_add.setTextColor(Color.parseColor("#d2d2d2"));
			holder.tv_add.setEnabled(false);
		} else if (mList.get(position).isFlag() == true) {
			holder.tv_add.setBackgroundResource(R.drawable.main_approve_card_add_btn_off);
			holder.tv_add.setText("已请求");
			holder.tv_add.setTextColor(Color.parseColor("#d2d2d2"));
			holder.tv_add.setEnabled(false);
		} else if (mList.get(position).getIsFriend().equals("N")) {
			holder.tv_add.setBackgroundResource(R.drawable.main_approve_card_add_btn);
			holder.tv_add.setText("加好友");
			holder.tv_add.setTextColor(Color.parseColor("#000000"));
			holder.tv_add.setEnabled(true);
		}

		holder.tv_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
							.post(new FormBody.Builder().add("masterId", sp.getString("username", ""))
									.add("slaveId", mList.get(position).getUserLoginId())
									.add("accessToken", sp.getString("accessToken", "")).add("changeType", "ask")
									.build())
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
											mList.get(position).setFlag(true);
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

		return convertView;
	}

	class ViewHolder {
		ImageView iv_header;
		TextView tv_name, tv_job, tv_add;
	}

}
