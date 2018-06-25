package com.tanjinc.omgvideoplayer;

import android.util.Log;

public class GestureOrientation {

    public enum ScrollOrientation {
        SCROLL_INVALID, SCROLL_VERTICAL_UP, SCROLL_VERTICAL_DOWN, SCROLL_HORIZONTAL

    }

    private static final String TAG = "GestureOrientation";
    private static float MIN_DISTANCE = 10.0f;
    private static float ORIENTATION_MIN_DISTANCE = 10.0f;
    private static GestureOrientation mGestureOrientation = null;
    private float mCurrentX = 0;
    private float mCurrentY = 0;
    private float mPreviousX = -1.0F;
    private float mPreviousY = -1.0F;
    private float mChangeOrientationBeginY;
    private float mDownX;
    private float mDownY;
    private float mSwitchY;
    private ScrollOrientation mScrollOrientation = ScrollOrientation.SCROLL_INVALID;

    public GestureOrientation(float downX, float downY) {
        this.mDownX = downX;
        this.mDownY = downY;
        mPreviousX = mDownX;
        mPreviousY = mDownY;
    }

    public static GestureOrientation getInstance(float downX, float downY) {
        if (mGestureOrientation == null) {
            mGestureOrientation = new GestureOrientation(downX, downY);
        } else {
            mGestureOrientation.setDownPoint(downX, downY);
        }
        return mGestureOrientation;
    }

    private void setDownPoint(float downX, float downY) {
        this.mDownX = downX;
        this.mDownY = downY;
        mPreviousX = mDownX;
        mPreviousY = mDownY;
    }

    public ScrollOrientation computeFirstAngle(float currentX, float currentY) {
        mCurrentX = currentX;
        mCurrentY = currentY;
        float distanceX = mCurrentX - mDownX;
        float distanceY = mCurrentY - mDownY;
        int angle = 0;
        if (Math.abs(distanceX) > MIN_DISTANCE || Math.abs(distanceY) > MIN_DISTANCE) {
            angle = (int) (180.0D * (Math.atan2(Math.abs(distanceY), Math.abs(distanceX)) / 3.141592653589793D));
        }
        if (angle < 45) {
            mScrollOrientation = ScrollOrientation.SCROLL_HORIZONTAL;
        } else {
            if (distanceY > 0.0f) {
                mScrollOrientation = ScrollOrientation.SCROLL_VERTICAL_DOWN;
            } else {
                mScrollOrientation = mScrollOrientation.SCROLL_VERTICAL_UP;
            }
        }
        Log.d(TAG, " GestureListener computeFirstAngle  degree = " + angle);
        Log.d(TAG, " GestureListener computeFirstAngle  ScrollOrientation = " + mScrollOrientation);
        return mScrollOrientation;
    }

    /*
     * 该函数用于计算在滑动过程中改变方向的情况,UP---->DOWN,DOWN---->UP 1 记录第一次滑动方向 记录downX downY 2
     * onscroll回调里面 判断是否出现 当前的Y坐标值的变化是否与滑动方向相同 3
     */
    public ScrollOrientation computeScrollAngle(float distanceY) {
        if (mScrollOrientation == ScrollOrientation.SCROLL_INVALID) {
            return ScrollOrientation.SCROLL_INVALID;
        } else if (mScrollOrientation == ScrollOrientation.SCROLL_HORIZONTAL) {
            return ScrollOrientation.SCROLL_HORIZONTAL;
        } else {
            if (distanceY >= 0) {
                return ScrollOrientation.SCROLL_VERTICAL_UP;
            } else {
                return ScrollOrientation.SCROLL_VERTICAL_DOWN;
            }
        }
    }

    public void resetPreviousPoint() {
        this.mPreviousX = -1.0F;
        this.mPreviousY = -1.0F;
        this.mCurrentX = -1.0F;
        this.mCurrentY = -1.0F;
        this.mDownX = -1.0F;
        this.mDownY = -1.0F;
        this.mScrollOrientation = ScrollOrientation.SCROLL_INVALID;
    }

    public void setFirstDegree(int paramInt) {
    }
}
