package com.yifeng.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.yifeng.ads.Utils.TToast;
import com.yifeng.ads.Utils.UIUtils;

import java.util.List;

public class Banner {
    private static Banner _Insstance;
    public static Banner Inst() {
        if (_Insstance == null) {
            _Insstance = new Banner();
        }
        return _Insstance;
    }
    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;
    private Context mContext;
    private final long startTime = 0;
    private boolean mHasShowDownloadActive = false;

    public void Init(Context context, FrameLayout frameLayout, String codeId) {
        mContext = context;
        mExpressContainer = frameLayout;
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
        //step3:可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(context);
        loadBannerAd(codeId);
    }

    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,注意其中的setNativeAdtype方法，具体参数含义参考文档
        int width = UIUtils.getScreenWidthInPx(mContext);
        int height = UIUtils.getScreenHeight(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1 建议调整为1~3
                .setExpressViewAcceptedSize(width, 50) //期望模板广告view的size,单位dp
                .setAdLoadType(TTAdLoadType.LOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                TToast.show(mContext, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads.get(0) == null) {
                    return;
                }

                final TTNativeExpressAd ad = ads.get(0);
                ad.setSlideIntervalTime(30 * 1000);
                bindAdListener(ad);
                ad.render();
                TToast.show(mContext, "load success!");
            }
        });

    }

    private void bindAdListener(TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                TToast.show(mContext, "渲染成功");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, false);

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(mContext, "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(mContext, "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(mContext, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(mContext, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(mContext, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 设置广告的不喜欢, 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     *
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */

    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                TToast.show(mContext, "bindDislike setDislikeCallback onShow");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                TToast.show(mContext, "点击 " + value);
                mExpressContainer.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    TToast.show(mContext, "模版Banner 穿山甲sdk强制将view关闭了");
                }
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "点击取消 ");
            }
        });
    }
}
