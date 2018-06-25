package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class ResizeSurfaceView extends SurfaceView {
    private static final String TAG = "ResizeSurfaceView";

    private int mVideoWidth;
    private int mVideoHeight;

    private BaseVideoPlayer.VideoViewType mSizeType;
    public ResizeSurfaceView(Context context) {
        super(context);
    }

    public ResizeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        requestLayout();
    }


    @Deprecated
    public void setFixMode(boolean fix) {
        setVideoViewSize(fix ? BaseVideoPlayer.VideoViewType.SCREEN_ADAPTATION : BaseVideoPlayer.VideoViewType.FULL_SCREEN);
    }

    public void setVideoViewSize(BaseVideoPlayer.VideoViewType sizeType) {
        mSizeType = sizeType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (mVideoHeight > 0 && mVideoWidth > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            boolean isHorizon = mVideoWidth * height > mVideoHeight * width;


            switch (mSizeType) {
                case FULL_SCREEN:
                    width = widthSpecSize;
                    height = heightSpecSize;
                    break;
                case FULL_SCALE:
                    width = widthSpecSize;
                    height = heightSpecSize;
                    if (isHorizon) {
                        width = mVideoWidth * height / mVideoHeight;
                    } else {
                        height = mVideoHeight * width / mVideoWidth;
                    }
                    break;
                case SCREEN_ADAPTATION:
                    width = widthSpecSize;
                    height = heightSpecSize;
                    if (isHorizon) {
                        //too wide
                        height = mVideoHeight * width / mVideoWidth;
                    } else {
                        width = mVideoWidth * height / mVideoHeight;
                    }
                    break;
                default:
                    width = widthSpecSize;
                    height = heightSpecSize;
                    if (isHorizon) {
                        //too wide
                        height = mVideoHeight * width / mVideoWidth;
                    } else {
                        width = mVideoWidth * height / mVideoHeight;
                    }
                    break;
            }
        }
        setMeasuredDimension(width, height);
    }
}
