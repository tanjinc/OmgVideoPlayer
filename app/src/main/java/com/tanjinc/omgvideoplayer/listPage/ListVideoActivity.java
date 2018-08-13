package com.tanjinc.omgvideoplayer.listPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tanjinc.playermanager.R;

public class ListVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setPresenter(new VideoPresenter(videoFragment));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, videoFragment)
                .commit();
    }
}
