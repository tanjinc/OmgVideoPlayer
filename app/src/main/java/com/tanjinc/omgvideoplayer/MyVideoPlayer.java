package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * Created by tanjincheng on 18/1/19.
 */
public class MyVideoPlayer extends BaseVideoPlayer {
    private static final String TAG = "MyVideoPlayer";
    private LiveDamnuWidget mLiveDamnuWidget;
    private View mBottomDanmu;
    private EditText mDanmuEt;
    private View mDanmuInputLayout;
    public MyVideoPlayer(Context context, MediaPlayerType type) {
        super(context,
                new Builder()
                        .setMiniLayoutId(com.tanjinc.omgvideoplayer.R.layout.om_video_mini_layout)
                        .setFullLayoutId(com.tanjinc.omgvideoplayer.R.layout.om_video_fullscreen_layout)
                        .setFloatLayoutId(com.tanjinc.omgvideoplayer.R.layout.om_video_float_layout)
                        .setDisplayType(DisplayType.TextureView)
                        .setMediaType(type)
        );

        registerWidget(WidgetType.LOADING, com.tanjinc.omgvideoplayer.R.layout.om_video_loading_view);
        registerWidget(WidgetType.VOLUME, com.tanjinc.omgvideoplayer.R.layout.om_volume_info_layout);
        registerWidget(WidgetType.NETWORK, com.tanjinc.omgvideoplayer.R.layout.om_video_network_warn_layout);
        registerWidget(WidgetType.LIGHT, com.tanjinc.omgvideoplayer.R.layout.om_light_info_layout);
        mLiveDamnuWidget = new LiveDamnuWidget();
        registerWidget(mLiveDamnuWidget);
    }

    @Override
    public void setContentView(int id) {
        super.setContentView(id);
        mDanmuInputLayout = findViewById(R.id.danmuInputLayout);
        if (mDanmuInputLayout != null) {
            mDanmuInputLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDanmu.setVisibility(View.VISIBLE);
                    mDanmuEt.requestFocus();
                }
            });
        }
        mDanmuEt = (EditText) findViewById(R.id.danmuEditView);
        mBottomDanmu = findViewById(R.id.full_screen_danmu_input_layout);
        mLiveDamnuWidget.addDanmaku(true);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hideIMM(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param view
     */
    public static void showIMM(Context context, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void setVideoUrl(String videoPath) {
        String proxyUrl = ProxyFactory.getProxy(getContext()).getProxyUrl(videoPath);
        super.setVideoUrl(proxyUrl);
    }

    @Override
    public void showController() {
        super.showController();
        mLiveDamnuWidget.addDanmaku(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLiveDamnuWidget != null) {
            mLiveDamnuWidget.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLiveDamnuWidget != null) {
            mLiveDamnuWidget.onResume();
        }
    }

    private static boolean isHaveExoPlayer() {
        try {
            Class c = Class.forName("com.google.android.exoplayer2.ExoPlayer");
            Log.d(TAG, "video isHaveExoplayer: exit");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "video isHaveExoplayer: not exit");

            return false;
        }
        return true;
    }
}
