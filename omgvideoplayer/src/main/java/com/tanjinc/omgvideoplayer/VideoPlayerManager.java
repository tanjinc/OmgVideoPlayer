package com.tanjinc.omgvideoplayer;

/**
 * Created by tanjinc on 17-7-7.
 */
public class VideoPlayerManager {
    public static BaseVideoPlayer mFirstPlayer;

    public static void setFirstPlayer(BaseVideoPlayer player) {
        mFirstPlayer = player;
    }
    public static BaseVideoPlayer getVideoPlayer() {
        return mFirstPlayer;
    }

    public static void releaseAll() {
        if (mFirstPlayer != null) {
            mFirstPlayer = null;
        }
    }
}
