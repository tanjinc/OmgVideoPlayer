package com.tanjinc.omgvideoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tanjinc.playermanager.R;


public class OmgTestActivity extends AppCompatActivity {

    private OmgVideoView mOmgVideoView;

    private FrameLayout mPreLayout;
    private View mMaskLayout;
    private ImageView mVideoImage;
    private ImageView mPlayBtn;

    private String mImageUrl = "https://ws1.sinaimg.cn/large/610dc034ly1fp9qm6nv50j20u00miacg.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omg_test);
//        mOmgVideoView = new OmgVideoView(this);
        mOmgVideoView = (OmgVideoView) findViewById(R.id.video_player);
        mMaskLayout = findViewById(R.id.mask_layout);
        mPreLayout = (FrameLayout) findViewById(R.id.pre_layout);
        mPreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });
        mPlayBtn = (ImageView) findViewById(R.id.video_play_btn);
        mVideoImage = (ImageView) findViewById(R.id.video_image);
        Glide.with(this).load(mImageUrl).centerCrop().into(mVideoImage);



    }

    private void playVideo() {
//        mOmgVideoView.setImageUrl(mImageUrl);
        scaleView(mMaskLayout);
        mVideoImage.setVisibility(View.GONE);
        mPlayBtn.setVisibility(View.GONE);
//        mOmgVideoView.setRootView(mPreLayout);

        mOmgVideoView.setVideoUrl("http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4");
        mOmgVideoView.start();
    }

    private void scaleView(final View view) {


        ScaleAnimation scaleAnimation = new ScaleAnimation(1,1.2f,1,1f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(scaleAnimation);
        scaleAnimation.start();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mOmgVideoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOmgVideoView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOmgVideoView.onDestroy();
    }
}
