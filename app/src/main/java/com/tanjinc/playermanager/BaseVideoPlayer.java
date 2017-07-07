package com.tanjinc.playermanager;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.playermanager.utils.ImageLoader;
import com.tanjinc.playermanager.utils.Utils;


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

    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    //ui
    private View mStartBtn;
    private View mSwitchBtn;
    private TextView mCurrentPositionTv;
    private TextView mDurationTv;
    private ProgressBar mLoadingView;
    private ImageView mVideoThumbView;
    private SeekBar mSeekbar;
    private ViewGroup mVideoContainer;

    private ViewGroup mFirstVideoRoot;
    private ViewGroup mVideoPlayerRoot;

    private static int SEEK_MAX = 1000;

    private @LayoutRes int mFirstLayoutId;
    private @LayoutRes int mSecondLayoutId;

    private boolean isFull;
    private boolean mScreenOn = true;

    private int mCurrentPosition;
    private int mDuration;
    private int mBufferPercent;
    private AudioManager mAudioManager;

    private int mCurrentState = STATE_IDLE;


    private ResizeTextureView mTextureView;
    private Context mContext;

    public static final String ACTION_SWITCH_TO_FULL = "action_switch_to_full";

    public BaseVideoPlayer(Context context, ViewGroup rootView) {
        super(context);
        mContext = context;
        mFirstVideoRoot = rootView;
        mVideoPlayerRoot = mFirstVideoRoot;
        setRootView(mVideoPlayerRoot);
        init(context);
    }

    private void init(Context context) {
        mTextureView = new ResizeTextureView(context);
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
                    mCurrentPositionTv.setText(Utils.stringForTime(mCurrentPosition));
                    mDurationTv.setText(Utils.stringForTime(mDuration));
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
        mVideoPlayerRoot = videoPlayerRoot;

        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        mVideoPlayerRoot.addView(this);
    }

    public void setContentView(@LayoutRes int id) {
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
        mVideoThumbView = (ImageView) findViewById(R.id.video_thumb);

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
        Log.d(TAG, "video onBufferingUpdate: i=" + i);
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
                if (mVideoThumbView != null && mVideoThumbView.isShown()) {
                    mVideoThumbView.setVisibility(GONE);
                }
                break;
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "video onCompletion: ");
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        setScreenOn(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.start_btn:
                if(!isPlaying()) {
                    start();
                } else {
                    pause();
                }
                break;
            case R.id.switch_full_btn:
                switchToFull();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    public void switchToFull() {
        isFull = true;
        boolean new_activity = false;
        if (!new_activity) {
            setRootView(((ViewGroup) Utils.scanForActivity(mContext).getWindow().getDecorView()));
            setContentView(R.layout.om_video_fullscreen_layout);
        } else {
            Intent intent = new Intent(mContext, VideoWindowActivity.class);
            intent.putExtra("action", ACTION_SWITCH_TO_FULL);
            intent.putExtra("layout_id", mSecondLayoutId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public void exitFull() {
        isFull = false;
        setRootView(mFirstVideoRoot);
        setContentView(R.layout.om_video_mini_layout);
    }

    public boolean isFull() {
        return isFull;
    }
    public void onResume() {
        if (mFirstLayoutId != 0) {
            setContentView(mFirstLayoutId);
        }
        start();
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
        setScreenOn(false);
        mContext = null;
    }

    public void setVideoPath(String videoPath) {
        MediaPlayerManager.getInstance().setVideoPath(videoPath);
        if (mVideoThumbView != null) {
            mVideoThumbView.setVisibility(VISIBLE);
        }
        showLoading();
    }

    public void setVideoThumb(String thumbPath) {
        if (mVideoThumbView != null) {
            ImageLoader.getInstance().loadImage(thumbPath, mVideoThumbView, 250, 150);
        }
    }

    private void setScreenOn(boolean isScreenOn) {
        if (Utils.scanForActivity(mContext) == null || !mScreenOn) {
            return;
        }
        Log.d(TAG, "video setScreenOn: " + isScreenOn);
        if (isScreenOn) {
            Utils.scanForActivity(mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            Utils.scanForActivity(mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    public void start() {
        setScreenOn(true);
        mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (mCurrentState != STATE_IDLE) {
            MediaPlayerManager.getInstance().mediaPlayer.start();
            mHandler.post(mProgressRunnable);
            mCurrentState = STATE_PLAYING;
        }
    }

    @Override
    public void pause() {
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
}
