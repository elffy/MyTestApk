package com.baidu.zjl.systeminfo;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;

public class ListInfoFragment extends ListFragment {

    private ListView mListView = null;

    private int mTabIndex = 0;

    private Context mContext = null;

//    private ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
    private List mData;
    public ListInfoFragment(int tab) {
        mTabIndex = tab;
//        initData();
    }

    private void initView() {
        if (mTabIndex == SystemInfoActivity.TAB_APP) {
            mData = SystemInfoUtils.getInstalledApps(mContext);
            setListAdapter(new SimpleAdapter(mContext, mData,android.R.layout.simple_list_item_2,
                    new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } else if (mTabIndex == SystemInfoActivity.TAB_PROCESS) {
            mData = SystemInfoUtils.getRunningServicesInfo(mContext);
            setListAdapter(new SimpleAdapter(mContext, mData,android.R.layout.simple_list_item_2,
                    new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2}));
        } else {
            mData = SystemInfoUtils.getRunningTaskInfo(mContext);
            setListAdapter(new SimpleAdapter(mContext, mData,android.R.layout.simple_list_item_1,
                    new String[]{"info"}, new int[]{android.R.id.text1}));
        }
        
    }

    class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
//        getListView().setOnItemClickListener((OnItemClickListener) activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mListView = getListView(); //the view is not ready yet in onCreate()
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mListView = getListView();
//        return mListView;
//    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


}
