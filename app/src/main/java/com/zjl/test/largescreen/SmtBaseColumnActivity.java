package com.zjl.test.largescreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zjl.test.R;

/**
 * Created by zengjinlong on 18-1-9.
 */

public abstract class SmtBaseColumnActivity extends Activity {

    /**
     * Two columns for this activity.
     */
    public static final int FEATURE_TWO_COLUMN = 1;
    /**
     * Three columns for this activity.
     */
    public static final int FEATURE_THREE_COLUMN = 2;
    public static final int COLUMN_MASK = 3;
    /**
     * request title for this activity.
     */
    public static final int FEATURE_HAS_TITLE = 4;
    /**
     * request setting bottom bar in navigation column.
     */
    public static final int FEATURE_HAS_SETTINGS_BAR = 8;
    int mFeatureFlags;

    ViewGroup mFirstColumn;
    ViewGroup mSecondColumn;
    ViewGroup mThirdColumn;

    View mSearchView;
    ListView mNavigationListView;

    boolean mViewInited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tablet_columns_activity_layout);
        initView();
    }

    private void initView() {
        mSearchView = findViewById(R.id.search);
        mNavigationListView = (ListView) findViewById(R.id.navigation_list);
        mFirstColumn = (ViewGroup) findViewById(R.id.first_column);
        mSecondColumn = (ViewGroup) findViewById(R.id.second_column);
        mThirdColumn = (ViewGroup) findViewById(R.id.third_column);

        LinearLayout.LayoutParams lp;
        if ((mFeatureFlags & FEATURE_THREE_COLUMN) == FEATURE_THREE_COLUMN) {
//            lp = (LinearLayout.LayoutParams) mSecondColumn.getLayoutParams();
//            lp.weight = 3;
//            mSecondColumn.setLayoutParams(lp);
            findViewById(R.id.second_divider).setVisibility(View.VISIBLE);
            mThirdColumn.setVisibility(View.VISIBLE);
//            lp = (LinearLayout.LayoutParams) mThirdColumn.getLayoutParams();
//            lp.weight = 5;
//            mThirdColumn.setLayoutParams(lp);
        } else {
            lp = (LinearLayout.LayoutParams) mSecondColumn.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
            mSecondColumn.setLayoutParams(lp);
//            mThirdColumn.setVisibility(View.GONE);
        }
        if ((mFeatureFlags & FEATURE_HAS_SETTINGS_BAR) == FEATURE_HAS_SETTINGS_BAR) {
            initSettingsBar();
        }
        if ((mFeatureFlags & FEATURE_HAS_TITLE) == FEATURE_HAS_TITLE) {
            initTitleBar();
        }
        mViewInited = true;
    }

    /**
     * the adapter for Navigation ListView
     * @param adapter
     */
    protected void setListAdapter(BaseAdapter adapter) {
        mNavigationListView.setAdapter(adapter);
    }

    /**
     * request layout Feature for this activity
     * @param flags feature flag
     */
    protected void requestFeature(int flags) {
        if ((flags & COLUMN_MASK) != 0) {
            setFlags(flags, COLUMN_MASK);
        } else {
            setFlags(flags, flags);
        }
    }

    /**
     * request columns Feature for this activity, {@link #FEATURE_TWO_COLUMN}, {@link #FEATURE_THREE_COLUMN}
     * @param flags feature flag
     */
    protected void requestColumnsFeature(int flags) {
        setFlags(flags, COLUMN_MASK);
        if ((flags & FEATURE_THREE_COLUMN) == FEATURE_THREE_COLUMN) {

        }
    }

    /**
     * request Title Feature for this activity, must be called before setContentView.
     */
    protected void requestTitleFeature() {
        mFeatureFlags |= FEATURE_HAS_TITLE;
    }

    private void initTitleBar() {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.tablet_second_col_with_title_layout, mSecondColumn, true);
        if ((mFeatureFlags & FEATURE_THREE_COLUMN) == FEATURE_THREE_COLUMN) {
            inflater.inflate(R.layout.tablet_third_col_with_title_layout, mThirdColumn, true);
        }
    }

    /**
     * request setting bottom bar in navigation column for this activity
     */
    protected void requestSettingBarFeature() {
        mFeatureFlags |= FEATURE_HAS_SETTINGS_BAR;
        if (!mViewInited) {
            return;
        }
        initSettingsBar();
    }

    private void initSettingsBar() {
        ViewStub vs = (ViewStub) findViewById(R.id.setting_bar_stub);
        View settingbar = vs.inflate();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mNavigationListView.getLayoutParams();
        lp.addRule(RelativeLayout.ABOVE, settingbar.getId());
        View settingBtn = settingbar.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingBtnClicked();
                mShowMoreSettingBtns = !mShowMoreSettingBtns;
                if (mShowMoreSettingBtns) {
                    showMoreBtnsInSettingBar();
                } else {
                    hideMoreBtnsInSettingBar();
                }
            }
        });
    }

    boolean mShowMoreSettingBtns;
    private void showMoreBtnsInSettingBar() {
        View settingbar = findViewById(R.id.settings_bar);
        if (settingbar != null) {
            View btnsLayout = settingbar.findViewById(R.id.setting_btns_layout);
            btnsLayout.setVisibility(View.VISIBLE);
            View settingBtn = settingbar.findViewById(R.id.setting_btn);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) settingBtn.getLayoutParams();
            lp.removeRule(RelativeLayout.CENTER_IN_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            settingbar.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddItemClicked();
                }
            });
            settingbar.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteItemClicked();
                }
            });
            settingbar.findViewById(R.id.more_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMoreBtnClicked();
                }
            });
        }
    }

    private void hideMoreBtnsInSettingBar() {
        View settingbar = findViewById(R.id.settings_bar);
        if (settingbar != null) {
            View btnsLayout = settingbar.findViewById(R.id.setting_btns_layout);
            btnsLayout.setVisibility(View.GONE);
            View settingBtn = settingbar.findViewById(R.id.setting_btn);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) settingBtn.getLayoutParams();
            lp.removeRule(RelativeLayout.ALIGN_PARENT_END);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
    }

    protected void onAddItemClicked() {
    }

    protected void onDeleteItemClicked() {
    }

    protected void onMoreBtnClicked() {
    }

    protected void onSettingBtnClicked() {
    }

    void setFlags(int flags, int mask) {
        mFeatureFlags = (mFeatureFlags & ~mask) | (flags & mask);
    }

    /**
     * @return int return the layout id (RelativeLayout) for first column
     */
    protected int getFirstColumnLayoutId() {
        return R.id.first_column;
    }

    /**
     * @return int return the content layout id (RelativeLayout) for second column
     */
    protected int getSecondColumnLayoutId() {
        if ((mFeatureFlags & FEATURE_HAS_TITLE) == FEATURE_HAS_TITLE) {
            return R.id.second_content_parent;
        }
        return R.id.second_column;
    }

    /**
     * @return int return the content layout id (RelativeLayout) for third column
     */
    protected int getThirdColumnLayoutId() {
        if ((mFeatureFlags & FEATURE_HAS_TITLE) == FEATURE_HAS_TITLE) {
            return R.id.third_content_parent;
        }
        return R.id.third_column;
    }

    /**
     * @return ListView return the Navigation ListView in first column
     */
    protected ListView getNavigationListView() {
        return mNavigationListView;
    }


    /**
     * @return ViewGroup return the layout for second column
     */
    protected ViewGroup getSecondColumnLayout() {
        if ((mFeatureFlags & FEATURE_HAS_TITLE) == FEATURE_HAS_TITLE) {
            return (ViewGroup) mSecondColumn.findViewById(R.id.second_content_parent);
        }
        return mSecondColumn;
    }

    /**
     * @return ViewGroup return the layout for third column
     */
    protected ViewGroup getThirdColumnLayout() {
        if ((mFeatureFlags & FEATURE_HAS_TITLE) == FEATURE_HAS_TITLE) {
            return (ViewGroup) mThirdColumn.findViewById(R.id.third_content_parent);
        }
        return mThirdColumn;
    }

    protected void setFirstColumnBgColor(int color) {
        mFirstColumn.setBackgroundColor(color);
    }

    protected void setFirstColumnBgColorResId(int colorResId) {
        mFirstColumn.setBackgroundResource(colorResId);
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
