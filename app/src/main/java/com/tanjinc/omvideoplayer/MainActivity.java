package com.tanjinc.omvideoplayer;

import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.tanjinc.omvideoplayer_lib.VideoPlayer;
import com.tanjinc.omvideoplayer_lib.http.HttpGetProxy;
import com.tanjinc.omvideoplayer_lib.http.Utils;
import com.tanjinc.playermanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button mButton;
    private boolean mIsFirst = true;

    private RecyclerView mRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ArrayList<VideoItem> mVideoItemList;

    ScalpelFrameLayout scalpelView;

    private VideoPlayer mVideoPlayer;
    private String mVideoUrl;
    private HttpGetProxy mHttpGetProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoPlayer = new VideoPlayer(MainActivity.this);
        mButton = (Button) findViewById(R.id.switch_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //3. 将原来的textureView添加到新控件
                mIsFirst = !mIsFirst;
            }
        });

        mVideoAdapter = new VideoAdapter(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mVideoAdapter);
        setData();

        mVideoAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VideoAdapter.ViewHolder viewHolder = (VideoAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                Log.d(TAG, "video onItemClick: " + viewHolder.root.getWidth() + " " +viewHolder.root.getHeight());
//                mVideoPlayer.setRootView(viewHolder.root);
//                mVideoPlayer.setVideoPath(mVideoAdapter.getItem(position).getVideoPath());
//                mVideoPlayer.setTitle(mVideoAdapter.getItem(position).getVideoTitle());
//                mVideoPlayer.start();
                playVideo(viewHolder.root, mVideoAdapter.getItem(position).getVideoPath(), position % 2 == 0);

            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        scalpelView = (ScalpelFrameLayout) findViewById(R.id.scalpel);
    }

    private void playVideo(ViewGroup viewGroup, final String videoUrl, boolean enablePrebuffer) {
        mVideoPlayer.setRootView(viewGroup);
        startTimeMills = System.currentTimeMillis();
        if (enablePrebuffer) {//使用预加载
            //初始化代理服务器

            if (mHttpGetProxy == null) {
                mHttpGetProxy = new HttpGetProxy(9110);
            }
            mHttpGetProxy.asynStartProxy();
            new Thread() {
                @Override
                public void run() {
                    String[] urls = mHttpGetProxy.getLocalURL(videoUrl);
                    String mp4Url = urls[0];
                    mVideoUrl = urls[1];

                    try {
                        String preBufferFilePath = mHttpGetProxy.prebuffer(mp4Url,
                                HttpGetProxy.SIZE);

                        Log.e(TAG, "预加载文件：" + preBufferFilePath);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.toString());
                        Log.e(TAG, Utils.getExceptionMessage(ex));
                    }

                    delayToStartPlay.sendEmptyMessage(0);
                }
            }.start();


        }else {//不使用预加载
            mVideoPlayer.setVideoPath(videoUrl);
            mVideoPlayer.start();
        }

    }

    long startTimeMills;
    private Handler delayToStartPlay = new Handler() {
        public void handleMessage(Message msg) {
            startTimeMills=System.currentTimeMillis();
            mVideoPlayer.setVideoPath(mVideoUrl);
        }
    };

    private Handler showController = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private void setData() {
        mVideoItemList = new ArrayList<>();
        VideoItem item = new VideoItem();
        item.setVideoTitle("王者荣耀视频");
        item.setVideoPath("http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4");
        item.setThumbPath("http://pic28.nipic.com/20130417/3822951_115921742000_2.jpg");
        mVideoItemList.add(item);

        VideoItem item2 = new VideoItem();
        item2.setVideoTitle("DNF视频2");
        item2.setVideoPath("http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/064b72f707e4edbecf824d105a8e7b94.mp4");
        item2.setThumbPath("http://p4.qhimg.com/t01be414a28e864dc80.jpg");
        mVideoItemList.add(item2);


        VideoItem item3 = new VideoItem();
        item3.setVideoTitle("DNF视频3");
        item3.setVideoPath("http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/064b72f707e4edbecf824d105a8e7b94.mp4");
        item3.setThumbPath("http://p4.qhimg.com/t01be414a28e864dc80.jpg");
        mVideoItemList.add(item3);

        VideoItem item4 = new VideoItem();
        item4.setVideoTitle("DNF视频4");
        item4.setVideoPath("http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/064b72f707e4edbecf824d105a8e7b94.mp4");
        item4.setThumbPath("http://p4.qhimg.com/t01be414a28e864dc80.jpg");
        mVideoItemList.add(item4);

        mVideoAdapter.setDatas(mVideoItemList);

    }

    @Override
    protected void onPause() {
        if (mVideoPlayer != null) {
            mVideoPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mVideoPlayer != null) {
            mVideoPlayer.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayer.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayer.isFull()) {
            mVideoPlayer.exitFull();
            return;
        }
        super.onBackPressed();
    }

    public class VideoItem {
        String thumbPath;
        String videoPath;


        String videoTitle;

        public VideoItem() {

        }

        public void setThumbPath(String thumbPath) {
            this.thumbPath = thumbPath;
        }
        public String getThumbPath() {
            return thumbPath;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }


        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

    }
}
