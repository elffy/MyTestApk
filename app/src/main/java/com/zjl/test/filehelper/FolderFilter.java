package com.zjl.test.filehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FolderFilter {

    public FolderFilter() {
    }

    public static HashMap<String, Folder> sFilterFolders = new HashMap<String, Folder>();

    private static ArrayList<Folder> sSpecialFolders = new ArrayList<Folder>();

    // these are black list of folders which we can ingore.

    public static final String ANDROID_SYSTEM = "android";// the folder created by android sytem
    //"Android/data"

    public static final String SMARTISAN = "smartisan";// the folder created by smartisan
    public static final String SMARTISAN_ALBUMART = "music/albumart";

    public static final String TENCENT = "tencent";
    // folder like /tencent/MicroMsg/528ae84413905ffb9956ba571e46a2dc/ may have huge amount of files and folders.
    // we need filter out this folder, it doesn't contain readable, useful info anyway.
    public static final String TENCENT_MICROMSG = "MicroMsg";// the folder /Tencent/MicroMsg/
    public static final int TENCENT_USER_ID_LENGTH = 32;// i.e. 528ae84413905ffb9956ba571e46a2dc
    public static final String TENCENT_UPDATE = "MicroMsg/CheckResUpdate";
    public static final String TENCENT_WALLET = "MicroMsg/wallet";

    public static final String TENCENT_QQ = "MobileQQ";
    public static final String TENCENT_REDPACKET = "MobileQQ/RedPacket";
    public static final String TENCENT_QBIZ = "MobileQQ/qbiz";

    public static final String TENCENT_GAME = "GameHelper";

    public static final String BAIDU = "baidu";

    public static final String QQMUSIC = "qqmusic";

    public static final String NETEASE = "netease";

    public static final String DANGDANG = "dangdang";

    public static final String SINA = "sina";

    public static final String YOUKU = "youku";

    public static final String POCO = "poco";//Poco/Thumbs

    public static final String SNOWBALL = "snowball";// snowball/image_cache

    public static final String ALIPAY = "alipay";

    public static final String AMAP = "amap"; // Qmap & Amap
    public static final String QMAP = "qmap";
    public static final String BAIDU_MAP = "baidumap"; // BaiduMap


    static {
        // init sFilterFolders
        // addIgnoreSubDirs
        sFilterFolders.put(ANDROID_SYSTEM, new Folder(ANDROID_SYSTEM).addIgnoreSubDirs("data", "obb", "obj"));
        sFilterFolders.put(SMARTISAN, new Folder(SMARTISAN).addIgnoreSubDirs("music", "bugreport", "browser"));
        sFilterFolders.put(TENCENT, new Folder(TENCENT).addIgnoreSubDirs("micromsg", "mobileqq", "gamehelper", "now", "msflogs"));
        sFilterFolders.put(BAIDU, new Folder(BAIDU).addIgnoreSubDirs("flyflow"));
//        sFilterFolders.put(QQMUSIC, new Folder(QQMUSIC).addIgnoreSubDirs("qqmusic/gift_anim_zip"));
        sFilterFolders.put(NETEASE, new Folder(NETEASE).addIgnoreSubDirs("cloudmusic", "adcache", "webcache"));
        sFilterFolders.put(DANGDANG, new Folder(DANGDANG).addIgnoreSubDirs("imgs"));
        sFilterFolders.put(YOUKU, new Folder(YOUKU).addIgnoreSubDirs("cacheData", "playercache"));
        sFilterFolders.put(POCO, new Folder(POCO).addIgnoreSubDirs("thumbs"));
        sFilterFolders.put(SNOWBALL, new Folder(SNOWBALL).addIgnoreSubDirs("image_cache"));
        sFilterFolders.put("tieba", new Folder("tieba").addIgnoreSubDirs("images"));
        sFilterFolders.put("youdao", new Folder("youdao").addIgnoreSubDirs("dict"));
        sFilterFolders.put("douyu", new Folder("douyu").addIgnoreSubDirs("face", "crash", "logs"));
        sFilterFolders.put("huijiachifan", new Folder("huijiachifan").addIgnoreSubDirs("cache"));
        sFilterFolders.put("sogousearch", new Folder("sogousearch").addIgnoreSubDirs("imgcache"));
        sFilterFolders.put("immomo", new Folder("immomo").addIgnoreSubDirs("moment", "users", "mk", "cache", "avatar"));

        // addUsefulSubDirs
        sFilterFolders.put(QQMUSIC, new Folder(QQMUSIC).addUsefulSubDirs("downloadalbum", "song"));
        sFilterFolders.put(SINA, new Folder(SINA).addUsefulSubDirs("weibo/weibo"));

        // filter all sub files for those folder
        sFilterFolders.put(ALIPAY, new Folder(ALIPAY));
        sFilterFolders.put(AMAP, new Folder(AMAP));
        sFilterFolders.put(QMAP, new Folder(QMAP));
        sFilterFolders.put(BAIDU_MAP, new Folder(BAIDU_MAP));
        sFilterFolders.put("cmblife", new Folder("cmblife"));
        sFilterFolders.put("uxinsdk", new Folder("uxinsdk"));
        sFilterFolders.put("com.smartisanos.tracker", new Folder("com.smartisanos.tracker"));

        // init sSpecialFolders
        sSpecialFolders.add(new Folder("tencent/micromsg").addUsefulSubDirs("download", "weixin"));
        sSpecialFolders.add(new Folder("baidu/flyflow").addUsefulSubDirs("downloads"));
        sSpecialFolders.add(new Folder("smartisan/music").addUsefulSubDirs("cloud"));
        sSpecialFolders.add(new Folder("netease/cloudmusic").addIgnoreSubDirs("cache"));
    }

    public static Set<String> getFilterSet() {
        return sFilterFolders.keySet();
    }

    public static Folder getFilterFolder(String folderName) {
        return sFilterFolders.get(folderName);
    }

    public static ArrayList<Folder> getSpecialFolders() {
        return sSpecialFolders;
    }

    public static final int TYPE_IGNORE = 0;
    public static final int TYPE_USEFUL = 1;
    static class Folder {
        String mFolderName;
        boolean mIgnoreAll = true;
        int mHandlerType;
        HashSet<String> mIgnoreSubDirs;
        ArrayList<String> mUsefulSubDirs;

        public Folder(String folderName) {
            mFolderName = folderName;
        }

        public Folder addIgnoreSubDirs(String... subDirs) {
            mIgnoreAll = false;
            mHandlerType = TYPE_IGNORE;
            mIgnoreSubDirs = new HashSet<String>();
            for (String dir : subDirs) {
                mIgnoreSubDirs.add(dir);
            }
            return this;
        }

        public Folder addUsefulSubDirs(String... subDirs) {
            mIgnoreAll = false;
            mHandlerType = TYPE_USEFUL;
            mUsefulSubDirs = new ArrayList<String>();
            for (String dir : subDirs) {
                mUsefulSubDirs.add(dir);
            }
            return this;
        }
    }
}
