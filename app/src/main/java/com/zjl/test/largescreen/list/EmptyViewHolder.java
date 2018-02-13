package com.zjl.test.largescreen.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjl.test.R;

import java.util.ArrayList;

/**
 * Created by zengjinlong on 18-1-10.
 */

public class EmptyViewHolder implements ViewHolder{

    public EmptyViewHolder() {
    }

    @Override
    public void bindView(int pos, View view) {
        // do nothing
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View emptyView = new View(inflater.getContext());
        return emptyView;
    }

    @Override
    public void onItemClicked(View view) {
    }

    public int getType() {
        return TYPE_EMPTY_ITEM;
    }

    public int getId() {
        return -1;
    }
}
