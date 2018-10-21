package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;
import com.tanjinc.omgvideoplayer.utils.Utils;

/**
 * Created by tanjinc on 18-6-12.
 */

public class OmSeekWidget extends BaseWidget {
    protected static int SEEK_MAX = 1000;
    private static int WIDGET_DISMISS_TIME = 180;
    private TextView mCurrentSeektimeTv;
    private TextView mChangeSeektimeTv;
    private SeekBar mSeektimeSeekbar;

    private int mGestureDownPlayTime = 0;
    private int mDuration = 0;

    public OmSeekWidget(int id) {
        super(id);
    }

    @Override
    public void attachTo(@NonNull ViewGroup parent) {
        super.attachTo(parent);
        mCurrentSeektimeTv = (TextView) findViewById(R.id.current_time_tv);
        mChangeSeektimeTv = (TextView) findViewById(R.id.change_time_tv);
        mSeektimeSeekbar = (SeekBar) findViewById(R.id.seektime_info_seekbar);
        if (mSeektimeSeekbar != null) {
            mSeektimeSeekbar.setMax(SEEK_MAX);
        }
    }

    public void setGestureDownPlayTime(int gestureDownPlayTime, int duration) {
        mGestureDownPlayTime = gestureDownPlayTime;
        mDuration = duration;
    }

    public void setCurrentPosition(int currentPosition) {
        String seekTime;
        String skipTimeStr =  Utils.stringForTime((int) Math.abs(currentPosition - mGestureDownPlayTime));
        if (currentPosition > mGestureDownPlayTime) {
            seekTime = "+"+skipTimeStr;
        } else if (currentPosition == mGestureDownPlayTime) {
            seekTime = " "+skipTimeStr;
        } else {
            seekTime = "-"+skipTimeStr;
        }
        if (mChangeSeektimeTv != null) {
            mChangeSeektimeTv.setText(seekTime);
        }
        if (mCurrentSeektimeTv != null) {
            mCurrentSeektimeTv.setText(Utils.stringForTime(currentPosition));
        }
        if (mSeektimeSeekbar != null) {
            mSeektimeSeekbar.setProgress((currentPosition * SEEK_MAX / mDuration));
        }
        show();
    }
}
