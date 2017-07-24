package com.tanjinc.omvideoplayer_lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omvideoplayer_lib.utils.Utils;


/**
 * Created by tanjincheng on 17/7/1.
 */
public  abstract class BaseVideoPlayer extends FrameLayout implements
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl{

    private static final String TAG = "BaseVideoPlayer";
    private static final int MSG_PROCESS = 100;

    public static final int STATE_ERROR              = -1;
    public static final int STATE_IDLE               = 0;
    public static final int STATE_PREPARING          = 1;
    public static final int STATE_PREPARED           = 2;
    public static final int STATE_PLAYING            = 3;
    public static final int STATE_PAUSED             = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    //ui
    protected View mStartBtn;
    protected View mSwitchBtn;
    protected TextView mTitleTv;
    protected TextView mCurrentPositionTv;
    protected TextView mDurationTv;
    protected ProgressBar mLoadingView;
    protected SeekBar mSeekbar;
    protected ViewGroup mVideoContainer;

    private ViewGroup mSaveVideoRoot;
    private ViewGroup mVideoPlayerRoot;

    private static int SEEK_MAX = 1000;

    private @LayoutRes int mMiniLayoutId;
    private @LayoutRes int mFullLayoutId;

    private boolean isFull;
    private boolean mScreenOn = true;
    private boolean mIsControllerShowing = true;

    private int mCurrentPosition;
    private int mDuration;
    private int mBufferPercent;
    private AudioManager mAudioManager;


    private int mCurrentState = STATE_IDLE;

    private String mVideoTitle;

    private ResizeTextureView mTextureView;
    private Context mContext;
    private Context mSaveContext;


    public static final String ACTION_SWITCH_TO_FULL = "action_switch_to_full";
    public static final String FULL_SCREEN_LAYOUT_ID = "full_screen_layout_id";

    public BaseVideoPlayer(Context context) {
        super(context);
        setContext(context);
        mTextureView = new ResizeTextureView(context.getApplicationContext());
        initMediaPlayer();
    }

    public BaseVideoPlayer(Context context, ViewGroup rootView) {
        super(context);
        setContext(context);
        mTextureView = new ResizeTextureView(context.getApplicationContext());
        setRootView(mVideoPlayerRoot);
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        MediaPlayerManager.getInstance().setTextureView(mTextureView);
        MediaPlayerManager.getInstance().setOnInfoListener(this);
        MediaPlayerManager.getInstance().setOnBufferingUpdateListener(this);
        MediaPlayerManager.getInstance().setOnCompletionListener(this);
        MediaPlayerManager.getInstance().setOnPreparedListener(this);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        isFull = false;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_PROCESS:
                    mCurrentPosition = getCurrentPosition();
                    mDuration = getDuration();
                    if(mCurrentPositionTv!= null) {
                        mCurrentPositionTv.setText(Utils.stringForTime(mCurrentPosition));
                    }
                    if (mDurationTv != null) {
                        mDurationTv.setText(Utils.stringForTime(mDuration));
                    }
                    if (mSeekbar != null) {
                        if (mDuration > 0) {
                            mSeekbar.setProgress(mCurrentPosition * SEEK_MAX / mDuration);
                        } else {
                            mSeekbar.setProgress(0);
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROCESS, 1000);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_PROCESS);
        }
    };

    /**
     * 设置整个videoplayer的父控件,包括video,和controller
     * @param videoPlayerRoot
     */
    public void setRootView(ViewGroup videoPlayerRoot) {
        if (videoPlayerRoot == null) {
            Log.e(TAG, "video setRootView is null");
            return;
        }
        mSaveVideoRoot = mVideoPlayerRoot;
        mVideoPlayerRoot = videoPlayerRoot;

        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (videoPlayerRoot.getWidth() == 0 || videoPlayerRoot.getHeight() == 0) {
            mVideoPlayerRoot.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mVideoPlayerRoot.addView(this, new ViewGroup.LayoutParams(videoPlayerRoot.getWidth(), videoPlayerRoot.getHeight()));
        }


        if (isFull && mFullLayoutId != 0) {
            setContentView(mFullLayoutId);
        } else {
            setContentView(mMiniLayoutId);
        }

    }

    public void setTitle(String title) {
        mVideoTitle = title;
        if (mTitleTv != null) {
            mTitleTv.setText(mVideoTitle);
        }
    }

    public void setMiniLayoutId(@LayoutRes int id) {
        mMiniLayoutId = id;
    }

    public void setFullLayoutId(@LayoutRes int id) {
        mFullLayoutId = id;
    }

    public void setContext(Context context) {
        mSaveContext = mContext;
        mContext = context;
    }

    public void setContentView(@LayoutRes int id) {
        Log.d(TAG, "video setContentView: ");
        if (id == 0) {
            Log.e(TAG, "video setContentView: id is 0");
            return;
        }
        removeAllViews();
        View.inflate(mContext, id, this);
        mVideoContainer = (ViewGroup) findViewById(R.id.video_container);
        mStartBtn = findViewById(R.id.start_btn);
        if (mStartBtn != null) {
            mStartBtn.setOnClickListener(this);
        }
        mSwitchBtn = findViewById(R.id.switch_full_btn);
        if (mSwitchBtn != null) {
            mSwitchBtn.setOnClickListener(this);
        }
        mLoadingView = (ProgressBar) findViewById(R.id.video_loading_view);
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
        mTitleTv = (TextView) findViewById(R.id.video_title);
        if (mTitleTv != null && mVideoTitle != null) {
            mTitleTv.setText(mVideoTitle);
        }

        mCurrentPositionTv = (TextView) findViewById(R.id.video_position_tv);
        mDurationTv = (TextView) findViewById(R.id.video_duration_tv);
        mSeekbar = (SeekBar) findViewById(R.id.video_seekbar);
        if (mSeekbar != null) {
            mSeekbar.setMax(SEEK_MAX);
            if (mDuration > 0) {
                mSeekbar.setProgress(mCurrentPosition * SEEK_MAX / mDuration);
            } else {
                mSeekbar.setProgress(0);
            }
            mSeekbar.setSecondaryProgress(mBufferPercent * SEEK_MAX / 100);
            mSeekbar.setOnSeekBarChangeListener(this);
        }
        if (mTextureView == null) {
            initMediaPlayer();
        }
        mVideoContainer.setOnTouchListener(this);
        MediaPlayerManager.getInstance().setRootView(mVideoContainer);
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        mScreenOn = screenOn;
    }

    public void showLoading() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(VISIBLE);
        }
    }

    public void hideLoading() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//        Log.d(TAG, "video onBufferingUpdate: i=" + i);
        if (mSeekbar != null) {
            mBufferPercent = i;
            mSeekbar.setSecondaryProgress(mBufferPercent * SEEK_MAX / 100);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d(TAG, "video onError: error =  " + i);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "video onPrepared: ");
        mCurrentState = STATE_PREPARED;
        start();
    }

    @Override
    @CallSuper
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start_btn) {
            if (!isPlaying()) {
                start();
            } else {
                pause();
            }
            if (mStartBtn != null) {
                mStartBtn.setActivated(isPlaying());
            }

        } else if (id == R.id.switch_full_btn) {
            switchToFull();

        } else {
        }
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "video onAttachedToWindow: ");
        super.onAttachedToWindow();
        MediaPlayerManager.getInstance().setRootView(mVideoPlayerRoot);
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int ext) {
        Log.d(TAG, "video onInfo: what = " + what);
        switch(what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                showLoading();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                hideLoading();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                hideLoading();
                onBeginPlay();
                break;
        }
        return true;
    }


    protected void onBeginPlay() {

    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "video onCompletion: ");
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        setScreenOn(false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mIsControllerShowing) {

        }
        mIsControllerShowing = !mIsControllerShowing;
        return true;
    }

    public void switchToFull() {
        isFull = true;
        boolean new_activity = true;
        if (!new_activity) {
            setRootView(((ViewGroup) Utils.scanForActivity(mContext).getWindow().getDecorView()));
            setContentView(mFullLayoutId);
        } else {
            VideoPlayerManager.setFirstPlayer(this);
            Intent intent = new Intent(mContext.getApplicationContext(), VideoWindowActivity.class);
            intent.putExtra("action", ACTION_SWITCH_TO_FULL);
            intent.putExtra("full_layout_id", mFullLayoutId);
            intent.putExtra("current_state", mCurrentState);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public void exitFull() {
        isFull = false;
        mContext = mSaveContext;
        mSaveContext = null;
        setRootView(mSaveVideoRoot);
        setContentView(R.layout.om_video_mini_layout);
    }

    public boolean isFull() {
        return isFull;
    }

    public void onPause() {
        if (isPlaying()) {
            pause();
        }
    }
    public void release() {
        MediaPlayerManager.getInstance().release();
    }

    public void onDestroy() {
        mHandler.removeMessages(MSG_PROCESS);
        mHandler.removeCallbacks(mProgressRunnable);
        MediaPlayerManager.getInstance().release();
        VideoPlayerManager.releaseAll();
        setScreenOn(false);
        mContext = null;
        mSaveContext = null;
        mSaveVideoRoot = null;
    }

    public void setVideoPath(String videoPath) {
        initMediaPlayer();
        MediaPlayerManager.getInstance().setVideoPath(videoPath);
        showLoading();
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    private void setScreenOn(boolean isScreenOn) {
        Activity activity = Utils.scanForActivity(mContext);
        if (activity == null || !mScreenOn) {
            return;
        }
        Log.d(TAG, "video setScreenOn: " + isScreenOn);
        if (isScreenOn) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "video onAudioFocusChange: " + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    release();
                    Log.d(TAG, "video AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        pause();
                    }
                    Log.d(TAG, "video AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

    @Override
    @CallSuper
    public void start() {
        Log.d(TAG, "video start: ");
        setScreenOn(true);
        mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (mCurrentState != STATE_IDLE) {
            MediaPlayerManager.getInstance().mediaPlayer.start();
            mHandler.post(mProgressRunnable);
            mCurrentState = STATE_PLAYING;
        }
        if (mStartBtn != null) {
            mStartBtn.setActivated(true);
        }
    }

    @Override
    @CallSuper
    public void pause() {
        Log.d(TAG, "video pause: ");
        setScreenOn(false);
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        mHandler.removeCallbacks(mProgressRunnable);
        MediaPlayerManager.getInstance().mediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        return isInPlaybackState() ? MediaPlayerManager.getInstance().mediaPlayer.getDuration() : -1;
    }

    @Override
    public int getCurrentPosition() {
        return isInPlaybackState() ? MediaPlayerManager.getInstance().mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int i) {
        MediaPlayerManager.getInstance().mediaPlayer.seekTo(i);
    }

    private boolean isInPlaybackState() {
        return (mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }
    @Override
    public boolean isPlaying() {
        return  isInPlaybackState() && MediaPlayerManager.getInstance().mediaPlayer.isPlaying();
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser) {
            int progress = seekBar.getProgress();
            seekTo(progress * mDuration / SEEK_MAX);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
