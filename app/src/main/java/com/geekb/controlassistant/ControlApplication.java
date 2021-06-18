package com.geekb.controlassistant;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class ControlApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetManager.getInstance().initRetrofit();
        MMKV.initialize(this);
    }
}
