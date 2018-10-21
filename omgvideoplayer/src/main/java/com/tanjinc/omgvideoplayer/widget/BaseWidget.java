package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;

import java.lang.ref.WeakReference;

import static android.view.View.VISIBLE;

/**
 * Created by tanjinc on 18-6-12.
 */

public abstract class BaseWidget {

    protected static final int MSG_WIDGET_SHOW = 1000;
    protected static final int MSG_WIDGET_HIDE = 1001;

    private View mWidgetRoot;
    private  @LayoutRes int mId;
    private BaseVideoPlayer mBaseVideoPlayer;
    protected MyHandler mHandler;

    public BaseWidget(@LayoutRes int id) {
        mId = id;
    }

    public BaseWidget(BaseVideoPlayer videoPlayer, @LayoutRes int id) {
        mId = id;
        mBaseVideoPlayer = videoPlayer;
    }

    public View findViewById(@IdRes int id) {
        if (mWidgetRoot != null) {
            return mWidgetRoot.findViewById(id);
        }
        return null;
    }

    public Context getContext() {
        if (mWidgetRoot != null) {
            return mWidgetRoot.getContext();
        }
        return null;
    }

    public BaseVideoPlayer getBaseVideoPlayer() {
        return mBaseVideoPlayer;
    }

    @CallSuper
    public void attachTo(@NonNull ViewGroup parent) {
        detach();
        if (mWidgetRoot == null) {
            mWidgetRoot = LayoutInflater.from(parent.getContext()).inflate(mId, parent, false);
        }
        parent.addView(mWidgetRoot, mWidgetRoot.getLayoutParams());
        mWidgetRoot.setVisibility(View.GONE);
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
    }


    public void detach() {
        if (mWidgetRoot !=  null && mWidgetRoot.getParent() != null ) {
            ((ViewGroup) mWidgetRoot.getParent()).removeView(mWidgetRoot);
        }
    }
    public void show() {
        if (mWidgetRoot != null) {
            mWidgetRoot.setVisibility(VISIBLE);
        }
    }

    public void showWithAutoHide(int delay) {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WIDGET_HIDE);
            mHandler.sendEmptyMessageDelayed(MSG_WIDGET_HIDE, delay);
        }
    }

    public void hide() {
        if (mWidgetRoot != null) {
            mWidgetRoot.setVisibility(View.GONE);
        }
    }

    public boolean isShown() {
        return mWidgetRoot != null && mWidgetRoot.isShown();
    }

    public void release () {
        if (mBaseVideoPlayer != null) {
            mBaseVideoPlayer = null;
        }
    }


    static class MyHandler extends Handler {

        WeakReference<BaseWidget> mWeakReference;

        MyHandler(BaseWidget widget) {
            mWeakReference = new WeakReference<>(widget);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WIDGET_SHOW:
                    if (mWeakReference != null && mWeakReference.get() != null) {
                        mWeakReference.get().show();
                    }
                    break;
                case MSG_WIDGET_HIDE:
                    if (mWeakReference != null && mWeakReference.get() != null) {
                        mWeakReference.get().hide();
                    }
                    break;
            }
        }
    }
}
