package com.zjl.test.filehelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import static android.R.attr.name;

/**
 * AsyncTask for search file.
 */
public class SearchFileTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "FM_SearchFileTask";


    // these are black list of folders which we can ingore.

    public static final String ANDROID_SYSTEM = "Android";// the folder created by android sytem

    public static final String SMARTISAN = "smartisan";// the folder created by smartisan
    public static final String SMARTISAN_ALBUMART = "music/albumart";

    // folder like /tencent/MicroMsg/528ae84413905ffb9956ba571e46a2dc/ may have huge amount of files and folders.
    // we need filter out this folder, it doesn't contain readable, useful info anyway.
    public static final String TENCENT_MICROMSG = "MicroMsg";// the folder /Tencent/MicroMsg/
    public static final int TENCENT_USER_ID_LENGTH = 32;// i.e. 528ae84413905ffb9956ba571e46a2dc
    public static final String TENCENT = "Tencent";
    public static final String TENCENT_UPDATE = "MicroMsg/CheckResUpdate";
    public static final String TENCENT_WALLET = "MicroMsg/wallet";
    public static final String TENCENT_REDPACKET = "MobileQQ/RedPacket";
    public static final String TENCENT_QBIZ = "MobileQQ/qbiz";

    public static final String MAP = "map"; // Qmap & Amap
    public static final String BAIDU_MAP = "BaiduMap"; // BaiduMap

    private static final long MIN_UPDATE_INTEVAL = 200;// avoid update too frequently
    private int mFoundCount = 0;
    private long mLastUpdateTime = 0;
    private boolean mIsCanceled;
    private String mQueryText;
    public static int sNewestTaskId = 0;
    private int mId;

    // these are for test purpose.
    private long mStartTime;
    private int mFileCount = 0;
    private int mFolderCount = 0;

    public SearchFileTask(String queryText) {
//        mQueryText = queryText.toLowerCase();// transfer to lower case
        sNewestTaskId++;
        mId = sNewestTaskId;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... params) {
        mStartTime = System.currentTimeMillis();

        searchRecursiveFromRoot(StorageHelper.sPrimaryStorageRoot);
        StorageHelper storageHelper = StorageHelper.getInstance(null);
        String sdRoot = storageHelper.getSdCardRootPath();
        if (!TextUtils.isEmpty(sdRoot)) {
            searchRecursiveFromRoot(sdRoot);
        }
        return true;
    }

    private void searchRecursiveFromRoot(String rootPath) {
        if (mIsCanceled) {
            return;
        }
        File[] files = new File(rootPath).listFiles();
        String name;
        Set<String> folderSet = FolderFilter.getFilterSet();
        for (File f : files) {
            name = f.getName();
            if (mIsCanceled) {
                return;
            }
            mFileCount++;
            if (f.isDirectory()) {
                mFolderCount++;
                String lowerCaseName = name.toLowerCase();
                if (folderSet.contains(lowerCaseName)) {
                    searchByFilter(FolderFilter.getFilterFolder(lowerCaseName), f);
                } else {
                    if (canSearch(f)) {
                        Log.d("zjltest", "searchRecursive for folder:" + lowerCaseName);
                        searchRecursive(f);
                    }
                }
            }
        }

        ArrayList<FolderFilter.Folder> folders = FolderFilter.getSpecialFolders();
        for (int i = 0; i < folders.size(); i++) {
            searchByFilter(folders.get(i));
        }
    }

    private void searchByFilter(FolderFilter.Folder folder) {
        File file = new File(getFullPath(folder.mFolderName));
        if (file.exists()) {
            searchByFilter(folder, file);
        }
    }
    private static final String NOMEDIA = ".nomedia";
    private void searchByFilter(FolderFilter.Folder folder, File filterFile) {
        Log.d("zjltest", "searchByFilter for folder:" + folder.mFolderName );
        if (folder.mIgnoreAll) {
            Log.d("zjltest", "ignore all for folder:" + folder.mFolderName );
            return;
        }

        if (folder.mUsefulSubDirs != null) {
            File file = new File(getFullPath(folder.mFolderName));
            if (file.exists()) {
                File subFile;
                for (String fileName : folder.mUsefulSubDirs) {
                    subFile = new File(file, fileName);
                    if (subFile.exists()) {
                        Log.d("zjltest", "search useful folder:" + folder.mFolderName + "/" + fileName);
                        searchRecursive(subFile);
                    }
                }
            }
        } else if (folder.mIgnoreSubDirs != null) {
            File[] files = filterFile.listFiles();
            if (files != null) {
                String fileName;
                for (File f : files) {
                    mFileCount++;
                    if (f.isDirectory()) {
                        fileName = f.getName().toLowerCase();
                        if (folder.mIgnoreSubDirs.contains(fileName)) {
                            Log.d("zjltest", "ignore folder:" + folder.mFolderName + "/" + fileName);
                            continue;
                        }
                        mFolderCount++;
                        if (canSearch(f)) {
//                            Log.d("zjltest", "searchRecursive for folder:" + folder.mFolderName + "/" + fileName);
                            searchRecursive(f);
                        }
                    }
                }
            }
        }
    }

    private void searchRecursiveByFilter(FolderFilter.Folder folder, File file) {
        File[] files = file.listFiles();
        if (files != null) {
            String relativePath;
            for (File f : files) {
                relativePath = getRelativePathBySdcard(f.getPath());
                if (folder.mIgnoreSubDirs.contains(relativePath)) {
                    continue;
                }
                mFileCount++;
                if (f.isDirectory()) {
                    mFolderCount++;
                    if (canSearch(f)) {
                        searchRecursiveByFilter(folder, f);
                    }
                }
            }
        }
    }

    private void searchRecursive(File folder) {
        if (mIsCanceled) {
            return;
        } 
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (mIsCanceled) {
                    return;
                }
                mFileCount++;
                if (f.isDirectory()) {
                    mFolderCount++;
                    if (canSearch(f)) {
                        searchRecursive(f);
                    }
                }
            }
        }

    }

    private void searchTencentRecursive(File folder) {
        if (mIsCanceled) {
            return;
        } 
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (mIsCanceled) {
                    return;
                }
                if (folder.getParent().contains(TENCENT_MICROMSG)
                        && folder.getName().length() > TENCENT_USER_ID_LENGTH - 2) {
                    continue;
                }
                mFileCount++;
                String lowerCaseName = f.getName().toLowerCase();
                if (lowerCaseName.contains(mQueryText)) {
                }
                if (f.isDirectory()) {
                    mFolderCount++;
                    if (canSearch(f)) {
                        searchTencentRecursive(f);
                    }
                }
            }
        }

    }

    public static String getRelativePathBySdcard(String path) {
        if (TextUtils.isEmpty(path) || path.length() <= StorageHelper.sPrimaryStorageRoot.length()) {
            return "";
        } else {
            return path.substring(StorageHelper.sPrimaryStorageRoot.length() + 1).toLowerCase();
        }
    }

    public static String getFullPath(String path) {
        return StorageHelper.sPrimaryStorageRoot + "/" + path;
    }

    /**
     * Define search rules here to improve search performance.
     * @param folder
     * @return
     */
    private boolean canSearch(File folder) {
        if (folder.isHidden()) {
            return false;
        }
        if (new File(folder, NOMEDIA).exists()) {
            Log.d("zjltest", "no media for : " + folder.getPath());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d(TAG, "onPostExecute:" + mFoundCount + ",takes time:"
                        + (System.currentTimeMillis() - mStartTime) + ",mFileCount:"
                        + mFileCount + ",mFolderCount:" + mFolderCount);
    }

    @Override
    protected void onCancelled(Boolean result) {
        Log.d(TAG, "onCancelled:" + mFoundCount + ",takes time:"
                + (System.currentTimeMillis() - mStartTime) + ",mSearchCount:" + mFileCount);
    }

    /**
     * cancel this search.
     */
    public void cancelSearch() {
        mIsCanceled = true;
        cancel(true);
    }

}