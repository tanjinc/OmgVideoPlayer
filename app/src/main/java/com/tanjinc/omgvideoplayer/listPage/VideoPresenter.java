package com.tanjinc.omgvideoplayer.listPage;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tanjinc.omgvideoplayer.ApiService;
import com.tanjinc.omgvideoplayer.RequestManager;
import com.tanjinc.omgvideoplayer.listPage.bean.BaseItem;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VideoPresenter implements VideoContract.Presenter {
    private static final String TAG = "VideoPresenter";
    private VideoContract.View mView;

    private final ApiService apiService = RequestManager.getApiService();

    public VideoPresenter(VideoContract.View view) {
        mView = view;
    }

    @SuppressLint("CheckResult")
    public void updateItem(String type) {

        Observable<? extends List<? extends BaseItem>> observable;

        switch (type) {
            case "a":
                observable = apiService.getItemListOfTypeA();
                break;
            case "b":
                observable = apiService.getItemListOfTypeB();
                break;
            case "c":
                observable = apiService.getItemListOfTypeC();
                break;
            case "video":
                observable = apiService.getVideo().startWith(new ArrayList<VideoBean>());
                break;
                default:
                    observable = apiService.getItemListOfTypeA();
                    break;

        }

        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<? extends BaseItem>>() {
                    @Override
                    public void accept(List<? extends BaseItem> items) throws Exception {
                        Log.d(TAG, "video accept: =====" + items.size());

                        for (int i = 0; i < items.size(); i++) {
                            Log.d(TAG, "video accept: i" + i + " " + items.get(i).getClass());
                        }
                        Log.d(TAG, "video accept: ===== end");
                        mView.showVideoList((List<BaseItem>) items, true);
                    }

                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadVideo() {
        apiService.getColumns()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        Log.d(TAG, "video accept: " + strings);
                        for (String path: strings) {
                            updateItem(path);
                        }
                    }
                });
    }


    @Override
    public void loadMoreVideo() {
//        ArrayList<VideoBean> beanArrayList = new ArrayList<>();
//        for(int i=0; i < urlArray.length; i++) {
//            VideoBean item = new VideoBean();
//            item.setPreImageUrl(imgArray[i]);
//            item.setVideoUrl(urlArray[i]);
//            item.setVideoTitle("");
//            beanArrayList.add(item);
//        }
//        mView.showVideoList(beanArrayList, true);
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
