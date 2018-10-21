package com.tanjinc.omgvideoplayer.listPage.bean;


public abstract class BaseItem {

    public static final int TYPE_A = 1;
    public static final int TYPE_B = 2;
    public static final int TYPE_C = 3;
    public static final int TYPE_VIDEO = 4;

    abstract public int getType();
}
