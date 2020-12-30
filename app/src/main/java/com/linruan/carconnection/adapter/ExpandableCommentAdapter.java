package com.linruan.carconnection.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.linruan.carconnection.R;
import com.linruan.carconnection.entities.Comment;

import java.util.ArrayList;

/**
 * author：shichengxiang on 2020/6/4 13:53
 * email：1328911009@qq.com
 */
public class ExpandableCommentAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<Comment> mData = new ArrayList<>();

    public ExpandableCommentAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<Comment> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getSub().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getSub().get(childPosition);
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
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.item_rv_comment, null);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.item_rv_comment, null);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
