package com.tanjinc.omgvideoplayer;

import com.tanjinc.omgvideoplayer.listPage.bean.BaseItem;
import com.tanjinc.omgvideoplayer.listPage.bean.ItemA;
import com.tanjinc.omgvideoplayer.listPage.bean.ItemB;
import com.tanjinc.omgvideoplayer.listPage.bean.ItemC;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * test for api
 */
public class ApiServiceImpl implements ApiService {


    @Override
    public Observable<List<VideoBean>> getUserInfo(String userName) {
        return null;
    }

    @Override
    public Observable<List<ItemA>> getItemListOfTypeA() {
        return Observable.create(new ObservableOnSubscribe<List<ItemA>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ItemA>> emitter) throws Exception {
                ArrayList<ItemA> result = new ArrayList<>();
                result.add(new ItemA());
                result.add(new ItemA());
                result.add(new ItemA());
                emitter.onNext(result);
            }
        });    }

    @Override
    public Observable<List<ItemB>> getItemListOfTypeB() {
        return Observable.create(new ObservableOnSubscribe<List<ItemB>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ItemB>> emitter) throws Exception {
                ArrayList<ItemB> result = new ArrayList<>();
                result.add(new ItemB());
                result.add(new ItemB());
                result.add(new ItemB());
                emitter.onNext(result);
            }
        });    }

    @Override
    public Observable<List<ItemC>> getItemListOfTypeC() {
        return Observable.create(new ObservableOnSubscribe<List<ItemC>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ItemC>> emitter) throws Exception {
                ArrayList<ItemC> result = new ArrayList<>();
                result.add(new ItemC());
                result.add(new ItemC());
                result.add(new ItemC());
                emitter.onNext(result);
            }
        });
    }

    @Override
    public Observable<List<VideoBean>> getVideo() {
         String mVideoUrl = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";
         String mImageUrl = "http://p8.qhimg.com/t019283d762742416df.jpg";
         String[] urlArray = {
                "http://v.g.m.liebao.cn/trans/7aef0775a9ee6ecd1072f6cf3cc4a746.mp4",
                "http://v.g.m.liebao.cn/trans/945862650ea2096540978d49b0e7fa89.mp4",
                "http://v.g.m.liebao.cn/trans/90ddef8f126c168f345a1fcf1498424a.mp4",
                "http://v.g.m.liebao.cn/trans/b8ee9d9a1b9c61b02a99adc9a5b8aecf.mp4"
                // "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4",
        };
         String[] imgArray = {
                "http://img.contents.m.liebao.cn/394c6adf-9d40-5bdb-9fb7-c5adbd5fb344_crop_660x370.jpg",
                "http://img.contents.m.liebao.cn/5e057bf0-fde4-5bc8-88ea-67519595f49f_crop_660x370.jpg",
                "http://img.contents.m.liebao.cn/ecf6eafd-5e4d-5259-a552-5989a4490de4_crop_660x370.jpg",
                "http://img.contents.m.liebao.cn/7977c7ff-bc52-515d-b01a-1654033e43ee_crop_660x370.jpg"
        };

         List<VideoBean> beanArrayList = new ArrayList<>();
        for(int i=0; i < urlArray.length; i++) {
            VideoBean item = new VideoBean();
            item.setPreImageUrl(imgArray[i]);
            item.setVideoUrl(urlArray[i]);
            item.setVideoTitle("");
            beanArrayList.add(item);
        }
        return Observable.fromArray(beanArrayList);
    }

    @Override
    public Observable<List<BaseItem>> getItemList() {
        return null;
    }

    @Override
    public Observable<List<String>> getColumns() {

        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                ArrayList<String> result = new ArrayList<>();
                result.add("video");
                result.add("a");
                result.add("b");
                result.add("c");
                emitter.onNext(result);
            }
        });
    }
}
