package com.tanjinc.playermanager;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by tanjincheng on 17/6/28.
 */
public class MediaPlayerManager implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    private static final String TAG = "MediaPlayerManager";

    private static MediaPlayerManager sInstance;
    private SurfaceTexture mSurfaceTexture;
    private ResizeTextureView mTextureView;
    private Uri mUri;
    private String mPath = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";

    public MediaPlayer mediaPlayer;
    private PlayState mPlayState;

    public enum PlayState {
        Playing,
        Paused,
        Buffering,
        Ended
    }

    // listener
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnInfoListener mOnInfoListener;
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public static MediaPlayerManager getInstance() {
        if (sInstance == null) {
            sInstance = new MediaPlayerManager();
        }
        return sInstance;
    }

    private MediaPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    private void openVideo() {
        if (mPath == null || mSurfaceTexture == null) {
            Log.d(TAG, "video openVideo not ready");
            return;
        }
        try {
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //通过反射,就不需要传入context
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            method.invoke(mediaPlayer, mPath, null);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setSurface(new Surface(mSurfaceTexture));
        } catch (Exception e) {
            Log.e(TAG, "video openVideo: ", e);
        }
    }

    public void setVideoPath(String path) {
        mPath = path;
        openVideo();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(null);
            mediaPlayer.release();
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mTextureView = null;
        mSurfaceTexture = null;

    }
    public void setTextureView(ResizeTextureView textureView) {
        mTextureView = textureView;
        mTextureView.setSurfaceTextureListener(this);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    /**
     *
     * @param parent 父控件,必须为FrameLayout或RelativeLayout
     */
    public void setRootView(ViewGroup parent) {
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
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(mediaPlayer, i);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        if (mOnInfoListener != null) {
            return mOnInfoListener.onInfo(mediaPlayer, i, i1);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(mediaPlayer);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        Log.d(TAG, "video onVideoSizeChanged: width=" + width + " height=" + height);
        if (mTextureView != null) {
            mTextureView.setVideoSize(width, height);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mediaPlayer);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
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
