package com.tanjinc.omgvideoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.utils.Utils;
import com.tanjinc.omgvideoplayer.widget.BaseWidget;
import com.tanjinc.omgvideoplayer.widget.OmLightWidget;
import com.tanjinc.omgvideoplayer.widget.OmLoadWidget;
import com.tanjinc.omgvideoplayer.widget.OmNetworkWarnWidget;
import com.tanjinc.omgvideoplayer.widget.OmVolumeWidget;

import java.util.ArrayList;


/**
 * Created by tanjincheng on 17/7/1.
 */
public class BaseVideoPlayer extends FrameLayout implements
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener,
        IMediaPlayerControl.OnBufferingUpdateListener,
        IMediaPlayerControl.OnCompletionListener,
        IMediaPlayerControl.OnErrorListener,
        IMediaPlayerControl.OnInfoListener,
        IMediaPlayerControl.OnPreparedListener,
        IMediaPlayerControl.MediaPlayerControl{

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
    private @LayoutRes int mFloatLayoutId = -1;


    private OmVolumeWidget mVolumeWidget;
    private OmLoadWidget mLoadWidget;
    private OmNetworkWarnWidget mNetworkWarnWidget;
    private OmLightWidget mLightWidget;


    /**
     * 屏幕适配
     */
    public enum VideoViewType {
        SCREEN_ADAPTATION,  //屏幕等比自适应，无裁剪，无压缩，有可能黑边
        FULL_SCREEN,        //铺满全屏，无裁剪，有可能压缩，无黑边
        FULL_SCALE          ////等比放大，有裁剪，无压缩，无黑边
    }

    /**
     * 显示方式
     */
    public enum DisplayType {
        SurfaceView,
        TextureView
    }

    /**
     * 播放器类型
     */
    public enum MediaPlayerType {
        MEDIA_PLAYER,
        EXO_PLAYER
    }

    private VideoViewType mVideoViewType = VideoViewType.SCREEN_ADAPTATION;
    private DisplayType mDisplayType = DisplayType.TextureView;
    private MediaPlayerType mMediaPlayerType = MediaPlayerType.MEDIA_PLAYER;




    //ui
    protected View mStartBtn;
    protected View mSwitchBtn;
    protected View mCloseBtn;
    protected TextView mTitleTv;
    protected TextView mCurrentPositionTv;
    protected TextView mDurationTv;
    protected SeekBar mSeekbar;
    protected FrameLayout mVideoContainer;

    protected View mTopLayout, mBottomLayout, mLeftLayout, mRightLayout;

    private ImageView mPreviewImage;

    private ViewGroup mSaveVideoRoot;
    private ViewGroup mVideoPlayerRoot;

    private static int SEEK_MAX = 1000;


    private boolean isFloat;
    private boolean isFull;
    private boolean mScreenOn = true;
    private boolean mIsControllerShowing = true;
    private boolean mHaveRegister;

    private int mCurrentPosition;
    private int mDuration;
    private int mBufferPercent;
    private AudioManager mAudioManager;


    private int mCurrentState = STATE_IDLE;

    private String mVideoTitle;

    private ResizeTextureView mTextureView;
    private ResizeSurfaceView mSurfaceView;
    private Context mContext;
    private Context mSaveContext;


    private IMediaPlayerControl mMediaPlayerManager;
    private static BaseVideoPlayer sBaseVideoPlayer = null;

    public static final String ACTION_SWITCH_TO_FULL = "action_switch_to_full";
    public static final String FULL_SCREEN_LAYOUT_ID = "full_screen_layout_id";


    protected BaseVideoPlayer(Context context) {
        this(context, new Builder());
    }

    public BaseVideoPlayer(Context context, Builder builder) {
        super(context);
        mContext = context;
        if (builder.getMediaPlayerType() != null) {
            mMediaPlayerType = builder.getMediaPlayerType();
        }
        if (builder.getDisplayType() != null) {
            mDisplayType = builder.getDisplayType();
        }
        if (builder.getVideoViewType() != null) {
            mVideoViewType = builder.getVideoViewType();
        }

        mMiniLayoutId = builder.getMiniId();
        mFullLayoutId = builder.getFullId();
        mFloatLayoutId = builder.getFloatId();

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        initMediaPlayer();
    }

    public BaseVideoPlayer setVideoViewType(VideoViewType videoViewType) {
        mVideoViewType = videoViewType;
        if (mSurfaceView != null) {
            mSurfaceView.setVideoViewSize(mVideoViewType);
        }
        if (mTextureView != null) {
            mTextureView.setVideoViewSize(mVideoViewType);
        }
        return this;
    }

    private void initMediaPlayer() {
        if (mMediaPlayerType == MediaPlayerType.EXO_PLAYER) {
            mMediaPlayerManager = new ExoPlayerManager(mContext);
        } else {
            mMediaPlayerManager = new MediaPlayerManager(mContext);
        }

        if (mDisplayType == DisplayType.TextureView) {
            mTextureView = new ResizeTextureView(mContext);
            mMediaPlayerManager.setTextureView(mTextureView);
            mTextureView.setVideoViewSize(mVideoViewType);
        } else if (mDisplayType == DisplayType.SurfaceView) {
            mSurfaceView = new ResizeSurfaceView(mContext);
            mMediaPlayerManager.setSurfaceView(mSurfaceView);
            mSurfaceView.setVideoViewSize(mVideoViewType);
        }
        mMediaPlayerManager.setOnInfoListener(this);
        mMediaPlayerManager.setOnBufferingUpdateListener(this);
        mMediaPlayerManager.setOnCompletionListener(this);
        mMediaPlayerManager.setOnPreparedListener(this);

        isFull = false;
    }

    @SuppressLint("HandlerLeak")
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

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean isNetwork = Utils.isNetworkConnected(getContext());
                boolean isMobileNetWork = Utils.isMobileNet(getContext());
                int netWorkType = Utils.getNetWorkType(getContext());
                Log.d(TAG, "video onReceive: isNetwork=" + isNetwork + " isMobileNetWork=" + isMobileNetWork);
                if (isMobileNetWork && mNetworkWarnWidget != null) {
                    onPause();
                    mNetworkWarnWidget.show();
                }
            }
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




    public enum WidgetType {
        LOADING,
        ERROR,
        NETWORK,
        VOLUME,
        LIGHT,
    }
    private ArrayList<BaseWidget> mWidgetArrayList = new ArrayList<>();

    protected void registerWidget(WidgetType widgetType, @LayoutRes int layoutId) {
        switch (widgetType) {
            case ERROR:
                break;
            case VOLUME:
                mVolumeWidget = new OmVolumeWidget(layoutId);
                mWidgetArrayList.add(mVolumeWidget);
                break;
            case LOADING:
                mLoadWidget = new OmLoadWidget(layoutId);
                mWidgetArrayList.add(mLoadWidget);
                break;
            case NETWORK:
                mNetworkWarnWidget = new OmNetworkWarnWidget(this, layoutId);
                mWidgetArrayList.add(mNetworkWarnWidget);
                break;
            case LIGHT:
                mLightWidget = new OmLightWidget(layoutId);
                mWidgetArrayList.add(mLightWidget);
                break;
        }
    }

    public BaseVideoPlayer setFull(boolean isFull) {

        this.isFull = isFull;
        return this;
    }

    public void setContext(Context context) {
        if (mHaveRegister && mSaveContext != null) {
            mSaveContext.unregisterReceiver(mNetworkReceiver);
        }
        mSaveContext = mContext;
        mContext = context;
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

        mCloseBtn = findViewById(R.id.video_close_btn);
        if (mCloseBtn != null) {
            mCloseBtn.setOnClickListener(this);
        }

        mTopLayout = findViewById(R.id.top_layout);
        mBottomLayout = findViewById(R.id.bottom_layout);
//        mLeftLayout = findViewById(R.id.left_layout);
//        mRightLayout = findViewById(R.id.right_layout);

        if (mVolumeWidget != null) {
            for (BaseWidget widget : mWidgetArrayList) {
                widget.attachTo(mVideoContainer);
            }
        }

        mVideoContainer.setOnTouchListener(this);
        mMediaPlayerManager.setParentView(mVideoContainer);

//        if (mGuestureListenr == null) {
//            mGuestureListenr = new VideoGestureListener(getContext(), new GestureListener());
//        }
        registerReceiver();
    }


    public void registerReceiver() {
        if (mContext!= null && !mHaveRegister) {
            try {
                mContext.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                mHaveRegister = true;
            } catch (Exception e) {
                Log.e(TAG, "video registerReceiver: ", e);
            }
        }
    }

    public void unRegisterReceiver() {
        if (mContext!= null && mHaveRegister) {
            try {
                mContext.unregisterReceiver(mNetworkReceiver);
                mHaveRegister = false;
            } catch (Exception e) {
                Log.e(TAG, "video unRegisterReceiver: ", e);
            }
        }
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        mScreenOn = screenOn;
    }

    public void showLoading() {
        if (mLoadWidget != null) {
            mLoadWidget.show();
        }
    }

    public void hideLoading() {
        if (mLoadWidget != null) {
            mLoadWidget.hide();
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

        } else if (id == R.id.video_close_btn){
            onDestroy();
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

    public void showController() {
        if (mTopLayout != null) {
            mTopLayout.setVisibility(VISIBLE);
        }
        if (mBottomLayout != null) {
            mBottomLayout.setVisibility(VISIBLE);
        }

        mIsControllerShowing = true;
    }

    public void hideController() {
        if (mTopLayout != null) {
            mTopLayout.setVisibility(GONE);
        }
        if (mBottomLayout != null) {
            mBottomLayout.setVisibility(GONE);
        }
        mIsControllerShowing = false;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mVolumeWidget != null) {
                    mVolumeWidget.setKeyVolumeChange(false);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mVolumeWidget != null) {
                    mVolumeWidget.setKeyVolumeChange(true);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (isFull) {
                    exitFull();
                }
        }
        return false;
    }

    public Bitmap getScreenShort() {
        if (mTextureView != null) {
            return mTextureView.getBitmap();
        }
        if (mSurfaceView != null) {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = mSurfaceView.getHolder().lockCanvas();
            canvas.drawBitmap(bitmap,0, 0, null);
            return bitmap;
        }
        return null;
    }

    public void switchToFull() {
        isFull = true;
        mSaveVideoRoot = mVideoPlayerRoot;
        setStaticPlayer(this);
        Intent intent = new Intent(mContext.getApplicationContext(), VideoWindowActivity.class);
        intent.putExtra("action", ACTION_SWITCH_TO_FULL);
        intent.putExtra("full_layout_id", mFullLayoutId);
        intent.putExtra("current_state", mCurrentState);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        ((Activity)mContext).overridePendingTransition(0, 0);
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
        if (mContext == null) {
            ((Activity)mContext).finish();
        }
        unRegisterReceiver();
    }

    public void resetRootView() {
        isFull = false;
        mContext = mSaveContext;
        mSaveContext = null;
        setRootView(mSaveVideoRoot);
        setContentView(R.layout.om_video_mini_layout);
        releaseStaticPlayer();
    }

    private FloatWindowService mFloatService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FloatWindowService.MyBinder binder =  (FloatWindowService.MyBinder)service;
            mFloatService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mFloatService = null;
        }
    };
    public void startFloat(int x, int y) {
        Activity activity = Utils.scanForActivity(mContext);
        if (activity == null) {
            return;
        }

        setStaticPlayer(this);
        mSaveVideoRoot = mVideoPlayerRoot;
        int[] location = new int[2];
        getLocationOnScreen(location);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(mContext)) {
//                activity.startActivityForResult(
//                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())), 0);
//            }
//        }
        Intent intent = new Intent(mContext, FloatWindowService.class);
        intent.putExtra("float_layout_id", mFloatLayoutId);
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        isFloat = true;
    }

    public void exitFloat() {
        mSaveContext.unbindService(mServiceConnection);
        mContext = mSaveContext;
        mSaveContext = null;
        setRootView(mSaveVideoRoot);
        setContentView(mMiniLayoutId);
        releaseStaticPlayer();
        if (mFloatService != null) {
            mFloatService.stop();
        }
        isFloat = false;
    }

    public boolean isFull() {
        return isFull;
    }

    public boolean isFloat() {
        return isFloat;
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
        if (isFloat && mFloatService != null) {
            mFloatService.stop();
        }

        if (mWidgetArrayList != null) {
            for (BaseWidget widget : mWidgetArrayList) {
                widget.release();
            }
        }
        unRegisterReceiver();
        mMediaPlayerManager.release();
        mMediaPlayerManager = null;
        releaseStaticPlayer();
        setScreenOn(false);
        mContext = null;
        mSaveContext = null;
        mSaveVideoRoot = null;
    }


    public void setVideoUrl(String videoPath) {
        Log.d(TAG, "video setVideoUrl: ");
        if (mMediaPlayerManager == null) {
            initMediaPlayer();
        }
        mMediaPlayerManager.setVideoUrl(videoPath);
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

    /**
     * 设置ImageView
     * @param imageView
     */
    public BaseVideoPlayer setPreviewImage(ImageView imageView) {
        if (imageView != null) {
            mPreviewImage = new ImageView(mContext);
            mPreviewImage.setImageDrawable(imageView.getDrawable());
            mPreviewImage.setScaleType(imageView.getScaleType());
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
        return (mMediaPlayerManager != null && mCurrentState != STATE_ERROR &&
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
        if (mMediaPlayerManager != null) {
            mMediaPlayerManager.pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mMediaPlayerManager != null) {
            mMediaPlayerManager.start();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
//
//    private class GestureListener extends VideoGestureListener.SimpleOnGestureListener {
//        private float MIN_DISTANCE = 6.0f;
//        private boolean mIsFirstScroll = true;
//        private float mDownX;
//        private float mDownY;
//        private GestureOrientation mGestureOrientation;
//
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            if (mIsControllerShowing) {
//                hideController();
//            } else {
//                showController();
//            }
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            return true;
//        }
//
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "GestureListener onDown:" + e);
//            mDownX = e.getX();
//            mDownY = e.getY();
//            mSysBrightness = ScreenUtils.getCurScreenBrightness(getContext());
//            mGestureOrientation = GestureOrientation.getInstance(e.getX(), e.getY());
//            return true;
//        }
//
//        @Override
//        public boolean onUp(MotionEvent e) {
////            if (mIsSetSeek) {
////                mCurrentState = STATE_PLAYING;
////                seekTo((int) mCurrentPosition);
////                start();
////                mHandler.removeMessages(MSG_HIDE_SEEK_VIEW);
////                mHandler.sendEmptyMessageDelayed(MSG_HIDE_SEEK_VIEW, GESTURE_WIDGET_DISMISS_TIME);
////            }
//            if (mVolumeWidget != null && mVolumeWidget.isShown()) {
//                mVolumeWidget.showWithAutoHide(1000);
//            }
//            if (mLightWidget != null && mLightWidget.isShown()) {
//                mLightWidget.showWithAutoHide(1000);
//            }
//
//            mIsFirstScroll = true;
//            mDownX = -1.0f;
//            mDownY = -1.0f;
//            mOldX = -1;
//            mOldY = -1;
//            return true;
//
//        }
//
//        @Override
//        public void onCancel(MotionEvent e) {
//            Log.d(TAG, "video OnCancel");
//            mIsFirstScroll = true;
//            mDownX = -1.0f;
//            mDownY = -1.0f;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            if (!isFull) {
//                return true;
//            }
//            try {
//                if ((Math.abs(e2.getX() - mDownX) > MIN_DISTANCE || Math.abs(e2.getY() - mDownY) > MIN_DISTANCE)) {
////                    mIsFirstScroll = false;
//                    GestureOrientation.ScrollOrientation mScrollOrientation = mGestureOrientation.computeFirstAngle(e2.getX(), e2.getY());
//
//                    if (GestureOrientation.ScrollOrientation.SCROLL_INVALID == mScrollOrientation) {
//
//                    } else if (GestureOrientation.ScrollOrientation.SCROLL_HORIZONTAL == mScrollOrientation) {
////                        if (mIsSetVolumeLight) {
////                            return true;
////                        }
////                        if (mCurrentState != STATE_PLAYBACK_COMPLETED) {
////                            hideController();
////                        }
////                        mIsSetSeek = true;
////                        if (isPlaying()) {
////                            pause();
////                        }
////                        onSeekbarProgressTouch(mSeekbar, e2);
//                    } else {
//                        if (e1.getY() < mScreenHeight * 0.1f || mIsSetSeek) {
//                            return true;
//                        }
//                        mIsSetVolumeLight = true;
//                        if (mControllerShowing) {
//                            hideController();
//                        }
//                        if (e1.getX() > mScreenWidth * 0.5f) {
//                            int scrollProgress = (int) (distanceY * SEEK_MAX / (mScreenHeight * 0.8));
//                            mVolumeProgress += scrollProgress;
//                            if (mVolumeProgress >= SEEK_MAX) {
//                                mVolumeProgress = SEEK_MAX;
//                            }
//                            if (mVolumeProgress <= 0) {
//                                mVolumeProgress = 0;
//                            }
//
//                            mCurrentVolume = mVolumeProgress * mMaxVolume / SEEK_MAX;
//                            changeVolume();
//                            if (mVolumeWidget != null) {
//                                mVolumeWidget.getVolume()
//                            }
//                        } else {
//                            int scrollProgress = (int) (distanceY * SEEK_MAX / (mScreenHeight * 0.8));
//                            mLightProgress += scrollProgress;
//                            if (mLightProgress >= SEEK_MAX) {
//                                mLightProgress = SEEK_MAX;
//                            }
//                            if (mLightProgress <= 0) {
//                                mLightProgress = 0;
//                            }
//                            changeLight();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "video onScroll Exception: " + e);
//            }
//            return true;
//        }
//    }

    static class Builder {
        private MediaPlayerType mMediaPlayerType;
        private DisplayType displayType;
        private VideoViewType videoViewType;

        private @LayoutRes int miniId, fullId, floatId;

        public Builder setMiniLayoutId(@LayoutRes int id) {
            miniId = id;
            return this;
        }

        public int getMiniId() {
            return miniId;
        }

        public Builder setFullLayoutId(@LayoutRes int id) {
            fullId = id;
            return this;
        }

        public int getFullId() {
            return fullId;
        }

        public Builder setFloatLayoutId(@LayoutRes int id) {
            floatId = id;
            return this;
        }

        public int getFloatId() {
            return floatId;
        }

        public Builder setMediaType(MediaPlayerType playerType) {
            this.mMediaPlayerType = playerType;
            return this;
        }

        public MediaPlayerType getMediaPlayerType() {
            return mMediaPlayerType;
        }

        public DisplayType getDisplayType() {
            return displayType;
        }

        public Builder setDisplayType(DisplayType displayType) {
            this.displayType = displayType;
            return this;
        }

        public VideoViewType getVideoViewType() {
            return videoViewType;
        }

        public Builder setVideoViewType(VideoViewType videoViewType) {
            this.videoViewType = videoViewType;
            return this;
        }

        public BaseVideoPlayer create(Context context) {
            return new BaseVideoPlayer(context,this);
        }

    }

}
