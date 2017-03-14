package com.spt.adapter;

import java.util.LinkedList;
import java.util.List;

import com.spt.bean.VariantsInfo;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MtsGoodsEditDetailPicAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<VariantsInfo> mList;

    public void update(LinkedList<VariantsInfo> mList) {
        this.mList = mList;

        VariantsInfo info = new VariantsInfo();
        info.setSkuImage("");
        mList.addLast(info);
        notifyDataSetChanged();
    }

    public MtsGoodsEditDetailPicAdapter(Context mContext, LinkedList<VariantsInfo> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size() == 0 ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.size() == 0 ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = ln.inflate(R.layout.item_goods_detail_pic, parent, false);
            holder = new ViewHolder();

            holder.iv_goods_pic = (ImageView) convertView.findViewById(R.id.iv_goods_pic);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getSkuImage().equals("")) {
            holder.iv_goods_pic.setImageResource(R.drawable.goods_edit_pic_add_btn);
        } else if (!mList.get(position).getSkuImage().equals("null") && mList.get(position).getIsLocal()) {
            Bitmap bitmap = ImageUtils.getImageThumbnail(mList.get(position).getSkuImage(),
                    ImageUtils.getWidth(mContext) / 4 - 5, ImageUtils.getWidth(mContext) / 4 - 5);
            holder.iv_goods_pic.setImageBitmap(bitmap);
        } else {
            Picasso.with(mContext).load(mList.get(position).getSkuImage())
                    .resize(ImageUtils.getWidth(mContext) / 4 - 5, ImageUtils.getWidth(mContext) / 4 - 5).centerCrop()
                    .into(holder.iv_goods_pic);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv_goods_pic;
    }
}
