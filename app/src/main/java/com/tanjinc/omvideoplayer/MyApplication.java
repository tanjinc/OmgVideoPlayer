package com.tanjinc.omvideoplayer;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by tanjincheng on 17/7/1.
 */
public class MyApplication extends Application {
    DaoSession mDaoSession;
    DaoMaster mDaoMaster;
    @Override
    public void onCreate() {
        LeakCanary.install(this);
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
