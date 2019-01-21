package com.tanjinc.omgvideoplayer.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.tanjinc.omgvideoplayer.R;

public class AnimUtils {

    public static void showView(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.abc_fade_in);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
}
