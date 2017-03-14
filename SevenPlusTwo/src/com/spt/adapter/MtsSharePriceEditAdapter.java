package com.spt.adapter;

import java.util.List;

import com.spt.bean.MtsBarcodeInfo;
import com.spt.page.MtsEditChangeActivity;
import com.spt.page.MtsSharepriceEditActivity;
import com.spt.sht.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MtsSharePriceEditAdapter extends BaseAdapter {

    Context mContext;
    List<MtsBarcodeInfo> mList;
    // private myWatcher mWatcher;
    private int index = -1;// 记录选中的位置
    private Double mPrice[];// 记录输入的值

    public MtsSharePriceEditAdapter(Context mContext, List<MtsBarcodeInfo> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    // public MtsSharePriceEditAdapter(Context mContext, List<MtsBarcodeInfo> mList, Double[]
    // mPrice) {
    // super();
    // this.mContext = mContext;
    // this.mList = mList;
    // this.mPrice = mPrice;
    // }

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
            convertView = ln.inflate(R.layout.item_shareprice_edit, parent, false);
            holder = new ViewHolder();

            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_color = (TextView) convertView.findViewById(R.id.tv_color);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_color.setText(mList.get(position).getProColor());
        holder.tv_size.setText(mList.get(position).getProSize());
        holder.tv_price.setText("" + mList.get(position).getProprice());

        holder.tv_price.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MtsEditChangeActivity.class);
                Bundle b = new Bundle();
                b.putInt("position", position);
                b.putString("flag", "price");
                intent.putExtras(b);
                ((MtsSharepriceEditActivity) mContext).startActivityForResult(intent, 0);
            }
        });

        // holder.et_price.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        //
        // if (event.getAction() == MotionEvent.ACTION_UP) {
        // index = position;
        // }
        // return false;
        // }
        // });

        // holder.et_price.setOnFocusChangeListener(new OnFocusChangeListener() {
        // // 设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
        //
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // EditText et = (EditText) v;
        // if (mWatcher == null) {
        // mWatcher = new myWatcher();
        // }
        // if (hasFocus) {
        // et.addTextChangedListener(mWatcher);// 设置edittext内容监听
        // } else {
        // et.removeTextChangedListener(mWatcher);
        // }
        // }
        // });

        // holder.et_price.clearFocus();
        // if (index != -1 && index == position) {
        // // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
        // holder.et_price.requestFocus();
        // }

        // holder.et_price.setText(text[position]);//这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        // holder.et_price
        // .setText(mList.get(position).getProprice().equals("null") ? "" :
        // mList.get(position).getProBarcode());
        // holder.et_price.setText(""+mPrice[position]);
        // holder.et_price.setSelection(holder.et_price.getText().length());

        return convertView;
    }

    // class myWatcher implements TextWatcher {
    //
    // @Override
    // public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onTextChanged(CharSequence s, int start, int before, int count) {
    // // TODO Auto-generated method stub
    //
    //
    // }
    //
    // @Override
    // public void afterTextChanged(Editable s) {
    // mPrice[index] = s.toString().equals("null") ? 0 : Double.valueOf(s.toString());//
    // 为输入的位置内容设置数组管理器，防止item重用机制导致的上下内容一样的问题
    // mPrice[index] = Double.valueOf(s.toString());
    // }
    // }

    class ViewHolder {
        TextView tv_color, tv_size, tv_price;
    }

}
