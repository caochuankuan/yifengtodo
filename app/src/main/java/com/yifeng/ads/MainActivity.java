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
import com.yifeng.ads.RewardVideoActivity;

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
                    startActivity(new Intent(MainActivity.this, RewardVideoActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //获取到声明中的权限，提高广告变现效率。记得禁止插屏和Banner广告的权限申请否则会重复。
        TTAdManagerHolder.get().requestPermissionIfNecessary(MyApplication.getContext());
        //新插屏广告                    插屏广告代码位id
        InsertScreen.Inst().Init(this, "956462967", TTAdConstant.HORIZONTAL);
    }
}