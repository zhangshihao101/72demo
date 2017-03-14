package com.spt.adapter;

import java.util.ArrayList;

import com.spt.bean.ChildEntity;
import com.spt.bean.ParentEntity;
import com.spt.sht.R;
import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsGoodsClassifyAdapter extends BaseExpandableListAdapter {


    private Context mContext;// 上下文
    private ArrayList<ParentEntity> mParents;// 数据源
    private OnClickListener onTextClickListener;

    public void setOnTextClickListener(OnClickListener onTextClickListener) {
        this.onTextClickListener = onTextClickListener;
    }

    public MtsGoodsClassifyAdapter(Context mContext, ArrayList<ParentEntity> mParents) {
        super();
        this.mContext = mContext;
        this.mParents = mParents;
    }

    @Override
    public int getGroupCount() {
        return mParents != null ? mParents.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mParents.get(groupPosition).getChilds() != null ? mParents.get(groupPosition).getChilds().size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mParents.get(groupPosition);
    }

    @Override
    public ChildEntity getChild(int groupPosition, int childPosition) {
        return mParents.get(groupPosition).getChilds().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.parent_group_item, null);
            holder = new GroupHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.update(mParents.get(groupPosition));
        
        if (isExpanded) {
            Drawable nav_dw = mContext.getResources().getDrawable(R.drawable.list_down_icon);
            nav_dw.setBounds(0, 0, nav_dw.getMinimumWidth(), nav_dw.getMinimumHeight());
            holder.iv_arrow.setBackgroundDrawable(nav_dw);
            // holder.parentGroupTV.setCompoundDrawables(null, null, nav_dw, null);
        } else {
            Drawable nav_dw = mContext.getResources().getDrawable(R.drawable.list_up_icon);
            nav_dw.setBounds(0, 0, nav_dw.getMinimumWidth(), nav_dw.getMinimumHeight());
            holder.iv_arrow.setBackgroundDrawable(nav_dw);
            // holder.parentGroupTV.setCompoundDrawables(null, null, nav_dw, null);
        }

        holder.parentGroupTV.setTag(groupPosition);
        holder.parentGroupTV.setOnClickListener(onTextClickListener);
        
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        ChildHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_child_item, null);
            holder = new ChildHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.update(getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    public class GroupHolder {

        private TextView parentGroupTV;
        private ImageView iv_arrow;

        public GroupHolder(View v) {
            parentGroupTV = (TextView) v.findViewById(R.id.parentGroupTV);
            iv_arrow = (ImageView) v.findViewById(R.id.iv_arrow);
        }

        public void update(ParentEntity model) {
            parentGroupTV.setText(model.getGroupName());
        }
    }

    class ChildHolder {

        private TextView childChildTV;

        public ChildHolder(View v) {
            childChildTV = (TextView) v.findViewById(R.id.childChildTV);
        }

        public void update(ChildEntity child) {
            childChildTV.setText(child.getGroupName());
        }
    }

}
