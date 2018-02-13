package com.zjl.test.largescreen.list;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjl.test.R;

/**
 * Created by zengjinlong on 18-1-10.
 */

public class ItemViewHolder implements ViewHolder{

    int mId;
    int mNameResId;
    int mLeftIconResId;
    String mRightText = "";
    String mName;
    Bitmap mLeftIcon;

    boolean mSelected;

    View mBindView;

    public ItemViewHolder(int id) {
        mId = id;
    }

    public ItemViewHolder(int id, int nameResId, int iconRes) {
        mId = id;
        mNameResId = nameResId;
        mLeftIconResId = iconRes;
    }

    public ItemViewHolder(int id, String name, int iconRes) {
        mId = id;
        mName = name;
        mLeftIconResId = iconRes;
    }

    public ItemViewHolder(int id, int nameResId, int iconRes, String rightText) {
        this(id, nameResId, iconRes);
        mRightText = rightText;
    }

    public ItemViewHolder(int id, String name, int iconRes, String rightText) {
        this(id, name, iconRes);
        mRightText = rightText;
    }

    public void setLeftIcon(Bitmap bitmap) {
        mLeftIcon = bitmap;
    }

    public void setRightText(String text) {
        mRightText = text;
        if (mBindView != null && mBindView.getTag() == this) {
            TextView tv = (TextView) mBindView.findViewById(R.id.sub_content);
            tv.setText(mRightText);
        }
    }

    /**
     * @return mBindView return the view that currently bound to this view holder.
     */
    public View getBindView() {
        if (mBindView != null && mBindView.getTag() == this) {
            return mBindView;
        } else {
            return  null;
        }
    }

    @Override
    public void bindView(int pos, View view) {
        bindCommonView(pos,view);
        TextView tv = (TextView) view.findViewById(R.id.sub_content);
        tv.setText(mRightText);
    }

    protected void bindCommonView(int pos, View view) {
        mBindView = view;
        ImageView iv = (ImageView) view.findViewById(R.id.left_icon);
        if (mLeftIconResId > 0) {
            iv.setImageResource(mLeftIconResId);
        } else {
            iv.setImageBitmap(mLeftIcon);
        }
        TextView tv = (TextView) view.findViewById(R.id.content);
        if (mNameResId > 0) {
            tv.setText(mNameResId);
        } else {
            tv.setText(mName);
        }
        Log.d("zjltest", "bindView pos: " + pos + ", mSelected:" + mSelected);
        view.setSelected(mSelected);
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View recycleView = inflater.inflate(R.layout.nav_list_item_layout, null);
        return recycleView;
    }

    @Override
    public void onItemClicked(View view) {
        mSelected = true;
        Log.d("zjltest", "onItemClicked , mSelected:" + mSelected);
        view.setSelected(mSelected);
    }

    public void setSelected(boolean selected) {
        Log.d("zjltest", "setSelected :" + selected);
        mSelected = selected;
        if (mBindView != null && mBindView.getTag() == this) {
            mBindView.setSelected(selected);
        }
    }

    public boolean isSelected() {
        return mSelected;
    }

    public int getType() {
        return TYPE_ITEM;
    }

    public int getId() {
        return mId;
    }
}
