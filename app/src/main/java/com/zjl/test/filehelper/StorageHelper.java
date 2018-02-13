package com.zjl.test.filehelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Note that this class has overlay file under /overlay/below6.0/
 * Modification about public methods should be synced.
 *
 */
public class StorageHelper {

    private static final String TAG = "FM_StorageHelper";

    // storage types we supported
    public static final int TYPE_PRIMARY_STORAGE = 1;
    public static final int TYPE_SD = 2;
    public static final int TYPE_USB = 3;
    public static final int TYPE_DOPPELGANGER = 4;
    public static final int TYPE_MTP = 5;

    // these flags are added in Android7.0
    public static final int FLAG_SUPPORTS_IS_CHILD = 1 << 4;//Root.FLAG_SUPPORTS_IS_CHILD
    public static final int FLAG_REMOVABLE_SD = 1 << 19;//Root.FLAG_REMOVABLE_SD;
    public static final int FLAG_REMOVABLE_USB = 1 << 20;//Root.FLAG_REMOVABLE_USB;

    public static final String STORAGE_ROOT = "/storage";

    private static StorageHelper sInstance;
    public static String sPrimaryStorageRoot = Environment.getExternalStorageDirectory().toString();
    public static String sDoppelgangerRoot;

    Context mContext;
    ArrayList<StorageChangeListener> mListeners = new ArrayList<StorageChangeListener>();
    BroadcastReceiver mReceiver;
    ArrayList<RootInfo> mUsbInfoList = new ArrayList<RootInfo>();

    public interface StorageChangeListener {
        /**
         * @param type the storage type, sd or usb.
         * @param mount mount or unmount.
         */
        void onStorageChanged(int type, boolean mount);
    }

    private StorageHelper(Context context) {
        mContext = context;
    }

    public static StorageHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StorageHelper(context);
        }
        return sInstance;
    }

    public void unRegisterReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mListeners.clear();
        mUsbInfoList.clear();
    }

    public void addListener(StorageChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(StorageChangeListener listener) {
        mListeners.remove(listener);
    }

    public boolean isStorageRootPath(int type, String path) {
        switch (type) {
            case TYPE_SD:
                return path.equals(getSdCardRootPath());
            case TYPE_USB:
                if (getUsbRootInfoByPath(path) == null) {
                    return false;
                }
                return path.equals(getUsbRootInfoByPath(path).path);
            case TYPE_DOPPELGANGER:
                return path.equals(getDoppelgangerUserPath());
            default:
                return sPrimaryStorageRoot.equals(path);
        }
    }

    public boolean canCopyBetweenStorage(int storageType) {
        return storageType != TYPE_PRIMARY_STORAGE || mUsbInfoList.size() > 0;
    }

    public int getStorageType(String path) {
       if (path.startsWith(sPrimaryStorageRoot)) {
           return TYPE_PRIMARY_STORAGE;
       } else if (getSdCardRootPath() != null && path.startsWith(getSdCardRootPath())) {
           return TYPE_SD;
       } else if (getUsbRootInfoByPath(path) != null) {
           return TYPE_USB;
       } else if (getDoppelgangerUserPath() != null && path.startsWith(getDoppelgangerUserPath())) {
           return TYPE_DOPPELGANGER;
       }
       return TYPE_PRIMARY_STORAGE;
    }

    public String getSdCardRootPath() {
        File storageRoot = new File(STORAGE_ROOT);
        File[] files = storageRoot.listFiles();
        for (File file : files) {
            String path = file.getPath();
            Log.d("zjltest", "path:" + path);
            if ("emulated".equals(file.getName()) || "self".equals(file.getName())) {

            } else {
//                return path;
            }
        }
        return null;
    }

    public String getUsbStorageTitle(String uuid, String path) {
        if (uuid != null) {
            return getRootInfo(uuid).title;
        } else {
            RootInfo info = getUsbRootInfoByPath(path);
            return info == null ? null : info.title;
        }
    }

    public List<RootInfo> getUsbRootInfos() {
        return mUsbInfoList;
    }

    public RootInfo getRootInfo(String uuid) {
        int count = mUsbInfoList.size();
        if (count > 0) {
            if (count == 1) {
                mUsbInfoList.get(0);
            } else {
                for (int i = 0; i < count; i++) {
                    if (mUsbInfoList.get(i).fsUuid.equals(uuid)) {
                        return mUsbInfoList.get(i);
                    }
                }
            }
        }
        return null;
    }

    public RootInfo getUsbRootInfoByPath(String path) {
        int count = mUsbInfoList.size();
        if (count > 0 && path != null) {
            for (int i = 0; i < count; i++) {
                if (path.startsWith(mUsbInfoList.get(i).path)) {
                    return mUsbInfoList.get(i);
                }
            }
        }
        return null;
    }


    /*************** doppelganger User related code **************/
    public static final String DEFAULT_DOPPELGANGER_USER_PATH = "/storage/emulated/10";

    public static String getDoppelgangerUserPath() {
        return DEFAULT_DOPPELGANGER_USER_PATH;
    }

}
