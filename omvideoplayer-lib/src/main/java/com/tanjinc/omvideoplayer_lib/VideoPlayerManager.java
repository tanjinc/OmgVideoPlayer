package com.tanjinc.omvideoplayer_lib;

/**
 * Created by tanjinc on 17-7-7.
 */
public class VideoPlayerManager {
    public static BaseVideoPlayer mFirstPlayer;
    public static BaseVideoPlayer mSecondPlayer;

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
        if (mSecondPlayer != null) {
            mSecondPlayer = null;
        }
    }
}
