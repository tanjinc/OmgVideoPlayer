package com.tanjinc.omgvideoplayer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.detailPage.DetailActivity;
import com.tanjinc.omgvideoplayer.floatPage.FloatDemoActivity;
import com.tanjinc.omgvideoplayer.listPage.ListVideoActivity;

import com.tanjinc.playermanager.R;



/**
 * Created by tanjinc on 17-4-5.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";

    View mBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main_layout);
        mBtn = findViewById(R.id.test_video_float_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_video_list_btn: {
                Intent intent = new Intent(getApplicationContext(),ListVideoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.test_video_detail_btn: {
                Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.test_video_float_btn: {
//                Intent intent = new Intent(getApplicationContext(),FloatDemoActivity.class);
//                startActivity(intent);
                showPopWindow();
                break;
            }
        }
    }

    private void showPopWindow() {
        PopupWindow popupWindow = new PopupWindow(this);
        View view = View.inflate(this, R.layout.popup_window_layout, null);
        popupWindow.setContentView(view);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        popupWindow.setTouchable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        mBtn.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = mBtn.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = mBtn.getMeasuredHeight();  //获取测量后的高度
        int[] location = new int[2];
        mBtn.getLocationOnScreen(location);

        int x = view.getMeasuredWidth();
        int y = popupHeight + view.getMeasuredHeight();
        popupWindow.showAsDropDown(mBtn, -x ,-y);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
