package com.tanjinc.omgvideoplayer;

import com.tanjinc.omgvideoplayer.listPage.bean.*;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/users")
    Observable<List<VideoBean>> getUserInfo(@Query("users") String userName);

    @GET("/path/to/api")
    Observable<List<ItemA>> getItemListOfTypeA();
    @GET("/path/to/api")
    Observable<List<ItemB>> getItemListOfTypeB();
    @GET("/path/to/api")
    Observable<List<ItemC>> getItemListOfTypeC();

    @GET("/video")
    Observable<List<VideoBean>> getVideo();

    @GET("/path/to/api2")
    Observable<List<BaseItem>> getItemList();


    // 需要展示的数据顺序
    @GET("/path/to/api")
    Observable<List<String>> getColumns();
}
