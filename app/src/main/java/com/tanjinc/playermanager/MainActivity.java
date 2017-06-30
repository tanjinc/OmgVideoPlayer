package com.tanjinc.playermanager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mVideoRootView;
    private FrameLayout mVideoRootView2;
    private ResizeTextureView mTextureView1;
    private Button mButton;
    private Button mSwitchFullBtn;
    private boolean mIsFirst = true;
    private boolean mIsFull;
    private Context mContext;

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
                VideoPlayerManager.getInstance().setRootView(mIsFirst ? mVideoRootView : mVideoRootView2);

            }
        });


        mSwitchFullBtn = (Button) findViewById(R.id.switch_full_btn);
        mSwitchFullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 切换全屏
                ViewGroup vp = (ViewGroup) getWindow().getDecorView();
                VideoPlayerManager.getInstance().setRootView(vp);
                mIsFull = true;
            }
        });

        mTextureView1 = new ResizeTextureView(this);
        //1. 保存textureview,切换时,直接将保存好的textureview add到目标控件
        //2. 设置Listener, 只有textureview准备好了才能播
        VideoPlayerManager.getInstance().setTextureView(mTextureView1);
        VideoPlayerManager.getInstance().setRootView(mVideoRootView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        if (mIsFull) {
            VideoPlayerManager.getInstance().setRootView(mVideoRootView);
            return;
        }
        super.onBackPressed();
    }
}
