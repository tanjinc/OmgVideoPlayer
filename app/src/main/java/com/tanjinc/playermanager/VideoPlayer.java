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


    public VideoPlayer(Context context, ViewGroup viewGroup) {
        super(context, viewGroup);
        mContext = context;
        setContentView(R.layout.om_video_mini_layout);
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
