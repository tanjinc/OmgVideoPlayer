package com.tanjinc.omvideoplayer;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;
import com.tanjinc.playermanager.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tanjincheng on 18/1/19.
 */
public class MyVideoPlayer extends BaseVideoPlayer {
    private static final String TAG = "MyVideoPlayer";

    public MyVideoPlayer(Context context) {
        super(context, isHaveExoplayer() ? VideoPlayerType.EXO_PLAYER : VideoPlayerType.MEDIA_PLAYER);
        setMiniLayoutId(R.layout.om_video_mini_layout);          //设置小窗布局
        setFullLayoutId(R.layout.om_video_fullscreen_layout);   //设置全局布局
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
//        final MyGifView myGifView = new MyGifView(getContext());
//        addView(myGifView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                removeView(myGifView);
//            }
//        }, 3000);
    }

    private static boolean isHaveExoplayer() {
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
