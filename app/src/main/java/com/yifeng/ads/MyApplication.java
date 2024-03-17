package com.yifeng.ads;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //初始化及设置开屏广告 参数 this
        new TTAdManagerHolder().init(this);
    }

    public static Context getContext() {
        return mContext;
    }
}