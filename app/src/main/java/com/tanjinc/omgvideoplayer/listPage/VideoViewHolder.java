package com.tanjinc.omgvideoplayer.listPage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tanjinc.playermanager.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public ViewGroup mVideoRoot;
    public ImageView mPreViewImg;
    public ImageView mPlayBtn;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mVideoRoot = (ViewGroup) itemView.findViewById(R.id.test_video_root);
        mPreViewImg = (ImageView) itemView.findViewById(R.id.test_preview_img);
        mPlayBtn = (ImageView) itemView.findViewById(R.id.test_play_btn1);
    }

    public void setActive(boolean active) {
        if (active) {
            mPlayBtn.setVisibility(View.GONE);
            mPreViewImg.setVisibility(View.GONE);
        } else {
            mPlayBtn.setVisibility(View.VISIBLE);
            mPreViewImg.setVisibility(View.VISIBLE);
        }
    }
}
