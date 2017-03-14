package com.spt.adapter;

import java.util.List;

import com.spt.bean.VariantsInfo;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsGoodsEditPicAdapter extends BaseAdapter {

    private Context mContext;
    private List<VariantsInfo> mList;

    public MtsGoodsEditPicAdapter(Context mContext, List<VariantsInfo> mList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = ln.inflate(R.layout.item_goods_sku_pic, parent, false);
            holder = new ViewHolder();

            holder.iv_goods_pic = (ImageView) convertView.findViewById(R.id.iv_goods_pic);
            holder.tv_goods_color = (TextView) convertView.findViewById(R.id.tv_goods_color);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getSkuImage().equals("null")) {
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

        if (mList.get(position).getSkuColor() == null) {
            holder.tv_goods_color.setVisibility(View.GONE);
        } else {
            holder.tv_goods_color.setText(mList.get(position).getSkuColor());
        }
        
        return convertView;
    }

    class ViewHolder {
        ImageView iv_goods_pic;
        TextView tv_goods_color;
    }
}
