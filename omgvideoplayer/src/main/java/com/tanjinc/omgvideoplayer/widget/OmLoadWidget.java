package com.tanjinc.omgvideoplayer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.R;


public class OmLoadWidget extends BaseWidget {
    private static final String TAG = "OmLoadWidget";

    private TextView mLoadingPercentTv;

    public OmLoadWidget(int id) {
        super(id);
    }

    public void setPercent(int percent) {
        Log.d(TAG, "video setPercent: " + percent);
        if (mLoadingPercentTv != null) {
            mLoadingPercentTv.setText(percent+"");
        }
    }
}
