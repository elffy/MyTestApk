package com.zjl.test.filehelper;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

public class MemoryUtils {
    Context mContext;

    public MemoryUtils(Context context) {
        mContext = context;
    }

    /**
     * 获得SD卡总大小
     * 
     * @return
     */
    public long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     * 
     * @return
     */
    public long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 获得机身内存总大小
     * 
     * @return
     */
    public long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     * 
     * @return
     */
    public long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 外部存储中所有音频文件所占内存
     * 
     * @return
     */
    public long getAudioTotalSize() {
        ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        long size = 0L;
        for (MemoryInfo cInfo : resultList) {
            File file = new File(cInfo.getFilePath());
            if(null!=file &&file.exists()){
                size += cInfo.getFileSize();
            }
        }
        return size;
    }

    /**
     * 外部存储中除音频、视频、图片之前其他文件所占内存
     * 
     * @return
     */
    public long getOtherTotalSize() {
        long size = getSDTotalSize() - getSDAvailableSize()
                - getPictureTotalSize() - getVideoTotalSize()
                - getAudioTotalSize();
        if (size < 0L) {
            size = 0L;
        }
        return size;
    }

    /**
     * 外部存储中所有图片文件所占内存
     * 
     * @return
     */
    public long getPictureTotalSize() {
        ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        long size = 0L;
        for (MemoryInfo cInfo : resultList) {
            File file = new File(cInfo.getFilePath());
            if(null!=file &&file.exists()){
                size += cInfo.getFileSize();
            }
        }
        return size;
    }

    /**
     * 外部存储中所有视频文件所占内存
     * 
     * @return
     */
    public long getVideoTotalSize() {
        ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        long size = 0L;
        for (MemoryInfo cInfo : resultList) {
            File file = new File(cInfo.getFilePath());
            if(null!=file &&file.exists()){
                size += cInfo.getFileSize();
            }
        }
        return size;
    }

    public ArrayList<MemoryInfo> queryAllMediaList(Uri uri) {
        //我们只需要两个字段：大小、文件路径
        Cursor cursor = mContext.getContentResolver().query(
                uri,new String[] { MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA }, null, null, null);

        ArrayList<MemoryInfo> musicList = new ArrayList<MemoryInfo>();
        
        try{
            if (cursor.moveToFirst()) {
                do {
                    MemoryInfo mInfo = new MemoryInfo();
                    mInfo.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                    mInfo.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                }while(cursor.moveToNext());
            }
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }
        
        return musicList;

    }
    
    class MemoryInfo {
        private long fileSize = 0L;
        private String filePath = "";

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
