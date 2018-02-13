package com.zjl.test.largescreen.list;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjl.test.R;

/**
 * Created by zengjinlong on 18-1-10.
 */

public class CollapsibleHeaderViewHolder extends HeaderViewHolder {

    public CollapsibleHeaderViewHolder(String name) {
        super(name);
    }

    public CollapsibleHeaderViewHolder(int resId) {
        super(resId);
    }

    @Override
    public void bindView(int pos, View view) {
        view.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
        TextView tv = (TextView) view.findViewById(R.id.text);
        if (mNameResId > 0) {
            tv.setText(mNameResId);
        } else {
            tv.setText(mName);
        }
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View recycleView = inflater.inflate(R.layout.nav_collapsible_header, null);
        return recycleView;
    }

    @Override
    public void onItemClicked(View view) {
        mShowChildren = !mShowChildren;
        ImageView iv = (ImageView) view.findViewById(R.id.arrow);
        if (mShowChildren) {
            iv.setImageResource(R.drawable.nav_group_fold);
        } else {
            iv.setImageResource(R.drawable.nav_group_expand);
        }
    }

    public int getType() {
        return TYPE_COLLAPSIBLE_HEADER;
    }
}
