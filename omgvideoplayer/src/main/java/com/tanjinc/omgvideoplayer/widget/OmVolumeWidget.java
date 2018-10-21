package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;

/**
 * Created by tanjinc on 18-6-12.
 */

public class OmVolumeWidget extends BaseWidget {
    private static final String TAG = "OmVolumeWidget";
    private static int SEEK_MAX = 1000;
    private static int WIDGET_DISMISS_TIME = 180;

    protected TextView mVolumePercentTextView;
    protected ImageView mVolumeImg;
    protected SeekBar mVolumeProgressBar;

    private int mCurrentVolume = 0;
    private int mMaxVolume;
    private int mVolumeStep;
    private int mVolumePercent;
    private AudioManager mAudioManager;

    public OmVolumeWidget(int id) {
        super(id);
    }


    @Override
    public void attachTo(ViewGroup parent) {
        super.attachTo(parent);
        mVolumeProgressBar = (SeekBar) findViewById(R.id.volume_progressbar);
        if (mVolumeProgressBar != null) {
            mVolumeProgressBar.setMax(SEEK_MAX);
        }
        mVolumePercentTextView = (TextView) findViewById(R.id.volume_percentage);
        mVolumeImg = (ImageView) findViewById(R.id.volume_img);

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mVolumeStep = (mMaxVolume / 15) != 0 ? (mMaxVolume / 15) : 1;
            mVolumePercent = mCurrentVolume * SEEK_MAX / mMaxVolume;
        }
    }

    public void onKeyVolumeChange(boolean isUp) {
        mCurrentVolume += isUp ? mVolumeStep : - mVolumeStep;
        if (mCurrentVolume < 0) {
            mCurrentVolume = 0;
        } else if (mCurrentVolume > mMaxVolume) {
            mCurrentVolume = mMaxVolume;
        }
        mVolumePercent = mCurrentVolume * SEEK_MAX / mMaxVolume;
        if (mVolumePercent < 0) {
            mVolumePercent = 0;
        } else if (mVolumePercent > SEEK_MAX) {
            mVolumePercent = SEEK_MAX;
        }
        setProgress(mVolumePercent);
        changeVolume();
        showWithAutoHide(1000);
    }

    public void onScrollVolumeChange(int distance, int screenHeight) {
        int scrollProgress = (int) (distance * SEEK_MAX / (screenHeight * 0.8));
        mVolumePercent += scrollProgress;
        if (mVolumePercent >= SEEK_MAX) {
            mVolumePercent = SEEK_MAX;
        }
        if (mVolumePercent <= 0) {
            mVolumePercent = 0;
        }
        mCurrentVolume = mVolumePercent * mMaxVolume / SEEK_MAX;
        setProgress(mVolumePercent);
        changeVolume();
    }

    //set ui
    private void setProgress(int progress) {
        String s = getContext().getResources().getString(R.string.om_volume_light_info, progress * 100 / SEEK_MAX);
        if (mVolumePercentTextView != null) {
            mVolumePercentTextView.setText(s);
        }

//        if (progress == 0) {
//            mVolumeImg.setImageResource(R.drawable.mz_video_player_toast_ic_volume_off);
//        } else {
//            mVolumeImg.setImageResource(R.drawable.mz_video_player_toast_ic_volume_on);
//        }
        mVolumeProgressBar.setProgress(progress);
        show();
    }

    //set volume
    private void changeVolume() {
        Log.d(TAG, "video changeVolume() " + mCurrentVolume);
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, AudioManager.FLAG_VIBRATE);
        }
    }
}
