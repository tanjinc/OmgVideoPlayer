package com.tanjinc.omgvideoplayer.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtils {
    public static void loadImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

//    public static void loadImageBlur(String url, ImageView imageView, int radius) {
//        Glide.with(imageView.getContext())
//                .load(url)
//                .skipMemoryCache(true)
//                .centerCrop()
//                .transform(new BlurTransformation(radius, 10))
//                .into(imageView);
//    }
}
