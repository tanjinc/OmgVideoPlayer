package com.tanjinc.omgvideoplayer;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by tanjincheng on 17/7/1.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication sMyApplication;

    public static MyApplication getInstance() {
        return sMyApplication;
    }

    public void onCreate() {
        LeakCanary.install(this);
        super.onCreate();

        sMyApplication = this;
        Stetho.initializeWithDefaults(this);

    }
}
