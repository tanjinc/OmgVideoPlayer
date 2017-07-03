package com.tanjinc.playermanager;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;

/**
 * Created by tanjincheng on 17/7/1.
 */
public abstract class BaseVideoPlayer extends FrameLayout implements View.OnClickListener, View.OnTouchListener, MediaController.MediaPlayerControl{

    private static final String TAG = "BaseVideoPlayer";
    private View mStartBtn;
    private View mSwitchBtn;
    private ViewGroup mVideoContainer;

    private ViewGroup mFirstVideoRoot;
    private ViewGroup mVideoPlayerRoot;

    private @LayoutRes int mFirstLayoutId;
    private @LayoutRes int mSecondLayoutId;

    private boolean isFull;
    private boolean mScreenOn = true;

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
        isFull = false;
    }

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
        mStartBtn.setOnClickListener(this);
        mSwitchBtn = findViewById(R.id.switch_btn);
        mSwitchBtn.setOnClickListener(this);
        MediaPlayerManager.getInstance().setRootView(mVideoContainer);
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        mScreenOn = screenOn;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.start_btn:
                start();
                break;
            case R.id.switch_btn:
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

    public void onCompletion(){
        MediaPlayerManager.getInstance().release();
        setScreenOn(false);
        mContext = null;
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
        MediaPlayerManager.getInstance().mediaPlayer.start();
    }

    @Override
    public void pause() {
        setScreenOn(false);
        MediaPlayerManager.getInstance().mediaPlayer.start();
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
}
