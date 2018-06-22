package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;

import static android.view.View.VISIBLE;

/**
 * Created by tanjinc on 18-6-12.
 */

public abstract class BaseWidget {

    private View mWidgetRoot;
    private  @LayoutRes int mId;
    private BaseVideoPlayer mBaseVideoPlayer;

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

    public Context getContent() {
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
}
