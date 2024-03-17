package com.yifeng.ads.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.yifeng.ads.R;

public class EssayActivity extends Activity {

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        String st = bd.getString("title");
        int position = getIntent().getIntExtra("position", -1);

        TextView textView = findViewById(R.id.lanmu);
        textView.setText(st);

        TextView content = findViewById(R.id.content);
        // 假设 position 是一个整数变量，表示位置

        // 获取字符串资源ID
        int resourceId = getResources().getIdentifier("scroll_text" + position, "string", getPackageName());

        // 检查资源ID是否有效
        if (resourceId != 0) {
            // 通过资源ID获取字符串
            String text = getString(resourceId);
            // 然后将获取到的字符串应用到你的 TextView 或其他 UI 元素中
            content.setText(text);
        } else {
            // 处理资源未找到的情况
            // 可以设置默认文本或者做其他处理
        }


        // 模拟处理逻辑后延迟一段时间再设置返回结果，这里您应该根据实际情况处理
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 设置返回结果，传递位置
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                setResult(RESULT_OK, resultIntent);
                //finish();
            }
        }, 2000); // 假设延迟2秒，您可以根据实际情况调整
    }
}
