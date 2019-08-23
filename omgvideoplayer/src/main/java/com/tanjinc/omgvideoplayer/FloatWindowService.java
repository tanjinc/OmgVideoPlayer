package com.tanjinc.omgvideoplayer;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by tanjincheng on 18/3/18.
 * 悬浮窗
 */
public class FloatWindowService extends Service {
    private static final String TAG = "FloatWindowService";

    public static final String ACTION_EXIT_FLOAT = "exit_float";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowManagerLp;
    private FrameLayout mRootView;
    private FrameLayout.LayoutParams mRootLayoutParams;



    private BaseVideoPlayer mBaseVideoPlayer;
    private MyBinder binder = new MyBinder();

    public boolean hasAnima = true;
    public int mAniationDuration = 500;

    @Override
    public void onCreate() {
        Log.d(TAG, "video onCreate: ");
        super.onCreate();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Service.WINDOW_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "video onBind: ");

        FloatWindowOption option = (FloatWindowOption) intent.getSerializableExtra(FloatWindowOption.NAME);

        mRootView = new FrameLayout(getApplication());
        mRootView.setBackgroundColor(intent.getIntExtra("background", Color.BLACK));

        mBaseVideoPlayer = BaseVideoPlayer.getStaticPlayer();

        int[] preLocation = new int[2];
        mBaseVideoPlayer.getLocationInWindow(preLocation);

        ((ViewGroup)mBaseVideoPlayer.getParent()).removeView(mBaseVideoPlayer);
        mBaseVideoPlayer.setContext(this);
        mBaseVideoPlayer.setRootView(mRootView);
        mBaseVideoPlayer.setContentView(option.getLayoutId());
        mRootView.setOnTouchListener(new FloatTouchListener());

        mRootLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWindowManagerLp = new WindowManager.LayoutParams();
        mWindowManagerLp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //可延伸到刘海区域内
            mWindowManagerLp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowManagerLp.gravity = Gravity.TOP | Gravity.START;
        mWindowManagerLp.token = mRootView.getWindowToken();

        mWindowManagerLp.width = option.getWidth();
        mWindowManagerLp.height = option.getHeight();

        final int targetX = option.getTargetX();
        final int targetY = option.getTargetY();
        final int rawX;
        final int rawY;
        if (hasAnima) {
            mWindowManager.addView(mRootView, mWindowManagerLp);
            rawX = preLocation[0];
            rawY = preLocation[1];
            ValueAnimator valueAnimator = ValueAnimator.ofInt(rawY);
            valueAnimator.setIntValues(rawY, targetY);
            valueAnimator.setDuration(mAniationDuration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int updateValue = (int) animation.getAnimatedValue();
                    mWindowManagerLp.y = updateValue;
                    mWindowManagerLp.x = updateValue * rawX  / (rawY - targetY);
                    Log.d(TAG, "video onAnimationUpdate: ｙ＝" + updateValue + " targetX =" + mWindowManagerLp.x);
                    mWindowManager.updateViewLayout(mRootView, mWindowManagerLp);
                }
            });
            valueAnimator.start();
        } else {
            mWindowManagerLp.x = targetX;
            mWindowManagerLp.y = targetY;
            mWindowManager.addView(mRootView, mWindowManagerLp);
        }

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "video onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "video onDestroy: ");
        if (mBaseVideoPlayer != null) {
            mBaseVideoPlayer.onDestroy();
        }
        super.onDestroy();
    }


    public void stop() {
        Log.d(TAG, "video stop: ");
        if (mWindowManager != null) {
            mWindowManager.removeView(mRootView);
        }
        mBaseVideoPlayer = null;
        stopSelf();
    }
    public class MyBinder extends Binder {
        public FloatWindowService getService(){
            return FloatWindowService.this;
        }
    }

    private class FloatTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mWindowManagerLp.x = mWindowManagerLp.x + movedX;
                    mWindowManagerLp.y = mWindowManagerLp.y + movedY;

                    // 更新悬浮窗控件布局
                    mWindowManager.updateViewLayout(view, mWindowManagerLp);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
