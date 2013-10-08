
package com.baidu.zjl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.zjl.filehelper.FileBrowserListActivity;
import com.baidu.zjl.filehelper.FlipperReaderActivity;
import com.baidu.zjl.systeminfo.SystemInfoActivity;
import com.baidu.zjl.utils.LocationManager;

public class TestActivity extends Activity implements OnClickListener, LocationManager.Listener {
    /** Called when the activity is first created. */

    public static final String TAG = MyTestApp.TAG;

    private static final int EVENT_MSG_1 = 1;
    private static final int EVENT_MSG_2 = 2;
    private static final int EVENT_MSG_3 = 3;
    private static final int EVENT_MSG_4 = 4;
    private static final int EVENT_MSG_5 = 5;
    private Handler mHandler;

    private Button button1;
    private Button button2;
    private Button button3;

    private EditText editText1;
    private EditText editText2;

    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private CheckBox checkBox1;
    
    public static final String TEST_ACTION = "android.intent.action.TEST";

    private Spinner mSpinner;
    private static String[] mTestOptions = {"xxxTest", "NvTest", "PackageTest", "SmsTest",
                                            "DialogTest", "ThreadTest", "CallTest", "FileTest",
                                            "FileReader"};
    public enum TestOptions {
        NONE,
        NV_TEST,
        PACKAGE_TEST,
        SMS_TEST,
        DIALOG_TEST,
        THREAD_TEST,
        CALL_TEST,
        FILE_TEST,
        FILE_READER
    };

    public TestOptions mTestSelected;
    private ArrayAdapter<String> mSpinnerAdapter;

    private static final String MSG_SENT_ACTION = "com.baidu.zjl.msg_sent";

    public static final String URI = "content://sendTestSms/";

    private static final int REQUEST_ENCODE_FILE = 1;

    private static final int REQUEST_READ_FILE = 2;

    private int timer = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        setContentView(R.layout.main);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
//                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
//                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
//        setTheme(android.R.style.Theme_Light);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);

        textView = (TextView) findViewById(R.id.textview);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox1.setChecked(true);
//        checkBox1.setEnabled(false);

        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTestOptions);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mTestSelected = TestOptions.values()[arg2];
                textView.setText(arg0.getItemAtPosition(arg2).toString());
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                
            }
            
        });

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (msg.what == EVENT_MSG_1) {
                    Log.d(TAG, "msg received");
                    startMyService(1);
                    mHandler.sendEmptyMessageDelayed(EVENT_MSG_1, 1000);
                } else if (msg.what == EVENT_MSG_2) {
                    Log.d(TAG, "msg received 2");
                    textView1.setVisibility(View.VISIBLE);
                    textView1.setText("timer:" + (timer++));
                    mHandler.sendEmptyMessageDelayed(EVENT_MSG_2, 1000);
                } else if (msg.what == EVENT_MSG_3) {
                    finish();
                } else if (msg.what == EVENT_MSG_4) {
                    Log.v(TAG, "msg received 4");
                    log("button1=" + button1);
                    log("mTestSelected=" + mTestSelected);
                } else if (msg.what == EVENT_MSG_5) {
                    Log.v(TAG, "msg received 5");
                    log("msg:" + msg.obj);
                }
            }

        };
        
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mSMSChangeObserver);
        this.getContentResolver().registerContentObserver(Uri.parse("content://telephony"), true, mSimInfoChangeObserver);
    }

    @Override
    protected void onResume() {
        log("onResume");
        MyTestApp.setScreenShowFlags(this);
        super.onResume();
    }

    private final ContentObserver mSMSChangeObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfUpdate) {
            Log.d(TAG, "mSMSChangeObserver onChange");
        }
    };
    
    private final ContentObserver mSimInfoChangeObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfUpdate) {
            Log.d(TAG, "mSimInfoChangeObserver onChange");
        }
    };
    protected void startMyService(int id) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("button", id);
        startService(intent);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mHandler.sendEmptyMessageDelayed(EVENT_MSG_4, 1000);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.d(TAG, "onStop");
        MyTestApp.clearScreenShowFlags(this);
        if (mLocationManager != null) {
            mLocationManager.recordLocation(false);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // if do not removeMessage, looper will go on, even if the activity has been destroyed.
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        // mHandler.getLooper().quit();
        mHandler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.systeminfo);
        menu.add(0, 1, 0, R.string.filetest);
        menu.add(0, 2, 0, "simpleActivity");
        menu.add(0, 3, 0, "Location");
        
        MenuItem actionItem = menu.add(0, 2, 0, "Action Button");

        // Items that show as actions should favor the "if room" setting, which will
        // prevent too many buttons from crowding the bar. Extra items will show in the
        // overflow area.
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Items that show as actions are strongly encouraged to use an icon.
        // These icons are shown without a text description, and therefore should
        // be sufficiently descriptive on their own.
        actionItem.setIcon(android.R.drawable.ic_menu_share);
        
        menu.add(0, 3, 0, "just").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 0, "example").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        log("menu:" + itemId + "," + item.getTitle() + "has been selected.");
        switch(itemId) {
            case 0:
                startActivity(new Intent(this, SystemInfoActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, FileBrowserListActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, SimpleTestActivity.class));
                break;
            case 3:
                getLocation();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.button1:
                onButton1Clicked();
                // textView1.setText("button1");
                break;
            case R.id.button2:
                onButton2Clicked();
                break;
            case R.id.button3:
                String msg = "hello wtf";
                mHandler.sendMessageDelayed(mHandler.obtainMessage(EVENT_MSG_2, msg), 1000);
                msg = null;
//                onButton3Clicked();

                break;
        }
    }

    private void onButton3Clicked() {
        log("onButton3Clicked");
        TelephonyManager telMgr = (TelephonyManager)this.getSystemService("phone");
        log("SPN:" + telMgr.getSimOperator());
        log("SPN:" + telMgr.getSimOperatorName());
        mHandler.sendEmptyMessageDelayed(EVENT_MSG_2, 1000);
        try {
            Process proc = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        finish();
//        SmsTest();
//        startMyService(3);
//        textView2.setText("button2");
    }

    private void onButton2Clicked() {
        log("onButton2Clicked");
        startActivity(new Intent(this, FlipperReaderActivity.class));
//        logTest();
//        packageMagagerTest();
//        startMyService(2);
//        textView3.setText("button3");
    }

    public void onButton1Clicked() {
        log("onButton1Clicked");
        switch(mTestSelected) {
            case NONE:
                xxxTest();
                break;
            case NV_TEST:
                nvTest();
                break;
            case PACKAGE_TEST:
                packageMagagerTest();
                break;
            case SMS_TEST:
                SmsTest();
                break;
            case DIALOG_TEST:
                dialogTest();
                break;
            case THREAD_TEST:
                threadTest();
                break;
            case CALL_TEST:
                callTest();
                break;
            case FILE_TEST:
                simpleFileEncode();
                break;
            case FILE_READER:
                readFile();
                break;
            default:
                return;
        }
       
        // startMyService(1);
    }

    private Process mprocess;
    private static final String LOG_DIR = "/sdcard/log/";

    private void logTest() {
        File dir = new File(LOG_DIR);
        try {
            mprocess = new ProcessBuilder(
                    new String[] {
                            "/system/bin/logcat",
                            "-v", "time", "-b", "main", "-f",
                            new File(dir, "main.txt").getAbsolutePath()
                    }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void packageMagagerTest() {
        PackageManager pm = getPackageManager();
        ApplicationInfo ai = this.getApplicationInfo();
        log("ApplicationInfo:" + ai.toString());
        pm.getInstalledApplications(0);
        
    }

    private void SmsTest() {
        TelephonyManager tlmgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        log("hasIccCard:" + tlmgr.hasIccCard());
        String address;
        String msg;
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                new Intent(MSG_SENT_ACTION, Uri.parse(URI), this, TestActivity.class), 0);
        try {
            SmsManager.getDefault().sendTextMessage("10010", null, "helloworld", pi, null);
        } catch (Exception e) {
            log("error:" + e);
            e.printStackTrace();
        }
        
    }

    private void dialogTest() {
        MyDialogListener mListener = new MyDialogListener();
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle("Hey")
                .setIcon(android.R.drawable.stat_sys_warning)
                .setMessage("sure to make a call?")
                .setPositiveButton("OK", mListener)
                .setNeutralButton("Cancel", null).setCancelable(false)
                .create();

        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        d.show();
    }

    private void nvTest() {
//        String input = editText1.getText().toString();
//        int itemId = Integer.valueOf(input);
//        QcNvItems sQcNvItems = new QcNvItems();
//        try {
//            byte[] ret = sQcNvItems.readNvEx(itemId);
//            log("readNv " + itemId + ":" + Utils.bytesToHexString(ret));
//            textView1.setText("readNv:" + Utils.bytesToHexString(ret));
//        } catch (IOException e1) {
//            Log.e(TAG, "readNv iccInfo error:" + e1.toString());
//            return;
//        }
    }

    private void callTest() {
        launchCall();
    }

    private void simpleFileEncode() {

      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("text/*");
      startActivityForResult(intent, REQUEST_ENCODE_FILE);
    }

    private void xxxTest() {
//        Intent intent = new Intent(TEST_ACTION);
//        this.sendBroadcast(intent);
        
        //---the following is about standby current issue---//
        // Trigger a notification that, when clicked, will show the alarm alert
        // dialog. No need to check for fullscreen since this will always be
        // launched from a user action.
        Intent notify = new Intent(this, TestActivity.class);
        notify.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNotify = PendingIntent.getActivity(this, 1, notify, 0);

        // Use the alarm's label or the default label as the ticker text and
        // main text of the notification.
        String label = TAG;
        
        Notification n = null;
        n = new Notification(R.drawable.ic_launcher, label, System.currentTimeMillis());
        n.setLatestEventInfo(this, label,
                this.getString(R.string.app_name),
                pendingNotify);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.flags |= Notification.FLAG_ONGOING_EVENT;
        n.defaults |= Notification.DEFAULT_LIGHTS;
        // Send the notification using the alarm id to easily identify the
        // correct notification.
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, n);
    }

    private void readFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(intent, REQUEST_READ_FILE);
        
    }

    private void threadTest() {
        log("mainThread:" + Thread.currentThread().toString());
        new Thread(new Runnable(){

            @Override
            public void run() {
                log("workThread:" + Thread.currentThread().toString());
                for (int i = 0; i < 10; i++) {                    
                    log("time:" + i);
                    try {
//                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }).start();
        mHandler.sendEmptyMessageDelayed(EVENT_MSG_3, 2000);
    }

    class MyDialogListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialog) {
            log("canceled");
            
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                log("BUTTON_POSITIVE");
                launchCall();
                
            } else if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
                log("BUTTON_NEGATIVE");
            }
            
        }
    }

    public void launchCall() {
        String number = editText1.getText().toString();
        if (number == null || number.equals("")) {
            Toast.makeText(this, "please input number", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        log("number:" + uri.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Uri uri = data.getData();
        log("uri=" + uri);
        if (requestCode == REQUEST_ENCODE_FILE) {
            try {
                String filePath = getFilePath(uri);
                File srcFile = new File(filePath);
                String filename = srcFile.getName();
                filename = filename.substring(0, filename.lastIndexOf("."));
                log("file:" + filePath + "," + srcFile.getParent() + "," + filename);
                
                File destFile = new File(srcFile.getParent() + File.separator + filename);
                if (!destFile.exists() && !destFile.createNewFile()) {
                    return;
                }
                log("work begin:" + System.currentTimeMillis());
                FileInputStream fis = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(destFile);
                int length = 0;
                byte[] ret = new byte[1024];
//                int b = 0;
//                while ((b = fis.read()) != -1) {
//                    b ^= 0x0ef;
//                    fos.write(b);
//                }
                while ((length = fis.read(ret)) != -1) {
                    fos.write(ret, 0, length);
                }
                fos.flush();
                fos.close();
                fis.close();
                log("work done:" + System.currentTimeMillis());
            } catch (NullPointerException e) {

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_READ_FILE){
            String filePath = getFilePath(uri);
            File srcFile = new File(filePath);
            if (!srcFile.exists()) {
                Toast.makeText(this, "file dose not exists!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ShowInfoActivity.class);
            intent.putExtra("filePath", filePath);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFilePath(Uri uri) {
        log("getScheme:" + uri.getScheme());
        if ("file".equals(uri.getScheme())) {
            log("getPath:" + uri.getPath());
            return uri.getPath();
        }
        //else uri.getScheme() == "content"
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        try {
            cursor.moveToFirst();
            int count = cursor.getColumnCount();
            log("cursor count=" + count);
//            for (int i = 0; i < count; i++) {
//                log(cursor.getColumnName(i) + "\t:" + cursor.getString(i));
//            }
            filePath = cursor.getString(1);
            log("filePath:" + filePath);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

    public void log(String msg) {
        Log.d(TAG, msg);
    }

    private LocationManager mLocationManager;
    private void getLocation() {
        if (mLocationManager == null) {
            mLocationManager = new LocationManager(this, this);
        }
        textView1.setVisibility(View.VISIBLE);
        boolean isOn = mLocationManager.isProviderEnabled();
        textView1.setText("GPS:" + (isOn ? "on" : "off"));
        mLocationManager.recordLocation(true);
        Location loc = mLocationManager.getCurrentLocation();
        textView2.setVisibility(View.VISIBLE);
        if (loc != null) {
            textView2.setText("Loction:" + "(" + loc.getLatitude() + "," + loc.getLongitude() + ")");
        } else {
            textView2.setText("Loction: unknown");
        }
    }

    @Override
    public void showGpsOnScreenIndicator(boolean hasSignal) {
        textView3.setVisibility(View.VISIBLE);
        textView3.setText("GPS:" + (hasSignal ? "hasSignal" : "noSingal"));
    }

    @Override
    public void hideGpsOnScreenIndicator() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLocationChanged(Location loc) {
        textView2.setVisibility(View.VISIBLE);
        textView2.setText("Loction:" + "(" + loc.getLatitude() + "," + loc.getLongitude() + ")");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(TAG, "current orientation is landscape.");
        } else {
            Log.v(TAG, "current orientation is portait.");
        }
        super.onConfigurationChanged(newConfig);
    }

    
}
