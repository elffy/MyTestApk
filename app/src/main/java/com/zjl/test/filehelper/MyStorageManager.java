package com.zjl.test.filehelper;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
//import android.os.storage.StorageVolume;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class MyStorageManager {

    private static final String TAG = "MyStorageManager";

    private static final int TYPE_INTERNAL = 0;
    private static final int TYPE_EXTERNAL = 1;
    public static MyStorageManager mStorageManager;
    private File mInternalDirectory;
    private File mExternalDirectory;
    private boolean mInternalMounted;
    private boolean mExternalMounted;
    private int mInternalBlocks = 0;
    private int mExternalBlocks = 0;
    private boolean mDsDevice = false;
    private StorageManager mManager;
//    private StorageVolume[] mVolumes;
    private ArrayList<String> mPaths;

    public static MyStorageManager get(Context context) {
        if (mStorageManager == null) {
            mStorageManager = new MyStorageManager();
        }
        mStorageManager.init(context);
        return mStorageManager;
    }

    private void init(Context context) {
//        if (mManager == null || mVolumes == null) {
//            mManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//            mVolumes = mManager.getVolumeList();
//            mPaths = new ArrayList<String>();
//            Log.v(TAG, "ContactStorageManager create");
//        }
//        if (mVolumes == null) {
//            return;
//        }
//        int length = mVolumes.length;
//        mDsDevice = length > 1;
//        mPaths.clear();
//        for (int i = 0; i < length; i++) {
//            StorageVolume volume = mVolumes[i];
//            if (volume == null) continue;
//
//            String path = volume.getPath();
//            if (path == null) continue;
//
//            boolean removable = volume.isRemovable();
//            boolean mounted = Environment.MEDIA_MOUNTED.equals(mManager.getVolumeState(path));
//            int blocks = 0;
//            if (mounted) {
//                StatFs sf = new StatFs(path);
//                blocks = sf.getAvailableBlocks();
//                mPaths.add(path);
//
//                //we should only care about mounted disk
//                if (removable) {
//                    mExternalDirectory = new File(path);
//                    mExternalMounted = mounted;
//                    mExternalBlocks = blocks;
//                } else {
//                    mInternalDirectory = new File(path);
//                    mInternalMounted = mounted;
//                    mInternalBlocks = blocks;
//                }
//            }
//            Log.d(TAG, "init[path=" + path + ", mounted="
//                    + mounted + ", blocks=" + blocks + ", removable=" + removable + "]");
//        }

    }
}
