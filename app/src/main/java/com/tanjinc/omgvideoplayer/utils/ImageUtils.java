package com.tanjinc.omgvideoplayer.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtils {
    public static void loadImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).fitCenter().into(imageView);
    }
}
