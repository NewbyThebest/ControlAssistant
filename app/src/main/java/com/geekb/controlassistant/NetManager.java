package com.geekb.controlassistant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {
    private static final String BASE_IP = "192.168.10.114";
    private static NetManager manager;
    private ApiInterface mNetService;

    private NetManager() {

    }

    public static NetManager getInstance() {
        if (manager == null) {
            synchronized (NetManager.class) {
                if (manager == null) {
                    manager = new NetManager();
                }
            }
        }
        return manager;
    }

    public void initRetrofit() {
        String BASE_URL = "http://" + BASE_IP;
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        mNetService = retrofit.create(ApiInterface.class);
    }

    public ApiInterface getNetService() {
        return mNetService;
    }
}
