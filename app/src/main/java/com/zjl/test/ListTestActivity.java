package com.zjl.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import static android.R.attr.id;

/**
 * Created by zengjinlong on 17-3-6.
 */

public class ListTestActivity extends Activity {

    private static final String TAG = "ListText";

    ListView mListView;
    ArrayList<String> mData = new ArrayList<String>();
    private int mHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeight = (int) (30 * getResources().getDisplayMetrics().density);
        setContentView(R.layout.list_layout);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(new MyAdapter(this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                log("onItemClick, position:" + position + ", id:" + id);
            }
        });
        TextView tv = new TextView(ListTestActivity.this);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
        tv.setText("Header1");
        mListView.addHeaderView(tv);
    }

    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MyAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < 10; i++) {
                mData.add("testItem " + i);
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            log("getview:" + position);
            if (convertView == null) {
                convertView = new TextView(ListTestActivity.this);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight));
            }
            ((TextView) convertView).setText(mData.get(position));
            return convertView;
        }

    }


    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
