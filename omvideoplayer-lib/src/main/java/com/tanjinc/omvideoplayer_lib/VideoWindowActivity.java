package com.tanjinc.omvideoplayer_lib;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;


public class VideoWindowActivity extends AppCompatActivity {

    private String mAction = "";
    private BaseVideoPlayer mBaseVideoPlayer;
    private int mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.om_video_window_activity_layout);

        mAction = getIntent().getStringExtra("action");

        mBaseVideoPlayer = VideoPlayerManager.getVideoPlayer();
        ((ViewGroup)mBaseVideoPlayer.getParent()).removeView(mBaseVideoPlayer);
        mBaseVideoPlayer.setContext(this);
        mBaseVideoPlayer.setRootView((ViewGroup) findViewById(R.id.full_container));
        mBaseVideoPlayer.setContentView(getIntent().getIntExtra("full_layout_id", 0));
        mCurrentState = getIntent().getIntExtra("current_state", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBaseVideoPlayer != null) {
            if (mCurrentState == BaseVideoPlayer.STATE_PLAYING) {
                mBaseVideoPlayer.start();
            } else {
                mBaseVideoPlayer.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBaseVideoPlayer != null) {
            if (mCurrentState == BaseVideoPlayer.STATE_PLAYING) {
                mBaseVideoPlayer.start();
            } else {
                mBaseVideoPlayer.pause();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {
            mBaseVideoPlayer.exitFull();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (!mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {
            VideoPlayerManager.releaseAll();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
