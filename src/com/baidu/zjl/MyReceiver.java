package com.baidu.zjl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(MyTestApp.TAG, "action:" + action);
//        if (MyService.sInstance == null) {
//            context.startService(new Intent(context, MyService.class));
//        }
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            
        } 

    }

}
