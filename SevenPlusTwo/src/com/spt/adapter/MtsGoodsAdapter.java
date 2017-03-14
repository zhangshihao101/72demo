package com.spt.adapter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.MtsGoodsListInfo;
import com.spt.page.MtsGoodsEditActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsGoodsAdapter extends BaseAdapter {

    private Context mContext;
    private List<MtsGoodsListInfo> mList;
    private Handler mHandler;

    private OnClickListener onShareClickListener, onEditClickListener;

    public void setOnShareClickListener(OnClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public void setOnEditClickListener(OnClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public MtsGoodsAdapter(Context mContext, List<MtsGoodsListInfo> mList, Handler mHandler) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.mHandler = mHandler;
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
            convertView = ln.inflate(R.layout.item_goods_list, parent, false);
            holder = new ViewHolder();
            holder.tv_goods_name = (TextView) convertView.findViewById(R.id.tv_goods_name);
            holder.tv_goods_style_no = (TextView) convertView.findViewById(R.id.tv_goods_style_no);
            holder.tv_goods_price_no = (TextView) convertView.findViewById(R.id.tv_goods_price_no);
            holder.tv_goods_brand_no = (TextView) convertView.findViewById(R.id.tv_goods_brand_no);
            holder.iv_goods_pic = (ImageView) convertView.findViewById(R.id.iv_goods_pic);
            holder.rl_edit = (RelativeLayout) convertView.findViewById(R.id.rl_edit);
            holder.rl_soldout = (RelativeLayout) convertView.findViewById(R.id.rl_soldout);
            holder.rl_share = (RelativeLayout) convertView.findViewById(R.id.rl_share);
            holder.rl_delete = (RelativeLayout) convertView.findViewById(R.id.rl_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_goods_name.setText(mList.get(position).getProductName());
        holder.tv_goods_style_no.setText(mList.get(position).getStyleId());
        holder.tv_goods_price_no.setText("￥" + mList.get(position).getProductPrice());
        holder.tv_goods_brand_no.setText(mList.get(position).getBrandId());
        if (!mList.get(position).getProductPic().equals("") || mList.get(position).getProductPic() == null) {
            Picasso.with(mContext).load(mList.get(position).getProductPic()).resize(150, 150).into(holder.iv_goods_pic);
        } else {
            holder.iv_goods_pic.setImageResource(R.drawable.test140140);
        }

        holder.rl_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OkHttpManager.client
                        .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.remove_product)
                                .post(new FormBody.Builder()
                                        .add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
                                        .add("productId", mList.get(position).getProductId()).build())
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
                                    String _ERROR_MESSAGE_ = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(mContext, _ERROR_MESSAGE_, Toast.LENGTH_SHORT).show();
                                    } else {
                                        mHandler.sendEmptyMessage(1);
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
                                Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        holder.rl_soldout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OkHttpManager.client
                        .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.off_shelf)
                                .post(new FormBody.Builder()
                                        .add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
                                        .add("productId", mList.get(position).getProductId()).build())
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
                                    String _ERROR_MESSAGE_ = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(mContext, _ERROR_MESSAGE_, Toast.LENGTH_SHORT).show();
                                    } else {
                                        mHandler.sendEmptyMessage(2);
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
                                Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        holder.rl_edit.setTag(position);
        holder.rl_edit.setOnClickListener(onEditClickListener);

        holder.rl_share.setTag(position);
        holder.rl_share.setOnClickListener(onShareClickListener);



        return convertView;
    }

    class ViewHolder {
        TextView tv_goods_name, tv_goods_style_no, tv_goods_price_no, tv_goods_brand_no;
        ImageView iv_goods_pic;
        RelativeLayout rl_edit, rl_soldout, rl_share, rl_delete;
    }

}
