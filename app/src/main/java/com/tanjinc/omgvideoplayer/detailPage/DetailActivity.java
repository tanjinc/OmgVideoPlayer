package com.tanjinc.omgvideoplayer.detailPage;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;
import com.tanjinc.omgvideoplayer.MyVideoPlayer;
import com.tanjinc.omgvideoplayer.SampleVideoPlayer;
import com.tanjinc.omgvideoplayer.listPage.VideoAdapter;
import com.tanjinc.playermanager.R;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private NestedScrollView mScrollView;
    private RecyclerView mRecyclerView;
    MyVideoPlayer mVideoPlayer;

    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    private ArrayList<Fragment> mFragmentArray = new ArrayList<>();

    private String mVideoUrl = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";
    private String mImageUrl = "http://p8.qhimg.com/t019283d762742416df.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mScrollView = (NestedScrollView) findViewById(R.id.scroller_view);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().setAttributes(lp);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        FrameLayout videoRoot = (FrameLayout) findViewById(R.id.video_root_layout);

        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mVideoPlayer = new MyVideoPlayer(this, BaseVideoPlayer.MediaPlayerType.EXO_PLAYER);
        mVideoPlayer.setRootView(videoRoot);
        mVideoPlayer.setVideoUrl(mVideoUrl);
        mVideoPlayer.start();

        initViewPager();
    }


    private void initViewPager() {

        mFragmentArray.add(new DetailFragment());
        mFragmentArray.add(new DetailFragment2());

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentArray);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        String a = String.format(getString(R.string.live_chat_welcome_word),  "bbb", "aaa");
    }

    private void openVideo() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mVideoPlayer.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayer != null) {
            mVideoPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
