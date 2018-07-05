package com.tanjinc.omgvideoplayer;

import android.content.Context;


/**
 * Created by tanjincheng on 17/7/2.
 * 基本的播放功能实现在BaseVideoPlayer类中,
 * 此类可以根据业务需求增加不同需求如: 动画, 布局, 用户行为收集等
 */
public class SampleVideoPlayer extends BaseVideoPlayer{
    private static final String TAG = "SampleVideoPlayer";

    public SampleVideoPlayer(Context context) {
        super(context);
        setMediaType(VideoPlayerType.EXO_PLAYER);
        setDisplayType(DisplayType.TextureView);

        setMiniLayoutId(R.layout.om_video_mini_layout);         //设置小窗布局
        setFullLayoutId(R.layout.om_video_fullscreen_layout);   //设置全局布局
        setFloatLayoutId(R.layout.om_video_float_layout);       //设置悬浮窗布局

        registerWidget(WidgetType.LOADING, R.layout.om_video_loading_view);
        registerWidget(WidgetType.VOLUME, R.layout.om_video_volume_progress_layout);
        //registerWidget(WidgetType.NETWORK, R.layout.om_video_network_warn_layout);
        init();
    }

}
