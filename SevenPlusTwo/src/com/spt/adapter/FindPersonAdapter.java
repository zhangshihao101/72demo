package com.spt.adapter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.SearchFriendsInfo;
import com.spt.controler.CircleImageView;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
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

@SuppressLint("NewApi")
public class FindPersonAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchFriendsInfo> mList;
    private String myId, astoken;

    public FindPersonAdapter(Context mContext, List<SearchFriendsInfo> mList, String myId, String astoken) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.myId = myId;
        this.astoken = astoken;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext).load(mList.get(position).getmHeader()).placeholder(R.drawable.noheader)
                .error(R.drawable.noheader).resize(100, 100).into(holder.iv_friends_header);
        holder.tv_friends_name.setText(mList.get(position).getmName());
        holder.tv_name_position.setText(mList.get(position).getmPositon());
        if (mList.get(position).getmFlag().equals("Leader")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag1);
        } else if (mList.get(position).getmFlag().equals("Club")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag5);
        } else if (mList.get(position).getmFlag().equals("MassOrganizations")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag4);
        } else if (mList.get(position).getmFlag().equals("WebShopOwner")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag3);
        } else if (mList.get(position).getmFlag().equals("StoreOwner")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag2);
        } else if (mList.get(position).getmFlag().equals("Other")) {
            holder.iv_friends_flag.setImageResource(R.drawable.new_peer_tag6);
        } else {
            holder.iv_friends_flag.setVisibility(View.INVISIBLE);
        }

        if (mList.get(position).getmAdd().equals("Y")) {
            holder.tv_add_friends.setText("已添加");
            holder.tv_add_friends.setTextColor(0xffb4b4b4);
            holder.tv_add_friends.setGravity(Gravity.CENTER);
            holder.tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
            holder.tv_add_friends.setOnClickListener(null);
        } else if (mList.get(position).getmAdd().equals("N")) {
            holder.tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn);
            holder.tv_add_friends.setText("加好友");
            holder.tv_add_friends.setTextColor(0xffffffff);
            holder.tv_add_friends.setGravity(Gravity.CENTER);
            holder.tv_add_friends.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    OkHttpManager.client
                            .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
                                    .post(new FormBody.Builder().add("masterId", myId)
                                            .add("slaveId", mList.get(position).getUserLoginId())
                                            .add("accessToken", astoken).add("changeType", "ask").build())
                                    .build())
                            .enqueue(new Callback() {

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
                                        if (error.equals("")) {
                                            mList.get(position).setmAdd("W");
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
                                    Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        } else if (mList.get(position).getmAdd().equals("W")) {
            holder.tv_add_friends.setText("等待验证");
            holder.tv_add_friends.setTextColor(0xffb4b4b4);
            holder.tv_add_friends.setGravity(Gravity.CENTER);
            holder.tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
            holder.tv_add_friends.setOnClickListener(null);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv_friends_header, iv_friends_flag;
        TextView tv_friends_name, tv_name_position, tv_add_friends;
    }

}
