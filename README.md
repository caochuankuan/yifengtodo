# 说明
# 1.开屏广告
## 1.在settings.gradle文件中添加Maven的引用
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 下面三行
        maven {
            url 'https://artifact.bytedance.com/repository/pangle'
        }
    }
}
```
## 2.在build.gradle(Module:app)添加SDK依赖。
```groovy
dependencies {
    implementation 'com.pangle.cn:ads-sdk-pro:4.4.0.9' //按需升级
}
```
## 3.AndroidManifest配置添加权限
```xml
    <!-- 必要权限 -->
    <uses-permission android:name="android.permission.INTERNET" /> <!-- 必要权限，解决安全风险漏洞，发送和注册广播事件需要调用带有传递权限的接口 -->
    <permission
        android:name="${applicationId}.openadsdk.permission.TT_PANGOLIN"
        android:protectionLevel="signature" />

    <uses-permission android:name="${applicationId}.openadsdk.permission.TT_PANGOLIN" /> <!-- 可选权限 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission
        android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
    <uses-permission android:name="android.permission.GET_TASKS" /> <!-- 可选，穿山甲提供“获取地理位置权限”和“不给予地理位置权限，开发者传入地理位置参数”两种方式上报用户位置，两种方式均可不选，添加位置权限或参数将帮助投放定位广告 -->
    <!-- 请注意：无论通过何种方式提供给穿山甲用户地理位置，均需向用户声明地理位置权限将应用于穿山甲广告投放，穿山甲不强制获取地理位置信息 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <!-- 如果视频广告使用textureView播放，请务必添加，否则黑屏 -->
    <uses-permission android:name="android.permission.WAKE_LOCK" /> <!-- 穿山甲3400版本新增：建议添加“query_all_package”权限，穿山甲将通过此权限在Android R系统上判定广告对应的应用是否在用户的app上安装，避免投放错误的广告，以此提高用户的广告体验。若添加此权限，需要在您的用户隐私文档中声明！ -->
    <uses-permission
        android:name="android.permission.QUERY_ALL_PACKAGES"
        tools:ignore="QueryAllPackagesPermission" />

```
## 4.在AndroidManifest中添加如下代码(android7+)：
```xml
        <!-- application标签内 -->
        <provider
            android:name="com.bytedance.sdk.openadsdk.TTFileProvider"
            android:authorities="${applicationId}.TTFileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

        <provider
            android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
            android:authorities="${applicationId}.TTMultiProvider"
            android:exported="false" />
```
## 5.在res/xml目录下，新建file_paths.xml，代码：
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
   <!--为了适配所有路径可以设置 path = "." -->
   <external-path name="tt_external_root" path="." />
   <external-path name="tt_external_download" path="Download" />
   <external-files-path name="tt_external_files_download" path="Download" />
   <files-path name="tt_internal_file_download" path="Download" />
   <cache-path name="tt_internal_cache_download" path="Download" />
</paths>
```
## 6.初始化SDK类
### a.创建一个SDK初始化类TTAdManagerHolder.java
```java
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
```
### b.新建类MyApplication.java 用来做初始化，配置全局上下文
```java
package com.yifeng.daiban;

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
```
### c.在AndroidManifest.xml文件做配置：
```xml
<application
        android:name=".MyApplication"
        android:usesCleartextTraffic="true" <!--安卓高系统p配置使用明文流量 -->
        android:allowBackup="true"
        // 省略
```
### d.(非必需）
建议在广告请求前，合适的时机调用SDK提供的方法，在用户可以授权的情况下获取到声明中的权限，提高广告变现效率，申请通话、位置、存储权限，代码如下：
```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取到声明中的权限，提高广告变现效率。记得禁止插屏和Banner广告的权限申请否则会重复。
        TTAdManagerHolder.get().requestPermissionIfNecessary(MyApplication.getContext());
    }
}
```
## 7.新建一些工具类
（新建软件包：Utils）
### a.屏幕适配工具类UIUtils.java
```java
package com.yifeng.ads.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UIUtils {
    public static float getScreenWidthDp(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float width = context.getResources().getDisplayMetrics().widthPixels;
        return width / (scale <= 0 ? 1 : scale) + 0.5f;
    }

    //全面屏、刘海屏适配
    public static float getHeight(Activity activity) {
        hideBottomUIMenu(activity);
        float height;
        int realHeight = getRealHeight(activity);
        if (UIUtils.hasNotchScreen(activity)) {
            height = px2dip(activity, realHeight - getStatusBarHeight(activity));
        } else {
            height = px2dip(activity, realHeight);
        }
        return height;
    }

    public static void hideBottomUIMenu(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取屏幕真实高度，不包含下方虚拟导航栏
    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        int realHeight = dm.heightPixels;
        return realHeight;
    }

    //获取状态栏高度
    public static float getStatusBarHeight(Context context) {
        float height = 0;
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 判断是否是刘海屏
     *
     * @return
     */
    public static boolean hasNotchScreen(Activity activity) {
        return isAndroidPHasNotch(activity)
                || getInt("ro.miui.notch", activity) == 1
                || hasNotchAtHuawei(activity)
                || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity);
    }

    /**
     * Android P 刘海屏判断
     *
     * @param activity
     * @return
     */
    public static boolean isAndroidPHasNotch(Activity activity) {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout displayCutout = null;
            try {
                WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    displayCutout = windowInsets.getDisplayCutout();
                }
                if (displayCutout != null) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key, Activity activity) {
        int result = 0;
        if (isMiui()) {
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    /**
     * O-P-P-O刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtOPPO(Context context) {
        String temp = "com.kllk.feature.screen.heteromorphism";
        String name = getKllkDecryptString(temp);
        return context.getPackageManager().hasSystemFeature(name);
    }

    public static boolean isMiui() {
        boolean sIsMiui = false;
        try {
            Class<?> clz = Class.forName("miui.os.Build");
            if (clz != null) {
                sIsMiui = true;
                //noinspection ConstantConditions
                return sIsMiui;
            }
        } catch (Exception e) {
            // ignore
        }
        return sIsMiui;
    }

    /**
     * 用于o-p-p-o 版本隐私协议
     */
    public static String getKllkDecryptString(String encryptionString) {

        if (TextUtils.isEmpty(encryptionString)) {
            return "";
        }
        String decryptTag = "";
        String decryptCapitalized = "O" + "P" + "P" + "O";
        String decrypt = "o" + "p" + "p" + "o";
        if (encryptionString.contains("KLLK")) {
            decryptTag = encryptionString.replace("KLLK", decryptCapitalized);
        } else if (encryptionString.contains("kllk")) {
            decryptTag = encryptionString.replace("kllk", decrypt);
        }
        return decryptTag;

    }

    public static void setViewSize(View view, int width, int height) {
        if (view.getParent() instanceof FrameLayout) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
        }
    }

    public static int getScreenWidthInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeightInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getScreenHeight(Context context) {
        return (int) (getScreenHeightInPx(context) + getStatusBarHeight(context));
    }

    public static void removeFromParent(View view) {
        if (view != null) {
            ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }
    }

    /**
     * 获取全面屏宽高
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[]{0, 0};
        if (context == null) {
            return size;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }
}
```
### b.弹窗提示工具类TToast.java
```java
package com.yifeng.ads.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class TToast {
    private static Toast sToast;

    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String msg, int duration) {
        Toast toast = getToast(context);
        if (toast != null) {
            toast.setDuration(duration);
            toast.setText(String.valueOf(msg));
            toast.show();
        } else {
            Log.i("TToast", "toast msg: " + String.valueOf(msg));
        }
    }

    @SuppressLint("ShowToast")
    private static Toast getToast(Context context) {
        if (context == null) {
            return sToast;
        }
//        if (sToast == null) {
//            synchronized (TToast.class) {
//                if (sToast == null) {
        sToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
//                }
//            }
//        }
        return sToast;
    }

    public static void reset() {
        sToast = null;
    }
}
```
## 8.创建Activity
### a.SplashActivity.java
```java
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
```
### b.activity_splashxml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/splash_container"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    tools:context=".SplashActivity">

</FrameLayout>
```
## 9.修改第一界面
```xml
        <activity
            android:name=".MainActivity"
            android:exported="false" />
        <activity
            android:name=".SplashActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```
## 10.项目gradle.properties
添加如下代码
```groovy
android.useAndroidX=true   // 这行可能存在
android.enableJetifier=true
```
## 11.适应全面屏
AndroidManifest.xml文件
```xml
    <application
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
```
```xml
        <activity
            android:name=".SplashActivity"
            android:exported="true"
            android:theme="@style/Theme.遇见待办">
```
themes.xml
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.Theme.遇见待办" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
        <item name="android:windowTranslucentNavigation" tools:ignore="NewApi">true</item>
        <!--解决部分手机隐藏状态栏顶部出现小黑条的问题-->
        <item name="android:windowLayoutInDisplayCutoutMode" tools:ignore="NewApi">shortEdges</item>
        <item name="android:windowFullscreen">true</item>
    </style>

    <style name="Theme.遇见待办" parent="Base.Theme.遇见待办" />
</resources>
```

## 注意事项
1.新创建的代码位或编辑代码位的操作需要2-3分钟左右的生效时间。
2.如遇以下错误
```agsl
Caused by: java.lang.SecurityException: Writable dex file '/data/user/0/com.yifeng.ads/files/pangle_p/com.byted.pangle/version-4409/apk/base-1.apk' is not allowed.
```
这是Android 14的锅，改一下target api

# 2.插屏广告
新建InsertScreen.java
```java
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
```
然后在主界面MainActivity.java或其他界面的Oncreate()方法下做调用，代码如下：
```java
package com.yifeng.ads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.yifeng.ads.UI.LoginActivity;
import java.util.Objects;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    String n, x ,sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取到声明中的权限，提高广告变现效率。记得禁止插屏和Banner广告的权限申请否则会重复。
        TTAdManagerHolder.get().requestPermissionIfNecessary(MyApplication.getContext());
        //新插屏广告                    插屏广告代码位id
        InsertScreen.Inst().Init(this, "956462967", TTAdConstant.HORIZONTAL);
        //banner广告   banner广告代码位id
        //FrameLayout mExpressBannerContainer = findViewById(R.id.banner_container);
        //Banner.Inst().Init(this, mExpressBannerContainer, "956462966");


        SharedPreferences sp = getSharedPreferences("yifeng", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (sp.getString("xingming",n) != null && sp.getString("xuehao",x) !=null  && Objects.equals(sp.getString("sign", sign), "1")) {
            // 使用clear()方法清除所有数据
            TextView user =findViewById(R.id.user);
            user.setText("欢迎回来，"+sp.getString("xingming",n));

        }

        Button bt = findViewById(R.id.center);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
```
# 3.Banner广告
新建Banner.java
```java
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
```
然后在主界面MainActivity.java或其他界面的布局xml文件中的合适位置，设置广告显示布局，代码如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg"
    tools:context=".MainActivity">

    <!--  banner_start  -->
    <LinearLayout
        android:id="@+id/linearLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        tools:ignore="MissingConstraints">

        <FrameLayout
            android:id="@+id/banner_container"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent"
            tools:ignore="MissingConstraints" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:text="这里是一行示例布局展示文字"
            android:textSize="18sp" />
    </LinearLayout>
    <!--  banner_start  -->

    <TextView
        android:id="@+id/emptytext"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:textSize="10sp"/>

    <TextView
        android:id="@+id/title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@id/emptytext"
        android:gravity="center"
        android:text="遇见广告"
        android:textColor="#2A62A0"
        android:textSize="50sp"/>

    <TextView
        android:id="@+id/user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@id/title"
        android:gravity="center"
        android:text=""
        android:textColor="#f9f000"
        android:textSize="25sp"/>

    <TextView
        android:id="@+id/staff"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:text="designed by：于逸风"
        android:textColor="#2A62A0"
        android:gravity="center"
        android:layout_marginBottom="6dp"/>

    <Button
        android:id="@+id/center"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_marginBottom="30dp"
        android:layout_centerHorizontal="true"
        android:text="进入软件"
        android:textSize="20sp"
        android:theme="@style/Widget.Material3.Button"
        android:background="#f9f000"/>

</RelativeLayout>
```
然后在主界面MainActivity.java或其他界面的Oncreate()方法下做调用，代码如下：
```java
package com.yifeng.ads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.yifeng.ads.UI.LoginActivity;
import java.util.Objects;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    String n, x ,sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取到声明中的权限，提高广告变现效率。记得禁止插屏和Banner广告的权限申请否则会重复。
        TTAdManagerHolder.get().requestPermissionIfNecessary(MyApplication.getContext());
        //新插屏广告                    插屏广告代码位id
        InsertScreen.Inst().Init(this, "956462967", TTAdConstant.HORIZONTAL);
        //banner广告   banner广告代码位id
        FrameLayout mExpressBannerContainer = findViewById(R.id.banner_container);
        Banner.Inst().Init(this, mExpressBannerContainer, "956462966");


        SharedPreferences sp = getSharedPreferences("yifeng", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (sp.getString("xingming",n) != null && sp.getString("xuehao",x) !=null  && Objects.equals(sp.getString("sign", sign), "1")) {
            // 使用clear()方法清除所有数据
            TextView user =findViewById(R.id.user);
            user.setText("欢迎回来，"+sp.getString("xingming",n));

        }

        Button bt = findViewById(R.id.center);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
```
## 注意事项
1.参考：[CSDN](https://blog.csdn.net/yi_rui_jie/article/details/125535082)

2.本项目开屏广告sdk采用穿山甲ads-sdk-pro:4.4.0.9
```
implementation 'com.pangle.cn:ads-sdk-pro:4.4.0.9'
```
3.本项目插屏、banner广告sdk采用穿山甲ads-sdk-pro:5.6.1.5
（如由4.4.0.9-->5.6.1.5，需将TTAdManagerHolder.java中的.asyncInit(true)注释：
```
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
```
```
implementation 'com.pangle.cn:ads-sdk-pro:5.6.1.5'
```

# 4.激励广告
## 1.
![激励广告](E:\Androidstudio\program\ad\app\src\main\res\drawable\img.png "激励广告")
## 2.新建活动RewardVideoActivity
java:
```java
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


```
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:ignore="HardcodedText"
    android:background="@drawable/bg"
    android:gravity="center"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!--    <Button-->
    <!--        android:id="@+id/btn_arv_back"-->
    <!--        android:layout_width="match_parent"-->
    <!--        android:layout_height="wrap_content"-->
    <!--        android:drawablePadding="2dp"-->
    <!--        android:gravity="left|center_vertical"-->
    <!--        android:paddingLeft="13dp"-->
    <!--        android:text="Back"-->
    <!--        android:textAllCaps="false"-->
    <!--        android:textSize="15sp" />-->

    <!--    <Button-->
    <!--        android:id="@+id/btn_reward_load"-->
    <!--        android:layout_width="match_parent"-->
    <!--        android:layout_marginTop="10dp"-->
    <!--        android:layout_height="52dp"-->
    <!--        android:layout_marginStart="8dp"-->
    <!--        android:layout_marginLeft="8dp"-->
    <!--        android:layout_marginEnd="8dp"-->
    <!--        android:layout_marginRight="8dp"-->
    <!--        android:gravity="center"-->
    <!--        android:text="加载横版激励视频广告"-->
    <!--        android:textColor="@android:color/black"-->
    <!--        android:textSize="14sp"/>-->

<!--    <Button-->
<!--        android:id="@+id/btn_reward_load_vertical"-->
<!--        android:layout_width="match_parent"-->
<!--        android:layout_height="52dp"-->
<!--        android:layout_marginStart="8dp"-->
<!--        android:layout_marginLeft="8dp"-->
<!--        android:layout_marginEnd="8dp"-->
<!--        android:layout_marginRight="8dp"-->
<!--        android:gravity="center"-->
<!--        android:text="加载竖版激励视频广告"-->
<!--        android:textColor="@android:color/black"-->
<!--        android:textSize="14sp"/>-->

<!--    <Button-->
<!--        android:id="@+id/btn_reward_show"-->
<!--        android:layout_width="match_parent"-->
<!--        android:layout_height="52dp"-->
<!--        android:layout_marginStart="8dp"-->
<!--        android:layout_marginLeft="8dp"-->
<!--        android:layout_marginEnd="8dp"-->
<!--        android:layout_marginRight="8dp"-->
<!--        android:gravity="center"-->
<!--        android:text="展示激励视频广告"-->
<!--        android:textColor="@android:color/black"-->
<!--        android:textSize="14sp"/>-->

    <Button
        android:id="@+id/main"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:layout_marginStart="8dp"
        android:layout_marginLeft="8dp"
        android:layout_marginEnd="8dp"
        android:layout_marginRight="8dp"
        android:gravity="center"
        android:text="进入主页"
        android:textColor="@android:color/black"
        android:textSize="14sp"/>


</LinearLayout>

```

参考资料：

1.[Android项目集成穿山甲开屏/插屏/横幅广告教程大全](https://blog.csdn.net/yi_rui_jie/article/details/125535082)

2.[Android 接入穿山甲激励视频广告步骤与错误总结](https://blog.csdn.net/Ann_52547/article/details/128102566)

3.[Android 接入穿山甲SDK之激励视频广告](https://blog.csdn.net/qq_41973169/article/details/125429587)

