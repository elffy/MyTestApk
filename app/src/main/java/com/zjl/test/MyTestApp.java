package com.zjl.test;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.smtt.sdk.QbSdk;

public class MyTestApp extends Application {

    public static final String TAG = "zjltest";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App oncreate");
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "onViewInitFinished:" + b);
            }
        });

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "App onTerminate");
    }

    public static void setScreenShowFlags(Context context) {
        final Window win = ((Activity) context).getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();

        params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        win.setAttributes(params);
    }

    public static void clearScreenShowFlags(Context context) {
        final Window win = ((Activity) context).getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();

        params.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        win.setAttributes(params);
    }

}
