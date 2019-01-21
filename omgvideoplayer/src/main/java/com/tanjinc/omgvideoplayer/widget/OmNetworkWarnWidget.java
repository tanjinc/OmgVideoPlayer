package com.tanjinc.omgvideoplayer.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;
import com.tanjinc.omgvideoplayer.R;

public class OmNetworkWarnWidget extends BaseWidget {

    private TextView mWarnTV;
    private View mConfirmBtn;
    private View mCancelBtn;

    private String mPaddingVideoUrl;
    private boolean mHaveConfirm;

    public OmNetworkWarnWidget(BaseVideoPlayer videoPlayer, int layoutId) {
        super(videoPlayer, layoutId);
    }


    @Override
    public void attachTo(@NonNull ViewGroup parent) {
        super.attachTo(parent);
        mWarnTV = (TextView) findViewById(R.id.video_network_warn_tv);

        mConfirmBtn = findViewById(R.id.video_network_confirm_btn);
        if (mConfirmBtn != null) {
            mConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm();
                }
            });
        }
        mCancelBtn = findViewById(R.id.video_network_cancel_btn);
        if (mCancelBtn != null) {
            mCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }


    public void setVideoUrl(String url) {
        mPaddingVideoUrl = url;
    }

    public void setWarningString(String string) {
        if (mWarnTV != null) {
            mWarnTV.setText(string);
        }
    }
    /**
     * 确认
     */
    private void confirm() {
        if (getBaseVideoPlayer() != null) {
            if (mPaddingVideoUrl != null) {
                mHaveConfirm = true;
                getBaseVideoPlayer().setVideoUrl(mPaddingVideoUrl);
                mPaddingVideoUrl = null;
            } else {
                getBaseVideoPlayer().onResume();
            }
        }
        hide();
    }

    public boolean isConfirm() {
        return mHaveConfirm;
    }

    /**
     * 取消
     */
    public void cancel() {
        if (getBaseVideoPlayer() != null) {
            getBaseVideoPlayer().onDestroy();
        }
        hide();
    }
}
