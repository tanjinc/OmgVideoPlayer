package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;


public class OmVolumeWidget extends BaseWidget{
    private static final String TAG = "OmVolumeWidget";

    private ImageView mVolumeImage;
    private TextView mVolumeProgress;

    private int mCurrentVolume = 0;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mVolumeStep;
    private static int SEEK_MAX = 1000;
    private int mVolumePercent;

    public OmVolumeWidget(@LayoutRes int layoutId) {
        super(layoutId);

    }

    @Override
    public void attachTo(ViewGroup parent) {
        super.attachTo(parent);
        mVolumeImage = (ImageView) findViewById(R.id.video_volume_img);
        mVolumeProgress = (TextView) findViewById(R.id.video_volume_progress);
        hide();

        mAudioManager = (AudioManager) getContent().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mVolumeStep = (mMaxVolume / 15) != 0 ? (mMaxVolume / 15) : 1;
            mVolumePercent = mCurrentVolume * SEEK_MAX / mMaxVolume;
        }
    }

    public void setKeyVolumeChange(boolean isUp) {
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
        changeVolume();
        showWithAutoHide(1000);
    }

    private void changeVolume() {
        mVolumeProgress.setText(mVolumePercent+"%");
        show();
        Log.d(TAG, "video changeVolume() " + mCurrentVolume);
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, AudioManager.FLAG_VIBRATE);
        }
    }

    public void setProgress(int progress) {
        mCurrentVolume = progress;
        show();
    }

    public int getVolume() {
        return mCurrentVolume;
    }

}
