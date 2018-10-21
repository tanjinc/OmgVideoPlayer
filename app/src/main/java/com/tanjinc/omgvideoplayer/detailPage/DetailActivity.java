package com.tanjinc.omgvideoplayer.detailPage;

import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;
import com.tanjinc.omgvideoplayer.SampleVideoPlayer;
import com.tanjinc.playermanager.R;

public class DetailActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private NestedScrollView mScrollView;

    private String mVideoUrl = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";
    private String mImageUrl = "http://p8.qhimg.com/t019283d762742416df.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mScrollView = (NestedScrollView) findViewById(R.id.scroller_view);

        FrameLayout videoRoot = (FrameLayout) findViewById(R.id.video_root_layout);
        SampleVideoPlayer videoPlayer = new SampleVideoPlayer(this, BaseVideoPlayer.MediaPlayerType.EXO_PLAYER);
        videoPlayer.setRootView(videoRoot);
        videoPlayer.setVideoUrl(mVideoUrl);
        videoPlayer.start();
    }

    private void openVideo() {

    }
}
