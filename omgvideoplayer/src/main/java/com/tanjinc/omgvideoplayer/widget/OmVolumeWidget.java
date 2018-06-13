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

    public OmVolumeWidget(@NonNull Context context, @LayoutRes int layoutId) {
        super(context, layoutId);

        mVolumeImage = (ImageView) findViewById(R.id.video_volume_img);
        mVolumeProgress = (TextView) findViewById(R.id.video_volume_progress);
        hide();
    }

    @Override
    public void attachTo(ViewGroup parent) {
        if (parent != null) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            parent.addView(this,layoutParams);
        }
    }

    public void setProgress(int progress) {
        mCurrentVolume = progress;
        mVolumeProgress.setText(mCurrentVolume+"%");
    }

    public int getVolume() {
        return mCurrentVolume;
    }
}
