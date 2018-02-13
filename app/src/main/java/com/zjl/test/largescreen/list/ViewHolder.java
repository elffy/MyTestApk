package com.zjl.test.largescreen.list;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by zengjinlong on 18-1-15.
 */

public interface ViewHolder {
    int TYPE_COUNT = 6;
    int TYPE_ITEM = 0;
    int TYPE_CHECKABLE_ITEM = 1;
    /**
     * User can extend custom item using this item type.
     */
    int TYPE_CUSTOM_ITEM = 2;
    int TYPE_EMPTY_ITEM = 3;
    int TYPE_SIMPLE_HEADER = 4;
    int TYPE_COLLAPSIBLE_HEADER = 5;

    void bindView(int pos, View view);

    View createView(LayoutInflater inflater);

    void onItemClicked(View view);

    int getType();

    int getId();
}
