package com.baidu.zjl;

import android.app.Activity;
import android.os.Bundle;

public class SimpleTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_layout);
    }

    @Override
    protected void onDestroy() {
        MyTestApp.clearScreenShowFlags(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        MyTestApp.setScreenShowFlags(this);
        super.onResume();
    }

}
