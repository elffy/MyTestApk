
package com.baidu.zjl.systeminfo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.os.Build;

import com.baidu.zjl.utils.CMDExecute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SystemInfoUtils {

    /**
     * @return system version info
     */
    public static String fetch_version_info() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        try {
            String[] args = {
                    "/system/bin/cat", "/proc/version"
            };
            result = cmdexe.run(args, "system/bin/");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static StringBuffer buffer = null;

    private static String initProperty(String description, String propertyStr) {
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        buffer.append(description).append(":");
        buffer.append(System.getProperty(propertyStr)).append("\n");
        return buffer.toString();
    }

    /**
     * get system property info
     * System.getProperty(xxx)
     */
    public static String getSystemProperty() {
        buffer = new StringBuffer();
        initProperty("java.vendor.url", "java.vendor.url");
        initProperty("java.class.path", "java.class.path");
        //more...
        return buffer.toString();
    }

    /**
     * @param cx
     * @return Telephony info
     */
    public static String fetch_tel_status(Context cx) {
        String result = null;
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder str = new StringBuilder();
        str.append("DeviceId(IMEI) = " + tm.getDeviceId() + "\n");
        str.append("DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion()+"\n");
        // TODO: Do something ...
         int mcc = cx.getResources().getConfiguration().mcc;
        int mnc = cx.getResources().getConfiguration().mnc;
        str.append("IMSI MCC (Mobile Country Code): " +String.valueOf(mcc) + "\n");
        str.append("IMSI MNC (Mobile Network Code): " +String.valueOf(mnc) + "\n");
        str.append("phone number:" + tm.getLine1Number() + "\n");
        result = str.toString();
        return result;
    }

    /**
     * @return CPU info
     */
    public static String fetch_cpu_info() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        try {
            String[ ] args = {"/system/bin/cat", "/proc/cpuinfo"};
            result = cmdexe.run(args, "/system/bin/");
            Log.i("result", "result=" + result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     *@return get Memory Info
     */
     public static String getMemoryInfo(Context context) {
        StringBuffer memoryInfo = new StringBuffer();
        
        final ActivityManager activityManager = 
            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        
        memoryInfo.append("\nTotal Available Memory :").append(outInfo.availMem >> 10).append("k");
        memoryInfo.append("\nTotal Available Memory :").append(outInfo.availMem >> 20).append("k");
        memoryInfo.append("\nIn low memory situation:").append(outInfo.lowMemory);
        
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        try {
            String[ ] args = {"/system/bin/cat", "/proc/meminfo"};
            result = cmdexe.run(args, "/system/bin/");
        } catch (IOException ex) {
            Log.i("fetch_process_info","ex=" + ex.toString());
        }
        return (memoryInfo.toString() + "\n\n" + result);
    }


     /**
      * get disk info
     * @return
     */
    public static String fetch_disk_info() {
         String result = null;
         CMDExecute cmdexe = new CMDExecute();
         try {
             String[ ] args = {"/system/bin/df"};
             result = cmdexe.run(args, "/system/bin/");
             Log.i("result", "result=" + result);
         } catch (IOException ex) {
             ex.printStackTrace();
         }
         return result;
     }

    /**
     * fetch network config info
     * @return
     */
    public static String fetch_netcfg_info() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();
        
        try {
            String[ ] args = {"/system/bin/netcfg"};
            result = cmdexe.run(args, "/system/bin/");
        } catch (IOException ex) {
            Log.i("fetch_process_info","ex=" + ex.toString());
        }
        return result;
    }

    /**
     * get system DisplayMetrics info
     * @param cx
     * @return
     */
    public static String getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        str += "The absolute width: " + String.valueOf(screenWidth) + "pixels\n";
        str += "The absolute heightin: " + String.valueOf(screenHeight) + "pixels\n";
        str += "The logical density of the display. : " + String.valueOf(density) + "\n";
        str += "X dimension : " + String.valueOf(xdpi) +"pixels per inch\n";
        str += "Y dimension : " + String.valueOf(ydpi) +"pixels per inch\n";
        return str;
    }

    /**
     * getInstalledApps
     * @param context
     * @return
     */
    public static ArrayList getInstalledApps (Context context) {
        List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(packages.size());
        
        Iterator<ApplicationInfo> l = packages.iterator();
        while (l.hasNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            ApplicationInfo app = (ApplicationInfo) l.next();
            String packageName = app.packageName;
            String label = " ";
            try {
               label = context.getPackageManager().getApplicationLabel(app).toString();
            } catch (Exception e) {
                Log.i("Exception", e.toString());
            }
            map.put("name", label);
            map.put("info", packageName);
            list.add(map);
        }
        return list;
    }

    //正在运行的服务列表
    /**
     * getRunningServicesInfo
     * @param context
     * @return
     */
    public static ArrayList getRunningServicesInfo(Context context) {
        StringBuffer serviceInfo = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context. ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = activityManager.getRunningServices(100);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(services.size());
        Iterator<RunningServiceInfo> l = services.iterator();
        while (l.hasNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            RunningServiceInfo si = (RunningServiceInfo) l.next();
            serviceInfo.append("pid: ").append(si.pid);
            serviceInfo.append("\nprocess: ").append(si.process);
//            serviceInfo.append("\nservice: ").append(si.service.getShortClassName());
            serviceInfo.append("\ncrashCount: ").append(si. crashCount);
            serviceInfo.append("\nclicentCount: ").append(si.clientCount);
            serviceInfo.append("\nactiveSince:").append(si.activeSince);
            serviceInfo.append("\nlastActivityTime: ").append(si.lastActivityTime);
            serviceInfo.append("\n");
            map.put("name", si.service.getShortClassName());
            map.put("info", serviceInfo.toString());
            list.add(map);
            serviceInfo.delete(0, serviceInfo.length());    //clear StringBuilder
        }
        return list;
    }

    public static ArrayList getRunningTaskInfo(Context context) {
        StringBuffer sInfo = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context. ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(100);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(tasks.size());
        Iterator<RunningTaskInfo> l = tasks.iterator();
        while (l.hasNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            RunningTaskInfo ti = (RunningTaskInfo) l.next();
            sInfo.append("id: ").append(ti.id);
            sInfo.append("\nbaseActivity: ").append(ti.baseActivity.flattenToString());
            sInfo.append("\nnumActivities: ").append(ti.numActivities);
            sInfo.append("\nnumRunning: ").append(ti.numRunning);
            sInfo.append("\ndescription: ").append(ti.description);
            sInfo.append("\n");
            map.put("info", sInfo.toString());
            list.add(map);
            sInfo.delete(0, sInfo.length());    //clear StringBuilder
        }
        return list;
    }

    public static String dumpSystemInfo() {
        StringBuilder sInfo = new StringBuilder();
        sInfo.append("DEVICE:" + Build.DEVICE);
        sInfo.append("\nMODEL:" + Build.MODEL);
        sInfo.append("\nVERSION:" + Build.VERSION.SDK_INT);
        sInfo.append("\nTYPE:" + Build.TYPE);
        return sInfo.toString();
    }
}
