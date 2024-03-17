package com.yifeng.ads.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yifeng.ads.R;

import java.util.Objects;

public class StartActivity extends AppCompatActivity {

    TextView tx1,tx2,tx3,tx4;
    String n, x,sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tx1 =findViewById(R.id.queren1);
        tx2 =findViewById(R.id.queren2);
        tx3 =findViewById(R.id.queren3);
        tx4 =findViewById(R.id.queren4);

        Intent it = getIntent();
        Bundle bd = it.getExtras();
        String st1 = bd.getString("xingming");
        String st2 = bd.getString("xuehao");
        String st3 = bd.getString("zhengzhi");
        String st4 = bd.getString("yuanxi");

        tx1.setText("姓名:"+st1);
        tx2.setText("学号:"+st2);
        tx3.setText("政治面貌:"+st3);
        tx4.setText("所属院系:"+st4);

        Button bt1 = findViewById(R.id.start);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(StartActivity.this, ChoiceActivity.class);
                startActivity(it);
                finish();
            }
        });

        Button bt2 = findViewById(R.id.relogin);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sp11 = getSharedPreferences("yifeng", MODE_PRIVATE);

                // 获取SharedPreferences.Editor实例
                SharedPreferences.Editor ed11 = sp11.edit();

                if (sp11.getString("xingming",n) != null && sp11.getString("xuehao",x) !=null  && Objects.equals(sp11.getString("sign", sign), "1")) {

                    // 使用clear()方法清除所有数据
                    ed11.clear().commit();

                    Toast.makeText(StartActivity.this, "已清除自动登录信息", Toast.LENGTH_SHORT).show();

                }

                Intent it = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(it);

                finish();
            }
        });
    }
}