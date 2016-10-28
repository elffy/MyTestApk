package com.zjl.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static int sNotifyId = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(MyTestApp.TAG, "action:" + action);
//        if (MyService.sInstance == null) {
//            context.startService(new Intent(context, MyService.class));
//        }
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            
        } 
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            String label = "Date changed";
            Time t = new Time();
            t.setToNow();
            String time = t.format2445();
            
            Notification n = null;
            n = new Notification(R.drawable.ic_launcher, label, System.currentTimeMillis());
            n.setLatestEventInfo(context, label, time, null);
            n.flags |= Notification.FLAG_AUTO_CANCEL;
            n.flags |= Notification.FLAG_SHOW_LIGHTS;
            n.flags |= Notification.FLAG_ONGOING_EVENT;
            n.defaults |= Notification.DEFAULT_LIGHTS;
            // Send the notification using the alarm id to easily identify the
            // correct notification.
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(1);
            nm.notify(1, n);
        }
        if (TestActivity.ALARM_INTENT.equals(action)) {
            String label = "ALARM_INTENT";
            Time t = new Time();
            t.setToNow();
            String time = t.format2445();
            
            Notification n = null;
            n = new Notification(R.drawable.ic_launcher, label, System.currentTimeMillis());
            n.setLatestEventInfo(context, label, time, null);
            n.flags |= Notification.FLAG_AUTO_CANCEL;
            // Send the notification using the alarm id to easily identify the
            // correct notification.
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            nm.cancel(1);
            nm.notify(sNotifyId, n);
            sNotifyId++;
        }

    }

}
