package com.zjl.test.largescreen;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjl.test.R;
import com.zjl.test.largescreen.list.CheckableItemViewHolder;
import com.zjl.test.largescreen.list.CollapsibleHeaderViewHolder;
import com.zjl.test.largescreen.list.HeaderListAdapter;
import com.zjl.test.largescreen.list.HeaderViewHolder;
import com.zjl.test.largescreen.list.ItemViewHolder;
import com.zjl.test.largescreen.list.ViewHolder;

import java.util.ArrayList;

/**
 * Created by zengjinlong on 18-1-9.
 */

public class TestColumnActivity extends SmtBaseColumnActivity {
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";

    ArrayList<HeaderViewHolder> mItemGroups = new ArrayList<HeaderViewHolder>();
    private HeaderListAdapter mAdapter;

    static boolean sThreeColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sThreeColumns = !sThreeColumns;
        if (sThreeColumns) {
            requestSettingBarFeature();
            requestColumnsFeature(FEATURE_THREE_COLUMN);
            requestTitleFeature();
        } else {
            requestColumnsFeature(FEATURE_TWO_COLUMN);
        }
        super.onCreate(savedInstanceState);

        initNavigationData();
        initColumnsView();
    }

    private void initNavigationData() {
        HeaderViewHolder header = new HeaderViewHolder("HEAD ONE");
        ArrayList<ViewHolder> children = new ArrayList<ViewHolder>();
        for (int i = 0; i < 2; i++) {
            children.add(new ItemViewHolder(i, "This is Item " + i, R.drawable.nav_icon_color_red, "" +i * i));
        }
        header.setChildren(children);
        mItemGroups.add(header);

        header = new CollapsibleHeaderViewHolder("HEAD TWO");
        children = new ArrayList<ViewHolder>();
        for (int i = 3; i < 5; i++) {
            children.add(new ItemViewHolder(i, "This is Item " + i, R.drawable.nav_icon_image, "" + i * i));
        }
        ItemViewHolder item = new ItemViewHolder(5, "This is Item " + 5, R.drawable.nav_icon_image, "" + 6);
        header.setChildren(children);
        mItemGroups.add(header);

        header = new HeaderViewHolder("HEAD THREE");
        children = new ArrayList<ViewHolder>();
        for (int i = 6; i < 9; i++) {
            children.add(new CheckableItemViewHolder(i, "This is Item " + i, R.drawable.nav_icon_dot_blue, true));
        }
        header.setChildren(children);
        mItemGroups.add(header);

        mAdapter = new HeaderListAdapter(this, mItemGroups, mOnItemClickListener);
        // Set up our adapter
        setListAdapter(mAdapter);
    }

    HeaderListAdapter.OnItemClickListener mOnItemClickListener = new HeaderListAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(View view, ItemViewHolder holder) {
            Log.d("zjltest", holder.getId() + ", clicked!");
        }
    };

    private void initColumnsView() {
        TextView tv = new TextView(this);
        tv.setText("this is columns two");
        tv.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = getSecondColumnLayout();
        viewGroup.addView(tv, lp);

        if (sThreeColumns) {
            tv = new TextView(this);
            tv.setText("this is columns three");
            tv.setGravity(Gravity.CENTER);
            viewGroup = getThirdColumnLayout();
            viewGroup.addView(tv, lp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
