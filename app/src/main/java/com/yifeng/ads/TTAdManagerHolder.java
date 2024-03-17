package com.yifeng.ads;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {
    private static final String TAG = "TTAdManagerHolder";
    private static boolean sInit;
    private static String appid = "5495459";//这里写在穿山甲创建的应用id
    private static String appName = "遇见广告";
    private static TTAdManagerHolder _Instance;

    public static TTAdManager get() {
        return TTAdSdk.getAdManager();
    }

    public static TTAdManagerHolder Inst() {
        if (_Instance == null) {
            _Instance = new TTAdManagerHolder();
        }
        return _Instance;
    }

    public void init(final Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context), new TTAdSdk.InitCallback() {
                @Override
                public void success() {
                    Log.i(TAG, "success: ");
//                    SplashActivity.Inst().loadSplashAd();
                }

                @Override
                public void fail(int code, String msg) {
                    Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
                }
            });
            sInit = true;
        }
    }


    private TTAdConfig buildConfig(Context context) {
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        return new TTAdConfig.Builder()
                .appId(appid)
                .useTextureView(true) //默认使用SurfaceView播放视频广告,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(appName)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)//落地页主题
                .allowShowNotify(true) //是否允许sdk展示通知栏提示,若设置为false则会导致通知栏不显示下载进度
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                .supportMultiProcess(false) //是否支持多进程，true支持
                //.asyncInit(true) //是否异步初始化sdk,设置为true可以减少SDK初始化耗时。3450版本开始废弃~~
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
    }
}