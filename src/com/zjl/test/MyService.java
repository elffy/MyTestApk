package com.zjl.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyService extends Service {
    
    private static final String TAG = MyTestApp.TAG;
    
    private ServiceHandler mServiceHandler;
    
    private NormalHandler mNormalHandler;
    private Looper mLooper;
    
    private int id = 0;

    protected final Object mLock = new Object();
    protected final Object mLock1 = new Object();
    
    private static final int EVENT_ID_1 = 1;
    private static final int EVENT_ID_2 = 2;
    private static final int EVENT_ID_3 = 3;

    static AtomicBoolean mstatus = new AtomicBoolean(false);

    private MyReceiver mMyReceiver;

    private static final String MY_ACTION = "com.baidu.zjl.testAction";

    public static MyService sInstance = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sInstance = this;
        HandlerThread hThread = new HandlerThread(TAG);
        hThread.start();
        mLooper = hThread.getLooper();
        mServiceHandler = new ServiceHandler(mLooper);
        mNormalHandler = new NormalHandler();
        mMyReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(MY_ACTION);
        registerReceiver(mMyReceiver, filter);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log("MyReceiver received action:" + action);
            if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                
            } else if (MY_ACTION.equals(action)) {
                log("Intent:Key =" + intent.getStringExtra("Key"));
                log("Intent:Value =" + intent.getStringExtra("Value"));
            }
            
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        log("onStart startId=" + startId + ",buttonId=" + intent.getIntExtra("button", 0));
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //doMyWork();
        id++;
        log("onStartCommand startId=" + startId + ",flags=" + flags + ",buttonId=" + intent.getIntExtra("button", 0));
//        mServiceHandler.sendEmptyMessage(intent.getIntExtra("button", 0));
        Intent myIntent = new Intent(MY_ACTION);
        myIntent.putExtra("Key", "key1");
        myIntent.putExtra("Value", "value1");
        myIntent.putExtra("Key", "key2");
        myIntent.putExtra("Value", "value2");
        sendBroadcast(myIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void doMyWork() {
        new Thread(){
            int i = 0;
            public void run() {
                Log.e(TAG, "doMyWork begin :" + i++);
                for (int i = 0; i < 100000; i++) {
                    for (int j = 0; j < 10000; j++) {
                        
                    }
                }
                Log.e(TAG, "doMyWork end :" + id);
            }
        }.start();
       
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    class NormalHandler extends Handler {
        
//        public NormalHandler (Looper looper) {
//            super(looper);
//        }
        @Override
        public void handleMessage(Message msg) {
            int msgId = msg.what;
            log("NormalHandler handleMessage:" + msgId);
            switch(msgId) {
                case 520:
                    synchronized(mLock1) {
                        log("NormalHandler handleMessage:mLock1");
                        AtomicBoolean status = (AtomicBoolean) msg.obj;
                        status.set(true);
                        mLock1.notifyAll();
                    }
                    break;
                case 0:
                    synchronized(mLock) {
                        AtomicBoolean status = (AtomicBoolean) msg.obj;
                        status.set(true);
                        mLock.notifyAll();
                    }
                    break;
                case EVENT_ID_1:
                    new Thread(){
                        public void run() {
                            myFun3();
                        }
                    }.start();
                    break;
                case EVENT_ID_2:
                    new Thread(){
                        public void run() {
                            myFun2();
                        }
                    }.start();
                   
                    break;
                case EVENT_ID_3:
                    new Thread(){
                        public void run() {
                            myFun3();
                        }
                    }.start();
                    break;
                default:
            }
        }
    }
    class ServiceHandler extends Handler {

        public ServiceHandler (Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            int msgId = msg.what;
            log("ServiceHandler handleMessage:" + msgId);
            switch(msgId) {
                case 520:
                    synchronized(mLock1) {
                        log("ServiceHandler handleMessage:mLock1");
                        AtomicBoolean status = (AtomicBoolean) msg.obj;
                        status.set(true);
                        mLock1.notifyAll();
                    }
                    break;
                case 0:
                    synchronized(mLock) {
                        log("ServiceHandler handleMessage:mLock");
                        AtomicBoolean status = (AtomicBoolean) msg.obj;
                        status.set(true);
                        mLock.notifyAll();
                    }
                    break;
                case EVENT_ID_1:
                    new Thread(){
                        public void run() {
                            myFun3();
                        }
                    }.start();
                    break;
                case EVENT_ID_2:
                    new Thread(){
                        public void run() {
                            myFun2();
                        }
                    }.start();
                   
                    break;
                case EVENT_ID_3:
                    new Thread(){
                        public void run() {
                            myFun3();
                        }
                    }.start();
                    break;
                default:
            }
        }

    }

    public void myFun3() {
        synchronized(mLock) {
            log("enter into myFun3");
            AtomicBoolean status = new AtomicBoolean(false);
            mstatus.set(false);
            Message reponse = mServiceHandler.obtainMessage(0, mstatus);
            mServiceHandler.sendMessageDelayed(reponse, 2000);
            waitForResult(mstatus, 3);
            log("exit myFun3");
        }
        
    }

    public void myFun2() {
        synchronized(mLock) {
            log("enter into myFun2");
            AtomicBoolean status = new AtomicBoolean(false);
            Message reponse = mNormalHandler.obtainMessage(0, status);
            funEx();
//            try {
//                Thread.currentThread().sleep(10000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            mServiceHandler.sendMessageDelayed(reponse, 3000);
//            waitForResult(mstatus, 2);
            log("exit myFun2");
            
        }
        
    }
    
    private void funEx() {
        synchronized(mLock1) {
            log("enter into funEx");
            AtomicBoolean status = new AtomicBoolean(false);
            Message reponse = mNormalHandler.obtainMessage(520, status);
            mNormalHandler.sendMessageDelayed(reponse, 5000);
            waitForResultEx(status, 520);
            log("exit funEx");
        }
        
    }
    protected void waitForResultEx(AtomicBoolean status, int id) {
        while (!status.get()) {
            try {
                log("mLock.wait id=" + id);
                mLock1.wait();
            } catch (InterruptedException e) {
                log("interrupted");
            }
            log("mLock.wait exit id=" + id);
        }
    }
    protected void waitForResult(AtomicBoolean status, int id) {
        while (!status.get()) {
            try {
                log("mLock.wait id=" + id);
                mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted");
            }
            log("mLock.wait exit id=" + id);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMyReceiver);
        sInstance = null;
        super.onDestroy();
    }

    public void log(String msg) {
        Log.d(TAG, msg);
    }
}
