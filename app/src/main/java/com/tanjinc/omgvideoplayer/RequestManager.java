package com.tanjinc.omgvideoplayer;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestManager {

    public static boolean isDebug = true;

    private static final String BASE_URL = "https://api.github.com";
    public static final String BASE_TEST_URL = "https://api.github.com";

    private static ApiService sApiService;


    public static ApiService getApiService() {
        if (sApiService != null) {
            return sApiService;
        }

        if (isDebug) {
            sApiService = new ApiServiceImpl();
            return sApiService;
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return null;
                    }
                })
                .build();

        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        sApiService = client.create(ApiService.class);
        return sApiService;
    }
}
