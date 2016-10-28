package com.zjl.test.systeminfo;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import com.zjl.test.R;

/**
 * @author zengjinlong
 *
 */
public class SystemInfoActivity extends Activity {

    private static final String TAG = SystemInfoActivity.class.getName();

    private ViewPager mViewPager;

    private ViewPagerAdapter mViewPagerAdapter;

    public static int TAB_APP = 0;
    public static int TAB_PROCESS = 1;
    public static int TAB_TASK = 2;

    public static int mCurrentTab = TAB_APP;

    private Fragment mAppFragment;
    private Fragment mServiceFragment;
    private Fragment mTaskFragment;

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onPageSelected(int position) {
            log("onPageSelected:" + position);
            getActionBar().selectTab(getActionBar().getTabAt(position));
            mCurrentTab = position;
        }
        
    };

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return getFragment(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_test);

        mFragments.clear();
        mFragments.add(new ListInfoFragment(TAB_APP));
        mFragments.add(new ListInfoFragment(TAB_PROCESS));
        mFragments.add(new ListInfoFragment(TAB_TASK));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setCurrentItem(TAB_APP);

        setupTab();
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);

        View fragment1 = findViewById(R.id.fragment1);
        fragment1.setVisibility(View.GONE);

        // setFragment(TAB_APP);
    }

    private void setupTab() {
        Tab tab = getActionBar().newTab();
        tab.setText("App");
        tab.setTag(TAB_APP);
        tab.setTabListener(mTabListener);
        getActionBar().addTab(tab, true);
        tab = getActionBar().newTab();
        tab.setText("Service");
        tab.setTabListener(mTabListener);
        getActionBar().addTab(tab);
        tab = getActionBar().newTab();
        tab.setText("Task");
        tab.setTabListener(mTabListener);
        getActionBar().addTab(tab);
        
    }

    private TabListener mTabListener = new TabListener() {

        @Override
        public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
            log("onTabReselected Position():" + arg0.getPosition());
            
        }

        @Override
        public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
            log("onTabSelected Position():" + arg0.getPosition());
            if (mCurrentTab == arg0.getPosition()) {
                return;
            }
            mCurrentTab = arg0.getPosition();
            mViewPager.setCurrentItem(mCurrentTab, true);
//            setFragment(mCurrentTab);
            
        }

        @Override
        public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
            // TODO Auto-generated method stub
            
        }
        
    };
    
    protected Fragment getFragment(int index) {
        if (mFragments.size() == 0) {
            mFragments.add(new ListInfoFragment(TAB_APP));
            mFragments.add(new ListInfoFragment(TAB_PROCESS));
            mFragments.add(new ListInfoFragment(TAB_TASK));
        }
        return mFragments.get(index);
    }

    protected void setFragment(int index) {
        Fragment fragment = getFragment(index);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    public void log(String msg) {
        Log.d(TAG, msg);
    }
}
