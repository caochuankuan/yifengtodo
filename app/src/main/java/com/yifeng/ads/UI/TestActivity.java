package com.yifeng.ads.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yifeng.ads.R;

public class TestActivity extends AppCompatActivity {

    public static TestActivity instance1 = null;
    public static TestActivity instance2 = null;
    public static TestActivity instance3 = null;
    public static TestActivity instance4 = null;
    public static TestActivity instance5 = null;

    TextView tv1,tv2,tv3;
    ListView lv;
    Button bt1;
    String st1 = "";
    String st2 = "";
    String a;
    String[] ctype;

    Intent it,it1;
    Bundle bd;
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        it = getIntent();
        bd = it.getExtras();
        st1 = bd.getString("title");
        a = bd.getString("timu");
        int position = getIntent().getIntExtra("position", -1);
        //Toast.makeText(TestActivity.this, ""+position, Toast.LENGTH_SHORT).show();
        tv1 = findViewById(R.id.kaoshi);
        tv2 = findViewById(R.id.timu1);
        tv3 = findViewById(R.id.question1);
        lv = findViewById(R.id.answer1);
        bt1 = findViewById(R.id.tijiao1);
        EditText ed = findViewById(R.id.zhuguan);
        tv1.setText(st1);

        if (position == 0){
            if (a.equals("0")){
                a = "1";
                tv2.setText("第一题");
                tv3.setText("中国人民解放军和中华人民共和国的主要缔造者和领导人是:");
                ctype = new String[]{
                        "A.孙中山","B.陈独秀","C.毛泽东","D.周恩来"
                };
            } else if (a.equals("1")) {
                a = "2";
                tv2.setText("第二题");
                tv3.setText("中国社会主义改革开放和现代化建设的总设计师是:");
                ctype = new String[]{
                        "A.刘少奇","B.华国锋","C.邓小平","D.赵紫阳"
                };
            }else if (a.equals("2")) {
                a = "3";
                tv2.setText("第三题");
                tv3.setText("“三个代表”重要思想的主要创立者是:");
                ctype = new String[]{
                        "A.李鹏","B.叶剑英","C.江泽民","D.彭德怀"
                };
            }else if (a.equals("3")) {
                a = "4";
                tv2.setText("第四题");
                tv3.setText("“科学发展观是坚持以人为本，全面、协调、可持续的发展观，促进经济社会协调发展和人的全面发展。”理论的提出者是:");
                ctype = new String[]{
                        "A.温家宝","B.王晨","C.胡锦涛","D.荣毅仁"
                };
            }else if (a.equals("4")) {
                a = "5";
                tv2.setText("第五题");
                tv3.setText("“实现中华民族伟大复兴是近代以来中华民族最伟大的梦想”提出者是:");
                ctype = new String[]{
                        "A.李克强","B.周恩来","C.习近平","D.王岐山"
                };
            }

            bd.putCharSequence("timu",a);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,ctype);
            lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    st2 = adapterView.getItemAtPosition(i).toString();
                    lv.setItemChecked(i,true);
                }
            });

            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (st2 == "") {
                        Toast.makeText(TestActivity.this, "请选择题目再提交", Toast.LENGTH_SHORT).show();
                    }else {
                        switch (a){
                            case "1":{
                                bd.putCharSequence("daan1",st2);
                                instance1 = TestActivity.this;
                            }
                            case "2":{
                                bd.putCharSequence("daan2",st2);
                                instance2= TestActivity.this;
                            }
                            case "3":{
                                bd.putCharSequence("daan3",st2);
                                instance3 = TestActivity.this;
                            }
                            case "4":{
                                bd.putCharSequence("daan4",st2);
                                instance4 = TestActivity.this;
                            }
                            case "5":{
                                bd.putCharSequence("daan5",st2);
                                instance5 = TestActivity.this;
                            }
                        }
                        if (a.equals("5")){
                            it1 = new Intent(TestActivity.this, FinishActivity.class);
                            it1.putExtras(bd);
                            startActivity(it1);
                        }else {
                            it.putExtras(bd);
                            startActivity(it);
                        }
                    }
                }
            });
        } else if (position == 1) {
            tv2.setText("主观题");
            tv3.setText("请填写您对文化自信的认识");
            lv.setVisibility(View.GONE);
            ed.setVisibility(View.VISIBLE);
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ed.equals("")){
                        Toast.makeText(TestActivity.this, "请作答再提交", Toast.LENGTH_SHORT).show();
                    }else {
                        new AlertDialog.Builder(TestActivity.this).setTitle("温馨提示")
                                .setMessage("答案已提交")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(TestActivity.this, ChoiceActivity.class));
                                        finish();
                                    }
                                }).show();
                    }
                }
            });
        } else  {
            tv2.setText("建设中");
            tv3.setVisibility(View.GONE);
            new AlertDialog.Builder(TestActivity.this).setTitle("温馨提示")
                    .setMessage("建设中")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(TestActivity.this, ChoiceActivity.class));
                            finish();
                        }
                    }).show();
        }

    }
}