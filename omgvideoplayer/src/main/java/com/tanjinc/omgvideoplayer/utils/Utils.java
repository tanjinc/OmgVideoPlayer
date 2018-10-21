package com.tanjinc.omgvideoplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tanjincheng on 17/7/2.
 */
public class Utils {
    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    public static String stringForTime(int millis) {
        int totalSeconds = millis / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        String textString = null;
        if (hours > 0) {
            textString = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            textString = String.format("%02d:%02d", minutes, seconds);
        }
        return textString;

    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getNetWorkType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static boolean isMobileNet(Context context) {
        boolean isMobileNet = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (ConnectivityManager.TYPE_WIFI == info[i].getType()) { // 判断是否是wifi网络
                            isMobileNet = false;
                            return isMobileNet;
                        } else { // 判断是否事移动网络
                            isMobileNet = true;
                        }
                    }
                }
            }
        }
        return isMobileNet;
    }
}
