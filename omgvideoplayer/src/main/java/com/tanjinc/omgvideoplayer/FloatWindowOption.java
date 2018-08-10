package com.tanjinc.omgvideoplayer;


import android.support.annotation.LayoutRes;

import java.io.Serializable;

class FloatWindowOption implements Serializable {
    private int targetX;
    private int targetY;
    private int width, height;
    private boolean anim;
    @LayoutRes
    int layoutId;
    public static final String NAME = "FloatWindowOption";
    public FloatWindowOption setTargetX(int targetX) {
        this.targetX = targetX;
        return this;
    }

    public FloatWindowOption setTargetY(int targetY) {
        this.targetY = targetY;
        return this;
    }

    public FloatWindowOption setWidth(int width) {
        this.width = width;
        return this;
    }

    public FloatWindowOption setHeight(int height) {
        this.height = height;
        return this;
    }

    public FloatWindowOption setAnim(boolean anim) {
        this.anim = anim;
        return this;
    }

    public FloatWindowOption setFloatLayoutId(int id) {
        this.layoutId = id;
        return this;
    }
    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isAnim() {
        return anim;
    }

    public int getLayoutId() {
        return layoutId;
    }

}
