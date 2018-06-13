package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.util.Log;

import com.tanjinc.playermanager.R;


/**
 * Created by tanjincheng on 18/1/19.
 */
public class MyVideoPlayer extends BaseVideoPlayer {
    private static final String TAG = "MyVideoPlayer";

    public MyVideoPlayer(Context context) {
        super(context);
        setMiniLayoutId(R.layout.om_video_mini_layout);          //设置小窗布局
        setFullLayoutId(R.layout.om_video_fullscreen_layout);   //设置全局布局
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        //        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                removeView(myGifView);
//            }
//        }, 3000);
    }

    @Override
    public void setVideoUrl(String videoPath) {
        String proxyUrl = ProxyFactory.getProxy(getContext()).getProxyUrl(videoPath);
        super.setVideoUrl(proxyUrl);
    }


    private static boolean isHaveExoPlayer() {
        try {
            Class c = Class.forName("com.google.android.exoplayer2.ExoPlayer");
            Log.d(TAG, "video isHaveExoplayer: exit");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "video isHaveExoplayer: not exit");

            return false;
        }
        return true;
    }
}
