package com.yifeng.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.yifeng.ads.Utils.TToast;

//新插屏广告
public class InsertScreen {
    private final String TAG = "InsertScreen";
    @SuppressLint("StaticFieldLeak")
    private static InsertScreen _Instance;

    public static InsertScreen Inst() {
        if (_Instance == null) {
            _Instance = new InsertScreen();
        }
        return _Instance;
    }

    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mttFullVideoAd;
    private boolean mIsLoaded = false; //视频是否加载完成
    private Context mContext;

    public void Init(Context context, String codeId, int orientation) {
        mContext = context;
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(mContext);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(mContext);
        loadAd(codeId, orientation);
    }

    private boolean mHasShowDownloadActive = false;

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setAdLoadType(TTAdLoadType.LOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .setOrientation(orientation)
                .build();
        //step5:请求广告
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                TToast.show(mContext, message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                Log.e(TAG, "Callback --> onFullScreenVideoAdLoad");

                TToast.show(mContext, "FullVideoAd loaded  广告类型：" + getAdType(ad.getFullVideoAdType()));
                mttFullVideoAd = ad;
                mIsLoaded = false;

                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> FullVideoAd show");
                        TToast.show(mContext, "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> FullVideoAd bar click");
                        TToast.show(mContext, "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> FullVideoAd close");
                        TToast.show(mContext, "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> FullVideoAd complete");
                        TToast.show(mContext, "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "Callback --> FullVideoAd skipped");
                        TToast.show(mContext, "FullVideoAd skipped");

                    }

                });

                ad.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            TToast.show(mContext, "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(mContext, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(mContext, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(mContext, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(mContext, "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.d(TAG, " onFullScreenVideoCached");
                TToast.show(mContext, "onFullScreenVideoCached");
            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ad) {
                Log.e(TAG, "Callback --> onFullScreenVideoCached");
                mIsLoaded = true;
                TToast.show(mContext, "FullVideoAd video cached");
                ad.showFullScreenVideoAd((Activity) mContext, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
            }
        });
    }

    private String getAdType(int type) {
        switch (type) {
            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通全屏视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable全屏视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;
            case TTAdConstant.AD_TYPE_LIVE:
                return "直播流，type=" + type;
        }
        return "未知类型+type=" + type;
    }
}
