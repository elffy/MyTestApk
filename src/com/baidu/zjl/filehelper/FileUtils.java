
package com.baidu.zjl.filehelper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.WeakHashMap;

public class FileUtils {

    public static boolean isSdCardInserted() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static File getAppDirectory(Context context) {
        return context.getFilesDir();
    }

    public static Map<String, Long> mCachedMap = new WeakHashMap<String, Long>();

    public static long getFileOrFolderSize(File file) {
        long size = 0;
        if (file.isFile()) {
            size = file.length();
        } else {
            if (mCachedMap.get(file.getAbsolutePath()) != null) {
//                Log.d("zjltest", file.getName()+":" + mCachedMap.get(file.getName()));
                return (Long) mCachedMap.get(file.getAbsolutePath());
            }
            // for (File subFile : file.listFiles()) {
            // size += getFileOrFolderSize(subFile);
            // }
            for (File subFile : file.listFiles()) {
                if (file.isDirectory()) {
                    size += getFileOrFolderSize(subFile);
                } else {
                    size += subFile.length();
                }
            }
            mCachedMap.put(file.getAbsolutePath(), size);
        }
        return size;
    }

    public static String getProperSize(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        double d = (double) size / (double) (1024 * 1024);
        if (d < 1) {
            return df.format((double) size / 1024) + " kB";
        }
        return df.format(d) + " MB";
    }

    public void method(Context context) {
        File skRoot = Environment.getExternalStorageDirectory();
        String fileRoot = context.getFilesDir() + "//";
        File myRoot = new File(fileRoot);
        // String path = File.getPath();//相对
        // String path = File.getAbsoultePath();//绝对
        // String parentPath = File.getParent();
        //
        // String Name = File.getName();
        //
        // File.mkDir(); //建立文件夹
        // File.createNewFile();//建立文件
        //
        // File.isDirectory()
        //
        // File[] files = File.listFiles();
        //
        // File.renameTo(dest);
        //
        // File.delete();
    }
}
