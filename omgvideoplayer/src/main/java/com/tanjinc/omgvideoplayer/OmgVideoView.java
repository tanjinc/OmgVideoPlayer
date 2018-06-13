package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by tanjincheng on 18/4/10.
 */
public class OmgVideoView extends BaseVideoPlayer {
    private ImageView mThumb;

    private int mMiniLayoutId;
    private int mFullLayoutId;

    public OmgVideoView(Context context) {
        this(context, null);

    }

    public OmgVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OmgVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
