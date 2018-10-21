package com.tanjinc.omgvideoplayer.widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;
import com.tanjinc.omgvideoplayer.utils.ScreenUtils;

public class OmLightWidget extends BaseWidget {
    private static final String TAG = "OmLightWidget";
    private static int SEEK_MAX = 1000;
    private static int WIDGET_DISMISS_TIME = 180;

    protected TextView mLightPercentTextView;
    protected ImageView mLightImg;
    protected SeekBar mLightProgressBar;


    private int mLightProgress;
    private int mSysBrightness;
    private int mMinWinBrightness;

    private int mMaxWinBrightness;

    private Activity mActivity;

    public OmLightWidget(int id) {
        super(id);
    }

    @Override
    public void attachTo(@NonNull ViewGroup parent) {
        super.attachTo(parent);
        mLightProgressBar = (SeekBar) findViewById(R.id.light_progressbar);
        if (mLightProgressBar != null) {
            mLightProgressBar.setMax(SEEK_MAX);
        }
        mLightPercentTextView = (TextView) findViewById(R.id.light_percentage);
        mLightImg = (ImageView) findViewById(R.id.light_img);

        mSysBrightness = ScreenUtils.getCurScreenBrightness(getContext());
        mMinWinBrightness = ScreenUtils.getMinScreenBrightness();
        mMaxWinBrightness = ScreenUtils.getMaxScreenBrightness();
        mLightProgress = (mSysBrightness - mMinWinBrightness) * SEEK_MAX / (mMaxWinBrightness - mMinWinBrightness);
    }

    @Override
    public void detach() {
        super.detach();
        mActivity = null;
    }

    public void setCurrentBrightness(int brightness) {
        mSysBrightness = brightness;
    }

    public void onScrollLightChange(Activity activity, int distance, int screenHeight) {
        int scrollProgress = (int) (distance * SEEK_MAX / (screenHeight * 0.8));
        mLightProgress += scrollProgress;
        if (mLightProgress >= SEEK_MAX) {
            mLightProgress = SEEK_MAX;
        }
        if (mLightProgress <= 0) {
            mLightProgress = 0;
        }

        if (mLightPercentTextView != null) {
            mLightPercentTextView.setText(getContext().getResources().getString(R.string.om_volume_light_info, mLightProgress * 100 / SEEK_MAX));
        }
        if (mLightImg != null) {
//            mLightImg.setImageResource();
        }
        if (mLightProgressBar != null) {
            mLightProgressBar.setProgress(mLightProgress);
        }

        mActivity = activity;
        changeLight();
        show();
    }
    private void changeLight() {
        if (mActivity == null) {
            return;
        }
        int light  = mMinWinBrightness + (mLightProgress * (mMaxWinBrightness - mMinWinBrightness) / SEEK_MAX);
        Log.d(TAG, "video setLightBarProgress mAppBrightness : " + light);
        ScreenUtils.setWinBrightness(mActivity.getWindow(), light);
    }
}
