package com.tanjinc.omvideoplayer_lib;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.exoplayer2.upstream.BandwidthMeter;
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

    private String mPath;
    private String mUserAgent;

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
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, mUserAgent);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mPath), dataSourceFactory, extractorsFactory, null, null);

        TrackSelection.Factory videoTrackSelectionFactory =new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        mExoPlayer.setVideoListener(this);
        mExoPlayer.addListener(this);
        mExoPlayer.setVideoTextureView(mTextureView);
        mExoPlayer.prepare(videoSource);
    }

    @Override
    public void setVideoPath(String path) {
        mPath = path;
        openVideo();
    }

    public void setTextureView(ResizeTextureView textureView) {
        Log.d(TAG, "video setTextureView: " + textureView);
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
//        mExoPlayer.setVideoTextureView(mTextureView);
    }

    /**
     *
     * @param parent 父控件,必须为FrameLayout或RelativeLayout
     */
    @Override
    public void setParentView(ViewGroup parent) {
        if (mTextureView != null && mTextureView.getParent() != null) {
            ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);

        }
        ViewGroup.LayoutParams layoutParams = null;
        if (parent instanceof FrameLayout) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((FrameLayout.LayoutParams)layoutParams).gravity = Gravity.CENTER;
        } else if (parent instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((RelativeLayout.LayoutParams)layoutParams).addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        parent.addView(mTextureView, layoutParams);
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
            mExoPlayer.setVideoTextureView(mTextureView);
            openVideo();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
            mExoPlayer.setVideoTextureView(mTextureView);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

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

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared();
                }
                break;
            case ExoPlayer.STATE_BUFFERING:
                if (mOnInfoListener != null) {
                    mOnInfoListener.onInfo(IMediaPlayerControl.MEDIA_INFO_VIDEO_RENDERING_START,0);
                }
                break;
            case ExoPlayer.STATE_ENDED:
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
