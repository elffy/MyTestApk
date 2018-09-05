package com.zjl.test;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class SimpleTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout2);
        Uri uri = getIntent().getData();
        Uri ExtraUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri == null) {
            uri = ExtraUri;
        }
        Log.e("zjltest", "uri:" + uri + ",EXTRA_STREAM:" + ExtraUri);
        if (uri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                byte[] bytes = new byte[1024];
                is.read(bytes);
                is.close();
                Log.e("zjltest", "read successfully!!!");
            } catch (IOException e) {
                Log.e("zjltest", "IOException:" + e);
            }/* catch (SecurityException e) {
                Log.e("zjltest", "SecurityException:" + e);
            }*/
        }
    }

    @Override
    protected void onResume() {
        Log.d("zjltest", "onResume");
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
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("zjltest", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("zjltest", "onDestroy");
        MyTestApp.clearScreenShowFlags(this);
        super.onDestroy();
    }

}
