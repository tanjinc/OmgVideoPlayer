package com.tanjinc.omgvideoplayer.detailPage;

import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.tanjinc.playermanager.R;

public class DetailActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mScrollView = (ScrollView) findViewById(R.id.scroller_view);
    }
}
