
package com.zjl.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
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
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.test.compat.StatusBarCompat;
import com.zjl.test.filehelper.FileBrowserListActivity;
import com.zjl.test.filehelper.FlipperReaderActivity;
import com.zjl.test.filehelper.SearchFileTask;
import com.zjl.test.largescreen.TestColumnActivity;
import com.zjl.test.systeminfo.SystemInfoActivity;
import com.zjl.test.utils.CMDExecute;
import com.zjl.test.utils.LocationManager;

public class TestActivity extends Activity implements OnClickListener, LocationManager.Listener {
    /** Called when the activity is first created. */

    public static final String TAG = MyTestApp.TAG;

    private static final int EVENT_MSG_1 = 1;
    private static final int EVENT_MSG_2 = 2;
    private static final int EVENT_MSG_3 = 3;
    private static final int EVENT_MSG_4 = 4;
    private static final int EVENT_MSG_5 = 5;
    private static final int EVENT_KEYGUARD_STATE = 6;
    private static final int POP_DIALOG = 7;

    private View mDecorView;

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
    
    public static final String TEST_ACTION = "smartisan.aciton.TOGGLE_FLASHLIGHT";//"android.intent.action.TEST";

    private Spinner mSpinner;
    private static String[] mTestOptions = {"xxxTest", "AlarmManager", "Add Event", "ProviderTest", "NvTest", "PackageTest", "SmsTest",
                                            "DialogTest", "ThreadTest", "CallTest", "FileTest",
                                            "FileReader"};
    public enum TestOptions {
        NONE,
        ALARM,
        ADD_EVENT,
        PROVIDER_TEST,
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

    private int timer;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case EVENT_MSG_1:
                    Log.d(TAG, "msg received");
                    startMyService(1);
                    mHandler.sendEmptyMessageDelayed(EVENT_MSG_1, 1000);
                    break;
                case EVENT_MSG_2:
                    Log.d(TAG, "msg received 2");
                    textView1.setVisibility(View.VISIBLE);
                    textView1.setText("timer:" + (timer++));
                    mHandler.sendEmptyMessageDelayed(EVENT_MSG_2, 1000);
                    break;
                case EVENT_MSG_3:
                    finish();
                    break;
                case EVENT_MSG_4:
                    Log.v(TAG, "msg received 4");
                    log("button1=" + button1);
                    log("mTestSelected=" + mTestSelected);
                    break;
                case EVENT_MSG_5:
                    Log.v(TAG, "msg received 5");
                    log("msg:" + msg.obj);
                    break;
                case EVENT_KEYGUARD_STATE:
                    getKeygaurdState();
                    break;
                case POP_DIALOG:
                    popUpDialog();
                    break;
            }

        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration mCurConfig = new Configuration();
//        try {
//            mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
//        } catch (RemoteException re) {
//            /* ignore */
//        }
        float scale = mCurConfig.fontScale;
        log("onCreate");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        // Translucent status bar
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.main);
        StatusBarCompat.setStatusBarColor(this, Color.CYAN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
//        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE,
//                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
//                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
//        setTheme(android.R.style.Theme_Light);
        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        Log.d(TAG, "onSystemUiVisibilityChange:" + visibility);
//                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                            showSystemUI();
//                        } else {
//                            hideSystemUI();
//                        }
                    }
                });

        initView();
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mSMSChangeObserver);
        this.getContentResolver().registerContentObserver(Uri.parse("content://telephony"), true, mSimInfoChangeObserver);

        // time test
        Time time = new Time();
        time.setToNow();
        printTime(time, "t1");
        time.timezone = "Asia/Tokyo";
        time.normalize(true);
        printTime(time, "2222");
        time.setToNow();
        printTime(time, "3333");
    }

    private boolean mShowSystemUI = true;
    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initView() {
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
        TextView textView31 = (TextView) findViewById(R.id.textView31);
        textView31.setTextIsSelectable(true);
//        textView1.setVisibility(View.GONE);
//        textView2.setVisibility(View.GONE);
//        textView3.setVisibility(View.GONE);
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
    }

    protected void popUpDialog() {
        Log.d(TAG, "popUpDialog");
        new AlertDialog.Builder(this)
                .setTitle("TEST").setIcon(android.R.drawable.stat_sys_warning)
                .setMessage("This is a test Dialog").setPositiveButton("OK", null)
                .setCancelable(false).create().show();
    }

    private static boolean first = true;
    @Override
    protected void onResume() {
        log("onResume");
//        MyTestApp.setScreenShowFlags(this);
        super.onResume();
//        log("first:" + first);
//        if (first) {
//            first = false;
//            startActivity(new Intent(this, TestActivity.class));
//        } else {
//            first = true;
//        }
        //get the system language setting
        String cont = getResources().getConfiguration().locale.getCountry();
        log(cont);

        // time test
        Time t2 = new Time();
        t2.setToNow();
        printTime(t2, "t2");
        t2.switchTimezone("Asia/Tokyo");
        printTime(t2, "4444");
        t2.setToNow();
        printTime(t2, "5555");
    }
    private void printTime(Time time, String prefix) {
        log(prefix + ":" + time.format2445() + ", " + time.timezone + ", " + time.toMillis(true));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log("onKeyDown:" + keyCode + ", " + event.getCharacters());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
//        mHandler.sendEmptyMessageDelayed(EVENT_MSG_4, 1000);
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
//        finish();
//        getKeygaurdState();
//        try {
//            Thread.currentThread().sleep(3000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    int queryCount = 0;
    private void getKeygaurdState() {
        KeyguardManager kgm = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        log(queryCount + ",mode:" + kgm.inKeyguardRestrictedInputMode());
        queryCount++;
        if (queryCount > 5) {
            queryCount = 0;
            return;
        }
        if (!kgm.inKeyguardRestrictedInputMode()) {
            mHandler.sendEmptyMessageDelayed(EVENT_KEYGUARD_STATE, 100);
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        log("isScreenOn:" + pm.isScreenOn());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // if do not removeMessage, looper will go on, even if the activity has been destroyed.
        mHandler.removeMessages(EVENT_MSG_1);
        mHandler.removeMessages(EVENT_MSG_2);
        // mHandler.getLooper().quit();
//        mHandler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Items that show as actions should favor the "if room" setting, which will
        // prevent too many buttons from crowding the bar. Extra items will show in the
        // overflow area.
        menu.add(0, 0, 0, "Info").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // Items that show as actions are strongly encouraged to use an icon.
        // These icons are shown without a text description, and therefore should
        // be sufficiently descriptive on their own.
//        actionItem.setIcon(android.R.drawable.ic_menu_share);
        
        menu.add(0, 1, 0, "Location").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 0, "example").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 0, R.string.systeminfo);
        menu.add(0, 4, 0, R.string.filetest);
        menu.add(0, 5, 0, "Clear logs");
        menu.add(0, 6, 0, "ListTest");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        log("menu:" + itemId + "," + item.getTitle() + "has been selected.");
        switch(itemId) {
            case 0:
                startActivity(new Intent(this, SimpleTestActivity.class));
                break;
            case 1:
                getLocation();
                break;
            case 2:
//                finish();
                break;
            case 3:
                startActivity(new Intent(this, SystemInfoActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, FileBrowserListActivity.class));
                break;
            case 5:
                clearLogs();
                break;
            case 6:
                startActivity(new Intent(this, ListTestActivity.class));
                break;
        }
        return false;
    }

    private void clearLogs() {
        // permission denied...
        CMDExecute executor = new CMDExecute();
        String[] args = new String[]{"/system/bin/rm", "events"};
        String ret = executor.runSafely(args, "/data/logs/");
//        log("/data/logs/ : " + ret);
//        ret = executor.runSafely(args, "/data/powerlog/");
//        log("/data/powerlog/ : " + ret);
    }

    private int count;
    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.button1:
                onButton1Clicked();
                textView1.setVisibility(View.VISIBLE);
                textView1.setText("button1");
                break;
            case R.id.button2:
                Toast.makeText(this, "count=" + count++, Toast.LENGTH_SHORT).show();
                log("editText1.length():" + editText1.getText().length());
                log("editText2.length():" + editText2.getText().length());
                if (mShowSystemUI) {
                    hideSystemUI();
                } else {
                    showSystemUI();
                }
                mShowSystemUI = !mShowSystemUI;
//                onButton2Clicked();
                break;
            case R.id.button3:
                String msg = "hello wtf";
                mHandler.sendMessageDelayed(mHandler.obtainMessage(EVENT_MSG_2, msg), 1000);
                msg = null;
                log("displayRotation:" + getWindowManager().getDefaultDisplay().getRotation());
//                onButton3Clicked();

                break;
        }
    }

    public void onReaderClicked(View view) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra("filePath", "/sdcard/aaa.pdf");
        startActivity(intent);
    }

    public void onBigScreenClicked(View view){
        Intent intent = new Intent(this, TestColumnActivity.class);
        startActivity(intent);
    }

    private void onButton3Clicked() {
        log("onButton3Clicked");
        TelephonyManager telMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
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
        Intent intent = new Intent(TEST_ACTION);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        this.sendBroadcast(intent);
        log("onButton2Clicked");
        startActivity(new Intent(this, FlipperReaderActivity.class));
//        logTest();
//        packageMagagerTest();
//        startMyService(2);
//        textView3.setText("button3");
    }

    public void onButton1Clicked() {
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
                traverseFileSystem();
//                simpleFileEncode();
                break;
            case FILE_READER:
                readFile();
                break;
            case ADD_EVENT:
                addEvent();
                break;
            case ALARM:
                alarmTest();
            case PROVIDER_TEST:
                providerTest();
            default:
                return;
        }
       
        // startMyService(1);
    }

    public static final String AUTHORITY = "com.smartisan.calendar.holidays";
    public static final String PERMISSION = "com.smartisan.calendar.ACCESS_HOLIDAY";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/julianday");
    private static final String[] COLUMN_NAMES = {"isHoliday", "holidayType"};
    private void providerTest() {
        Time time = new Time();
        time.set(25, 7, 2015);
        time.normalize(true);
        Uri uri;
        int julianDay = Time.getJulianDay(time.toMillis(true), time.gmtoff);
        for (int i = julianDay; i < julianDay + 60; i++) {
            uri = CONTENT_URI.buildUpon().appendPath(String.valueOf(i)).build();
            Cursor c = this.getContentResolver().query(uri, COLUMN_NAMES, null, null, null);
            time.setJulianDay(i);
            Log.d(TAG, "julianDay:" + i + "," + time.format2445());
            if (c != null) {
                c.moveToFirst();
                Log.d(TAG, "isHoliday:" + c.getInt(0));
                Log.d(TAG, "holidayType:" + c.getInt(1));
                c.close();
            }
        }
        time.year = 2015;
        time.month = 9;
        time.monthDay = 10;// 10.10, working type
        time.normalize(true);
        julianDay = Time.getJulianDay(time.toMillis(true), time.gmtoff);
        Log.d(TAG, "julianDay:" + julianDay + "," + time.format2445());
        uri = CONTENT_URI.buildUpon().appendPath(String.valueOf(julianDay)).build();
        Cursor c = this.getContentResolver().query(uri, COLUMN_NAMES, null, null, null);
        if (c != null) {
            c.moveToFirst();
            Log.d(TAG, "isHoliday:" + c.getInt(0));
            Log.d(TAG, "holidayType:" + c.getInt(1));
            if (c.getInt(0) == 1) {
                time.setJulianDay(julianDay);
                Log.d(TAG, "julianDay:" + time.format2445());
            }
            c.close();
        }
    }

    public static String ALARM_INTENT = "com.zjl.test.alarmTest";
    private void alarmTest() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        long time = System.currentTimeMillis() + 60 * 60 * 1000;
        Intent intent = new Intent(ALARM_INTENT);
//        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.set(AlarmManager.RTC, time, pi);
    }

    private static final long ONE_HOUR = 60 * 60 * 1000;
    private void addEvent() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setPackage("com.android.calendar");
        intent.setType("vnd.android.cursor.item/event");
//        intent.setClassName("com.android.calendar", "EditEventActivity");
//        Intent intent = new Intent("com.smartisan.ADD_CALENDAR_EVENT");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 61 * 60 *1000);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 121 * 60 *1000);
        intent.putExtra(CalendarContract.Events.TITLE, "18682943337 incoming call");
//        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "this is the description for this event");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "china beijing");
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170409;BYDAY=MO;");
        intent.putExtra(CalendarContract.Reminders.MINUTES, 5);
        startActivity(intent);
//        intent.putExtra("relation_app", "com.android.phone");
//        intent.putExtra("relation_id", "18682943337");
//        sendBroadcast(intent);
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
        AlertDialog d = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
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

    private int mFileCount;
    private int mFolderCount;
    private HashMap<String, Integer> mFolderToCountMap;
    private ArrayList<String> mPathList;
    public static String sPrimaryStorageRoot = Environment.getExternalStorageDirectory().toString();
    private void traverseFileSystem() {
        mFolderToCountMap = new HashMap<String, Integer>();
        mPathList = new ArrayList<String>();
        mFileCount = 0;
        mFolderCount = 0;
/*        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("zjltest", "traverseFileSystem");
                File[] files = new File(sPrimaryStorageRoot).listFiles();
                for (File f : files) {
                    traverseFolderRecursive(f);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d("zjltest", "mFileCount:" + mFileCount);
                Log.d("zjltest", "mFolderCount:" + mFolderCount);
                for(String path : mPathList) {
                    Log.e("zjltest", path + " : " + mFolderToCountMap.get(path));
                }
            }
        }.execute();*/
        new SearchFileTask(null).execute();
    }

    private static final String NOMEDIA = ".nomedia";
    private int traverseFolderRecursive(File folder) {
        File[] files = folder.listFiles();
        int subFilesCount = 0;
        if (files != null) {
            subFilesCount += files.length;
            for (File f : files) {
                mFileCount++;
                if (f.isDirectory()) {
                    mFolderCount++;
                    if (canSearch(f)) {
                        subFilesCount += traverseFolderRecursive(f);
                    }
                }
            }
        }
        if (subFilesCount >= 100) {
            String folerName = folder.getAbsolutePath().substring(sPrimaryStorageRoot.length() + 1);
            if (folerName.split("/").length <= 3) {
                mFolderToCountMap.put(folerName, subFilesCount);
                mPathList.add(folerName);
            }
        }
        return subFilesCount;
    }
    private boolean canSearch(File folder) {
        if (folder.isHidden()) {
            return false;
        }
        return true;
    }

    private void xxxTest() {
        
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
//        n.setLatestEventInfo(this, label, this.getString(R.string.app_name), pendingNotify);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
//        n.flags |= Notification.FLAG_SHOW_LIGHTS;
//        n.flags |= Notification.FLAG_ONGOING_EVENT;
//        n.defaults |= Notification.DEFAULT_LIGHTS;
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
            textView2.setText("Loction:" + "(" + loc.getLatitude() + "," + loc.getLongitude() + ")"
                    + ",Altitude:" + loc.getAltitude() + "\n Address:" + mLocationManager.getAddress(loc));
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
        if (loc != null) {
            textView2.setText("Loction:" + "(" + loc.getLatitude() + "," + loc.getLongitude() + ")"
                    + ",Altitude:" + loc.getAltitude() + "\n Address:" + mLocationManager.getAddress(loc));
        } else {
            textView2.setText("Loction: unknown");
        }
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
