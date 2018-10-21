package com.tanjinc.omgvideoplayer.listPage.bean;

public class VideoBean extends BaseItem {
    String preImageUrl;
    String videoUrl;
    String videoTitle;

    public String getPreImageUrl() {
        return preImageUrl;
    }

    public void setPreImageUrl(String preImageUrl) {
        this.preImageUrl = preImageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    @Override
    public int getType() {
        return TYPE_A;
    }

}
