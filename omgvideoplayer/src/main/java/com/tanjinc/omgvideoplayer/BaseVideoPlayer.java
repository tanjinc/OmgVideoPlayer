package com.tanjinc.omgvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.http.HttpGetProxy;
import com.tanjinc.omgvideoplayer.utils.Utils;
import com.tanjinc.omgvideoplayer.widget.BaseWidget;
import com.tanjinc.omgvideoplayer.widget.OmLoadWidget;
import com.tanjinc.omgvideoplayer.widget.OmVolumeWidget;

import java.util.ArrayList;


/**
 * Created by tanjincheng on 17/7/1.
 */
abstract class BaseVideoPlayer extends FrameLayout implements
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        IMediaPlayerControl.OnBufferingUpdateListener,
        IMediaPlayerControl.OnCompletionListener,
        IMediaPlayerControl.OnErrorListener,
        IMediaPlayerControl.OnInfoListener,
        IMediaPlayerControl.OnPreparedListener, IMediaPlayerControl.MediaPlayerControl{

    private static final String TAG = "BaseVideoPlayer";
    private static final int MSG_PROCESS = 100;
    private static final int MSG_LOADING_PROCESS = 101;



    public static final int STATE_ERROR              = -1;
    public static final int STATE_IDLE               = 0;
    public static final int STATE_PREPARING          = 1;
    public static final int STATE_PREPARED           = 2;
    public static final int STATE_PLAYING            = 3;
    public static final int STATE_PAUSED             = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    private @LayoutRes int mMiniLayoutId = -1;
    private @LayoutRes int mFullLayoutId = -1;

    private View mPreLoadingView;

    private OmVolumeWidget mVolumeWidget;
    private OmLoadWidget mLoadWidget;


    public enum VideoPlayerType {
        MEDIA_PLAYER,
        EXO_PLAYER
    }
    private VideoPlayerType mVideoPlayerType = VideoPlayerType.MEDIA_PLAYER;

    //ui
    protected View mStartBtn;
    protected View mSwitchBtn;
    protected TextView mTitleTv;
    protected TextView mCurrentPositionTv;
    protected TextView mDurationTv;
    protected SeekBar mSeekbar;
    protected FrameLayout mVideoContainer;

    private ImageView mPreviewImage;

    private ViewGroup mSaveVideoRoot;
    private ViewGroup mVideoPlayerRoot;

    private static int SEEK_MAX = 1000;


    private boolean isFull;
    private boolean mScreenOn = true;
    private boolean mIsControllerShowing = true;

    private boolean mUsePreBuffer;
    private HttpGetProxy mHttpGetProxy;

    private int mCurrentPosition;
    private int mDuration;
    private int mBufferPercent;
    private AudioManager mAudioManager;


    private int mCurrentState = STATE_IDLE;

    private String mVideoTitle;

    private ResizeTextureView mTextureView;
    private Context mContext;
    private Context mSaveContext;


    private IMediaPlayerControl mMediaPlayerManager;
    private static BaseVideoPlayer sBaseVideoPlayer = null;

    public static final String ACTION_SWITCH_TO_FULL = "action_switch_to_full";
    public static final String FULL_SCREEN_LAYOUT_ID = "full_screen_layout_id";

    public BaseVideoPlayer(Context context) {
        this(context, null);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContext(context);
        mTextureView = new ResizeTextureView(context.getApplicationContext());
        setRootView(mVideoPlayerRoot);
    }

    public BaseVideoPlayer setType(VideoPlayerType playerType) {
        mVideoPlayerType = playerType;
        return this;
    }

    public void init() {
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        if (mVideoPlayerType == VideoPlayerType.EXO_PLAYER) {
            mMediaPlayerManager = new ExoPlayerManager(mContext);
        } else {
            mMediaPlayerManager = new MediaPlayerManager(mContext);
        }
        mMediaPlayerManager.setTextureView(mTextureView);
        mMediaPlayerManager.setOnInfoListener(this);
        mMediaPlayerManager.setOnBufferingUpdateListener(this);
        mMediaPlayerManager.setOnCompletionListener(this);
        mMediaPlayerManager.setOnPreparedListener(this);

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
                    mBufferPercent = getBufferPercentage();
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
                        if (mBufferPercent > 0) {
                            mSeekbar.setSecondaryProgress(mBufferPercent * SEEK_MAX / 100);
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_PROCESS, 1000);
                    break;
                case MSG_LOADING_PROCESS:
                    if (mLoadWidget != null) {
                        mLoadWidget.setPercent(getBufferPercentage());
                        mHandler.sendEmptyMessageDelayed(MSG_LOADING_PROCESS, 100);
                    }
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
        mVideoPlayerRoot = videoPlayerRoot;

        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
//        if (videoPlayerRoot.getWidth() == 0 || videoPlayerRoot.getHeight() == 0) {
//            mVideoPlayerRoot.addView(this, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        } else {
//            mVideoPlayerRoot.addView(this, 0,new ViewGroup.LayoutParams(videoPlayerRoot.getWidth(), videoPlayerRoot.getHeight()));
//        }
        if (mVideoPlayerRoot instanceof FrameLayout) {
            (mVideoPlayerRoot).addView(this);
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

    public void setOutLineRadius(int radius) {
        setOutlineProvider(new TextureVideoViewOutlineProvider(50));
        setClipToOutline(true);
    }

    public BaseVideoPlayer setMiniLayoutId(@LayoutRes int id) {
        mMiniLayoutId = id;
        return this;
    }

    public BaseVideoPlayer setFullLayoutId(@LayoutRes int id) {
        mFullLayoutId = id;
        return this;
    }


    public enum WidgetType {
        LOADING,
        ERROR,
        NETWORK,
        VOLUME,
    }
    private ArrayList<BaseWidget> mWidgetArrayList = new ArrayList<>();

    protected void registerWidget(WidgetType widgetType, @LayoutRes int layoutId) {
        switch (widgetType) {
            case ERROR:
                break;
            case VOLUME:
                mVolumeWidget = new OmVolumeWidget(mContext, layoutId);
                mWidgetArrayList.add(mVolumeWidget);
                break;
            case LOADING:
                mLoadWidget = new OmLoadWidget(mContext, layoutId);
                mWidgetArrayList.add(mLoadWidget);
                break;
            case NETWORK:
                break;
        }
    }

    public BaseVideoPlayer setFull(boolean isFull) {
        this.isFull = isFull;
        return this;
    }

    public void setContext(Context context) {
        mSaveContext = mContext;
        mContext = context;
    }

    public void setVideoPlayerType(VideoPlayerType type) {
        mVideoPlayerType = type;
    }

    public void setContentView(@LayoutRes int id) {
        Log.d(TAG, "video setContentView: ");
        if (id == 0 || mContext == null) {
            Log.e(TAG, "video setContentView: id is 0");
            return;
        }
        removeAllViews();
        setBackgroundColor(Color.BLACK);
        mVideoContainer = (FrameLayout) LayoutInflater.from(mContext).inflate(id, this);
        mStartBtn = findViewById(R.id.start_btn);
        if (mStartBtn != null) {
            mStartBtn.setOnClickListener(this);
        }
        mSwitchBtn = findViewById(R.id.switch_full_btn);
        if (mSwitchBtn != null) {
            mSwitchBtn.setOnClickListener(this);
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


        if (mVolumeWidget != null) {
            for (BaseWidget widget : mWidgetArrayList) {
                widget.attachTo(mVideoContainer);
            }
        }

        mVideoContainer.setOnTouchListener(this);
        mMediaPlayerManager.setParentView(mVideoContainer);
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        mScreenOn = screenOn;
    }

    public void showLoading() {
        if (mLoadWidget != null) {
            mLoadWidget.setVisibility(VISIBLE);
        }
    }

    public void hideLoading() {
        if (mLoadWidget != null) {
            mLoadWidget.setVisibility(GONE);
        }
    }

    @Override
    public void onBufferingUpdate(int i) {
        Log.d(TAG, "video onBufferingUpdate: i=" + i);
        if (mSeekbar != null) {
            mBufferPercent = i;
            mSeekbar.setSecondaryProgress(mBufferPercent * SEEK_MAX / 100);
        }
    }

    @Override
    public boolean onError(int i, int i1) {
        Log.d(TAG, "video onError: error =  " + i);
        return false;
    }

    @Override
    public void onPrepared() {
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
//        MediaPlayerManager.getInstance().setRootView(mVideoPlayerRoot);
    }

    @Override
    public boolean onInfo(int what, int ext) {
        Log.d(TAG, "video onInfo: what = " + what);
        switch(what) {
            case IMediaPlayerControl.MEDIA_INFO_BUFFERING_START:
                showLoading();
                break;
            case IMediaPlayerControl.MEDIA_INFO_BUFFERING_END:
                hideLoading();
                break;
            case IMediaPlayerControl.MEDIA_INFO_VIDEO_RENDERING_START:
                hideLoading();
                onBeginPlay();
                if (mPreviewImage != null) {
                    mPreviewImage.setVisibility(GONE);
                    removeView(mPreviewImage);
                }
                break;
        }
        return true;
    }



    protected void onBeginPlay() {

    }
    @Override
    public void onCompletion() {
        Log.d(TAG, "video onCompletion: ");
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        setScreenOn(false);
        mHandler.removeCallbacks(mProgressRunnable);
        if (mStartBtn != null) {
            mStartBtn.setActivated(false);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mIsControllerShowing) {

        }
        mIsControllerShowing = !mIsControllerShowing;
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mVolumeWidget != null) {
                    int volume = mVolumeWidget.getVolume();
                    mVolumeWidget.setProgress(--volume);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                int volume = mVolumeWidget.getVolume();
                mVolumeWidget.setProgress(++volume);
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (isFull) {
                    exitFull();
                }
        }
        return false;
    }

    public void switchToFull() {
        isFull = true;
        boolean new_activity = true;
        mSaveVideoRoot = mVideoPlayerRoot;
        if (!new_activity) {
            setRootView(((ViewGroup) Utils.scanForActivity(mContext).getWindow().getDecorView()));
            setContentView(mFullLayoutId);
        } else {
            setStaticPlayer(this);
            Intent intent = new Intent(mContext.getApplicationContext(), VideoWindowActivity.class);
            intent.putExtra("action", ACTION_SWITCH_TO_FULL);
            intent.putExtra("full_layout_id", mFullLayoutId);
            intent.putExtra("current_state", mCurrentState);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public static void setStaticPlayer(BaseVideoPlayer player) {
        sBaseVideoPlayer = player;
    }
    public static BaseVideoPlayer getStaticPlayer() {
        return sBaseVideoPlayer;
    }

    public static void releaseStaticPlayer() {
        if (sBaseVideoPlayer != null) {
            sBaseVideoPlayer = null;
        }
    }

    public void exitFull() {
        isFull = false;
        mContext = mSaveContext;
        mSaveContext = null;
        setRootView(mSaveVideoRoot);
        setContentView(R.layout.om_video_mini_layout);
        releaseStaticPlayer();
    }

    public void startFloat() {
        setStaticPlayer(this);
        Intent intent = new Intent(mContext, FloatWindowService.class);
        mContext.startService(intent);
    }

    public boolean isFull() {
        return isFull;
    }

    public void onPause() {
        if (isPlaying()) {
            pause();
        }
    }

    public void onResume() {
        if (isInPlaybackState()) {
            start();
        }
    }

    public void release() {
        mMediaPlayerManager.release();
    }

    public void onDestroy() {
        mHandler.removeMessages(MSG_PROCESS);
        mHandler.removeCallbacks(mProgressRunnable);
        mMediaPlayerManager.release();
        releaseStaticPlayer();
        setScreenOn(false);
        mContext = null;
        mSaveContext = null;
        mSaveVideoRoot = null;
    }

    public void setUsePreBuffer(boolean usePreBuffer) {
        mUsePreBuffer = usePreBuffer;
    }

    public void setVideoUrl(String videoPath) {
        if (mMediaPlayerManager == null) {
            initMediaPlayer();
        }
        mMediaPlayerManager.setVideoPath(videoPath);
        showLoading();
    }

    /**
     * 设置ImageView
     * @param drawable
     */
    public BaseVideoPlayer setPreviewImage(Drawable drawable) {
        if (drawable != null) {
            mPreviewImage = new ImageView(mContext);
            mPreviewImage.setImageDrawable(drawable);
            mPreviewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            //添加到loading底下
            mVideoContainer.addView(mPreviewImage, 2, layoutParams);
        }
        return this;
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
            mMediaPlayerManager.start();
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
        mMediaPlayerManager.pause();
        mCurrentState = STATE_PAUSED;
        if (mStartBtn != null) {
            mStartBtn.setActivated(false);
        }
    }

    @Override
    public int getDuration() {
        return isInPlaybackState() ? mMediaPlayerManager.getDuration() : -1;
    }

    @Override
    public int getCurrentPosition() {
        return isInPlaybackState() ? mMediaPlayerManager.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int i) {
        mMediaPlayerManager.seekTo(i);
    }

    private boolean isInPlaybackState() {
        return (mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }
    @Override
    public boolean isPlaying() {
        return  isInPlaybackState() &&mMediaPlayerManager.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mMediaPlayerManager.getBufferPercentage();
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
        mMediaPlayerManager.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayerManager.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }


    public class TextureVideoViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public TextureVideoViewOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int leftMargin = 0;
            int topMargin = 0;
            Rect selfRect = new Rect(leftMargin, topMargin,
                    rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
            outline.setRoundRect(selfRect, mRadius);
        }
    }
}
