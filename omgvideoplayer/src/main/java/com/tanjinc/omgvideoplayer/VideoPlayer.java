package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by tanjincheng on 17/7/2.
 * 基本的播放功能实现在BaseVideoPlayer类中,
 * 此类可以根据业务需求增加不同需求如: 动画, 布局, 用户行为收集等
 */
public class VideoPlayer extends BaseVideoPlayer implements View.OnTouchListener{
    //播放地址
//    private String mPath = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";

//    private String mThumb = "http://pic28.nipic.com/20130417/3822951_115921742000_2.jpg";

    public VideoPlayer(Context context) {
        super(context);
        setMiniLayoutId(R.layout.om_video_mini_layout);          //设置小窗布局
        setFullLayoutId(R.layout.om_video_fullscreen_layout);   //设置全局布局
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
//        if (id == R.id.switch_btn) {
//        }
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
