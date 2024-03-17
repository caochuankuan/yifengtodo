package com.yifeng.ads.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yifeng.ads.R;

public class FinishActivity extends AppCompatActivity {

    TextView tv1;
    Intent it1,it2;
    Bundle bd1,bd2;
    String daan1,daan2,daan3,daan4,daan5,st1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        TestActivity.instance1.finish();
        TestActivity.instance2.finish();
        TestActivity.instance3.finish();
        TestActivity.instance4.finish();
        TestActivity.instance5.finish();

        it1=getIntent();
        bd1 = it1.getExtras();
        daan1 = bd1.getString("daan1");
        daan2 = bd1.getString("daan2");
        daan3 = bd1.getString("daan3");
        daan4 = bd1.getString("daan4");
        daan5 = bd1.getString("daan5");
        st1 = bd1.getString("title");

        tv1 = findViewById(R.id.kaoshichengji);
        tv1.setText(st1);

        int i = 0;
        if (daan1.equals("C.毛泽东")){
            i++;
        }
        if (daan2.equals("C.邓小平")){
            i++;
        }
        if (daan3.equals("C.江泽民")){
            i++;
        }
        if (daan4.equals("C.胡锦涛")){
            i++;
        }
        if (daan5.equals("C.习近平")){
            i++;
        }

        RatingBar ratingBar = findViewById(R.id.ratingbar);
        ratingBar.setRating(i);

        if (i == 5){
            ImageView ig = findViewById(R.id.perfet);
            ig.setImageResource(R.drawable.perfet);
            TextView tv2 = findViewById(R.id.chengjiwei);
            tv2.setText("");
            TextView tv3 = findViewById(R.id.quandui);
            tv3.setText("全部回答正确");
        }

        Button bt1 = findViewById(R.id.retest);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it2 = new Intent(FinishActivity.this, TestActivity.class);
                bd2 = it1.getExtras();
                String a ="0";
                bd2.putCharSequence("timu",a);
                it2.putExtras(bd2);
                startActivity(it2);
                finish();
            }
        });
    }
}