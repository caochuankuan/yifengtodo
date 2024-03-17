package com.yifeng.ads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.yifeng.ads.UI.LoginActivity;
import com.yifeng.ads.Utils.RewardAdvancedInfo;
import com.yifeng.ads.Utils.RewardBundleModel;
import com.yifeng.ads.Utils.TToast;

import static com.bytedance.sdk.openadsdk.TTAdLoadType.PRELOAD;

//穿山甲
public class RewardVideoActivity extends Activity {
    private static final String TAG = "OneActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;

    private Button main;

    private TTAdNative mTTAdNative;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mIsLoaded = false;
    // 是否开放进阶奖励功能
    private final boolean isEnableAdvancedReward = false;

    private RewardAdvancedInfo mRewardAdvancedInfo;
    private int mNowPlayAgainCount = 0;
    private int mNextPlayAgainCount = 0;
    private boolean mHasShowDownloadActive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);


//        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
//        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        main = findViewById(R.id.main);

        getExtraInfo();
        initClickEvent();

//        1.初始化穿山甲SDK
        TTAdConfig();
//        2.手动授权,我直接写Manitest里了，但是现在好像都需要手动授权了，这里我没写
//        3.创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        loadAd(mVerticalCodeId);
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
//        mHorizontalCodeId = intent.getStringExtra("horizontal_rit");
//        mVerticalCodeId = intent.getStringExtra("vertical_rit");
//        mHorizontalCodeId = "950516680";
        mVerticalCodeId = "956462970";

    }

    private void initClickEvent() {

//        mLoadAdVertical.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                loadAd(mVerticalCodeId);
//            }
//        });
//        mShowAd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                loadAd(mVerticalCodeId);
//
//                //当mIsLoaded标识为true 代表广告视频本地加载完整 可直接开启一个主线程处理showRewardVideoAd
//                if (mttRewardVideoAd != null && mIsLoaded) {
//                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
//                    //该方法直接展示广告
////                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
//
//                    //展示广告，并传入广告展示的场景
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
//                    mttRewardVideoAd = null;
//                } else {
//                    TToast.show(RewardVideoActivity.this, "请先加载广告");
//                }
//            }
//        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadAd(mVerticalCodeId);

                //当mIsLoaded标识为true 代表广告视频本地加载完整 可直接开启一个主线程处理showRewardVideoAd
                if (mttRewardVideoAd != null && mIsLoaded) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                    mttRewardVideoAd = null;
                } else {
                    TToast.show(RewardVideoActivity.this, "请先加载广告");
                }

            }
        });

    }

    public void loadAd(final String codeId) {
//        4。 创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
//                .setRewardName("饲料") //奖励的名称 选填 -已废弃
//                .setRewardAmount(300)  //奖励的数量 选填 -已废弃
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即
                // 且仅是模板渲染的代码位ID使用，非模板渲染代码位切勿使用
//                .setExpressViewAcceptedSize(500,500)  /
//                .setUserID("tag123")//tag_id
//                .setMediaExtra("media_extra") //附加参数
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();

//       5. 请求广告 -异步加载广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                TToast.show(RewardVideoActivity.this, message);

            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.e(TAG, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
                TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Log.e(TAG, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
                TToast.show(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached");
//                ad.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                //当mIsLoaded标识为true 代表广告视频本地加载完整 可直接开启一个主线程处理showRewardVideoAd
            }

            //　广告加载完成的回调　
            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.e(TAG, "Callback --> onRewardVideoAdLoad");
                TToast.show(RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));

                mttRewardVideoAd = ad;
                // 6. 广告交互监听器
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    //视频广告展示回调
                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> rewardVideoAd show");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                    }
                    //广告的下载bar点击回调
                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> rewardVideoAd bar click");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    //视频广告关闭回调
                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd close");
                        if (isEnableAdvancedReward && mRewardAdvancedInfo != null) {
                            Log.d(TAG, "本次奖励共发放：" + mRewardAdvancedInfo.getRewardAdvancedAmount());
                        }
                        startActivity(new Intent(RewardVideoActivity.this, LoginActivity.class));
                    }
                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> rewardVideoAd complete");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                        startActivity(new Intent(RewardVideoActivity.this, LoginActivity.class));
                    }
                    //视频广告播放错误回调
                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "Callback --> rewardVideoAd error");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                        startActivity(new Intent(RewardVideoActivity.this, LoginActivity.class));
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称，code：错误码，msg：错误信息
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName,int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Log.e(TAG, "Callback --> " + logString);
                        TToast.show(RewardVideoActivity.this, logString);
                        startActivity(new Intent(RewardVideoActivity.this, LoginActivity.class));
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        RewardBundleModel rewardBundleModel = new RewardBundleModel(extraInfo);
                        Log.e(TAG, "Callback --> rewardVideoAd has onRewardArrived " +
                                "\n奖励是否有效：" + isRewardValid +
                                "\n奖励类型：" + rewardType +
                                "\n奖励名称：" + rewardBundleModel.getRewardName() +
                                "\n奖励数量：" + rewardBundleModel.getRewardAmount() +
                                "\n建议奖励百分比：" + rewardBundleModel.getRewardPropose());
                        if (!isRewardValid) {
                            Log.d(TAG, "发送奖励失败 code：" + rewardBundleModel.getServerErrorCode() +
                                    "\n msg：" + rewardBundleModel.getServerErrorMsg());
                            return;
                        }

                        if (!isEnableAdvancedReward) {
                            // 未使用进阶奖励功能

                            if (rewardType == TTRewardVideoAd.REWARD_TYPE_DEFAULT) {
                                Log.d(TAG, "普通奖励发放，name:" + rewardBundleModel.getRewardName() +
                                        "\namount:" + rewardBundleModel.getRewardAmount());
                            }
                        } else {
                            // 使用了进阶奖励功能
                            if (mRewardAdvancedInfo != null) {
                                mRewardAdvancedInfo.proxyRewardModel(rewardBundleModel, false);
                            }
                        }
                    }

                    //视频广告跳过回调
                    @Override
                    public void onSkippedVideo() {
                        Log.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });

                //其实到这儿就没了，但是为了严谨，增加了再看一个的回调 和 下载的回调///
                mttRewardVideoAd.setRewardPlayAgainInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override

                    public void onAdShow() {
                        mNowPlayAgainCount = mNextPlayAgainCount;
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain show");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                    }

                    @Override

                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain bar click");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                    }

                    @Override

                    public void onAdClose() {
                        // 再看广告不会调到这个回调
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain complete");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain error");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override

                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "rewardPlayAgain verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 " + logString);
                        TToast.show(RewardVideoActivity.this, logString);
                    }

                    @Override
                    public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                        RewardBundleModel rewardBundleModel = new RewardBundleModel(extraInfo);
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain has onRewardArrived " +
                                "\n奖励是否有效：" + isRewardValid +
                                "\n奖励类型：" + rewardType +
                                "\n奖励名称：" + rewardBundleModel.getRewardName() +
                                "\n奖励数量：" + rewardBundleModel.getRewardAmount() +
                                "\n建议奖励百分比：" + rewardBundleModel.getRewardPropose());

                        if (!isEnableAdvancedReward) {
                            // 再看一个未使用进阶奖励功能

                            if (rewardType == TTRewardVideoAd.REWARD_TYPE_DEFAULT) {
                                Log.d(TAG, "再看一个普通奖励发放，name:" + rewardBundleModel.getRewardName() +
                                        "\namount:" + rewardBundleModel.getRewardAmount());
                            }
                        } else {
                            // 再看一个使用了进阶奖励功能
                            if (mRewardAdvancedInfo != null) {
                                mRewardAdvancedInfo.proxyRewardModel(rewardBundleModel, true);
                            }
                        }
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.e(TAG, "Callback --> 第 " + mNowPlayAgainCount + " 次再看 rewardPlayAgain has onSkippedVideo");
                        TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });

                mttRewardVideoAd.setRewardPlayAgainController(new TTRewardVideoAd.RewardAdPlayAgainController() {
                    @Override
                    public void getPlayAgainCondition(int nextPlayAgainCount, Callback callback) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(KEY_PLAY_AGAIN_ALLOW, true);
                        bundle.putString(KEY_PLAY_AGAIN_REWARD_NAME, "饲料");
                        bundle.putString(KEY_PLAY_AGAIN_REWARD_AMOUNT, nextPlayAgainCount + "g");
                        mNextPlayAgainCount = nextPlayAgainCount;
                        callback.onConditionReturn(bundle);
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                    }
                });


            }
        });
    }

    private String getAdType(int type) {
        switch (type) {

            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;

            case TTAdConstant.AD_TYPE_LIVE:
                return "直播流，type=" + type;
        }

        return "未知类型+type=" + type;
    }


    /*初始化穿山甲SDK*/
    private void TTAdConfig(){
        TTAdSdk.getAdManager().requestPermissionIfNecessary(this);

        TTAdConfig config=new TTAdConfig.Builder()
                .appId("5495459")
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName("遇见广告")
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                //.needClearTaskReset()
//                .injectionAuth(TTLiveTokenHelper.getInstance().useHostAuth() ? new TTInjectionAuthImpl() : null)
                .build();

        TTAdSdk.init(this, config, new TTAdSdk.InitCallback() {
            @Override
            public void success() {
                Log.i(TAG, "success: " + TTAdSdk.isInitSuccess());
                Log.i(TAG, "success: 初始化穿山甲成功");
            }

            @Override
            public void fail(int code, String msg) {
                Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
            }
        });
    }
}

