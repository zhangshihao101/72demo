package com.spt.adapter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.SearchFriendsInfo;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class PeopleSearchAdapter extends BaseAdapter {

	private Context mContext;
	private List<SearchFriendsInfo> mList;
	private String type;
	private SharedPreferences sp;

	public PeopleSearchAdapter(Context mContext, List<SearchFriendsInfo> mList, String type) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		this.type = type;
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
			convertView = ln.inflate(R.layout.item_new_friends, parent, false);
			holder = new ViewHolder();
			holder.iv_friends_header = (CircleImageView) convertView.findViewById(R.id.iv_friends_header);
			holder.iv_friends_flag = (ImageView) convertView.findViewById(R.id.iv_friends_flag);
			holder.tv_friends_name = (TextView) convertView.findViewById(R.id.tv_friends_name);
			holder.tv_name_position = (TextView) convertView.findViewById(R.id.tv_name_position);
			holder.tv_add_friends = (TextView) convertView.findViewById(R.id.tv_add_friends);
			holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
			holder.tv_ok = (TextView) convertView.findViewById(R.id.tv_ok);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		sp = mContext.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

		if (type.equals("company")) {
			holder.iv_friends_flag.setVisibility(View.INVISIBLE);
			holder.tv_name_position.setVisibility(View.INVISIBLE);

			if (mList.get(position).getUserLoginId().equals(sp.getString("username", ""))) {
				holder.tv_add_friends.setVisibility(View.INVISIBLE);
			} else {
				holder.tv_add_friends.setVisibility(View.VISIBLE);

				if (mList.get(position).getState().equals("agreed") && mList.get(position).isFlag() == false) {
					holder.tv_add_friends.setEnabled(true);
					holder.tv_add_friends.setText("删除");
					holder.tv_add_friends.setVisibility(View.VISIBLE);
					holder.tv_ok.setVisibility(View.GONE);
					holder.tv_no.setVisibility(View.GONE);
					holder.tv_add_friends.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							OkHttpManager.client
									.newCall(
											new Request.Builder()
													.url(MtsUrls.base
															+ MtsUrls.applyForGroup)
													.post(new FormBody.Builder()
															.add("userLoginId", mList.get(position).getUserLoginId())
															.add("position", "职员")
															.add("accessToken", sp.getString("accessToken", ""))
															.add("partyId", sp.getString("partyId", ""))
															.add("requestType", "delete").build())
													.build())
									.enqueue(new Callback() {

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									if (!response.isSuccessful()) {
										return;
									}
									final String jsonStr = response.body().string();
									new Handler(Looper.getMainLooper()).post(new Runnable() {

										@Override
										public void run() {
											try {
												JSONObject object = new JSONObject(jsonStr);
												String isSuccess = object.optString("isSuccess");
												if (isSuccess.equals("Y")) {
													mList.get(position).setFlag(true);
													Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
													notifyDataSetChanged();
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
											Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
										}
									});

								}
							});
						}
					});
				} else if (mList.get(position).getState().equals("agreed") && mList.get(position).isFlag() == true) {
					holder.tv_add_friends.setVisibility(View.VISIBLE);
					holder.tv_ok.setVisibility(View.GONE);
					holder.tv_no.setVisibility(View.GONE);
					holder.tv_add_friends.setText("已删除");
					holder.tv_add_friends.setTextColor(Color.parseColor("#d2d2d2"));
					holder.tv_add_friends.setEnabled(false);
				}
			}

			if (mList.get(position).getState().equals("waiting_consent")) {
				holder.tv_add_friends.setVisibility(View.GONE);
				holder.tv_ok.setVisibility(View.VISIBLE);
				holder.tv_no.setVisibility(View.VISIBLE);
				holder.tv_no.setTextColor(Color.parseColor("#52aee2"));
				holder.tv_ok.setEnabled(true);
				holder.tv_no.setEnabled(true);
				if (mList.get(position).isFlag1() == false) {
					holder.tv_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							OkHttpManager.client
									.newCall(
											new Request.Builder()
													.url(MtsUrls.base
															+ MtsUrls.applyForGroup)
													.post(new FormBody.Builder()
															.add("userLoginId", mList.get(position).getUserLoginId())
															.add("position", "职员")
															.add("accessToken", sp.getString("accessToken", ""))
															.add("partyId", sp.getString("partyId", ""))
															.add("requestType", "agreed").build())
													.build())
									.enqueue(new Callback() {

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									if (!response.isSuccessful()) {
										return;
									}
									final String jsonStr = response.body().string();
									new Handler(Looper.getMainLooper()).post(new Runnable() {

										@Override
										public void run() {
											try {
												JSONObject object = new JSONObject(jsonStr);
												String isSuccess = object.optString("isSuccess");
												if (isSuccess.equals("Y")) {
													Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
													mList.get(position).setFlag1(true);

													notifyDataSetChanged();
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
											Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
										}
									});

								}
							});
						}
					});
				} else {
					holder.tv_add_friends.setVisibility(View.GONE);
					holder.tv_ok.setVisibility(View.VISIBLE);
					holder.tv_no.setVisibility(View.GONE);
					holder.tv_ok.setTextColor(Color.parseColor("#d2d2d2"));
					holder.tv_ok.setEnabled(false);
					holder.tv_ok.setText("已同意");
				}

				if (mList.get(position).isFlag2() == false) {
					holder.tv_no.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							OkHttpManager.client
									.newCall(
											new Request.Builder()
													.url(MtsUrls.base
															+ MtsUrls.applyForGroup)
													.post(new FormBody.Builder()
															.add("userLoginId", mList.get(position).getUserLoginId())
															.add("position", "职员")
															.add("accessToken", sp.getString("accessToken", ""))
															.add("partyId", sp.getString("partyId", ""))
															.add("requestType", "refuse").build())
													.build())
									.enqueue(new Callback() {

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									if (!response.isSuccessful()) {
										return;
									}
									final String jsonStr = response.body().string();
									new Handler(Looper.getMainLooper()).post(new Runnable() {

										@Override
										public void run() {
											try {
												JSONObject object = new JSONObject(jsonStr);
												String isSuccess = object.optString("isSuccess");
												if (isSuccess.equals("Y")) {
													Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
													mList.get(position).setFlag2(true);

													notifyDataSetChanged();
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
											Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
										}
									});

								}
							});
						}
					});
				} else {
					holder.tv_add_friends.setVisibility(View.GONE);
					holder.tv_ok.setVisibility(View.GONE);
					holder.tv_no.setVisibility(View.VISIBLE);
					holder.tv_no.setTextColor(Color.parseColor("#d2d2d2"));
					holder.tv_ok.setEnabled(false);
					holder.tv_ok.setText("已拒绝");
				}

			}
		} else if (type.equals("newFri")) {
			holder.iv_friends_flag.setVisibility(View.INVISIBLE);
			holder.tv_name_position.setVisibility(View.INVISIBLE);
			holder.tv_add_friends.setVisibility(View.INVISIBLE);
			holder.tv_ok.setVisibility(View.VISIBLE);
			holder.tv_no.setVisibility(View.VISIBLE);
			holder.tv_ok.setText("同意");
			holder.tv_no.setText("拒绝");

			holder.tv_ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
								.post(new FormBody.Builder().add("masterId", sp.getString("username", ""))
										.add("slaveId", mList.get(position).getUserLoginId())
										.add("accessToken", sp.getString("accessToken", "")).add("changeType", "agreed")
										.build())
								.build()).enqueue(new Callback() {

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								if (!response.isSuccessful()) {
									return;
								}
								final String jsonStr = response.body().string();
								new Handler(Looper.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(jsonStr);
											String error = object.optString("_ERROR_MESSAGE_");
											if (error != null && "".equals(error)) {
												Toast.makeText(mContext, "已同意添加", Toast.LENGTH_SHORT).show();
												mList.remove(position);
												notifyDataSetChanged();
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
										Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
									}
								});

							}
						});
					}

				}
			});

			holder.tv_no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
								.post(new FormBody.Builder().add("masterId", sp.getString("username", ""))
										.add("slaveId", mList.get(position).getUserLoginId())
										.add("accessToken", sp.getString("accessToken", "")).add("changeType", "refuse")
										.build())
								.build()).enqueue(new Callback() {

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								if (!response.isSuccessful()) {
									return;
								}
								final String jsonStr = response.body().string();
								new Handler(Looper.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(jsonStr);
											String error = object.optString("_ERROR_MESSAGE_");
											if (error != null && "".equals(error)) {
												Toast.makeText(mContext, "已拒绝", Toast.LENGTH_SHORT).show();
												mList.remove(position);
												notifyDataSetChanged();
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
										Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
									}
								});

							}
						});
					}

				}
			});

		} else if (type.equals("myFri")) {
			holder.iv_friends_flag.setVisibility(View.INVISIBLE);
			holder.tv_name_position.setVisibility(View.INVISIBLE);
			holder.tv_add_friends.setVisibility(View.VISIBLE);
			holder.tv_ok.setVisibility(View.INVISIBLE);
			holder.tv_no.setVisibility(View.INVISIBLE);
			holder.tv_add_friends.setText("删除");
			holder.tv_add_friends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!NoDoubleClickUtils.isDoubleClick()) {
						OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
								.post(new FormBody.Builder().add("masterId", sp.getString("username", ""))
										.add("slaveId", mList.get(position).getUserLoginId())
										.add("accessToken", sp.getString("accessToken", "")).add("changeType", "delete")
										.build())
								.build()).enqueue(new Callback() {

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								if (!response.isSuccessful()) {
									return;
								}
								final String jsonStr = response.body().string();
								new Handler(Looper.getMainLooper()).post(new Runnable() {

									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(jsonStr);
											String error = object.optString("_ERROR_MESSAGE_");
											if (error != null && "".equals(error)) {
												Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
												mList.remove(position);
												notifyDataSetChanged();
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
										Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
									}
								});

							}
						});
					}

				}
			});

		} else if (type.equals("join")) {
			holder.iv_friends_flag.setVisibility(View.INVISIBLE);
			holder.tv_name_position.setVisibility(View.INVISIBLE);
			holder.tv_add_friends.setVisibility(View.INVISIBLE);
			if (mList.get(position).getmAdd().equals("3")) {
				holder.tv_ok.setVisibility(View.VISIBLE);
				holder.tv_no.setVisibility(View.VISIBLE);
				holder.tv_ok.setText("同意");
				holder.tv_no.setText("拒绝");

				holder.tv_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!NoDoubleClickUtils.isDoubleClick()) {
							operateCooperation(mList.get(position).getmFlag(), "1");
						}
					}
				});

				holder.tv_no.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!NoDoubleClickUtils.isDoubleClick()) {
							operateCooperation(mList.get(position).getmFlag(), "5");
						}
					}
				});
			}
		}

		Picasso.with(mContext).load(mList.get(position).getmHeader()).placeholder(R.drawable.noheader)
				.error(R.drawable.noheader).resize(50, 50).into(holder.iv_friends_header);
		holder.tv_friends_name.setText(mList.get(position).getmName());

		return convertView;
	}

	private static class ViewHolder {
		ImageView iv_friends_header, iv_friends_flag;
		TextView tv_friends_name, tv_name_position, tv_add_friends, tv_ok, tv_no;
	}

	private void operateCooperation(String id, String type) {
		OkHttpManager.client
				.newCall(
						new Request.Builder().url(MtsUrls.base + MtsUrls.operate_cooperation)
								.post(new FormBody.Builder().add("accessToken", sp.getString("accessToken", ""))
										.add("businessId", id).add("state", type).build())
								.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(jsonStr);
									String error = object.optString("_ERROR_MESSAGE_");
									if (error.equals("")) {
										Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(mContext, "操作失败" + error, Toast.LENGTH_SHORT).show();
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
								Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
							}
						});

					}
				});

	}

}
