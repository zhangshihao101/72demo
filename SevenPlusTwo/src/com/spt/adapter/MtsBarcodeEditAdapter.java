package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsBarcodeInfo;
import com.spt.page.MtsBarcodeEditActivity;
import com.spt.page.MtsEditChangeActivity;
import com.spt.sht.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsBarcodeEditAdapter extends BaseAdapter {

    private Context mContext;
    private List<MtsBarcodeInfo> mList;
    
    private OnClickListener shareClickListener;
    
    public void setShareClickListener(OnClickListener shareClickListener) {
        this.shareClickListener = shareClickListener;
    }

    public MtsBarcodeEditAdapter(Context mContext, List<MtsBarcodeInfo> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position) == null ? null : mList.get(position);
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
            convertView = ln.inflate(R.layout.item_barcode_edit, parent, false);
            holder = new ViewHolder();

            holder.et_barcode = (EditText) convertView.findViewById(R.id.et_barcode);
            holder.tv_color = (TextView) convertView.findViewById(R.id.tv_color);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            holder.iv_scan = (ImageView)convertView.findViewById(R.id.iv_scan);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.tv_color.setText(mList.get(position).getProColor());
        holder.tv_size.setText(mList.get(position).getProSize());
        holder.et_barcode
                .setText(mList.get(position).getProBarcode().equals("null") ? "" : mList.get(position).getProBarcode());
        holder.iv_scan.setTag(position);
        holder.iv_scan.setOnClickListener(shareClickListener);
        holder.et_barcode.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MtsEditChangeActivity.class);
                Bundle b = new Bundle();
                b.putInt("position", position);
                b.putString("flag", "barcode");
                intent.putExtras(b);
                ((MtsBarcodeEditActivity) mContext).startActivityForResult(intent, 0);
            }
        });
        
        return convertView;
    }

    class ViewHolder {
        TextView tv_color, tv_size;
        EditText et_barcode;
        ImageView iv_scan;
    }

}
