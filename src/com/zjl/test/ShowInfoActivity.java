package com.zjl.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import com.smartisan.zjl.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShowInfoActivity extends Activity {

    private TextView mTextView;
    private ScrollView mScrollView;
    private static final int MSG_UPDATE = 0;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == MSG_UPDATE) {
                mTextView.append((CharSequence) msg.obj);
                mTextView.invalidate();
            }
        }
        
    };
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.show_info);
        mTextView = (TextView) findViewById(R.id.detail_info);
        mScrollView = (ScrollView) findViewById(R.id.scrollView1);

        String filePath = getIntent().getStringExtra("filePath");
        File file = new File(filePath);
        setTitle(file.getName());

        getFileContent(file);
        mTextView.setText("");
    }

    private void getFileContent(final File file) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    StringBuilder sb = new StringBuilder("");
                    file.length();
                    byte[] ret = new byte[1024];
                    int length = 0;
                    boolean first = true;
                    while ((length = fis.read(ret)) != -1) {
                        if(first) {
                            Log.d(TestActivity.TAG, "first two bytes:" +Integer.toHexString(ret[0] & 0xff) + Integer.toHexString(ret[1] & 0xff));
                            first = false;
                        }
                        sb.append(new String(ret));
                        if (sb.length() > 1000) {
                            Message msg = mHandler.obtainMessage(MSG_UPDATE, sb.toString());
                            mHandler.sendMessage(msg);
                            sb.setLength(0);
                        }
                    }
                    Message msg = mHandler.obtainMessage(MSG_UPDATE, sb.toString());
                    mHandler.sendMessage(msg);
                    sb.setLength(0);
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
            
        }).start();
        
    }

}
