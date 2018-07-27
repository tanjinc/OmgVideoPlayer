package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by tanjincheng on 17/8/9.
 */
public class ExoPlayerManager implements TextureView.SurfaceTextureListener,
        IMediaPlayerControl, ExoPlayer.EventListener, SimpleExoPlayer.VideoListener {
    private static final String TAG = "ExoPlayerManager";

    SimpleExoPlayer mExoPlayer;
    private ResizeTextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;

    private String mVideoUrl;
    private String mUserAgent;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder.Callback mSHCallback;

    private Context mContext;
    //listener
    IMediaPlayerControl.OnPreparedListener mOnPreparedListener;
    IMediaPlayerControl.OnCompletionListener mOnCompletionListener;
    IMediaPlayerControl.OnErrorListener mOnErrorListener;
    IMediaPlayerControl.OnInfoListener mOnInfoListener;
    IMediaPlayerControl.OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    IMediaPlayerControl.OnSeekCompleteListener mOnSeekCompleteListener;
    IMediaPlayerControl.OnBufferingUpdateListener mOnBufferingUpdateListener;


    public ExoPlayerManager(Context context) {
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        mUserAgent = Util.getUserAgent(context, "yourApplicationName");
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory =
//                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector =
//                new DefaultTrackSelector(videoTrackSelectionFactory);
//        LoadControl loadControl = new DefaultLoadControl();
//        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
//        mExoPlayer.setVideoListener(this);
//        mExoPlayer.addListener(this);
//        mExoPlayer.setVideoTextureView(mTextureView);
    }

    private void openVideo() {
        if (mVideoUrl == null ) {
            Log.d(TAG, "video openVideo not ready");
            return;
        }
//        if(mSurfaceTexture == null && mSurfaceHolder == null) {
//            Log.e(TAG, "video openVideo: surface is null");
//            return;
//        }
        prepared = false;
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, mUserAgent);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mVideoUrl), dataSourceFactory, extractorsFactory, null, null);
        TrackSelection.Factory videoTrackSelectionFactory =new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        mExoPlayer.setVideoListener(this);
        mExoPlayer.addListener(this);
        if (mTextureView != null) {
            mExoPlayer.setVideoTextureView(mTextureView);
            mTextureView.setSurfaceTextureListener(this);
        } else {
            mExoPlayer.setVideoSurfaceView(mSurfaceView);
            mSurfaceView.getHolder().addCallback(mSHCallback);
        }
        mExoPlayer.prepare(videoSource);
    }

    @Override
    public void setVideoUrl(String url) {
        mVideoUrl = url;
        openVideo();
    }

    public void setTextureView(ResizeTextureView textureView) {
        Log.d(TAG, "video setTextureView: " + textureView);
        mTextureView = textureView;
    }

    @Override
    public void setSurfaceView(ResizeSurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSHCallback = new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                openVideo();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceHolder = null;
            }
        };
        mSurfaceView.getHolder().addCallback(mSHCallback);
    }

    /**
     *
     * @param parent 父控件,必须为FrameLayout或RelativeLayout
     */
    @Override
    public void setParentView(ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams = null;
        if (parent instanceof FrameLayout) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((FrameLayout.LayoutParams)layoutParams).gravity = Gravity.CENTER;
        } else if (parent instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((RelativeLayout.LayoutParams)layoutParams).addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        if (mTextureView != null) {
            if ( mTextureView.getParent() != null) {
                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
            }
            parent.addView(mTextureView, 0, layoutParams);
        }
        if (mSurfaceView != null ) {
            if(mSurfaceView.getParent() != null) {
                ((ViewGroup) mSurfaceView.getParent()).removeView(mSurfaceView);
            }
            parent.addView(mSurfaceView, 0, layoutParams);
        }
    }

    @Override
    public void release() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        mTextureView = null;
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnInfoListener = null;
        mOnPreparedListener = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d(TAG, "video onSurfaceTextureAvailable: " + surfaceTexture);
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            openVideo();
        } else {
            if (mTextureView != null) {
                mTextureView.setSurfaceTexture(mSurfaceTexture);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(TAG, "video onSurfaceTextureSizeChanged() called with: " + "surfaceTexture = [" + surfaceTexture + "], width = [" + width + "], height = [" + height + "]");
        if (mTextureView != null) {
            mTextureView.setVideoSize(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void start() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public int getDuration() {
        if (mExoPlayer != null) {
            return (int) mExoPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mExoPlayer != null) {
            return (int) mExoPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int i) {
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(i);
        }
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    @Override
    public int getBufferPercentage() {
        return mExoPlayer != null ? mExoPlayer.getBufferedPercentage() : 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    boolean prepared;
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "video onPlayerStateChanged() called with: " + "playWhenReady = [" + playWhenReady + "], playbackState = [" + playbackState + "]");
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                if (mOnPreparedListener != null && !prepared) {
                    mOnPreparedListener.onPrepared();
                    prepared = true;
                }
                break;
            case ExoPlayer.STATE_BUFFERING:
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(MEDIA_INFO_BUFFERING_START,0);
                }
                break;
            case ExoPlayer.STATE_ENDED:
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion();
                }
                break;
            case ExoPlayer.STATE_IDLE:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(error.type, error.rendererIndex);
        }
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(width, height);
        }
        if (mTextureView != null) {
            mTextureView.setVideoSize(width, height);
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(MEDIA_INFO_VIDEO_RENDERING_START, 0);
        }
    }
}
