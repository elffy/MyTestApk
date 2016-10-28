
package com.zjl.test.filehelper;

//import android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zjl.test.R;
import com.zjl.test.ShowInfoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBrowserListActivity extends ListActivity {

    private static final String TAG = "FileList";

    private static List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    private static ArrayList<String> mFileList = new ArrayList<String>();

    private static ArrayList<String> mFolderList = new ArrayList<String>();

    private static File mCurFile = null;

    private static boolean isRoot = true;

    private static MyHandler mHandler = null;

    private static final int MSG_UPDATE = 0;

    private static final String TAG_FILE = "file";
    private static final String TAG_FILE_NAME = "name";
    private static final String TAG_FILE_SIZE = "size";
    private static final String TAG_FILE_DIRECTORY = "isDirectory";

    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MyAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            // convertView =
            // mInflater.inflate(android.R.layout.simple_list_item_2, parent);
            // TextView textView = (TextView)
            // convertView.findViewById(android.R.id.text1);
            // textView.setText(mData.get(position).get("name"));
            // textView = (TextView)
            // convertView.findViewById(android.R.id.text2);
            // textView.setText(mData.get(position).get("info"));
            convertView = mInflater.inflate(R.layout.list_item, null);
            Map map = mData.get(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
            imageView.setImageResource((Boolean) map.get(TAG_FILE_DIRECTORY) ? R.drawable.icon_list_folder
                            : R.drawable.icon_list_default);
            TextView textView = (TextView) convertView.findViewById(R.id.textView1);
            textView.setText((CharSequence) map.get(TAG_FILE_NAME));
            textView = (TextView) convertView.findViewById(R.id.textView2);
            textView.setText((CharSequence) map.get(TAG_FILE_SIZE));
            convertView.setTag(map);
            return convertView;
        }

    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                // onContentChanged(); //onContentChanged() will cause blank view
                getListView().invalidateViews();
                setProgressBarIndeterminateVisibility(false);
            }
        }

    }

    @Override
    public void onContentChanged() {
        log("onContentChanged");
        super.onContentChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//圆形进度条
//        requestWindowFeature(Window.FEATURE_PROGRESS); //水平进度条
        setContentView(R.layout.list_layout);
//        setProgressBarVisibility(true);//
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mHandler = new MyHandler();

        getListView().setAdapter(new MyAdapter(this));
        registerForContextMenu(getListView());

        setScreenShowFlags();
    }

    void setScreenShowFlags() {
        final Window win = ((Activity) this).getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();

        params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        win.setAttributes(params);
    }

    void clearScreenShowFlags() {
        final Window win = this.getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();

        params.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        win.setAttributes(params);
    }

    private void initData() {
        setProgressBarIndeterminateVisibility(true);
        if (isRoot) {
            mCurFile = FileUtils.getExternalStorageDirectory();
        }
        mFolderList.clear();
        mFileList.clear();
        mData.clear();
        for (File file : mCurFile.listFiles()) {
//            if (file.isHidden()) {
//                continue;
//            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(TAG_FILE, file);
            map.put(TAG_FILE_NAME, file.getName());
            map.put(TAG_FILE_DIRECTORY, file.isDirectory());
            map.put(TAG_FILE_SIZE, "0B");
            mData.add(map);
            if (file.isDirectory()) {
                mFolderList.add(file.getName());
            } else {
                mFileList.add(file.getName());
            }
        }
        // Collections.sort(mData, new MyComparator());
        new MyAsyncTask().execute(new Object());
//        Collections.sort(mFolderList, new MyComparator());
//        Collections.sort(mFileList, new MyComparator());
        mFileList.addAll(mFolderList);
        setTitle(mCurFile.getAbsolutePath());
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            int length = mData.size();
            for (int i = 0; i < length; i++) {
                Map map = mData.get(i);
                File file = (File) map.get("file");
                String size = "0B";
                try {
                    size = FileUtils.getProperSize(FileUtils.getFileOrFolderSize(file));
                } catch (NullPointerException e) {
                    log("file:" + file);
                    e.printStackTrace();
                }
                map.put("size", size);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            log("onPostExecute");
            mHandler.sendEmptyMessage(MSG_UPDATE);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            log("onPreExecute");
            super.onPreExecute();
        }
    }

    class MyComparator implements Comparator {

        @Override
        public int compare(Object obj1, Object obj2) {
            return ((String) obj1).compareToIgnoreCase((String) obj2);
            // return ((String)
            // ((Map)obj1).get("name")).compareToIgnoreCase((String)((Map)obj2).get("name"));
        }

    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (HashMap<String, Object>) v.getTag();
        log("onListItemClick:" + map);
        log("mCachedMap.size:" + FileUtils.mCachedMap.size() + "," + map.get(TAG_FILE_NAME)
                + ":" + FileUtils.mCachedMap.get(((File)map.get(TAG_FILE)).getAbsolutePath()));
        if (!(Boolean) map.get(TAG_FILE_DIRECTORY)) {
//            Toast.makeText(this, "this is a file!", Toast.LENGTH_SHORT).show();
            File file = (File) map.get(TAG_FILE);
            Intent intent = new Intent(this, ShowInfoActivity.class);
            intent.putExtra("filePath", file.getPath());
            startActivity(intent);
            return;
        }
        isRoot = false;
        mCurFile = (File) map.get(TAG_FILE);
        initData();
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onBackPressed() {
        if (mCurFile.equals(FileUtils.getExternalStorageDirectory()) ||
                "mnt".equals(mCurFile.getName()) || "sdcard2".equals(mCurFile.getName())) {
            isRoot = true;
            finish();
            return;
        }
        if (mCurFile.getParent() != null) {
            mCurFile = new File(mCurFile.getParent());
        }
        initData();
//        super.onBackPressed();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("UP");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!"mnt".equals(mCurFile.getName())) {
                    isRoot = false; //no need to care about root here
                    mCurFile = new File(mCurFile.getParent());
                    log("mCurFile:" + mCurFile.getName());
                    initData();
                }
                return false;
            }
            
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, 1, Menu.FIRST, "delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            Map<String, Object> map = (Map<String, Object>) info.targetView.getTag();
            log("onContextItemSelected:" + map);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        FileUtils.mCachedMap.clear();
        log("onDestroy");
        super.onDestroy();
        clearScreenShowFlags();
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
