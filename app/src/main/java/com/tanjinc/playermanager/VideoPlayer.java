package com.tanjinc.playermanager;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tanjincheng on 17/7/2.
 */
public class VideoPlayer extends BaseVideoPlayer implements View.OnTouchListener{
    private Context mContext;

    private String mPath = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";

    public VideoPlayer(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
        mContext = context;
        setContentView(R.layout.om_video_mini_layout);

        setVideoPath(mPath);
        setVideoThumb("http://pic28.nipic.com/20130417/3822951_115921742000_2.jpg");
        start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_btn:
                break;
        }
        super.onClick(view);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
