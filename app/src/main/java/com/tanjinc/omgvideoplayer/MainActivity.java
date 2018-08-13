package com.tanjinc.omgvideoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tanjinc.omgvideoplayer.floatPage.FloatDemoActivity;
import com.tanjinc.omgvideoplayer.listPage.ListVideoActivity;

import com.tanjinc.playermanager.R;



/**
 * Created by tanjinc on 17-4-5.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main_layout);

        findViewById(R.id.test_video_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ListVideoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.test_video_float_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FloatDemoActivity.class);
                startActivity(intent);
            }
        });

    }

}
