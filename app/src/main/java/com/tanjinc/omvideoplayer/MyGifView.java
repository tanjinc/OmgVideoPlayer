package com.tanjinc.omvideoplayer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.tanjinc.playermanager.R;

/**
 * Created by tanjincheng on 18/1/19.
 */
public class MyGifView extends ImageView {

    private AnimationDrawable mAnimationDrawable;
    public MyGifView(Context context) {
        super(context);
        initView(context);
    }

    public MyGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyGifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mAnimationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.gif);
        setBackground(mAnimationDrawable);

    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mAnimationDrawable.start();
        } else {
            mAnimationDrawable.stop();
        }
    }
}
