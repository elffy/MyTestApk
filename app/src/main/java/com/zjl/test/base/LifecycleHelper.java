package com.zjl.test.base;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


/**
 * Manage the observers and dispatch the lifecycle events
 */
public class LifecycleHelper {

    protected final List<LifecycleObserver> mObservers = new ArrayList<>();

    /**
     * Registers a new observer of lifecycle events.
     */
    public <T extends LifecycleObserver> T addObserver(T observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
        return observer;
    }

    public <T extends LifecycleObserver> void removeObserver(T observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        for (LifecycleObserver observer : mObservers) {
            observer.onCreate();
        }
    }

    public void onStart() {
        for (LifecycleObserver observer : mObservers) {
            observer.onStart();
        }
    }

    public void onResume() {
        for (LifecycleObserver observer : mObservers) {
            observer.onResume();
        }
    }

    public void onPause() {
        for (LifecycleObserver observer : mObservers) {
            observer.onPause();
        }
    }

    public void onStop() {
        for (LifecycleObserver observer : mObservers) {
            observer.onStop();
        }
    }

    public void onDestroy() {
        for (LifecycleObserver observer : mObservers) {
            observer.onDestroy();
        }
    }

    public static class DefaultLifecycleObserver implements LifecycleObserver {
        @Override
        public void onCreate() {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }
    }
}
