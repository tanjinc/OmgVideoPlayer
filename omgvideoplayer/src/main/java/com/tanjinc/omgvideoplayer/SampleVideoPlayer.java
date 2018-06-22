package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by tanjincheng on 17/7/2.
 * 基本的播放功能实现在BaseVideoPlayer类中,
 * 此类可以根据业务需求增加不同需求如: 动画, 布局, 用户行为收集等
 */
public class SampleVideoPlayer extends BaseVideoPlayer implements View.OnTouchListener{
    private static final String TAG = "SampleVideoPlayer";

    public SampleVideoPlayer(Context context) {
        super(context);
        setType(VideoPlayerType.EXO_PLAYER);
        setMiniLayoutId(R.layout.om_video_mini_layout);         //设置小窗布局
        setFullLayoutId(R.layout.om_video_fullscreen_layout);   //设置全局布局
        setFloatLayoutId(R.layout.om_video_float_layout);       //设置悬浮窗布局

        registerWidget(WidgetType.LOADING, R.layout.om_video_loading_view);
        registerWidget(WidgetType.VOLUME, R.layout.om_video_volume_progress_layaout);
        //registerWidget(WidgetType.NETWORK, R.layout.om_video_network_warn_layout);
        init();
    }


    @Override
    public void setContentView(int id) {
        super.setContentView(id);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onBeginPlay() {
        super.onBeginPlay();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void pause() {
        super.pause();
    }


}
