package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;

public class OmVolumeWidget extends BaseWidget{

    private ImageView mVolumeImage;
    private TextView mVolumeProgress;

    private int mCurrentVolume = 0;

    public OmVolumeWidget(@LayoutRes int layoutId) {
        super(layoutId);

    }

    @Override
    public void attachTo(ViewGroup parent) {
        super.attachTo(parent);
        mVolumeImage = (ImageView) findViewById(R.id.video_volume_img);
        mVolumeProgress = (TextView) findViewById(R.id.video_volume_progress);
        hide();

    }

    public void setProgress(int progress) {
        mCurrentVolume = progress;
        mVolumeProgress.setText(mCurrentVolume+"%");
        show();
    }

    public int getVolume() {
        return mCurrentVolume;
    }
}
