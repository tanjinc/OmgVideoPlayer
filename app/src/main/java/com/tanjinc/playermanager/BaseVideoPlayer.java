package com.tanjinc.playermanager;

import android.content.Context;
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
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


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
    //ui
    private View mStartBtn;
    private View mSwitchBtn;
    private TextView mCurrentPositionTv;
    private TextView mDurationTv;
    private ProgressBar mLoadingView;
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

    public VideoState mVideoState;


    public enum VideoState {
        Playing,
        Pause,
        Complete,
        Release
    }

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
                break;
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "video onCompletion: ");
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
        return false;
    }

    public void switchToFull() {
        isFull = true;
        setRootView(((ViewGroup)Utils.scanForActivity(mContext).getWindow().getDecorView()));
        setContentView(R.layout.om_video_fullscreen_layout);
//                Intent intent = new Intent(mContext, VideoWindowActivity.class);
//                intent.putExtra("action", ACTION_SWITCH_TO_FULL);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
    }

    public void exitFull() {
        isFull = false;
        setRootView(mFirstVideoRoot);
        setContentView(R.layout.om_video_mini_layout);
    }

    public VideoState getVideoState() {
        return mVideoState;
    }

    public boolean isFull() {
        return isFull;
    }
    public void onResume() {
        if (mFirstLayoutId != 0) {
            setContentView(mFirstLayoutId);
        }
    }

    public void onDestroy() {
        MediaPlayerManager.getInstance().release();
        setScreenOn(false);
        mHandler.removeCallbacks(null);
        mContext = null;
    }

    public void setVideoPath(String videoPath) {
        MediaPlayerManager.getInstance().setVideoPath(videoPath);
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
    @Override
    public void start() {
        setScreenOn(true);
        mHandler.post(mProgressRunnable);
        MediaPlayerManager.getInstance().mediaPlayer.start();
    }

    @Override
    public void pause() {
        setScreenOn(false);
        mHandler.removeCallbacks(mProgressRunnable);
        MediaPlayerManager.getInstance().mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return MediaPlayerManager.getInstance().mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return MediaPlayerManager.getInstance().mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        MediaPlayerManager.getInstance().mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return  MediaPlayerManager.getInstance().mediaPlayer.isPlaying();
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
