package com.baidu.zjl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MyTestApp extends Application {

    public static final String TAG = "MyTestApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App oncreate");

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
