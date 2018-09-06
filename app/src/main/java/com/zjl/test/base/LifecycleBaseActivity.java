package com.zjl.test.base;

import android.app.Activity;
import android.os.Bundle;

public class LifecycleBaseActivity extends Activity {
    private final LifecycleHelper mLifecycle = new LifecycleHelper();

//    public Lifecycle getLifecycle() {
//        return mLifecycle;
//    }

    public void registerLifecycleObserver(LifecycleObserver observer) {
        mLifecycle.addObserver(observer);
    }
    public void unRegitsterLifecycleObserver(LifecycleObserver observer) {
        mLifecycle.removeObserver(observer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLifecycle.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        mLifecycle.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        mLifecycle.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLifecycle.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLifecycle.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLifecycle.onDestroy();
        super.onDestroy();
    }

    public boolean isAlive() {
        return !isDestroyed() && !isFinishing();
    }

}
