package com.zjl.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SimpleTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout2);
    }

    @Override
    protected void onResume() {
        MyTestApp.setScreenShowFlags(this);
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("Display: " + getResources().getDisplayMetrics().toString());
        sb.append("\n\nModel: " + android.os.Build.MODEL
                + "\n\nSDK: " + android.os.Build.VERSION.SDK
                + "\n\nAndroid: " + android.os.Build.VERSION.RELEASE);
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText(sb.toString());
    }

    public void onReturnClicked(View v) {
        startActivity(new Intent(this, TestActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MyTestApp.clearScreenShowFlags(this);
        super.onDestroy();
    }

}
