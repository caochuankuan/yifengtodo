package com.yifeng.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.yifeng.ads.Utils.TToast;
import com.yifeng.ads.Utils.UIUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    //是否强制跳转到主页面
    private boolean mForceGoMain;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3000;
    private String mCodeId = "889004737";//开屏广告代码位id
    private boolean mIsExpress = false; //是否请求模板广告
    private boolean mIsHalfSize = false;
    private static SplashActivity _Instance;

    public static SplashActivity Inst() {
        return _Instance;
    }

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _Instance = this;
        setContentView(R.layout.activity_splash);
        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        hideBottomUIMenu();  //隐藏虚拟按键，并且全屏
        getExtraInfo();
        loadSplashAd();
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String codeId = intent.getStringExtra("splash_rit");
        if (!TextUtils.isEmpty(codeId)) {
            mCodeId = codeId;
        }
        mIsExpress = intent.getBooleanExtra("is_express", false);
        mIsHalfSize = intent.getBooleanExtra("is_half_size", false);
    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    /**
     * 加载开屏广告
     */
    public void loadSplashAd() {
        AdSlot adSlot = null;
        float splashWidthDp = UIUtils.getScreenWidthDp(this);
        int splashWidthPx = UIUtils.getScreenWidthInPx(this);
        int screenHeightPx = UIUtils.getScreenHeight(this);
        float screenHeightDp = UIUtils.px2dip(this, screenHeightPx);
        float splashHeightDp;
        int splashHeightPx;
        if (mIsHalfSize) {
            // 开屏高度 = 屏幕高度 - 下方预留的高度，demo中是预留了屏幕高度的1/5，因此开屏高度传入 屏幕高度*4/5
            splashHeightDp = screenHeightDp * 4 / 5.f;
            splashHeightPx = (int) (screenHeightPx * 4 / 5.f);
        } else {
            splashHeightDp = screenHeightDp;
            splashHeightPx = screenHeightPx;
        }
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
//            float expressViewWidth = UIUtils.getScreenWidthDp(this);
//            float expressViewHeight = UIUtils.getHeight(this);
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(splashWidthPx, splashHeightPx)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(splashWidthDp, splashHeightDp)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(splashWidthPx, splashHeightPx)
                    .build();
        }
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "loadSplashAd OnError" + message + " code:" + code);
                showToast(message);
                goToMainActivity();
            }

            @Override
            public void onTimeout() {
                Log.d(TAG, "loadSplashAd onTimeout");
                showToast("开屏广告加载超时");
                goToMainActivity();
            }

            @Override
            public void onSplashAdLoad(com.bytedance.sdk.openadsdk.TTSplashAd ad) {
                Log.d(TAG, "loadSplashAd success");
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    goToMainActivity();
                }
                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                        showToast("开屏广告点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                        showToast("开屏广告展示");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        showToast("开屏广告跳过");
                        goToMainActivity();
                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        showToast("开屏广告倒计时结束");
                        goToMainActivity();
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                showToast("下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            showToast("下载完成...");

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            showToast("安装完成...");

                        }
                    });
                }
            }
        }, AD_TIME_OUT);
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        mSplashContainer.removeAllViews();  //移除所有视图
        this.finish();
    }

    private void showToast(String msg) {
        TToast.show(this, msg);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}