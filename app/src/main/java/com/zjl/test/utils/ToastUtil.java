package com.zjl.test.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zengjinlong on 18-3-14.
 */

public class ToastUtil {
    private static String sLastMsg;
    private static Toast sToast = null;
    private static long sLastShowTime = 0;

    /**
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            sToast.show();
            sLastShowTime = System.currentTimeMillis();
        } else {
            long timeMillis = System.currentTimeMillis();
            if (message.equals(sLastMsg)) {
                if (timeMillis - sLastShowTime > Toast.LENGTH_SHORT) {
                    sToast.show();
                }
            } else {
                sLastMsg = message;
                sToast.setText(message);
                sToast.show();
            }
            sLastShowTime = timeMillis;
        }
    }
}
