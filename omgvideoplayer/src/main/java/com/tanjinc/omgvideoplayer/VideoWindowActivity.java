package com.tanjinc.omgvideoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tanjinc.omgvideoplayer.utils.ScreenUtils;


public class VideoWindowActivity extends AppCompatActivity {
    private static final String TAG = "VideoWindowActivity";

    private String mAction = "";
    private BaseVideoPlayer mBaseVideoPlayer;
    private int mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        ScreenUtils.setFullScreen(window);
        setContentView(R.layout.om_video_window_activity_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAction = getIntent().getStringExtra("action");
        mBaseVideoPlayer = BaseVideoPlayer.getStaticPlayer();
        ((ViewGroup)mBaseVideoPlayer.getParent()).removeView(mBaseVideoPlayer);
        mBaseVideoPlayer.setContext(this);
        mBaseVideoPlayer.setRootView((ViewGroup) findViewById(R.id.full_container));
        mBaseVideoPlayer.setContentView(getIntent().getIntExtra("full_layout_id", 0));
        mCurrentState = getIntent().getIntExtra("current_state", 0);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBaseVideoPlayer == null) {
            return;
        }
        if (isFinishing()) {
            mBaseVideoPlayer.resetRootView();
        } else {
            mBaseVideoPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBaseVideoPlayer != null) {
            mBaseVideoPlayer.onResume();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mBaseVideoPlayer != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {
                    mBaseVideoPlayer.exitFull();
                }
            } else {
                return mBaseVideoPlayer.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (!mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {
            BaseVideoPlayer.releaseStaticPlayer();
        }
        mBaseVideoPlayer = null;
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
