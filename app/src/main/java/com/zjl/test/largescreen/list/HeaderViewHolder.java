package com.zjl.test.largescreen.list;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zjl.test.R;

import java.util.ArrayList;

/**
 * Created by zengjinlong on 18-1-10.
 */

public class HeaderViewHolder implements ViewHolder{

    int mId;
    String mName;
    int mNameResId;
    ArrayList<ViewHolder> mChildren;
    boolean mShowChildren = true;
    boolean mIsSingleChoice;

    public HeaderViewHolder(String name) {
        mName = name;
    }

    public HeaderViewHolder(int resId) {
        mNameResId = resId;
    }

    public void setChildren(ArrayList<ViewHolder> children) {
        mChildren = children;
    }

    public ArrayList<ViewHolder> getChildren() {
        return mChildren;
    }

    public int getChildrenCount() {
        return mChildren.size();
    }

    public void setIsSingleChoice(boolean isSingleChoice) {
        mIsSingleChoice = isSingleChoice;
    }

    @Override
    public void bindView(int pos, View view) {
        TextView tv = (TextView) view.findViewById(R.id.text);
        if (mNameResId > 0) {
            tv.setText(mNameResId);
        } else {
            tv.setText(mName);
        }
        view.findViewById(R.id.arrow).setVisibility(View.GONE);
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View recycleView = inflater.inflate(R.layout.nav_collapsible_header, null);
        recycleView.findViewById(R.id.arrow).setVisibility(View.GONE);
        return recycleView;
    }

    @Override
    public void onItemClicked(View view) {

    }

    public int getType() {
        return TYPE_SIMPLE_HEADER;
    }

    public int getId() {
        return mId;
    }
}
