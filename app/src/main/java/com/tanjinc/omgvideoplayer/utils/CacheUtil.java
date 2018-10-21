package com.tanjinc.omgvideoplayer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.internal.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.Buffer;
import okio.Source;

public class CacheUtil {
    public static final int CACHE_MAX = 10 * 1024 * 1024;
    public static final String SNAPSHORT_ALL_PHOTOS = "getAllPhotos";

    public static DiskLruCache sDiskLruCache;

    public static DiskLruCache getDiskLruCache(Context context) {
        if (sDiskLruCache != null) {
            return sDiskLruCache;
        }
        File cacheDir = getDiskCacheDir(context, "bitmap");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        sDiskLruCache = DiskLruCache.create(FileSystem.SYSTEM, cacheDir, getAppVersion(context), 1, CACHE_MAX);
        return sDiskLruCache;
    }


    public static InputStream getCacheStream(String snapshotStr) {
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = sDiskLruCache.get(snapshotStr);
            if (snapshot != null) {
                // 读取缓存数据并反序列化
                //获取资源的输出流,Source类似InputStream
                Source source = snapshot.getSource(0);
                Buffer buffer = new Buffer();
                //读取4*1024数据放入buffer中并返回读取到的数据的字节长度
                long ret = source.read(buffer, 4 * 1024);
                //判断文件是否读完
                while (ret != -1) {
                    ret = source.read(buffer, 4 * 1024);
                }
                return buffer.inputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
