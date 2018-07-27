package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by tanjincheng on 17/6/28.
 */
public class MediaPlayerManager implements TextureView.SurfaceTextureListener, IMediaPlayerControl
{
    private static final String TAG = "MediaPlayerManager";

    private static MediaPlayerManager sInstance;
    private SurfaceTexture mSurfaceTexture;
    private ResizeTextureView mTextureView;
    private Uri mUri;
    private String mVideoUrl;
    public MediaPlayer mMediaPlayer;
    private PlayState mPlayState;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceHolder.Callback mSHCallback;

    //listener
    IMediaPlayerControl.OnPreparedListener mOnPreparedListener;
    IMediaPlayerControl.OnCompletionListener mOnCompletionListener;
    IMediaPlayerControl.OnErrorListener mOnErrorListener;
    IMediaPlayerControl.OnInfoListener mOnInfoListener;
    IMediaPlayerControl.OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    IMediaPlayerControl.OnSeekCompleteListener mOnSeekCompleteListener;
    IMediaPlayerControl.OnBufferingUpdateListener mOnBufferingUpdateListener;

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int i) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
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


    public enum PlayState {
        Playing,
        Paused,
        Buffering,
        Ended
    }

    // listener
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared();
            }
        }
    };
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(i, i1);
            }
            return false;
        }
    };
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            if (mOnBufferingUpdateListener != null) {
                mOnBufferingUpdateListener.onBufferingUpdate(i);
            }
        }
    };
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion();
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            if (mOnErrorListener != null) {
                return mOnErrorListener.onError(i, i1);
            }
            return false;
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(width, height);
            }
            if (mTextureView != null) {
                mTextureView.setVideoSize(width, height);
            }
        }
    };

//    public static MediaPlayerManager getInstance() {
//        if (sInstance == null) {
//            sInstance = new MediaPlayerManager();
//        }
//        return sInstance;
//    }

    public MediaPlayerManager(Context context) {
        mMediaPlayer = new MediaPlayer();
    }

    long startTime = 0;
    private void openVideo() {
        if (mVideoUrl == null ) {
            Log.d(TAG, "video openVideo not ready");
            return;
        }
        if(mSurfaceTexture == null && mSurfaceHolder == null) {
            Log.e(TAG, "video openVideo: surface is null");
            return;
        }
        try {

            mMediaPlayer.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //通过反射,就不需要传入context
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            startTime = System.currentTimeMillis();
            mMediaPlayer.setDataSource(mVideoUrl);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
            mMediaPlayer.prepareAsync();

            if (mSurfaceHolder!=null) {
                //使用surfaceView
                mMediaPlayer.setDisplay(mSurfaceHolder);
                mSurfaceView.invalidate();
            } else {
                //使用textureView
                mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            }
        } catch (Exception e) {
            Log.e(TAG, "video openVideo: ", e);
        }
    }

    @Override
    public void setVideoUrl(String path) {
        mVideoUrl = path;
        openVideo();
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.release();
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mTextureView = null;
        mSurfaceView = null;
        mSurfaceTexture = null;
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnInfoListener = null;
        mOnPreparedListener = null;
    }
    @Override
    public void setTextureView(ResizeTextureView textureView) {
        Log.d(TAG, "video setTextureView: " + textureView);
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
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
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((FrameLayout.LayoutParams)layoutParams).gravity = Gravity.CENTER;
        } else if (parent instanceof RelativeLayout) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d(TAG, "video onSurfaceTextureAvailable: surfaceTexture" + surfaceTexture);
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            openVideo();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
