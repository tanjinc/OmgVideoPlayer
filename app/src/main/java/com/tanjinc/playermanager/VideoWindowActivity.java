package com.tanjinc.playermanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class VideoWindowActivity extends AppCompatActivity {

    private String mAction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_window_activity_layout);

        mAction = getIntent().getStringExtra("action");

        VideoPlayer videoPlayer = new VideoPlayer(this, (ViewGroup) findViewById(R.id.video_container));
        videoPlayer.setContentView(R.layout.om_video_fullscreen_layout);
        addContentView(videoPlayer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    protected void onResume() {
        super.onResume();
//        VideoPlayerManager.getCurrentVideoPlayer().setContentView(R.layout.om_video_fullscreen_layout);
    }

    @Override
    public void onBackPressed() {
        if (mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (!mAction.equals(BaseVideoPlayer.ACTION_SWITCH_TO_FULL)) {
        }
        super.onDestroy();
    }
}
