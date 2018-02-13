package com.zjl.test.largescreen.list;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjl.test.R;

/**
 * Created by zengjinlong on 18-1-24.
 */

public class CheckableItemViewHolder extends ItemViewHolder {

    boolean mChecked;

    public CheckableItemViewHolder(int id, int nameResId, int iconRes, boolean checked) {
        super(id, nameResId, iconRes);
        mChecked = checked;
    }

    public CheckableItemViewHolder(int id, String name, int iconRes, boolean checked) {
        super(id, name, iconRes);
        mName = name;
        mLeftIconResId = iconRes;
        mChecked = checked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        if (mBindView != null && mBindView.getTag() == this) {
            CheckBox cb = (CheckBox) mBindView.findViewById(R.id.right_icon);
            cb.setChecked(mChecked);
        }
    }

    @Override
    public void bindView(int pos, View view) {
        bindCommonView(pos,view);
        CheckBox cb = (CheckBox) view.findViewById(R.id.right_icon);
        cb.setChecked(mChecked);
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View recycleView = inflater.inflate(R.layout.checkable_list_item_layout, null);
        return recycleView;
    }

    @Override
    public void onItemClicked(View view) {
        mChecked = !mChecked;
        CheckBox cb = (CheckBox) view.findViewById(R.id.right_icon);
        cb.setChecked(mChecked);
    }

    public int getType() {
        return TYPE_CHECKABLE_ITEM;
    }
}
