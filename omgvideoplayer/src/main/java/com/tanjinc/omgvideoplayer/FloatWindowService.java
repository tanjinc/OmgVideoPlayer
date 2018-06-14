package com.tanjinc.omgvideoplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    /**
     * 浮动窗原始位置
     */
    private float startPositionX = 0;
    private float startPositionY = 0;

    private float lastX;
    private float lastY;
    private float mTouchStartX;
    private float mTouchStartY;

    private BaseVideoPlayer mBaseVideoPlayer;
    private MyBinder binder = new MyBinder();


    @Override
    public void onCreate() {
        Log.d(TAG, "video onCreate: ");
        super.onCreate();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Service.WINDOW_SERVICE);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            lastX = event.getRawX();
            lastY = event.getRawY();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    //记录悬浮窗原始位置
                    startPositionX = mWindowManagerLp.x;
                    startPositionY = mWindowManagerLp.y;
                    Log.d(TAG, "onTouch  down  : m  " + mTouchStartX + "   " + mTouchStartY);
                    Log.d(TAG, "onTouch  down  : last  " + lastX + "   " + lastY);
                    Log.d(TAG, "onTouch  down  : start  " + startPositionX + "   " + startPositionY);
                    Log.d(TAG, "onTouch  down  : Params  " + mWindowManagerLp.x + "  " + mWindowManagerLp
                            .y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //计算新的位置
                    mWindowManagerLp.x = (int) (lastX - mTouchStartX);
                    mWindowManagerLp.y = (int) (lastY - mTouchStartY);

                    //如果原始位置在中间，所以需要减去屏幕宽高的一半
//                      mWindowManagerLp.x = (int) (lastX - mTouchStartX - contentWidth / 2);
//                      mWindowManagerLp.y = (int) (lastY - mTouchStartY - contentHeight / 2);

                    Log.d(TAG, "onTouch  move  : m  " + mTouchStartX + "   " + mTouchStartY);
                    Log.d(TAG, "onTouch  move  : last  " + lastX + "   " + lastY);
                    Log.d(TAG, "onTouch  move  : start  " + startPositionX + "   " + startPositionY);
                    Log.d(TAG, "onTouch  move  : Params  " + mWindowManagerLp.x + "  " + mWindowManagerLp
                            .y);
//
                    mWindowManager.updateViewLayout(mRootView, mWindowManagerLp);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouch  up  : m  " + mTouchStartX + "   " + mTouchStartY);
                    Log.d(TAG, "onTouch  up  : last  " + lastX + "   " + lastY);
                    Log.d(TAG, "onTouch  up  : start  " + startPositionX + "   " + startPositionY);
                    Log.d(TAG, "onTouch  up  : Params  " + mWindowManagerLp.x + "  " + mWindowManagerLp.y);
                    if (Math.abs(mWindowManagerLp.x - startPositionX) < 20 && Math.abs(mWindowManagerLp.y
                            - startPositionY) < 20) {
                        Toast.makeText(getApplication(), "click", Toast.LENGTH_LONG);
                    }
                    break;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "video onBind: ");
        mRootView = new FrameLayout(getApplication());
        mRootView.setBackgroundColor(intent.getIntExtra("background", Color.BLACK));

        mBaseVideoPlayer = BaseVideoPlayer.getStaticPlayer();
        ((ViewGroup)mBaseVideoPlayer.getParent()).removeView(mBaseVideoPlayer);
        mBaseVideoPlayer.setContext(this);
        mBaseVideoPlayer.setRootView(mRootView);
        mRootView.setOnTouchListener(mOnTouchListener);

        mRootLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mWindowManagerLp = new WindowManager.LayoutParams();
        mWindowManagerLp.width = intent.getIntExtra("width", getResources().getDimensionPixelSize(R.dimen.omg_float_window_width));
        mWindowManagerLp.height = intent.getIntExtra("height", getResources().getDimensionPixelSize(R.dimen.omg_float_window_height));
        mWindowManagerLp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams
                .FLAG_NOT_FOCUSABLE;
        mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_TOAST;
        if (Build.VERSION.SDK_INT >= 19) {
            mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWindowManagerLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowManagerLp.gravity = Gravity.TOP | Gravity.START;
        mWindowManagerLp.x = 0;
        mWindowManagerLp.y = 0;
        mWindowManagerLp.token = mRootView.getWindowToken();
        mWindowManager.addView(mRootView, mWindowManagerLp);

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
        mWindowManager.removeView(mRootView);
        mBaseVideoPlayer = null;
        stopSelf();
    }
    public class MyBinder extends Binder {
        public FloatWindowService getService(){
            return FloatWindowService.this;
        }
    }
}
