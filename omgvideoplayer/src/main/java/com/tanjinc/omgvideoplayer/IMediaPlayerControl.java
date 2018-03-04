package com.tanjinc.omgvideoplayer;

import android.view.ViewGroup;
import android.widget.MediaController;

/**
 * Created by tanjincheng on 17/8/10.
 */
public interface IMediaPlayerControl extends MediaController.MediaPlayerControl {

    //MediaPlayer的info类型
    public static final int MEDIA_INFO_UNKNOWN = 1;
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;

    // MediaPlayer的Error类型
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_IO = -1004;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;
    public static final int MEDIA_ERROR_SYSTEM = -2147483648;

    interface OnInfoListener {
        boolean onInfo(int var2, int var3);
    }

    interface OnErrorListener {
        boolean onError(int var2, int var3);
    }

    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged( int var2, int var3);
    }

    interface OnSeekCompleteListener {
        void onSeekComplete(int pos);
    }

    interface OnBufferingUpdateListener {
        void onBufferingUpdate(int percent);
    }

    interface OnCompletionListener {
        void onCompletion();
    }


    public interface OnPreparedListener {
        void onPrepared();
    }


    void setOnInfoListener(OnInfoListener onInfoListener);
    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);
    void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener);
    void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);
    void setOnPreparedListener(OnPreparedListener listener);
    void setOnErrorListener(OnErrorListener listener);
    void setOnCompletionListener(OnCompletionListener listener);

    void release();
    void setVideoPath(String path);
    void setParentView(ViewGroup parent);
    void setTextureView(ResizeTextureView textView);

    interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int var1);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();

        int getAudioSessionId();
    }
}
