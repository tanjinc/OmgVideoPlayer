package com.tanjinc.omgvideoplayer.listPage;

import android.os.Handler;
import android.os.Looper;

import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

import java.util.ArrayList;

public class VideoPresenter implements VideoContract.Presenter {
    private VideoContract.View mView;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mVideoUrl = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";
    private String mImageUrl = "http://p8.qhimg.com/t019283d762742416df.jpg";

    private String[] urlArray = {
            "http://v.g.m.liebao.cn/trans/7aef0775a9ee6ecd1072f6cf3cc4a746.mp4",
            "http://v.g.m.liebao.cn/trans/945862650ea2096540978d49b0e7fa89.mp4",
            "http://v.g.m.liebao.cn/trans/90ddef8f126c168f345a1fcf1498424a.mp4",
            "http://v.g.m.liebao.cn/trans/b8ee9d9a1b9c61b02a99adc9a5b8aecf.mp4"
           // "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4",
    };

    private String[] imgArray = {
            "http://img.contents.m.liebao.cn/394c6adf-9d40-5bdb-9fb7-c5adbd5fb344_crop_660x370.jpg",
            "http://img.contents.m.liebao.cn/5e057bf0-fde4-5bc8-88ea-67519595f49f_crop_660x370.jpg",
            "http://img.contents.m.liebao.cn/ecf6eafd-5e4d-5259-a552-5989a4490de4_crop_660x370.jpg",
            "http://img.contents.m.liebao.cn/7977c7ff-bc52-515d-b01a-1654033e43ee_crop_660x370.jpg"
    };
    public VideoPresenter(VideoContract.View view) {
        mView = view;
    }

    @Override
    public void loadVideo() {
        ArrayList<VideoBean> beanArrayList = new ArrayList<>();
        for(int i=0; i < urlArray.length; i++) {
            VideoBean item = new VideoBean();
            item.setPreImageUrl(imgArray[i]);
            item.setVideoUrl(urlArray[i]);
            item.setVideoTitle("");
            beanArrayList.add(item);
        }
        mView.showVideoList(beanArrayList, false);
    }

    @Override
    public void loadMoreVideo() {
        ArrayList<VideoBean> beanArrayList = new ArrayList<>();
        for(int i=0; i < urlArray.length; i++) {
            VideoBean item = new VideoBean();
            item.setPreImageUrl(imgArray[i]);
            item.setVideoUrl(urlArray[i]);
            item.setVideoTitle("");
            beanArrayList.add(item);
        }
        mView.showVideoList(beanArrayList, true);
    }

    @Override
    public void refresh() {
//        ArrayList<VideoBean> beanArrayList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            beanArrayList.add(new VideoBean("refresh: " + i));
//        }
//        mView.showVideoList(beanArrayList, false);
    }
}
