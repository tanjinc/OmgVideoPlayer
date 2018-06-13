package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tanjinc.omgvideoplayer.R;

public abstract class BaseWidget extends FrameLayout{

    public int auto_hide_time = 5000;

    public BaseWidget(@NonNull Context context) {
        super(context);
    }

    public BaseWidget(@NonNull Context context, @LayoutRes int id) {
        super(context);
        LayoutInflater.from(context).inflate(id, this);
    }

//    public void add(ViewGroup parent) {
//        add(parent, null);
//    }

    public abstract void attachTo(ViewGroup parent);

    public void remove() {
        if (getParent() != null) {
            ((ViewGroup)getParent()).removeView(this);
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}
