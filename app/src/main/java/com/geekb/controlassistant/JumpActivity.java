package com.geekb.controlassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;

public class JumpActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFirstLaunch = MMKV.defaultMMKV().decodeBool("isFirstLaunch", true);
        Intent intent;
        if (isFirstLaunch) {
            intent = new Intent(this, BluetoothActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
