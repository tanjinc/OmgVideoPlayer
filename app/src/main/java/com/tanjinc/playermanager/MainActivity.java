package com.tanjinc.playermanager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mVideoRootView;
    private FrameLayout mVideoRootView2;
    private ResizeTextureView mTextureView1;
    private Button mButton;
    private Button mSwitchFullBtn;
    private boolean mIsFirst = true;
    private Context mContext;

    private VideoPlayer mVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mVideoRootView = (FrameLayout) findViewById(R.id.video_root);
        mVideoRootView2 = (FrameLayout) findViewById(R.id.second_root);
        mButton = (Button) findViewById(R.id.switch_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //3. 将原来的textureView添加到新控件
                mIsFirst = !mIsFirst;
                mVideoPlayer.setRootView(mIsFirst ? mVideoRootView : mVideoRootView2);


            }
        });


        mSwitchFullBtn = (Button) findViewById(R.id.switch_full_btn);
        mSwitchFullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 切换全屏
                ViewGroup vp = (ViewGroup) getWindow().getDecorView();
//                MediaPlayerManager.getInstance().setRootView(vp);

            }
        });

        mVideoPlayer = new VideoPlayer(MainActivity.this, mVideoRootView);

    }

    @Override
    protected void onResume() {
        if (mVideoPlayer != null) {
//            mVideoPlayer.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayer.isFull()) {
            mVideoPlayer.exitFull();
            return;
        }
        super.onBackPressed();
    }
}
