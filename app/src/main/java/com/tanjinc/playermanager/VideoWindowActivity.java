package com.tanjinc.playermanager;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class VideoWindowActivity extends AppCompatActivity {

    private String mAction = "";
    private BaseVideoPlayer mBaseVideoPlayer;
    private @LayoutRes int layoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_window_activity_layout);

        mAction = getIntent().getStringExtra("action");

        mBaseVideoPlayer = VideoPlayerManager.getVideoPlayer();
        ((ViewGroup)mBaseVideoPlayer.getParent()).removeView(mBaseVideoPlayer);

        addContentView(mBaseVideoPlayer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        layoutId = getIntent().getIntExtra("full_layout_id", 0);
        if (layoutId != 0) {
            mBaseVideoPlayer.setContentView(layoutId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBaseVideoPlayer != null) {
            mBaseVideoPlayer.start();
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
}
