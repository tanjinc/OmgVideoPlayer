package com.tanjinc.omgvideoplayer;

import android.content.Context;


/**
 * Created by tanjincheng on 17/7/2.
 * 基本的播放功能实现在BaseVideoPlayer类中,
 * 此类可以根据业务需求增加不同需求如: 动画, 布局, 用户行为收集等
 */
public class SampleVideoPlayer extends BaseVideoPlayer{
    private static final String TAG = "SampleVideoPlayer";

    public SampleVideoPlayer(Context context, MediaPlayerType mediaPlayerType) {

        super(context,
                new Builder()
                        .setMiniLayoutId(R.layout.om_video_mini_layout)
                        .setFullLayoutId(R.layout.om_video_fullscreen_layout)
                        .setFloatLayoutId(R.layout.om_video_float_layout)
                .setDisplayType(DisplayType.TextureView)
                .setMediaType(mediaPlayerType)
        );

        registerWidget(WidgetType.LOADING, R.layout.om_video_loading_view);
        registerWidget(WidgetType.VOLUME, R.layout.om_volume_info_layout);
    }

}
